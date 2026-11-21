package team.unnamed.creative.font;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

record BitMapFontProviderImpl(Key file, int height, int ascent, @Unmodifiable List<String> characters) implements BitMapFontProvider {
   BitMapFontProviderImpl(final @NotNull Key file, final int height, final int ascent, final @NotNull List<String> characters) {
      this.file = (Key)Objects.requireNonNull(file, "file");
      this.height = height;
      this.ascent = ascent;
      this.characters = MoreCollections.<String>immutableListOf((List)Objects.requireNonNull(characters, "characters"));
      this.validate();
   }

   private void validate() {
      if (this.ascent > this.height) {
         throw new IllegalArgumentException("Ascent (" + this.ascent + ") is higher than height (" + this.height + ")");
      } else if (this.characters.isEmpty()) {
         throw new IllegalArgumentException("Character list is empty");
      } else {
         String sample = (String)this.characters.getFirst();
         int codePointCount = sample.codePointCount(0, sample.length());

         for(String character : this.characters) {
            Objects.requireNonNull(character, "An element from the character list is null");
            if (character.codePointCount(0, character.length()) != codePointCount) {
               throw new IllegalArgumentException("Elements of character list must have the same codepoint count");
            }
         }

      }
   }

   public @NotNull Key file() {
      return this.file;
   }

   public @NotNull BitMapFontProvider file(@NotNull Key file) {
      Objects.requireNonNull(file, "file");
      return new BitMapFontProviderImpl(file, this.height, this.ascent, this.characters);
   }

   public @NotNull BitMapFontProvider height(int height) {
      return new BitMapFontProviderImpl(this.file, height, this.ascent, this.characters);
   }

   public @NotNull BitMapFontProvider ascent(int ascent) {
      return new BitMapFontProviderImpl(this.file, this.height, ascent, this.characters);
   }

   public @Unmodifiable @NotNull List<String> characters() {
      return this.characters;
   }

   public @NotNull BitMapFontProvider characters(@NotNull List<String> characters) {
      Objects.requireNonNull(characters, "characters");
      return new BitMapFontProviderImpl(this.file, this.height, this.ascent, characters);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{height=" + this.height + ", ascent=" + this.ascent + ", file=" + String.valueOf(this.file) + ", chars=" + String.valueOf(this.characters) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BitMapFontProviderImpl that = (BitMapFontProviderImpl)o;
         return this.height == that.height && this.ascent == that.ascent && this.file.equals(that.file) && this.characters.equals(that.characters);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements BitMapFontProvider.Builder {
      private Key file;
      private int height = 8;
      private int ascent;
      private List<String> characters = Collections.emptyList();

      public BitMapFontProvider.@NotNull Builder file(@NotNull Key file) {
         this.file = (Key)Objects.requireNonNull(file, "file");
         return this;
      }

      public BitMapFontProvider.@NotNull Builder height(int height) {
         this.height = height;
         return this;
      }

      public BitMapFontProvider.@NotNull Builder ascent(int ascent) {
         this.ascent = ascent;
         return this;
      }

      public BitMapFontProvider.@NotNull Builder characters(@NotNull List<String> characters) {
         this.characters = (List)Objects.requireNonNull(characters, "characters");
         return this;
      }

      public @NotNull BitMapFontProvider build() {
         return new BitMapFontProviderImpl(this.file, this.height, this.ascent, this.characters);
      }
   }
}
