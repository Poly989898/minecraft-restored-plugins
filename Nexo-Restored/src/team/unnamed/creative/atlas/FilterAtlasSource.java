package team.unnamed.creative.atlas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.base.KeyPattern;

@NonExtendable
public interface FilterAtlasSource extends AtlasSource {
   @NotNull KeyPattern pattern();
}
