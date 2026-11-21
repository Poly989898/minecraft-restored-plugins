package team.unnamed.creative.atlas;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record SingleAtlasSourceImpl(Key resource, Key sprite) implements SingleAtlasSource {
   SingleAtlasSourceImpl(final @NotNull Key resource, final @Nullable Key sprite) {
      this.resource = (Key)Objects.requireNonNull(resource, "resource");
      this.sprite = sprite;
   }

   public @NotNull Key resource() {
      return this.resource;
   }

   public @Nullable Key sprite() {
      return this.sprite;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{resource=" + String.valueOf(this.resource) + ", sprite=" + String.valueOf(this.sprite) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SingleAtlasSourceImpl that = (SingleAtlasSourceImpl)o;
         return !this.resource.equals(that.resource) ? false : Objects.equals(this.sprite, that.sprite);
      } else {
         return false;
      }
   }
}
