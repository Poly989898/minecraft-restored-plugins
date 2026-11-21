package team.unnamed.creative.atlas;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.KeyPattern;
import team.unnamed.creative.base.Vector2Float;

@NonExtendable
public interface AtlasSource {
   static @NotNull SingleAtlasSource single(@NotNull Key resource, @Nullable Key sprite) {
      return new SingleAtlasSourceImpl(resource, sprite);
   }

   static @NotNull SingleAtlasSource single(@NotNull Key resource) {
      return single(resource, (Key)null);
   }

   static @NotNull DirectoryAtlasSource directory(@NotNull String source, @NotNull String prefix) {
      return new DirectoryAtlasSourceImpl(source, prefix);
   }

   static @NotNull FilterAtlasSource filter(@NotNull KeyPattern pattern) {
      return new FilterAtlasSourceImpl(pattern);
   }

   static @NotNull UnstitchAtlasSource unstitch(@NotNull Key resource, @NotNull List<UnstitchAtlasSource.Region> regions, @NotNull Vector2Float divisor) {
      return new UnstitchAtlasSourceImpl(resource, regions, divisor);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull UnstitchAtlasSource unstitch(@NotNull Key resource, @NotNull List<UnstitchAtlasSource.Region> regions, double xDivisor, double yDivisor) {
      return unstitch(resource, regions, new Vector2Float((float)xDivisor, (float)yDivisor));
   }

   static @NotNull PalettedPermutationsAtlasSource palettedPermutations(@NotNull List<Key> textures, @NotNull Key paletteKey, @NotNull Map<String, Key> permutations) {
      return new PalettedPermutationsAtlasSourceImpl(textures, paletteKey, permutations);
   }
}
