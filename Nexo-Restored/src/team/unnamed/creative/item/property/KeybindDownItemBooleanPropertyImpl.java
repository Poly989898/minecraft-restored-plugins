package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record KeybindDownItemBooleanPropertyImpl(String key) implements KeybindDownItemBooleanProperty {
   KeybindDownItemBooleanPropertyImpl(final @NotNull String key) {
      this.key = (String)Objects.requireNonNull(key, "key");
   }

   public @NotNull String key() {
      return this.key;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + this.key + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         KeybindDownItemBooleanPropertyImpl that = (KeybindDownItemBooleanPropertyImpl)o;
         return this.key.equals(that.key);
      } else {
         return false;
      }
   }
}
