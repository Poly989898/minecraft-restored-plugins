package team.unnamed.creative.item.property;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface UseCycleItemNumericProperty extends ItemNumericProperty {
   @Internal
   float DEFAULT_PERIOD = 1.0F;

   float period();
}
