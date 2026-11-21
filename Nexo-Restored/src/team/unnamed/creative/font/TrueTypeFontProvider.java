package team.unnamed.creative.font;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.util.MoreCollections;

public class TrueTypeFontProvider implements FontProvider {
   public static final float DEFAULT_SIZE = 11.0F;
   public static final float DEFAULT_OVERSAMPLE = 1.0F;
   private final Key file;
   private final Vector2Float shift;
   private final float size;
   private final float oversample;
   private final @Unmodifiable List<String> skip;

   protected TrueTypeFontProvider(Key file, Vector2Float shift, float size, float oversample, List<String> skip) {
      Objects.requireNonNull(file, "file");
      Objects.requireNonNull(shift, "shift");
      Objects.requireNonNull(skip, "skip");
      this.file = file;
      this.shift = shift;
      this.size = size;
      this.oversample = oversample;
      this.skip = MoreCollections.<String>immutableListOf(skip);
   }

   public Key file() {
      return this.file;
   }

   public Vector2Float shift() {
      return this.shift;
   }

   public float size() {
      return this.size;
   }

   public float oversample() {
      return this.oversample;
   }

   public @Unmodifiable List<String> skip() {
      return this.skip;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{file=" + String.valueOf(this.file) + ", shift=" + String.valueOf(this.shift) + ", size=" + this.size + ", oversample=" + this.oversample + ", skip=" + String.valueOf(this.skip) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TrueTypeFontProvider that = (TrueTypeFontProvider)o;
         return Float.compare(that.size, this.size) == 0 && Float.compare(that.oversample, this.oversample) == 0 && this.file.equals(that.file) && this.shift.equals(that.shift) && this.skip.equals(that.skip);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.file, this.shift, this.size, this.oversample, this.skip});
   }

   public static class Builder {
      private Key file;
      private Vector2Float shift;
      private float size;
      private float oversample;
      private List<String> skip;

      protected Builder() {
         this.shift = Vector2Float.ZERO;
         this.size = 11.0F;
         this.oversample = 1.0F;
         this.skip = Collections.emptyList();
      }

      public Builder file(Key file) {
         this.file = (Key)Objects.requireNonNull(file, "file");
         return this;
      }

      public Builder shift(Vector2Float shift) {
         this.shift = (Vector2Float)Objects.requireNonNull(shift, "shift");
         return this;
      }

      public Builder size(float size) {
         this.size = size;
         return this;
      }

      public Builder oversample(float oversample) {
         this.oversample = oversample;
         return this;
      }

      public Builder skip(List<String> skip) {
         this.skip = (List)Objects.requireNonNull(skip, "skip");
         return this;
      }

      public Builder skip(String... skip) {
         Objects.requireNonNull(skip, "skip");
         this.skip = Arrays.asList(skip);
         return this;
      }

      public TrueTypeFontProvider build() {
         return new TrueTypeFontProvider(this.file, this.shift, this.size, this.oversample, this.skip);
      }
   }
}
