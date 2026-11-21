package team.unnamed.creative.sound;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface SoundEvent extends ResourcePackPart, net.kyori.adventure.sound.Sound.Type {
   boolean DEFAULT_REPLACE = false;

   static @NotNull SoundEvent soundEvent(@NotNull Key key, boolean replace, @Nullable String subtitle, @NotNull List<SoundEntry> sounds) {
      return new SoundEventImpl(key, replace, subtitle, sounds);
   }

   static @NotNull Builder soundEvent() {
      return new SoundEventImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull SoundEvent of(@NotNull Key key, boolean replace, @Nullable String subtitle, @NotNull List<SoundEntry> sounds) {
      return new SoundEventImpl(key, replace, subtitle, sounds);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return new SoundEventImpl.BuilderImpl();
   }

   @NotNull Key key();

   boolean replace();

   @Nullable String subtitle();

   @Unmodifiable @NotNull List<SoundEntry> sounds();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.soundEvent(this);
   }

   @Contract("-> new")
   @NotNull Builder toBuilder();

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder replace(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder subtitle(@Nullable String var1);

      @Contract("_ -> this")
      @NotNull Builder sounds(@NotNull List<SoundEntry> var1);

      @Contract("_ -> this")
      @NotNull Builder addSound(@NotNull SoundEntry var1);

      @Contract("_ -> this")
      default @NotNull Builder addSound(@NotNull Sound sound) {
         return this.addSound(SoundEntry.soundEntry(sound));
      }

      @Contract("_ -> this")
      default @NotNull Builder addSound(@NotNull SoundEvent soundEvent) {
         return this.addSound(SoundEntry.soundEntry(soundEvent));
      }

      @Contract("_ -> this")
      default @NotNull Builder sounds(@NotNull SoundEntry... sounds) {
         Objects.requireNonNull(sounds, "sounds");
         return this.sounds(Arrays.asList(sounds));
      }

      @NotNull SoundEvent build();
   }
}
