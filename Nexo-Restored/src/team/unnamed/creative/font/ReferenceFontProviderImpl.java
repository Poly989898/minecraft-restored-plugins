package team.unnamed.creative.font;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record ReferenceFontProviderImpl(Key id) implements ReferenceFontProvider {
   ReferenceFontProviderImpl(final @NotNull Key id) {
      this.id = (Key)Objects.requireNonNull(id, "id");
   }

   public @NotNull Key id() {
      return this.id;
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ReferenceFontProviderImpl that = (ReferenceFontProviderImpl)o;
         return this.id.equals(that.id);
      } else {
         return false;
      }
   }
}
