package team.unnamed.creative.lang;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record LanguageImpl(Key key, Map<String, String> translations) implements Language {
   LanguageImpl(final @NotNull Key key, final @NotNull Map<String, String> translations) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.translations = (Map)Objects.requireNonNull(translations, "translations");
      this.validate();
   }

   private void validate() {
      this.translations.forEach((key, value) -> {
         Objects.requireNonNull(key, "Translation key cannot be null");
         Objects.requireNonNull(value, "Translation cannot be null");
      });
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull Map<String, String> translations() {
      return this.translations;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", translations=" + String.valueOf(this.translations) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LanguageImpl that = (LanguageImpl)o;
         return this.key.equals(that.key) && this.translations.equals(that.translations);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements Language.Builder {
      private Key key;
      private Map<String, String> translations;

      public Language.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public Language.@NotNull Builder translations(@NotNull Map<String, String> translations) {
         this.translations = (Map)Objects.requireNonNull(translations, "translations");
         return this;
      }

      public Language.@NotNull Builder translation(@NotNull String key, @NotNull String value) {
         Objects.requireNonNull(key, "key");
         Objects.requireNonNull(value, "value");
         if (this.translations == null) {
            this.translations = new LinkedHashMap();
         }

         this.translations.put(key, value);
         return this;
      }

      public @NotNull Language build() {
         return new LanguageImpl(this.key, this.translations);
      }
   }
}
