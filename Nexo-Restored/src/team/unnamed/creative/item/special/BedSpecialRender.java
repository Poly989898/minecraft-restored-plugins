package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   since = "1.14.0"
)
public interface BedSpecialRender extends SpecialRender {
   @NotNull Key texture();
}
