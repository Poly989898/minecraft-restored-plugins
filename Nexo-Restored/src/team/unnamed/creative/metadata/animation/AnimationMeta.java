package team.unnamed.creative.metadata.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface AnimationMeta extends MetadataPart {
   int DEFAULT_FRAMETIME = 1;
   int DEFAULT_WIDTH = -1;
   int DEFAULT_HEIGHT = -1;
   boolean DEFAULT_INTERPOLATE = false;

   @Contract("_, _, _, _, _ -> new")
   static @NotNull AnimationMeta animation(boolean interpolate, int width, int height, int frameTime, @NotNull List<AnimationFrame> frames) {
      return new AnimationMetaImpl(interpolate, width, height, frameTime, frames);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_, _, _, _, _ -> new")
   static @NotNull AnimationMeta of(boolean interpolate, int width, int height, int frameTime, @NotNull List<AnimationFrame> frames) {
      return animation(interpolate, width, height, frameTime, frames);
   }

   @Contract("-> new")
   static @NotNull Builder animation() {
      return new AnimationMetaImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> new")
   static @NotNull Builder builder() {
      return animation();
   }

   boolean interpolate();

   int width();

   int height();

   int frameTime();

   @NotNull @Unmodifiable List<AnimationFrame> frames();

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder interpolate(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder width(int var1);

      @Contract("_ -> this")
      @NotNull Builder height(int var1);

      @Contract("_ -> this")
      @NotNull Builder frameTime(int var1);

      @Contract("_ -> this")
      @NotNull Builder frames(@NotNull List<AnimationFrame> var1);

      @Contract("_ -> this")
      default @NotNull Builder frames(@NotNull AnimationFrame... frames) {
         Objects.requireNonNull(frames, "frames");
         return this.frames(Arrays.asList(frames));
      }

      @Contract("_ -> this")
      default @NotNull Builder frames(@NotNull int... indexes) {
         Objects.requireNonNull(indexes, "indexes");
         List<AnimationFrame> frames = new ArrayList();

         for(int index : indexes) {
            frames.add(AnimationFrame.frame(index));
         }

         return this.frames(frames);
      }

      @Contract("_ -> this")
      @NotNull Builder addFrame(@NotNull AnimationFrame var1);

      @Contract("_ -> this")
      default @NotNull Builder addFrame(int index) {
         return this.addFrame(AnimationFrame.frame(index));
      }

      @Contract("_, _ -> this")
      default @NotNull Builder addFrame(int index, int frameTime) {
         return this.addFrame(AnimationFrame.frame(index, frameTime));
      }

      @Contract("-> new")
      @NotNull AnimationMeta build();
   }
}
