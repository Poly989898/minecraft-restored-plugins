package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

public interface ItemNumericProperty extends ItemProperty {
   default boolean isNumeric() {
      return true;
   }

   static @NotNull NoFieldItemNumericProperty bundleFullness() {
      return NoFieldItemNumericPropertyImpl.BUNDLE_FULLNESS;
   }

   static @NotNull CompassItemNumericProperty compass(CompassItemNumericProperty.@NotNull Target target, boolean wobble) {
      return new CompassItemNumericPropertyImpl(target, wobble);
   }

   static @NotNull CompassItemNumericProperty compass(CompassItemNumericProperty.@NotNull Target target) {
      return compass(target, true);
   }

   static @NotNull NoFieldItemNumericProperty cooldown() {
      return NoFieldItemNumericPropertyImpl.COOLDOWN;
   }

   static @NotNull NoFieldItemNumericProperty crossbowPull() {
      return NoFieldItemNumericPropertyImpl.CROSSBOW_PULL;
   }

   static @NotNull CountItemNumericProperty count(boolean normalize) {
      return new CountItemNumericPropertyImpl(normalize);
   }

   static @NotNull CountItemNumericProperty count() {
      return count(true);
   }

   static @NotNull CustomModelDataItemNumericProperty customModelData(int index) {
      return new CustomModelDataItemNumericPropertyImpl(index);
   }

   static @NotNull CustomModelDataItemNumericProperty customModelData() {
      return customModelData(0);
   }

   static @NotNull DamageItemNumericProperty damage(boolean normalize) {
      return new DamageItemNumericPropertyImpl(normalize);
   }

   static @NotNull DamageItemNumericProperty damage() {
      return damage(true);
   }

   static @NotNull TimeItemNumericProperty time(boolean wobble, TimeItemNumericProperty.@NotNull Source source) {
      return new TimeItemNumericPropertyImpl(wobble, source);
   }

   static @NotNull TimeItemNumericProperty time(TimeItemNumericProperty.@NotNull Source source) {
      return time(true, source);
   }

   static @NotNull UseCycleItemNumericProperty useCycle(float period) {
      return new UseCycleItemNumericPropertyImpl(period);
   }

   static @NotNull UseCycleItemNumericProperty useCycle() {
      return useCycle(1.0F);
   }

   static @NotNull UseDurationItemNumericProperty useDuration(boolean remaining) {
      return new UseDurationItemNumericPropertyImpl(remaining);
   }

   static @NotNull UseDurationItemNumericProperty useDuration() {
      return useDuration(false);
   }
}
