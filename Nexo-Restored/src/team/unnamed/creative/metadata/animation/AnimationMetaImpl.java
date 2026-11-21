package team.unnamed.creative.metadata.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.util.MoreCollections;

record AnimationMetaImpl(boolean interpolate, int width, int height, int frameTime, List<AnimationFrame> frames) implements AnimationMeta {
   AnimationMetaImpl(final boolean interpolate, final int width, final int height, final int frameTime, final @NotNull List<AnimationFrame> frames) {
      this.interpolate = interpolate;
      this.width = width;
      this.height = height;
      this.frameTime = frameTime;
      this.frames = MoreCollections.<AnimationFrame>immutableListOf((List)Objects.requireNonNull(frames, "frames"));
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return AnimationMeta.class;
   }

   public @NotNull @Unmodifiable List<AnimationFrame> frames() {
      return this.frames;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{interpolate=" + this.interpolate + ", width=" + this.width + ", height=" + this.height + ", frameTime=" + this.frameTime + ", frames=" + String.valueOf(this.frames) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AnimationMetaImpl that = (AnimationMetaImpl)o;
         return this.interpolate == that.interpolate && this.width == that.width && this.height == that.height && this.frameTime == that.frameTime && this.frames.equals(that.frames);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements AnimationMeta.Builder {
      private boolean interpolate = false;
      private int width = -1;
      private int height = -1;
      private int frameTime = 1;
      private List<AnimationFrame> frames = Collections.emptyList();

      public AnimationMeta.@NotNull Builder interpolate(boolean interpolate) {
         this.interpolate = interpolate;
         return this;
      }

      public AnimationMeta.@NotNull Builder width(int width) {
         this.width = width;
         return this;
      }

      public AnimationMeta.@NotNull Builder height(int height) {
         this.height = height;
         return this;
      }

      public AnimationMeta.@NotNull Builder frameTime(int frameTime) {
         this.frameTime = frameTime;
         return this;
      }

      public AnimationMeta.@NotNull Builder frames(@NotNull List<AnimationFrame> frames) {
         Objects.requireNonNull(frames, "frames");
         this.frames = new ArrayList(frames);
         return this;
      }

      public AnimationMeta.@NotNull Builder addFrame(@NotNull AnimationFrame frame) {
         Objects.requireNonNull(frame, "frame");
         this.frames.add(frame);
         return this;
      }

      public @NotNull AnimationMeta build() {
         return new AnimationMetaImpl(this.interpolate, this.width, this.height, this.frameTime, this.frames);
      }
   }
}
