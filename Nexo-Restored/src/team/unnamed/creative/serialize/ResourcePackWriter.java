package team.unnamed.creative.serialize;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;

public interface ResourcePackWriter<T> {
   void write(@NotNull T var1, @NotNull ResourcePack var2);
}
