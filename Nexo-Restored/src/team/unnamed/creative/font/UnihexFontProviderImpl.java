package team.unnamed.creative.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

record UnihexFontProviderImpl(Key file, List<UnihexFontProvider.SizeOverride> sizes) implements UnihexFontProvider {
   UnihexFontProviderImpl(final @NotNull Key file, final @NotNull List<UnihexFontProvider.SizeOverride> sizes) {
      this.file = (Key)Objects.requireNonNull(file, "file");
      this.sizes = MoreCollections.<UnihexFontProvider.SizeOverride>immutableListOf((List)Objects.requireNonNull(sizes, "sizes"));
   }

   public @NotNull Key file() {
      return this.file;
   }

   public @Unmodifiable @NotNull List<UnihexFontProvider.SizeOverride> sizes() {
      return this.sizes;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{file=" + String.valueOf(this.file) + ", sizes=" + String.valueOf(this.sizes) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UnihexFontProviderImpl that = (UnihexFontProviderImpl)o;
         return !this.file.equals(that.file) ? false : this.sizes.equals(that.sizes);
      } else {
         return false;
      }
   }

   static final class SizeOverrideImpl implements UnihexFontProvider.SizeOverride {
      private final int from;
      private final int to;
      private final int left;
      private final int right;

      SizeOverrideImpl(int from, int to, int left, int right) {
         this.from = from;
         this.to = to;
         this.left = left;
         this.right = right;
         this.validate();
      }

      SizeOverrideImpl(String from, String to, int left, int right) {
         this.left = left;
         this.right = right;
         int[] fromCodepoints = from.codePoints().toArray();
         if (fromCodepoints.length != 1) {
            throw new IllegalArgumentException("Only one codepoint is allowed for 'from' parameter, got " + fromCodepoints.length + " (" + from + ")");
         } else {
            this.from = fromCodepoints[0];
            int[] toCodepoints = to.codePoints().toArray();
            if (toCodepoints.length != 1) {
               throw new IllegalArgumentException("Only one codepoint is allowed for 'to' parameter, got " + toCodepoints.length + " (" + to + ")");
            } else {
               this.to = toCodepoints[0];
               this.validate();
            }
         }
      }

      private void validate() {
         if (this.from >= this.to) {
            throw new IllegalArgumentException("Invalid range: [" + this.from + ";" + this.to + "]");
         }
      }

      public int from() {
         return this.from;
      }

      public int to() {
         return this.to;
      }

      public int left() {
         return this.left;
      }

      public int right() {
         return this.right;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{from=" + this.from + ", to=" + this.to + ", left=" + this.left + ", right=" + this.right + "}";
      }

      public boolean equals(@Nullable Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            SizeOverrideImpl override = (SizeOverrideImpl)o;
            if (this.from != override.from) {
               return false;
            } else if (this.to != override.to) {
               return false;
            } else if (this.left != override.left) {
               return false;
            } else {
               return this.right == override.right;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.from;
         result = 31 * result + this.to;
         result = 31 * result + this.left;
         result = 31 * result + this.right;
         return result;
      }
   }

   static final class BuilderImpl implements UnihexFontProvider.Builder {
      private Key file;
      private List<UnihexFontProvider.SizeOverride> sizes = new ArrayList();

      public UnihexFontProvider.@NotNull Builder file(@NotNull Key file) {
         this.file = (Key)Objects.requireNonNull(file, "file");
         return this;
      }

      public UnihexFontProvider.@NotNull Builder sizes(@NotNull List<UnihexFontProvider.SizeOverride> sizes) {
         Objects.requireNonNull(sizes, "sizes");
         this.sizes = new ArrayList(sizes);
         return this;
      }

      public UnihexFontProvider.@NotNull Builder addSize(UnihexFontProvider.@NotNull SizeOverride size) {
         Objects.requireNonNull(size, "size");
         this.sizes.add(size);
         return this;
      }

      public @NotNull UnihexFontProvider build() {
         return new UnihexFontProviderImpl(this.file, this.sizes);
      }
   }
}
