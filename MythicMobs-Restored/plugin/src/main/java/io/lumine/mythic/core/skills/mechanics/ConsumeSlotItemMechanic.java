package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Phil",
   name = "consumeslotitem",
   aliases = {"consumeslot"},
   description = "Removes an amount of the target's item in a specified slot"
)
public class ConsumeSlotItemMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The item slot to remove from. Accepts 0-35 or equipment slots. Defaults to MainHand if the slot specified is not allowed",
      defValue = "HAND"
   )
   private final String slot;
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of items to remove",
      defValue = "1"
   )
   private final PlaceholderInt amount;

   public ConsumeSlotItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.slot = mlc.getString(new String[]{"slot", "s"}, "HAND");
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Player player = BukkitAdapter.adapt(target.asPlayer());
         Runnable applyRunnable = () -> {
            AbstractItemStack item;
            try {
               int slot = Integer.parseInt(this.slot);
               item = slot < 36 ? BukkitAdapter.adapt(player.getInventory().getItem(slot)) : BukkitAdapter.adapt(player.getInventory().getItem(0));
            } catch (NumberFormatException e) {
               item = EquipSlot.of(this.slot.toUpperCase()).get(target).orElse(null);
            }

            if (item != null) {
               int newAmount = item.getAmount() - this.amount.get(data, target);
               if (newAmount <= 0) {
                  item.amount(0);
                  this.getPlugin().getVolatileCodeHandler().getItemHandler().destroyItem(BukkitAdapter.adapt(item));
                  player.updateInventory();
                  Events.call(new MythicModifiedInventoryEvent(data.getCaster(), player));
               } else {
                  item.amount(newAmount);
               }
            }
         };
         Schedulers.runOnRegion(target.getBukkitEntity(), applyRunnable);
         return SkillResult.SUCCESS;
      }
   }
}
