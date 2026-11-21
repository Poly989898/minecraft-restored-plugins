package team.unnamed.creative.item.tint;

import org.jetbrains.annotations.NotNull;

record GrassTintSourceImpl(float temperature, float downfall) implements GrassTintSource {
   GrassTintSourceImpl(final float temperature, final float downfall) {
      this.temperature = temperature;
      this.downfall = downfall;
      if (!(temperature > 1.0F) && !(temperature < 0.0F)) {
         if (downfall > 1.0F || downfall < 0.0F) {
            throw new IllegalArgumentException("Downfall must be between 0 and 1");
         }
      } else {
         throw new IllegalArgumentException("Temperature must be between 0 and 1");
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{temperature=" + this.temperature + ", downfall=" + this.downfall + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         GrassTintSourceImpl that = (GrassTintSourceImpl)o;
         return Float.compare(this.temperature, that.temperature) == 0 && Float.compare(this.downfall, that.downfall) == 0;
      } else {
         return false;
      }
   }
}
