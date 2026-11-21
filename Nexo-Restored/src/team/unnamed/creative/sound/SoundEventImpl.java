package team.unnamed.creative.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

record SoundEventImpl(Key key, boolean replace, String subtitle, List<SoundEntry> sounds) implements SoundEvent {
   SoundEventImpl(final @NotNull Key key, final boolean replace, final @Nullable String subtitle, final @NotNull List<SoundEntry> sounds) {
      Objects.requireNonNull(key, "key");
      Objects.requireNonNull(sounds, "sounds");
      this.key = key;
      this.replace = replace;
      this.subtitle = subtitle;
      this.sounds = MoreCollections.<SoundEntry>immutableListOf(sounds);
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @Nullable String subtitle() {
      return this.subtitle;
   }

   public @Unmodifiable @NotNull List<SoundEntry> sounds() {
      return this.sounds;
   }

   public SoundEvent.@NotNull Builder toBuilder() {
      return SoundEvent.soundEvent().key(this.key).replace(this.replace).subtitle(this.subtitle).sounds(this.sounds);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{replace=" + this.replace + ", subtitle=" + this.subtitle + ", sounds=" + String.valueOf(this.sounds) + "}";
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.replace, this.subtitle, this.sounds});
   }

   static final class BuilderImpl implements SoundEvent.Builder {
      private Key key;
      private boolean replace = false;
      private String subtitle;
      private List<SoundEntry> sounds = new ArrayList();

      public SoundEvent.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public SoundEvent.@NotNull Builder replace(boolean replace) {
         this.replace = replace;
         return this;
      }

      public SoundEvent.@NotNull Builder subtitle(@Nullable String subtitle) {
         this.subtitle = subtitle;
         return this;
      }

      public SoundEvent.@NotNull Builder sounds(@NotNull List<SoundEntry> sounds) {
         Objects.requireNonNull(sounds, "sounds");
         this.sounds = new ArrayList(sounds);
         return this;
      }

      public SoundEvent.@NotNull Builder addSound(@NotNull SoundEntry soundEntry) {
         Objects.requireNonNull(soundEntry, "soundEntry");
         this.sounds.add(soundEntry);
         return this;
      }

      public @NotNull SoundEvent build() {
         return new SoundEventImpl(this.key, this.replace, this.subtitle, this.sounds);
      }
   }
}
