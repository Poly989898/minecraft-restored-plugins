package team.unnamed.creative.item.property;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface ComponentItemBooleanProperty extends ItemBooleanProperty {
   @NotNull String predicate();

   @NotNull JsonElement value();
}
