package team.unnamed.creative.atlas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface DirectoryAtlasSource extends AtlasSource {
   @NotNull String source();

   @NotNull String prefix();
}
