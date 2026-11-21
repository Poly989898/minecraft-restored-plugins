package team.unnamed.creative.metadata.animation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public interface AnimationFrame {
   int DELEGATE_FRAME_TIME = -1;

   @Contract("_, _ -> new")
   static @NotNull AnimationFrame frame(int index, int frameTime) {
      return new AnimationFrameImpl(index, frameTime);
   }

   /** @deprecated */
   @Deprecated
   @Contract("_, _ -> new")
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull AnimationFrame of(int index, int frameTime) {
      return frame(index, frameTime);
   }

   @Contract("_ -> new")
   static @NotNull AnimationFrame frame(int index) {
      return new AnimationFrameImpl(index, -1);
   }

   /** @deprecated */
   @Deprecated
   @Contract("_ -> new")
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull AnimationFrame of(int index) {
      return frame(index);
   }

   int index();

   int frameTime();
}
