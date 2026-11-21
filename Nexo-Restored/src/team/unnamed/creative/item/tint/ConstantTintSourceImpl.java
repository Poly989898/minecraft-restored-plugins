package team.unnamed.creative.item.tint;

import org.jetbrains.annotations.NotNull;

record ConstantTintSourceImpl(int tint) implements ConstantTintSource {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{tint=" + this.tint + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ConstantTintSourceImpl that = (ConstantTintSourceImpl)o;
         return this.tint == that.tint;
      } else {
         return false;
      }
   }
}
