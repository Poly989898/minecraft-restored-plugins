package team.unnamed.creative.atlas;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.KeyPattern;

record FilterAtlasSourceImpl(KeyPattern pattern) implements FilterAtlasSource {
   FilterAtlasSourceImpl(final @NotNull KeyPattern pattern) {
      this.pattern = (KeyPattern)Objects.requireNonNull(pattern, "pattern");
   }

   public @NotNull KeyPattern pattern() {
      return this.pattern;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{pattern=" + String.valueOf(this.pattern) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FilterAtlasSourceImpl that = (FilterAtlasSourceImpl)o;
         return this.pattern.equals(that.pattern);
      } else {
         return false;
      }
   }
}
