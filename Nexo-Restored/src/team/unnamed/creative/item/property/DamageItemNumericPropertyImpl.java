package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record DamageItemNumericPropertyImpl(boolean normalize) implements DamageItemNumericProperty {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{normalize=" + this.normalize + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         DamageItemNumericPropertyImpl that = (DamageItemNumericPropertyImpl)o;
         return this.normalize == that.normalize;
      } else {
         return false;
      }
   }
}
