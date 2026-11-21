package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "removeHeldItem",
   aliases = {"consumeHeldItem", "takeHeldItem"},
   description = "Removes an amount of the target's held item"
)
public class ConsumeHeldItemMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount to remove",
      defValue = "1"
   )
   private final PlaceholderInt amount;

   public ConsumeHeldItemMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!data.getCaster().getEntity().isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else if (data.getVariables().has("equip-item")) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "ConsumeHeldItemMechanic A");
         Player player = (Player)BukkitAdapter.adapt(data.getCaster().getEntity());
         ItemStack item = (ItemStack)data.getVariables().get("equip-item").get();
         if (item != null) {
            int newAmount = item.getAmount() - this.amount.get(data);
            if (newAmount <= 0) {
               this.getPlugin().getVolatileCodeHandler().getItemHandler().destroyItem(item);
               Events.call(new MythicModifiedInventoryEvent(data.getCaster(), player));
            } else {
               item.setAmount(newAmount);
            }

            player.updateInventory();
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Item not found");
         }

         return SkillResult.SUCCESS;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "ConsumeHeldItemMechanic B");
         Player player = (Player)data.getCaster().getEntity().getBukkitEntity();
         ItemStack item = player.getInventory().getItemInMainHand();
         if (item != null) {
            int newAmount = item.getAmount() - this.amount.get(data);
            if (newAmount <= 0) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Destroying item");
               this.getPlugin().getVolatileCodeHandler().getItemHandler().destroyItem(item);
               player.updateInventory();
               Events.call(new MythicModifiedInventoryEvent(data.getCaster(), player));
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Reducing amount to {0}", newAmount);
               item.setAmount(newAmount);
            }
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Item not found");
         }

         return SkillResult.SUCCESS;
      }
   }
}
