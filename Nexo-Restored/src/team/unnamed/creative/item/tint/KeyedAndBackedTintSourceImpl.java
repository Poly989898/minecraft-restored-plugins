package team.unnamed.creative.item.tint;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record KeyedAndBackedTintSourceImpl(Key key, int defaultTint) implements KeyedAndBackedTintSource {
   static final Key DYE = Key.key("dye");
   static final Key FIREWORK = Key.key("firework");
   static final Key MAP_COLOR = Key.key("map_color");
   static final Key POTION = Key.key("potion");
   static final Key TEAM = Key.key("team");

   KeyedAndBackedTintSourceImpl(final @NotNull Key key, final int defaultTint) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.defaultTint = defaultTint;
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", defaultTint=" + this.defaultTint + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         KeyedAndBackedTintSourceImpl that = (KeyedAndBackedTintSourceImpl)o;
         return this.key.equals(that.key) && this.defaultTint == that.defaultTint;
      } else {
         return false;
      }
   }
}
