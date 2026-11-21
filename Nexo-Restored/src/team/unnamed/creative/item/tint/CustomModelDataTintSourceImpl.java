package team.unnamed.creative.item.tint;

import org.jetbrains.annotations.NotNull;

record CustomModelDataTintSourceImpl(int index, int defaultTint) implements CustomModelDataTintSource {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{index=" + this.index + ", defaultTint=" + this.defaultTint + "}";
   }
}
