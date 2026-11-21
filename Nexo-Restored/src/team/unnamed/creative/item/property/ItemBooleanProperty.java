package team.unnamed.creative.item.property;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface ItemBooleanProperty extends ItemProperty {
   default boolean isBoolean() {
      return true;
   }

   static @NotNull ItemBooleanProperty broken() {
      return NoFieldItemBooleanPropertyImpl.BROKEN;
   }

   static @NotNull ItemBooleanProperty bundleHasSelectedItem() {
      return NoFieldItemBooleanPropertyImpl.BUNDLE_HAS_SELECTED_ITEM;
   }

   static @NotNull ItemBooleanProperty carried() {
      return NoFieldItemBooleanPropertyImpl.CARRIED;
   }

   static @NotNull ItemBooleanProperty damaged() {
      return NoFieldItemBooleanPropertyImpl.DAMAGED;
   }

   static @NotNull ItemBooleanProperty extendedView() {
      return NoFieldItemBooleanPropertyImpl.EXTENDED_VIEW;
   }

   static @NotNull ItemBooleanProperty fishingRodCast() {
      return NoFieldItemBooleanPropertyImpl.FISHING_ROD_CAST;
   }

   static @NotNull ItemBooleanProperty selected() {
      return NoFieldItemBooleanPropertyImpl.SELECTED;
   }

   static @NotNull ItemBooleanProperty usingItem() {
      return NoFieldItemBooleanPropertyImpl.USING_ITEM;
   }

   static @NotNull ItemBooleanProperty viewEntity() {
      return NoFieldItemBooleanPropertyImpl.VIEW_ENTITY;
   }

   static @NotNull CustomModelDataItemBooleanProperty customModelData(int index) {
      return new CustomModelDataItemBooleanPropertyImpl(index);
   }

   static @NotNull HasComponentItemBooleanProperty hasComponent(@NotNull String component, boolean ignoreDefault) {
      return new HasComponentItemBooleanPropertyImpl(component, ignoreDefault);
   }

   static @NotNull HasComponentItemBooleanProperty hasComponent(@NotNull String component) {
      return hasComponent(component, false);
   }

   static @NotNull KeybindDownItemBooleanProperty keybindDown(@NotNull String keybind) {
      return new KeybindDownItemBooleanPropertyImpl(keybind);
   }

   static @NotNull ComponentItemBooleanProperty component(@NotNull String predicate, @NotNull JsonElement value) {
      return new ComponentItemBooleanPropertyImpl(predicate, value);
   }
}
