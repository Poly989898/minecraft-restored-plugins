package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
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
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.MetaVariableMechanic;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "giveitem",
   aliases = {"give", "giveitems", "itemgive"},
   description = "Gives the target an item or droptable"
)
public class GiveItemMechanic extends MetaVariableMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The item(s) or droptable to give"
   )
   private DropTable dropTable;
   @MythicField(
      name = "doFakeLooting",
      aliases = {"fl", "fakelooting"},
      description = "Whether to play the pickup animation from the origin",
      defValue = "false"
   )
   private final boolean doFakeLooting;
   private PlaceholderString itemString;

   public GiveItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doFakeLooting = mlc.getBoolean(new String[]{"dofakelooting", "fakelooting", "fl"}, false);
      this.itemString = mlc.getPlaceholderString(new String[]{"items", "item", "i", "type", "t", "material", "mat", "m"}, null);
      if (this.itemString == null && this.variableName == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Either the 'items' attribute or the `variable` attribute is required.");
      }

      if (this.itemString != null && this.itemString.isStatic()) {
         this.getPlugin().getDropManager().queueSecondPass(() -> {
            String itemString = this.itemString.get();
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
               this.dropTable = new DropTable("GiveItemMechanic", "GiveItemMechanic", items);
            }
         });
      } else {
         this.dropTable = null;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.variableName != null && target.isPlayer()) {
         PlaceholderContext context = PlaceholderContext.of(data, target, null, null, null);
         if (this.getVariable(context) instanceof ItemVariable itemV) {
            ItemStack stack = itemV.get().getItemStack();
            Schedulers.runOnRegion(target.getBukkitEntity(), () -> BukkitAdapter.adapt(target.asPlayer()).getInventory().addItem(new ItemStack[]{stack}));
         }
      }

      if (this.itemString == null) {
         return SkillResult.SUCCESS;
      } else {
         LootBag loot = this.getLoot(PlaceholderContext.of(data, target, null, null, null));
         if (target.isPlayer()) {
            Schedulers.runOnRegion(target.getBukkitEntity(), () -> loot.give(target.asPlayer(), false));
         }

         if (this.doFakeLooting) {
            if (!target.isPlayer()) {
               return SkillResult.SUCCESS;
            }

            Player player = (Player)target.getBukkitEntity();
            Location location = BukkitAdapter.adapt(data.getOrigin());

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
                  ItemStack stack = ((BukkitItemStack)((VanillaItemDrop)drop).getDrop(new DropMetadataImpl(data.getCaster(), target, data, null), amount))
                     .getItemStack();
                  int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, stack, data.getOrigin());
                  Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
               }
            }
         }

         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location loc = BukkitAdapter.adapt(target);
      Block block = loc.getBlock();
      if (this.variableName != null) {
         PlaceholderContext context = PlaceholderContext.of(data, null, target, null, null);
         if (this.getVariable(context) instanceof ItemVariable itemV) {
            if (block.getState() instanceof InventoryHolder holder) {
               holder.getInventory().addItem(new ItemStack[]{itemV.get().getItemStack()});
            } else {
               loc.getWorld().dropItem(loc, itemV.get().getItemStack());
            }
         }
      }

      if (this.itemString == null) {
         return SkillResult.SUCCESS;
      } else {
         LootBag loot = this.getLoot(PlaceholderContext.of(data, null, target, null, null));
         if (block.getState() instanceof InventoryHolder) {
            loot.putInContainer(target);
            return SkillResult.SUCCESS;
         } else {
            loot.drop(target, false);
            return SkillResult.SUCCESS;
         }
      }
   }

   @Nullable
   private LootBag getLoot(PlaceholderContext context) {
      if (this.dropTable != null) {
         return this.dropTable.generate(new DropMetadataImpl(context.meta().getCaster(), context.meta().getCaster().getEntity(), context.meta(), null));
      } else {
         String in = this.itemString.get(context);
         Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(in);
         if (maybeTable.isPresent()) {
            return maybeTable.get().generate(new DropMetadataImpl(context.meta().getCaster(), context.meta().getCaster().getEntity(), context.meta(), null));
         } else {
            List<String> items = Lists.newArrayList(in.split(","));
            return new DropTable("GiveItemMechanic", "GiveItemMechanic", items)
               .generate(new DropMetadataImpl(context.meta().getCaster(), context.meta().getCaster().getEntity(), context.meta(), null));
         }
      }
   }
}
