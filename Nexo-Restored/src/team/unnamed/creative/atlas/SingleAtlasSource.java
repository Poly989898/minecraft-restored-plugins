package team.unnamed.creative.atlas;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface SingleAtlasSource extends AtlasSource {
   @NotNull Key resource();

   @Nullable Key sprite();
}
