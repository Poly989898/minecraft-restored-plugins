package team.unnamed.creative.metadata.language;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public interface LanguageEntry {
   boolean DEFAULT_BIDIRECTIONAL = false;

   @Contract("_, _, _ -> new")
   static @NotNull LanguageEntry languageEntry(@NotNull String name, @NotNull String region, boolean bidirectional) {
      return new LanguageEntryImpl(name, region, bidirectional);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_, _, _ -> new")
   static @NotNull LanguageEntry of(@NotNull String name, @NotNull String region, boolean bidirectional) {
      return languageEntry(name, region, bidirectional);
   }

   @Contract("-> new")
   static @NotNull Builder languageEntry() {
      return new LanguageEntryImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> new")
   static @NotNull Builder builder() {
      return languageEntry();
   }

   @NotNull String name();

   @NotNull String region();

   boolean bidirectional();

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder name(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull Builder region(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull Builder bidirectional(boolean var1);

      @Contract("-> new")
      @NotNull LanguageEntry build();
   }
}
