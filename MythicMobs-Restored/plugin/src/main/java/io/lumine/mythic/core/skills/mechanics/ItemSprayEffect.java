package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:itemspray",
   aliases = {"itemspray", "e:itemspray"},
   description = "Sprays items everywhere"
)
public class ItemSprayEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The item(s) or droptable to use"
   )
   private final DropTable dropTable;
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of items",
      defValue = "10"
   )
   protected PlaceholderInt amount;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration of the item spray effect",
      defValue = "20"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the item spray effect",
      defValue = "0"
   )
   protected PlaceholderDouble radius;
   @MythicFields({@MythicField(
         name = "force",
         aliases = {"f", "velocity", "v"},
         description = "The velocity of the item spray effect",
         defValue = "1"
      ), @MythicField(
         name = "yForce",
         aliases = {"yf", "yvelocity", "yv"},
         description = "The y-velocity of the item spray effect",
         defValue = "1"
      )})
   protected PlaceholderDouble force;
   @MythicFields({@MythicField(
         name = "force",
         aliases = {"f", "velocity", "v"},
         description = "The velocity of the item spray effect",
         defValue = "1"
      ), @MythicField(
         name = "yForce",
         aliases = {"yf", "yvelocity", "yv"},
         description = "The y-velocity of the item spray effect",
         defValue = "1"
      )})
   protected PlaceholderDouble yForce;
   @MythicField(
      name = "yOffset",
      aliases = {"yo", "y"},
      description = "The y-offset of the item spray effect",
      defValue = "1"
   )
   protected PlaceholderDouble yOffset;
   @MythicField(
      name = "allowPickup",
      aliases = {"ap"},
      description = "Whether or not to allow entities to pickup the item(s)",
      defValue = "false"
   )
   protected boolean allowPickup;
   @MythicField(
      name = "gravity",
      aliases = {"g"},
      description = "Apply gravity to the item spray effect",
      defValue = "true"
   )
   protected boolean applyGravity;
   @MythicField(
      name = "audience",
      description = "The audience of the effect"
   )
   protected SkillAudience audience;

   public ItemSprayEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 10);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "20");
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 0.0);
      this.force = mlc.getPlaceholderDouble(new String[]{"force", "f", "velocity", "v"}, 1.0);
      this.yForce = mlc.getPlaceholderDouble(new String[]{"yforce", "yf", "yvelocity", "yv"}, this.force);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "yo", "y"}, 1.0);
      this.allowPickup = mlc.getBoolean(new String[]{"allowpickup", "ap"}, false);
      this.applyGravity = mlc.getBoolean(new String[]{"gravity", "g"}, true);
      this.audience = this.config.getAudience("audience", "world");
      if (this.allowPickup && mlc.getString(new String[]{"audience"}, null) != null) {
         MythicLogger.errorMechanicConfig(
            this,
            mlc,
            "allowPickup=true drops a real, globally-visible item entity - the 'audience' parameter (and any execution audience restriction) has no effect on it."
         );
      }

      String itemString = mlc.getString(new String[]{"items", "item", "i"}, "iron_sword");
      Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(itemString);
      if (maybeTable.isPresent()) {
         this.dropTable = maybeTable.get();
      } else {
         if (itemString.startsWith("\"")) {
            try {
               itemString = itemString.substring(1, itemString.length() - 1);
            } catch (Exception e) {
               MythicLogger.errorMechanicConfig(this, mlc, "The 'items' attribute is required.");
            }

            itemString = SkillString.parseMessageSpecialChars(itemString);
         }

         List<String> items = new ArrayList<>(Arrays.asList(itemString.split(",")));
         this.dropTable = new DropTable("DropItemMechanic", "DropItemMechanic", items);
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target.getLocation());
      return SkillResult.SUCCESS;
   }

   protected void playEffect(SkillMetadata data, AbstractLocation location) {
      AbstractEntity caster = data.getCaster().getEntity();
      LootBag loot = this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      List<Drop> drops = loot.getLootTable();
      if (drops.size() > 1) {
         Collections.shuffle(drops);
      }

      for (Drop type : drops) {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop) {
            if (((CustomDrop)type).getDrop().isEmpty()) {
               continue;
            }

            drop = ((CustomDrop)type).getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            if (this.allowPickup) {
               ItemStack item = ((BukkitItemStack)itemDrop.getDrop(new DropMetadataImpl(data.getCaster(), caster, data, null), amount)).getItemStack();
               SkillAdapter.get()
                  .itemSprayEffect(
                     location,
                     item,
                     this.amount.get(data),
                     this.duration.get(data).getTicks(),
                     this.force.get(data),
                     this.yForce.get(data),
                     this.radius.get(data),
                     this.yOffset.get(data),
                     this.allowPickup,
                     this.applyGravity
                  );
            } else {
               this.playFakeItemSprayEffect(data, location, itemDrop);
            }
         }
      }
   }

   private void playFakeItemSprayEffect(SkillMetadata data, AbstractLocation source, IItemDrop itemDrop) {
      int amount = this.amount.get(data);
      int duration = this.duration.get(data).getTicks();
      double force = this.force.get(data);
      double yForce = this.yForce.get(data);
      double radius = this.radius.get(data);
      double yOffset = this.yOffset.get(data);
      DropMetadataImpl dropMetadata = new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null);
      ItemStack item = ((BukkitItemStack)itemDrop.getDrop(dropMetadata, amount)).getItemStack();
      AbstractLocation location = source.clone().add(0.0, yOffset, 0.0);
      ItemStack is = item;
      item.setAmount(1);
      PacketItem[] items = new PacketItem[amount];
      Collection<AbstractPlayer> seen = SkillAudience.getEffective(this.audience, data, data.getTrigger());
      if (radius <= 0.0) {
         for (int i = 0; i < amount; i++) {
            PacketItem packetItem = new PacketItem(source);
            packetItem.setItem(is);
            packetItem.getRenderer().spawn(seen);
            packetItem.setVelocity(
               new AbstractVector((Numbers.randomDouble() - 0.5) * force, (Numbers.randomDouble() - 0.5) * yForce, (Numbers.randomDouble() - 0.5) * force)
            );
            items[i] = packetItem;
         }
      } else {
         for (int i = 0; i < amount; i++) {
            AbstractLocation lx = location.clone();
            lx.setX(lx.getX() - radius + Numbers.randomDouble() * (radius * 2.0));
            lx.setZ(lx.getZ() - radius + Numbers.randomDouble() * (radius * 2.0));
            PacketItem packetItem = new PacketItem(lx);
            packetItem.setItem(is);
            packetItem.getRenderer().spawn(seen);
            packetItem.setVelocity(
               new AbstractVector((Numbers.randomDouble() - 0.5) * force, (Numbers.randomDouble() - 0.5) * yForce, (Numbers.randomDouble() - 0.5) * force)
            );
            items[i] = packetItem;
         }
      }

      Schedulers.sync().runLater(() -> {
         for (int ix = 0; ix < items.length; ix++) {
            items[ix].destroy();
         }
      }, duration);
   }
}
