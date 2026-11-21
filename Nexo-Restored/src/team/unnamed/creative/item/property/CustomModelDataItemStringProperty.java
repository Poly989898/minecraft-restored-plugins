package team.unnamed.creative.item.property;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface CustomModelDataItemStringProperty extends ItemStringProperty {
   @Internal
   int DEFAULT_INDEX = 0;

   int index();
}
