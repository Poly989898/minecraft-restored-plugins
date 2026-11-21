package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface NoFieldSpecialRender extends SpecialRender, Keyed {
   @NotNull Key key();
}
