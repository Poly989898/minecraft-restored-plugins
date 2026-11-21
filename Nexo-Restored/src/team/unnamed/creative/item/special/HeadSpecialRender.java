package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.base.HeadType;

public interface HeadSpecialRender extends SpecialRender {
   @Internal
   float DEFAULT_ANIMATION = 0.0F;

   @NotNull HeadType kind();

   @Nullable Key texture();

   float animation();
}
