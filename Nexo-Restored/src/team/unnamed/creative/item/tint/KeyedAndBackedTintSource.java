package team.unnamed.creative.item.tint;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface KeyedAndBackedTintSource extends TintSource, Keyed {
   @NotNull Key key();

   int defaultTint();
}
