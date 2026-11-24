package net.Indyuce.mmoitems.api.interaction.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UntargetedDurabilityItem extends DurabilityItem {
   private final EquipmentSlot slot;

   public UntargetedDurabilityItem(Player player, NBTItem nbtItem, EquipmentSlot equipmentSlot) {
      super(player, nbtItem);
      this.slot = equipmentSlot;
   }

   public UntargetedDurabilityItem decreaseDurability(int count) {
      return (UntargetedDurabilityItem)super.decreaseDurability(count);
   }

   public void inventoryUpdate() {
      ItemStack item = this.toItem();
      if (item == null) {
         if (this.slot == EquipmentSlot.OFF_HAND) {
            this.getPlayer().getInventory().setItemInOffHand(null);
         } else {
            this.getPlayer().getInventory().setItemInMainHand(null);
         }
      } else {
         this.getNBTItem().getItem().setItemMeta(this.toItem().getItemMeta());
      }
   }
}
