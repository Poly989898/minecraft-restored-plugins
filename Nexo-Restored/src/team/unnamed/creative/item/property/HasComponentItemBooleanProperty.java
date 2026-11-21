package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface HasComponentItemBooleanProperty extends ItemBooleanProperty {
   @Internal
   boolean DEFAULT_IGNORE_DEFAULT = false;

   @NotNull String component();

   boolean ignoreDefault();
}
