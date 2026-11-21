package team.unnamed.creative.metadata.language;

import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.util.MoreCollections;

record LanguageMetaImpl(Map<String, LanguageEntry> languages) implements LanguageMeta {
   LanguageMetaImpl(final @NotNull Map<String, LanguageEntry> languages) {
      this.languages = MoreCollections.<String, LanguageEntry>immutableMapOf((Map)Objects.requireNonNull(languages, "languages"));
      this.validate();
   }

   private void validate() {
      for(Map.Entry<String, LanguageEntry> language : this.languages.entrySet()) {
         String code = (String)language.getKey();
         Objects.requireNonNull(code, "Language code is null");
         Objects.requireNonNull((LanguageEntry)language.getValue(), "Language entry for " + code + " is null");
         if (code.length() > 16) {
            throw new IllegalArgumentException("Language code is more than 16 characters long");
         }
      }

   }

   public @NotNull Class<? extends MetadataPart> type() {
      return LanguageMeta.class;
   }

   public @NotNull @Unmodifiable Map<String, LanguageEntry> languages() {
      return this.languages;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{languages=" + String.valueOf(this.languages) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LanguageMetaImpl that = (LanguageMetaImpl)o;
         return this.languages.equals(that.languages);
      } else {
         return false;
      }
   }
}
