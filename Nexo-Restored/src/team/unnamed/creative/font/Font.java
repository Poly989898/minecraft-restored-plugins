package team.unnamed.creative.font;

import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Font extends ResourcePackPart, Keyed {
   Key MINECRAFT_DEFAULT = Key.key("default");
   Key MINECRAFT_ALT = Key.key("alt");
   Key MINECRAFT_ILLAGERALT = Key.key("illageralt");
   Key MINECRAFT_UNIFORM = Key.key("uniform");

   static @NotNull Font font(@NotNull Key key, @NotNull List<FontProvider> providers) {
      return new FontImpl(key, providers);
   }

   static @NotNull Font font(@NotNull Key key, @NotNull FontProvider... providers) {
      return of(key, Arrays.asList(providers));
   }

   static @NotNull Builder font() {
      return new FontImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Font of(@NotNull Key key, @NotNull List<FontProvider> providers) {
      return font(key, providers);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Font of(@NotNull Key key, @NotNull FontProvider... providers) {
      return font(key, Arrays.asList(providers));
   }

   @NotNull Key key();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull Font key(@NotNull Key var1);

   @NotNull List<FontProvider> providers();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull Font providers(@NotNull List<FontProvider> var1);

   default @NotNull Builder toBuilder() {
      return font().key(this.key()).providers(this.providers());
   }

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.font(this);
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder providers(@NotNull List<FontProvider> var1);

      @NotNull Builder providers(@NotNull FontProvider... var1);

      @Contract("_ -> this")
      @NotNull Builder addProvider(@NotNull FontProvider var1);

      @NotNull Font build();
   }
}
