package team.unnamed.creative.item.property;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface NoFieldItemBooleanProperty extends ItemBooleanProperty, Keyed {
   @NotNull Key key();
}
