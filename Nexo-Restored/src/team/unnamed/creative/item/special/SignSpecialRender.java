package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.WoodType;

/** @deprecated */
@Deprecated(
   since = "1.14.0"
)
public interface SignSpecialRender extends SpecialRender {
   boolean hanging();

   @NotNull WoodType woodType();

   @Nullable Key texture();
}
