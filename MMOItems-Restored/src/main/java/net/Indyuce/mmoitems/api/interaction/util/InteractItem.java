package net.Indyuce.mmoitems.api.interaction.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractItem {
   private final EquipmentSlot slot;
   private final ItemStack item;

   public InteractItem(Player player, Material material) {
      this.slot = this.hasItem(player.getInventory().getItemInMainHand(), material)
         ? EquipmentSlot.HAND
         : (this.hasItem(player.getInventory().getItemInOffHand(), material) ? EquipmentSlot.OFF_HAND : null);
      this.item = this.slot == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
   }

   @Deprecated
   public InteractItem(Player player, String text) {
      this.slot = this.hasItem(player.getInventory().getItemInMainHand(), text)
         ? EquipmentSlot.HAND
         : (this.hasItem(player.getInventory().getItemInOffHand(), text) ? EquipmentSlot.OFF_HAND : null);
      this.item = this.slot == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
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

   private boolean hasItem(ItemStack item, Material material) {
      return item != null && item.getType() == material;
   }

   private boolean hasItem(ItemStack item, String text) {
      return item != null && item.getType().name().endsWith(text);
   }
}
