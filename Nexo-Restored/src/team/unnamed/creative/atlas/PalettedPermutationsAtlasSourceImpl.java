package team.unnamed.creative.atlas;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

record PalettedPermutationsAtlasSourceImpl(List<Key> textures, Key paletteKey, Map<String, Key> permutations) implements PalettedPermutationsAtlasSource {
   PalettedPermutationsAtlasSourceImpl(final @NotNull List<Key> textures, final @NotNull Key paletteKey, final @NotNull Map<String, Key> permutations) {
      Objects.requireNonNull(textures, "textures");
      Objects.requireNonNull(paletteKey, "paletteKey");
      Objects.requireNonNull(permutations, "permutations");
      this.textures = MoreCollections.<Key>immutableListOf(textures);
      this.paletteKey = paletteKey;
      this.permutations = MoreCollections.<String, Key>immutableMapOf(permutations);
   }

   public @NotNull @Unmodifiable List<Key> textures() {
      return this.textures;
   }

   public @NotNull @Unmodifiable Key paletteKey() {
      return this.paletteKey;
   }

   public @NotNull @Unmodifiable Map<String, Key> permutations() {
      return this.permutations;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{textures=" + String.valueOf(this.textures) + ", paletteKey=" + String.valueOf(this.paletteKey) + ", permutations=" + String.valueOf(this.permutations) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PalettedPermutationsAtlasSourceImpl that = (PalettedPermutationsAtlasSourceImpl)o;
         if (!this.textures.equals(that.textures)) {
            return false;
         } else {
            return !this.paletteKey.equals(that.paletteKey) ? false : this.permutations.equals(that.permutations);
         }
      } else {
         return false;
      }
   }
}
