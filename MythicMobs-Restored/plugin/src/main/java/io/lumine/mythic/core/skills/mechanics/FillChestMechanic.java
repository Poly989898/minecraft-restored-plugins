package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IItemDrop;
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
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "fillChest",
   aliases = {"populateChest", "fillContainer", "populateContainer"},
   description = "Fills a container with loot"
)
public class FillChestMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The item(s) or a droptable to use to fill the chest"
   )
   private DropTable dropTable;
   private String itemString;
   @MythicField(
      name = "shouldEmptyChest",
      aliases = {"empty"},
      description = "Clears the container before filling it with new items",
      defValue = "false"
   )
   private final boolean shouldEmptyChest;
   @MythicField(
      name = "shouldStack",
      aliases = {"stack"},
      description = "Whether the item(s) should stack to a similar item",
      defValue = "false"
   )
   private final boolean shouldStack;
   private final Random random = new Random();

   public FillChestMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.shouldStack = mlc.getBoolean(new String[]{"shouldstack", "stack"}, false);
      this.itemString = mlc.getString(new String[]{"items", "item", "i"});
      this.shouldEmptyChest = mlc.getBoolean(new String[]{"shouldempty", "empty"}, false);
      this.getPlugin().getDropManager().queueSecondPass(() -> {
         Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(this.itemString);
         if (maybeTable.isPresent()) {
            this.dropTable = maybeTable.get();
         } else {
            if (this.itemString.startsWith("\"")) {
               try {
                  this.itemString = this.itemString.substring(1, this.itemString.length() - 1);
               } catch (Exception var4x) {
                  MythicLogger.errorMechanicConfig(this, mlc, "The 'items' attribute is required.");
               }

               this.itemString = SkillString.parseMessageSpecialChars(this.itemString);
            }

            List<String> items = new ArrayList<>(Arrays.asList(this.itemString.split(",")));
            this.dropTable = new DropTable("FillChestMechanic", "FillChestMechanic", items);
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      LootBag loot = this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      Location location = BukkitAdapter.adapt(target);
      if (!(location.getBlock().getState() instanceof Container container)) {
         MythicLogger.error("Location " + location + " was not a container");
         return SkillResult.INVALID_TARGET;
      } else {
         ArrayList<ItemStack> newArrayList = Lists.newArrayList();

         for (Drop type : loot.getLootTable()) {
            double amount = type.getAmount();
            IDrop drop;
            if (type instanceof CustomDrop customDrop) {
               if (customDrop.getDrop().isEmpty()) {
                  continue;
               }

               drop = customDrop.getDrop().get();
            } else {
               drop = type;
            }

            if (drop instanceof IItemDrop itemDrop) {
               ItemStack stack = ((BukkitItemStack)itemDrop.getDrop(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null), amount))
                  .getItemStack();
               newArrayList.add(stack);
            }
         }

         if (this.shouldEmptyChest) {
            container.getInventory().clear();
         }

         for (ItemStack itemStack : newArrayList) {
            int slot = this.getRandomSlot(container.getInventory());
            if (slot == -1 && this.shouldStack) {
               container.getInventory().addItem(new ItemStack[]{itemStack});
            } else {
               Schedulers.sync().run(() -> {
                  if (this.shouldStack) {
                     container.getInventory().addItem(new ItemStack[]{itemStack});
                  } else {
                     container.getInventory().setItem(slot, itemStack);
                  }
               });
            }
         }

         return SkillResult.SUCCESS;
      }
   }

   private int getRandomSlot(Inventory inventory) {
      int slot = inventory.firstEmpty();
      if (slot == -1) {
         return slot;
      } else {
         ItemStack[] items = inventory.getContents();

         while (items[slot = this.random.nextInt(items.length)] != null && items[slot].getType() != Material.AIR) {
         }

         return slot;
      }
   }
}
