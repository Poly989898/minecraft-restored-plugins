package team.unnamed.creative.base;

import java.util.Objects;
import java.util.regex.Pattern;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record KeyPatternImpl(@Nullable Pattern namespace, @Nullable Pattern value) implements KeyPattern {
   static final KeyPattern ANY = new KeyPatternImpl((Pattern)null, (Pattern)null);

   public boolean test(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (this.namespace == null || this.namespace.matcher(key.namespace()).matches()) && (this.value == null || this.value.matcher(key.value()).matches());
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{namespace=" + String.valueOf(this.namespace) + ", value=" + String.valueOf(this.value) + "}";
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         KeyPatternImpl that = (KeyPatternImpl)other;
         return patternEquals(this.namespace, that.namespace) && patternEquals(this.value, that.value);
      } else {
         return false;
      }
   }

   private static boolean patternEquals(@Nullable Pattern a, @Nullable Pattern b) {
      if (a == b) {
         return true;
      } else if (a == null) {
         return false;
      } else {
         return b == null ? false : a.pattern().equals(b.pattern());
      }
   }
}
