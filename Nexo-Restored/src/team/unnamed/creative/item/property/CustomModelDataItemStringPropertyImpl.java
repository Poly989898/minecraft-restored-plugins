package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record CustomModelDataItemStringPropertyImpl(int index) implements CustomModelDataItemStringProperty {
   CustomModelDataItemStringPropertyImpl(final int index) {
      this.index = index;
      if (index < 0) {
         throw new IllegalArgumentException("Index cannot be negative, got: " + index);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{index=" + this.index + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CustomModelDataItemStringPropertyImpl that = (CustomModelDataItemStringPropertyImpl)o;
         return this.index == that.index;
      } else {
         return false;
      }
   }
}
