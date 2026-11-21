package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface LocalTimeItemStringProperty extends ItemStringProperty {
   @Internal
   String DEFAULT_LOCALE = "";

   @NotNull String locale();

   @Nullable String timezone();

   @NotNull String pattern();
}
