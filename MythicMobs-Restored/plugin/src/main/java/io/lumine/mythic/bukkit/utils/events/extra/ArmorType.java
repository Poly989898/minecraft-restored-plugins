package io.lumine.mythic.bukkit.utils.events.extra;

import org.bukkit.inventory.ItemStack;

public enum ArmorType {
   HELMET(5),
   CHESTPLATE(6),
   LEGGINGS(7),
   BOOTS(8);

   private final int slot;

   private ArmorType(int slot) {
      this.slot = slot;
   }

   public static ArmorType matchType(ItemStack itemStack) {
      if (ArmorEquipEventListener.isAirOrNull(itemStack)) {
         return null;
      } else {
         String type = itemStack.getType().name();
         if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("PLAYER_HEAD")) {
            return HELMET;
         } else if (type.endsWith("_CHESTPLATE") || type.endsWith("ELYTRA")) {
            return CHESTPLATE;
         } else if (type.endsWith("_LEGGINGS")) {
            return LEGGINGS;
         } else {
            return type.endsWith("_BOOTS") ? BOOTS : null;
         }
      }
   }

   public int getSlot() {
      return this.slot;
   }
}
