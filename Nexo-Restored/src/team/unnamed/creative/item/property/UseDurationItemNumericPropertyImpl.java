package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

record UseDurationItemNumericPropertyImpl(boolean remaining) implements UseDurationItemNumericProperty {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{remaining=" + this.remaining + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         UseDurationItemNumericPropertyImpl that = (UseDurationItemNumericPropertyImpl)o;
         return this.remaining == that.remaining;
      } else {
         return false;
      }
   }
}
