package team.unnamed.creative.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface Sound extends ResourcePackPart, Keyed {
   static @NotNull Sound sound(@NotNull Key key, @NotNull Writable data) {
      return new SoundImpl(key, data);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Sound of(@NotNull Key key, @NotNull Writable data) {
      return new SoundImpl(key, data);
   }

   @NotNull Key key();

   @NotNull Writable data();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.sound(this);
   }
}
