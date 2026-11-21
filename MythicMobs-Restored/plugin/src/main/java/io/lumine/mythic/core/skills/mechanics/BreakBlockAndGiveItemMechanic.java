package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.drops.droppables.VanillaItemDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "breakBlockAndGiveItem",
   aliases = {"blockBreakAndGiveItem"},
   description = "Breaks the block at the target location"
)
public class BreakBlockAndGiveItemMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "doDrops",
      aliases = {"drops", "d"},
      description = "Drops the block broken",
      defValue = "true"
   )
   private final boolean doDrops;
   @MythicField(
      name = "doEffect",
      aliases = {"effect", "e"},
      description = "Plays the block break effect",
      defValue = "true"
   )
   private final boolean doEffect;
   @MythicField(
      name = "useTool",
      aliases = {"tool", "t"},
      description = "Whether to use the tool in the player's hand",
      defValue = "true"
   )
   private final boolean useTool;
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The item(s) to drop after the target block is broken. Can accept droptables"
   )
   private final DropTable dropTable;
   @MythicField(
      name = "doFakeLooting",
      aliases = {"fakelooting", "fl"},
      description = "Plays the pickup animation from the origin",
      defValue = "false"
   )
   private final boolean doFakeLooting;

   public BreakBlockAndGiveItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doDrops = mlc.getBoolean(new String[]{"dodrops", "drops", "d"}, true);
      this.doEffect = mlc.getBoolean(new String[]{"doeffect", "effect", "e"}, true);
      this.useTool = mlc.getBoolean(new String[]{"usetool", "tool", "t"}, true);
      this.doFakeLooting = mlc.getBoolean(new String[]{"doFakeLooting", "fakelooting", "fl"}, false);
      String itemString = mlc.getString(new String[]{"items", "item", "i"});
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
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target.getLocation());
      AbstractEntity caster = data.getCaster().getEntity();
      LootBag loot = this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      if (caster.isPlayer()) {
         loot.give(caster.asPlayer(), false);
      }

      if (this.doFakeLooting) {
         if (!caster.isPlayer()) {
            return SkillResult.SUCCESS;
         }

         Player player = (Player)caster.getBukkitEntity();

         for (Drop type : loot.getLootTable()) {
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

            if (drop instanceof VanillaItemDrop) {
               ItemStack stack = ((BukkitItemStack)((VanillaItemDrop)drop).getDrop(new DropMetadataImpl(data.getCaster(), caster, data, null), amount))
                  .getItemStack();
               int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, stack, target.getLocation());
               Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
            }
         }
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, target);
      AbstractEntity caster = data.getCaster().getEntity();
      LootBag loot = this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      if (caster.isPlayer()) {
         loot.give(caster.asPlayer(), false);
      }

      if (this.doFakeLooting) {
         if (!caster.isPlayer()) {
            return SkillResult.SUCCESS;
         }

         Player player = (Player)caster.getBukkitEntity();

         for (Drop type : loot.getLootTable()) {
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

            if (drop instanceof VanillaItemDrop) {
               ItemStack stack = ((BukkitItemStack)((VanillaItemDrop)drop).getDrop(new DropMetadataImpl(data.getCaster(), caster, data, null), amount))
                  .getItemStack();
               int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, stack, target);
               Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
            }
         }
      }

      return SkillResult.SUCCESS;
   }

   public void playEffect(SkillMetadata data, AbstractLocation location) {
      Location l = BukkitAdapter.adapt(location);
      if (this.doDrops) {
         if (this.useTool) {
            ItemStack tool;
            if (data.getCaster().getEntity().isPlayer()) {
               tool = ((Player)data.getCaster().getEntity().getBukkitEntity()).getInventory().getItemInMainHand();
            } else {
               tool = new ItemStack(Material.AIR);
            }

            l.getBlock().breakNaturally(tool);
         } else {
            l.getBlock().breakNaturally();
         }
      } else {
         l.getBlock().setType(Material.AIR);
      }
   }
}
