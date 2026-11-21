package team.unnamed.creative.item.property;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface CustomModelDataItemBooleanProperty extends ItemBooleanProperty {
   @Internal
   int DEFAULT_INDEX = 0;

   int index();
}
