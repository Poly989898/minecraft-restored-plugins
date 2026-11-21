package team.unnamed.creative.item.tint;

import org.jetbrains.annotations.NotNull;

public interface TintSource {
   static @NotNull ConstantTintSource constant(int tint) {
      return new ConstantTintSourceImpl(tint);
   }

   static @NotNull CustomModelDataTintSource customModelData(int index, int defaultTint) {
      return new CustomModelDataTintSourceImpl(index, defaultTint);
   }

   static @NotNull KeyedAndBackedTintSource dye(int defaultTint) {
      return new KeyedAndBackedTintSourceImpl(KeyedAndBackedTintSourceImpl.DYE, defaultTint);
   }

   static @NotNull GrassTintSource grass(float temperature, float downfall) {
      return new GrassTintSourceImpl(temperature, downfall);
   }

   static @NotNull KeyedAndBackedTintSource firework(int defaultTint) {
      return new KeyedAndBackedTintSourceImpl(KeyedAndBackedTintSourceImpl.FIREWORK, defaultTint);
   }

   static @NotNull KeyedAndBackedTintSource mapColor(int defaultTint) {
      return new KeyedAndBackedTintSourceImpl(KeyedAndBackedTintSourceImpl.MAP_COLOR, defaultTint);
   }

   static @NotNull KeyedAndBackedTintSource potion(int defaultTint) {
      return new KeyedAndBackedTintSourceImpl(KeyedAndBackedTintSourceImpl.POTION, defaultTint);
   }

   static @NotNull KeyedAndBackedTintSource team(int defaultTint) {
      return new KeyedAndBackedTintSourceImpl(KeyedAndBackedTintSourceImpl.TEAM, defaultTint);
   }
}
