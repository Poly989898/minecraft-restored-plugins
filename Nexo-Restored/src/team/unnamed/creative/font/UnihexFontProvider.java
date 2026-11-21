package team.unnamed.creative.font;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@NonExtendable
public interface UnihexFontProvider extends FontProvider {
   @NotNull Key file();

   @Unmodifiable @NotNull List<SizeOverride> sizes();

   public interface SizeOverride {
      static @NotNull SizeOverride override(int from, int to, int left, int right) {
         return new UnihexFontProviderImpl.SizeOverrideImpl(from, to, left, right);
      }

      static @NotNull SizeOverride override(String from, String to, int left, int right) {
         return new UnihexFontProviderImpl.SizeOverrideImpl(from, to, left, right);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      static @NotNull SizeOverride of(int from, int to, int left, int right) {
         return override(from, to, left, right);
      }

      int from();

      int to();

      int left();

      int right();
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder file(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder sizes(@NotNull List<SizeOverride> var1);

      @Contract("_ -> this")
      default @NotNull Builder sizes(@NotNull SizeOverride... sizes) {
         Objects.requireNonNull(sizes, "sizes");
         return this.sizes(Arrays.asList(sizes));
      }

      @Contract("_ -> this")
      @NotNull Builder addSize(@NotNull SizeOverride var1);

      @Contract("-> new")
      @NotNull UnihexFontProvider build();
   }
}
