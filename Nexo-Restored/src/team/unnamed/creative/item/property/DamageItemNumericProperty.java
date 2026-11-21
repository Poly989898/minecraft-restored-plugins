package team.unnamed.creative.item.property;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface DamageItemNumericProperty extends ItemNumericProperty {
   @Internal
   boolean DEFAULT_NORMALIZE = true;

   boolean normalize();
}
