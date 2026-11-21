package team.unnamed.creative.font;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface BitMapFontProvider extends FontProvider {
   int DEFAULT_HEIGHT = 8;

   @NotNull Key file();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull BitMapFontProvider file(@NotNull Key var1);

   int height();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull BitMapFontProvider height(int var1);

   int ascent();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull BitMapFontProvider ascent(int var1);

   @Unmodifiable @NotNull List<String> characters();

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull BitMapFontProvider characters(@NotNull List<String> var1);

   @Contract("-> new")
   default @NotNull Builder toBuilder() {
      return FontProvider.bitMap().file(this.file()).height(this.height()).ascent(this.ascent()).characters(this.characters());
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder file(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder height(int var1);

      @Contract("_ -> this")
      @NotNull Builder ascent(int var1);

      @Contract("_ -> this")
      @NotNull Builder characters(@NotNull List<String> var1);

      @Contract("_ -> this")
      default @NotNull Builder characters(@NotNull String... characters) {
         Objects.requireNonNull(characters, "characters");
         return this.characters(Arrays.asList(characters));
      }

      @Contract("-> new")
      @NotNull BitMapFontProvider build();
   }
}
