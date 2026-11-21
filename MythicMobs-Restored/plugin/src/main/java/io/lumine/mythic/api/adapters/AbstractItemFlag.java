package io.lumine.mythic.api.adapters;

public enum AbstractItemFlag {
   HIDE_ENCHANTS,
   HIDE_ATTRIBUTES,
   HIDE_UNBREAKABLE,
   HIDE_DESTROYS,
   HIDE_PLACED_ON,
   HIDE_ADDITIONAL_TOOLTIP,
   HIDE_DYE,
   HIDE_ARMOR_TRIM,
   HIDE_STORED_ENCHANTS,
   HIDE_POTION_EFFECTS;

   public static AbstractItemFlag of(String name) {
      try {
         return valueOf(name);
      } catch (IllegalArgumentException e) {
         return null;
      }
   }
}
