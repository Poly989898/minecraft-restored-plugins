package team.unnamed.creative;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Writable;

@NonExtendable
public interface BuiltResourcePack {
   static @NotNull BuiltResourcePack of(@NotNull Writable data, @NotNull String hash) {
      return new BuiltResourcePackImpl(data, hash);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull BuiltResourcePack of(byte @NotNull [] bytes, @NotNull String hash) {
      Objects.requireNonNull(bytes, "bytes");
      return new BuiltResourcePackImpl(Writable.bytes(bytes), hash);
   }

   @NotNull Writable data();

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default byte @NotNull [] bytes() {
      try {
         return this.data().toByteArray();
      } catch (IOException e) {
         throw new UncheckedIOException("Failed to convert data to byte array", e);
      }
   }

   @NotNull String hash();
}
