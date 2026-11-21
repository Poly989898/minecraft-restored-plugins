package team.unnamed.creative.item.property;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface NoFieldItemNumericProperty extends ItemNumericProperty, Keyed {
   @NotNull Key key();
}
