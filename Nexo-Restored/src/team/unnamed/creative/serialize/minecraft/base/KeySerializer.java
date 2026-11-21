package team.unnamed.creative.serialize.minecraft.base;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class KeySerializer {
   private KeySerializer() {
      throw new UnsupportedOperationException("Can't instantiate utility class");
   }

   public static @NotNull String toString(@NotNull Key key) {
      return key.namespace().equals("minecraft") ? key.value() : key.asString();
   }
}
