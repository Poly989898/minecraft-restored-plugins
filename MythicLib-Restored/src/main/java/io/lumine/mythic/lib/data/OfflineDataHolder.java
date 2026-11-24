package io.lumine.mythic.lib.data;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

@Deprecated
public interface OfflineDataHolder {
   @NotNull
   UUID getUniqueId();
}
