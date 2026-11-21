package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record UseCycleItemNumericPropertyImpl(float period) implements UseCycleItemNumericProperty {
   UseCycleItemNumericPropertyImpl(final float period) {
      this.period = period;
      if (period < 0.0F) {
         throw new IllegalArgumentException("Period must be greater than or equal to 0, got: " + period);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{period=" + this.period + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         UseCycleItemNumericPropertyImpl that = (UseCycleItemNumericPropertyImpl)o;
         return Float.compare(this.period, that.period) == 0;
      } else {
         return false;
      }
   }
}
