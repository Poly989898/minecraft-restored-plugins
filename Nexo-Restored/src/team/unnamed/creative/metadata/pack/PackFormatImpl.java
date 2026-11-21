package team.unnamed.creative.metadata.pack;

import org.jetbrains.annotations.NotNull;

record PackFormatImpl(@NotNull FormatVersion min, @NotNull FormatVersion max) implements PackFormat {
   PackFormatImpl(final @NotNull FormatVersion min, final @NotNull FormatVersion max) {
      this.min = min;
      this.max = max;
      if (min.compareTo(max) > 0) {
         String var10002 = String.valueOf(min);
         throw new IllegalArgumentException("Minimum " + var10002 + " is greater than maximum " + String.valueOf(max));
      }
   }

   public boolean isSingle() {
      return this.min.equals(this.max);
   }

   public boolean isInRange(@NotNull FormatVersion format) {
      return format.compareTo(this.min) >= 0 && format.compareTo(this.max) <= 0;
   }

   public boolean isInRange(@NotNull PackFormat format) {
      return this.isInRange(format.min()) || this.isInRange(format.max());
   }

   /** @deprecated */
   @Deprecated
   public boolean isInRange(int format) {
      return this.isInRange(FormatVersion.of(format));
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{min=" + String.valueOf(this.min) + ", max=" + String.valueOf(this.max) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PackFormatImpl that = (PackFormatImpl)o;
         return !this.min.equals(that.min) ? false : this.max.equals(that.max);
      } else {
         return false;
      }
   }
}
