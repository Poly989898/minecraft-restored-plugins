package team.unnamed.creative.atlas;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

record DirectoryAtlasSourceImpl(String source, String prefix) implements DirectoryAtlasSource {
   DirectoryAtlasSourceImpl(final @NotNull String source, final @NotNull String prefix) {
      this.source = (String)Objects.requireNonNull(source, "source");
      this.prefix = (String)Objects.requireNonNull(prefix, "prefix");
   }

   public @NotNull String source() {
      return this.source;
   }

   public @NotNull String prefix() {
      return this.prefix;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{source=" + this.source + ", prefix=" + this.prefix + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DirectoryAtlasSourceImpl that = (DirectoryAtlasSourceImpl)o;
         return !this.source.equals(that.source) ? false : this.prefix.equals(that.prefix);
      } else {
         return false;
      }
   }
}
