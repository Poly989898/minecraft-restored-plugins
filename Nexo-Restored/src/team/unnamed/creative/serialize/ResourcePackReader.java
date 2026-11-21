package team.unnamed.creative.serialize;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;

public interface ResourcePackReader<T> {
   @NotNull ResourcePack read(@NotNull T var1);
}
