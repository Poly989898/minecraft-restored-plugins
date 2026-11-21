package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record CustomModelDataItemNumericPropertyImpl(int index) implements CustomModelDataItemNumericProperty {
   CustomModelDataItemNumericPropertyImpl(final int index) {
      this.index = index;
      if (index < 0) {
         throw new IllegalArgumentException("Index must be greater than or equal to 0, got: " + index);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{index=" + this.index + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CustomModelDataItemNumericPropertyImpl that = (CustomModelDataItemNumericPropertyImpl)o;
         return this.index == that.index;
      } else {
         return false;
      }
   }
}
