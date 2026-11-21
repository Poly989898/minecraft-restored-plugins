package team.unnamed.creative.atlas;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface PalettedPermutationsAtlasSource extends AtlasSource {
   @NotNull @Unmodifiable List<Key> textures();

   @NotNull Key paletteKey();

   @NotNull @Unmodifiable Map<String, Key> permutations();
}
