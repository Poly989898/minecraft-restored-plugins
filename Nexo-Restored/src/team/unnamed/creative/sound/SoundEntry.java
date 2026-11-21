package team.unnamed.creative.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@NonExtendable
public interface SoundEntry extends Keyed {
   float DEFAULT_VOLUME = 1.0F;
   float DEFAULT_PITCH = 1.0F;
   int DEFAULT_WEIGHT = 1;
   boolean DEFAULT_STREAM = false;
   int DEFAULT_ATTENUATION_DISTANCE = 16;
   boolean DEFAULT_PRELOAD = false;
   Type DEFAULT_TYPE = SoundEntry.Type.FILE;

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull SoundEntry sound(@NotNull Key path, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload) {
      return new SoundEntryImpl(path, volume, pitch, weight, stream, attenuationDistance, preload, SoundEntry.Type.FILE);
   }

   static @NotNull SoundEntry soundEntry(@NotNull Sound sound) {
      return soundEntry().type(SoundEntry.Type.FILE).key(sound.key()).build();
   }

   static @NotNull SoundEntry soundEntry(@NotNull SoundEvent soundEvent) {
      return soundEntry().type(SoundEntry.Type.EVENT).key(soundEvent.key()).build();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull SoundEntry withDefaultValues(@NotNull Sound sound) {
      return builder().nameSound(sound.key()).build();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull SoundEntry event(@NotNull Key name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload) {
      return new SoundEntryImpl(name, volume, pitch, weight, stream, attenuationDistance, preload, SoundEntry.Type.EVENT);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static SoundEntry event(@Subst("minecraft:sound") @NotNull String name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload) {
      return event(Key.key(name), volume, pitch, weight, stream, attenuationDistance, preload);
   }

   static @NotNull Builder soundEntry() {
      return new SoundEntryImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return soundEntry();
   }

   @NotNull Key key();

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default @NotNull String name() {
      return this.key().value();
   }

   float volume();

   float pitch();

   int weight();

   boolean stream();

   int attenuationDistance();

   boolean preload();

   @NotNull Type type();

   boolean allDefault();

   @Contract("-> new")
   @NotNull Builder toBuilder();

   public static enum Type {
      FILE,
      EVENT;
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder type(@NotNull Type var1);

      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder nameSound(@NotNull Key key) {
         return this.type(SoundEntry.Type.FILE).key(key);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder nameEvent(@NotNull Key key) {
         return this.type(SoundEntry.Type.EVENT).key(key);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder nameEvent(@Subst("minecraft:sound") @NotNull String name) {
         return this.nameEvent(Key.key(name));
      }

      @Contract("_ -> this")
      @NotNull Builder volume(float var1);

      @Contract("_ -> this")
      @NotNull Builder pitch(float var1);

      @Contract("_ -> this")
      @NotNull Builder weight(int var1);

      @Contract("_ -> this")
      @NotNull Builder stream(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder attenuationDistance(int var1);

      @Contract("_ -> this")
      @NotNull Builder preload(boolean var1);

      @NotNull SoundEntry build();
   }
}
