package team.unnamed.creative.item.property;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface UseDurationItemNumericProperty extends ItemNumericProperty {
   @Internal
   boolean DEFAULT_REMAINING = false;

   boolean remaining();
}
