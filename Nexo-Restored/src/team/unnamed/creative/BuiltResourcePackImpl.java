package team.unnamed.creative;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;

record BuiltResourcePackImpl(Writable data, String hash) implements BuiltResourcePack {
   BuiltResourcePackImpl(final @NotNull Writable data, final @NotNull String hash) {
      this.data = (Writable)Objects.requireNonNull(data, "data");
      this.hash = (String)Objects.requireNonNull(hash, "hash");
   }

   public @NotNull Writable data() {
      return this.data;
   }

   public @NotNull String hash() {
      return this.hash;
   }
}
