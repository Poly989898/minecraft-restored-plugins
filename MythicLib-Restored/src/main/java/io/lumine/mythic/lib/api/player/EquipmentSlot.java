package io.lumine.mythic.lib.api.player;

import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.PlayerModifier;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public enum EquipmentSlot {
   ARMOR,
   ACCESSORY,
   MAIN_HAND,
   OFF_HAND,
   OTHER;

   @NotNull
   public org.bukkit.inventory.EquipmentSlot toBukkit() {
      switch (this) {
         case MAIN_HAND:
            return org.bukkit.inventory.EquipmentSlot.HAND;
         case OFF_HAND:
            return org.bukkit.inventory.EquipmentSlot.OFF_HAND;
         default:
            throw new RuntimeException("Not a hand slot");
      }
   }

   private EquipmentSlot getOppositeHand() {
      Validate.isTrue(this == MAIN_HAND || this == OFF_HAND, "Not a hand equipment slot");
      return this == MAIN_HAND ? OFF_HAND : MAIN_HAND;
   }

   public boolean isCompatible(PlayerModifier modifier) {
      return this.isCompatible(modifier.getSource(), modifier.getSlot());
   }

   public boolean isCompatible(@NotNull ModifierSource modifierSource, @NotNull EquipmentSlot equipmentSlot) {
      Validate.isTrue(this.isHand(), "Instance called must be a hand equipment slot");
      if (equipmentSlot == OTHER) {
         return true;
      }

      switch (modifierSource) {
         case VOID:
            return false;
         case OTHER:
            return true;
         case RANGED_WEAPON:
         case MELEE_WEAPON:
            return equipmentSlot == this;
         case OFFHAND_ITEM:
            return equipmentSlot == OFF_HAND;
         case MAINHAND_ITEM:
            return equipmentSlot == MAIN_HAND;
         case HAND_ITEM:
            return equipmentSlot.isHand();
         case ARMOR:
            return equipmentSlot == ARMOR;
         case ACCESSORY:
            return equipmentSlot == ACCESSORY;
         default:
            throw new NotImplementedException();
      }
   }

   public boolean isHand() {
      return this == MAIN_HAND || this == OFF_HAND;
   }

   public static EquipmentSlot fromBukkit(org.bukkit.inventory.EquipmentSlot slot) {
      switch (slot) {
         case HAND:
            return MAIN_HAND;
         case OFF_HAND:
            return OFF_HAND;
         case FEET:
         case HEAD:
         case LEGS:
         case CHEST:
            return ARMOR;
         default:
            return OTHER;
      }
   }
}
