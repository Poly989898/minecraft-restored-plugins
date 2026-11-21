package team.unnamed.creative.font;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@NonExtendable
public interface ReferenceFontProvider extends FontProvider {
   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull ReferenceFontProvider of(@NotNull Key id) {
      return FontProvider.reference(id);
   }

   @NotNull Key id();
}
