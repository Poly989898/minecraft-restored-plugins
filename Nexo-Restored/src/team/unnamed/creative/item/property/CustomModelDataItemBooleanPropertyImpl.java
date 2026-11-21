package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record CustomModelDataItemBooleanPropertyImpl(int index) implements CustomModelDataItemBooleanProperty {
   CustomModelDataItemBooleanPropertyImpl(final int index) {
      this.index = index;
      if (index < 0) {
         throw new IllegalArgumentException("custom_model_data condition must have a non-negative index, got " + index);
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{index=" + this.index + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CustomModelDataItemBooleanPropertyImpl that = (CustomModelDataItemBooleanPropertyImpl)o;
         return this.index == that.index;
      } else {
         return false;
      }
   }
}
