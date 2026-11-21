package team.unnamed.creative.item.tint;

import org.jetbrains.annotations.ApiStatus.Internal;

public interface CustomModelDataTintSource extends TintSource {
   @Internal
   int DEFAULT_INDEX = 0;

   int index();

   int defaultTint();
}
