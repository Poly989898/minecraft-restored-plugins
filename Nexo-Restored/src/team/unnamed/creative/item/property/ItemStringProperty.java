package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStringProperty extends ItemProperty {
   default boolean isString() {
      return true;
   }

   static @NotNull BlockStateItemStringProperty blockState(@NotNull String property) {
      return new BlockStateItemStringPropertyImpl(property);
   }

   static @NotNull NoFieldItemStringProperty chargeType() {
      return NoFieldItemStringPropertyImpl.CHARGE_TYPE;
   }

   static @NotNull NoFieldItemStringProperty contextDimension() {
      return NoFieldItemStringPropertyImpl.CONTEXT_DIMENSION;
   }

   static @NotNull NoFieldItemStringProperty contextEntityType() {
      return NoFieldItemStringPropertyImpl.CONTEXT_ENTITY_TYPE;
   }

   static @NotNull CustomModelDataItemStringProperty customModelData(int index) {
      return new CustomModelDataItemStringPropertyImpl(index);
   }

   static @NotNull CustomModelDataItemStringProperty customModelData() {
      return customModelData(0);
   }

   static @NotNull NoFieldItemStringProperty displayContext() {
      return NoFieldItemStringPropertyImpl.DISPLAY_CONTEXT;
   }

   static @NotNull LocalTimeItemStringProperty localTime(@NotNull String locale, @Nullable String timezone, @NotNull String format) {
      return new LocalTimeItemStringPropertyImpl(locale, timezone, format);
   }

   static @NotNull LocalTimeItemStringProperty localTime(@NotNull String format) {
      return localTime("", (String)null, format);
   }

   static @NotNull LocalTimeItemStringProperty localTime(@NotNull String locale, @NotNull String format) {
      return localTime(locale, (String)null, format);
   }

   static @NotNull NoFieldItemStringProperty mainHand() {
      return NoFieldItemStringPropertyImpl.MAIN_HAND;
   }

   static @NotNull NoFieldItemStringProperty trimMaterial() {
      return NoFieldItemStringPropertyImpl.TRIM_MATERIAL;
   }

   static @NotNull ComponentItemStringProperty component(@NotNull String component) {
      return new ComponentItemStringPropertyImpl(component);
   }
}
