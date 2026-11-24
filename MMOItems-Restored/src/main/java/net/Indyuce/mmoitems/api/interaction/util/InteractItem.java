package net.Indyuce.mmoitems.api.interaction.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractItem {
   private final EquipmentSlot slot;
   private final ItemStack item;

   public InteractItem(Player var1, Material var2) {
      this.slot = this.hasItem(var1.getInventory().getItemInMainHand(), var2)
         ? EquipmentSlot.HAND
         : (this.hasItem(var1.getInventory().getItemInOffHand(), var2) ? EquipmentSlot.OFF_HAND : null);
      this.item = this.slot == EquipmentSlot.HAND ? var1.getInventory().getItemInMainHand() : var1.getInventory().getItemInOffHand();
   }

   @Deprecated
   public InteractItem(Player var1, String var2) {
      this.slot = this.hasItem(var1.getInventory().getItemInMainHand(), var2)
         ? EquipmentSlot.HAND
         : (this.hasItem(var1.getInventory().getItemInOffHand(), var2) ? EquipmentSlot.OFF_HAND : null);
      this.item = this.slot == EquipmentSlot.HAND ? var1.getInventory().getItemInMainHand() : var1.getInventory().getItemInOffHand();
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public boolean hasItem() {
      return this.slot != null;
   }

   public ItemStack getItem() {
      return this.item;
   }

   private boolean hasItem(ItemStack var1, Material var2) {
      return var1 != null && var1.getType() == var2;
   }

   private boolean hasItem(ItemStack var1, String var2) {
      return var1 != null && var1.getType().name().endsWith(var2);
   }
}
