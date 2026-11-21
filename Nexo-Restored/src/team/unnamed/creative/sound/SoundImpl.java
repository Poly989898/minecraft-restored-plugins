package team.unnamed.creative.sound;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;

record SoundImpl(Key key, Writable data) implements Sound {
   SoundImpl(final @NotNull Key key, final @NotNull Writable data) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.data = (Writable)Objects.requireNonNull(data, "data");
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull Writable data() {
      return this.data;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SoundImpl sound = (SoundImpl)o;
         return this.key.equals(sound.key) && this.data.equals(sound.data);
      } else {
         return false;
      }
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", data=" + String.valueOf(this.data) + "}";
   }
}
