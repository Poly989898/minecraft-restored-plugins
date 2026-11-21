package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record CountItemNumericPropertyImpl(boolean normalize) implements CountItemNumericProperty {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{normalize=" + this.normalize + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CountItemNumericPropertyImpl that = (CountItemNumericPropertyImpl)o;
         return this.normalize == that.normalize;
      } else {
         return false;
      }
   }
}
