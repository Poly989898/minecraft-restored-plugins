package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.base.CubeFace;

public interface ShulkerBoxSpecialRender extends SpecialRender {
   @Internal
   float DEFAULT_OPENNESS = 0.0F;
   @Internal
   CubeFace DEFAULT_ORIENTATION = CubeFace.UP;

   @NotNull Key texture();

   float openness();

   @NotNull CubeFace orientation();
}
