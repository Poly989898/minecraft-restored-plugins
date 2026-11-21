package team.unnamed.creative.equipment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EquipmentLayerDye {
   @Nullable Integer colorWhenUndyed();

   static @NotNull EquipmentLayerDye hiddenIfNotDyed() {
      return EquipmentLayerDyeImpl.EMPTY;
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   static @NotNull EquipmentLayerDye colorWhenUndyed(int colorWhenUndyed) {
      return new EquipmentLayerDyeImpl(colorWhenUndyed);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   static @NotNull EquipmentLayerDye equipmentLayerDye(@Nullable Integer colorWhenUndyed) {
      return new EquipmentLayerDyeImpl(colorWhenUndyed);
   }
}
