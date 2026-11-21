package team.unnamed.creative.blockstate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record BlockStateImpl(Key key, Map<String, MultiVariant> variants, List<Selector> multipart) implements BlockState {
   BlockStateImpl(final @NotNull Key key, final @NotNull Map<String, MultiVariant> variants, final @NotNull List<Selector> multipart) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.variants = (Map)Objects.requireNonNull(variants, "variants");
      this.multipart = (List)Objects.requireNonNull(multipart, "multipart");
      this.validate();
   }

   private void validate() {
      if (this.variants.isEmpty() && this.multipart.isEmpty()) {
         throw new IllegalArgumentException("variants and multipart cannot be both empty!");
      }
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", variants=" + String.valueOf(this.variants) + ", multipart=" + String.valueOf(this.multipart) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockStateImpl that = (BlockStateImpl)o;
         return this.key.equals(that.key) && this.variants.equals(that.variants) && this.multipart.equals(that.multipart);
      } else {
         return false;
      }
   }
}
