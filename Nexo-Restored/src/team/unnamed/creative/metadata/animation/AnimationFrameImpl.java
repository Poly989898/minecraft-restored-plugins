package team.unnamed.creative.metadata.animation;

import org.jetbrains.annotations.NotNull;

record AnimationFrameImpl(int index, int frameTime) implements AnimationFrame {
   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{index=" + this.index + ", frameTime=" + this.frameTime + "}";
   }
}
