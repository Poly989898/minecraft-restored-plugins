package team.unnamed.creative.metadata.language;

import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface LanguageMeta extends MetadataPart {
   int MAX_LANGUAGE_LENGTH = 16;

   @Contract("_ -> new")
   static @NotNull LanguageMeta language(@NotNull Map<String, LanguageEntry> languages) {
      return new LanguageMetaImpl(languages);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_ -> new")
   static @NotNull LanguageMeta of(@NotNull Map<String, LanguageEntry> languages) {
      return language(languages);
   }

   @NotNull @Unmodifiable Map<String, LanguageEntry> languages();
}
