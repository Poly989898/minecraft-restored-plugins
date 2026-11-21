package team.unnamed.creative.item.property;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

record NoFieldItemNumericPropertyImpl(Key key) implements NoFieldItemNumericProperty {
   static final NoFieldItemNumericProperty BUNDLE_FULLNESS = new NoFieldItemNumericPropertyImpl("bundle/fullness");
   static final NoFieldItemNumericProperty COOLDOWN = new NoFieldItemNumericPropertyImpl("cooldown");
   static final NoFieldItemNumericProperty CROSSBOW_PULL = new NoFieldItemNumericPropertyImpl("crossbow/pull");

   NoFieldItemNumericPropertyImpl(final @NotNull Key key) {
      this.key = (Key)Objects.requireNonNull(key, "key");
   }

   private NoFieldItemNumericPropertyImpl(@Subst("bundle/fullness") @NotNull String keyValue) {
      this(Key.key("minecraft", keyValue));
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         NoFieldItemNumericPropertyImpl that = (NoFieldItemNumericPropertyImpl)o;
         return this.key.equals(that.key);
      } else {
         return false;
      }
   }
}
