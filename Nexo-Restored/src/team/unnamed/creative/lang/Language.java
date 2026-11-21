package team.unnamed.creative.lang;

import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Language extends ResourcePackPart, Keyed {
   static @NotNull Language language(@NotNull Key key, @NotNull Map<String, String> translations) {
      return new LanguageImpl(key, translations);
   }

   static @NotNull Builder language() {
      return new LanguageImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Language of(@NotNull Key key, @NotNull Map<String, String> translations) {
      return new LanguageImpl(key, translations);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return new LanguageImpl.BuilderImpl();
   }

   @NotNull Key key();

   @NotNull Map<String, String> translations();

   default @Nullable String translation(@NotNull String key) {
      Objects.requireNonNull(key, "key");
      return (String)this.translations().get(key);
   }

   default @Nullable String translation(@NotNull Translatable translatable) {
      Objects.requireNonNull(translatable, "translatable");
      return this.translation(translatable.translationKey());
   }

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.language(this);
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @NotNull Builder translations(@NotNull Map<String, String> var1);

      @NotNull Builder translation(@NotNull String var1, @NotNull String var2);

      @NotNull Language build();
   }
}
