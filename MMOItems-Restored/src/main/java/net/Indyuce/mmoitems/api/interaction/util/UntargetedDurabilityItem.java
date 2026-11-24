package net.Indyuce.mmoitems.api.interaction.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UntargetedDurabilityItem extends DurabilityItem {
   private final EquipmentSlot slot;

   public UntargetedDurabilityItem(Player var1, NBTItem var2, EquipmentSlot var3) {
      super(var1, var2);
      this.slot = var3;
   }

   public UntargetedDurabilityItem decreaseDurability(int var1) {
      return (UntargetedDurabilityItem)super.decreaseDurability(var1);
   }

   public void inventoryUpdate() {
      ItemStack var1 = this.toItem();
      if (var1 == null) {
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
