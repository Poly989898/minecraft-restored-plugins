package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record LocalTimeItemStringPropertyImpl(String locale, String timezone, String pattern) implements LocalTimeItemStringProperty {
   LocalTimeItemStringPropertyImpl(final @NotNull String locale, final @Nullable String timezone, final @NotNull String pattern) {
      this.locale = (String)Objects.requireNonNull(locale, "locale");
      this.timezone = timezone;
      this.pattern = (String)Objects.requireNonNull(pattern, "pattern");
   }

   public @NotNull String locale() {
      return this.locale;
   }

   public @Nullable String timezone() {
      return this.timezone;
   }

   public @NotNull String pattern() {
      return this.pattern;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{locale=" + this.locale + ", timezone=" + this.timezone + ", pattern=" + this.pattern + "}";
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof LocalTimeItemStringPropertyImpl)) {
         return false;
      } else {
         LocalTimeItemStringPropertyImpl that = (LocalTimeItemStringPropertyImpl)object;
         return this.locale.equals(that.locale) && this.pattern.equals(that.pattern) && Objects.equals(this.timezone, that.timezone);
      }
   }
}
