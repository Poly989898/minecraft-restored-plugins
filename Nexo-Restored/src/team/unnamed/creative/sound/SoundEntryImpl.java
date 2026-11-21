package team.unnamed.creative.sound;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record SoundEntryImpl(Key key, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload, SoundEntry.Type type) implements SoundEntry {
   SoundEntryImpl(final @NotNull Key key, final float volume, final float pitch, final int weight, final boolean stream, final int attenuationDistance, final boolean preload, final SoundEntry.@NotNull Type type) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.volume = volume;
      this.pitch = pitch;
      this.weight = weight;
      this.stream = stream;
      this.attenuationDistance = attenuationDistance;
      this.preload = preload;
      this.type = (SoundEntry.Type)Objects.requireNonNull(type, "type");
      this.validate();
   }

   private void validate() {
      if (this.volume <= 0.0F) {
         throw new IllegalArgumentException("Zero or negative volume");
      } else if (this.pitch <= 0.0F) {
         throw new IllegalArgumentException("Zero or negative pitch");
      } else if (this.weight <= 0) {
         throw new IllegalArgumentException("Zero or negative weight");
      }
   }

   public @NotNull Key key() {
      return this.key;
   }

   public SoundEntry.@NotNull Type type() {
      return this.type;
   }

   public boolean allDefault() {
      return this.volume == 1.0F && this.pitch == 1.0F && this.weight == 1 && !this.stream && this.attenuationDistance == 16 && !this.preload && this.type == DEFAULT_TYPE;
   }

   public SoundEntry.@NotNull Builder toBuilder() {
      return SoundEntry.soundEntry().key(this.key).volume(this.volume).pitch(this.pitch).weight(this.weight).stream(this.stream).attenuationDistance(this.attenuationDistance).preload(this.preload).type(this.type);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", volume=" + this.volume + ", pitch=" + this.pitch + ", weight=" + this.weight + ", stream=" + this.stream + ", attenuationDistance=" + this.attenuationDistance + ", preload=" + this.preload + ", type=" + String.valueOf(this.type) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SoundEntryImpl sound = (SoundEntryImpl)o;
         return Float.compare(sound.volume, this.volume) == 0 && Float.compare(sound.pitch, this.pitch) == 0 && this.weight == sound.weight && this.stream == sound.stream && this.attenuationDistance == sound.attenuationDistance && this.preload == sound.preload && this.key.equals(sound.key) && this.type == sound.type;
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements SoundEntry.Builder {
      private Key key;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private int weight = 1;
      private boolean stream = false;
      private int attenuationDistance = 16;
      private boolean preload = false;
      private SoundEntry.Type type;

      BuilderImpl() {
         this.type = SoundEntry.DEFAULT_TYPE;
      }

      public SoundEntry.@NotNull Builder type(SoundEntry.@NotNull Type type) {
         this.type = (SoundEntry.Type)Objects.requireNonNull(type, "type");
         return this;
      }

      public SoundEntry.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public SoundEntry.@NotNull Builder volume(float volume) {
         this.volume = volume;
         return this;
      }

      public SoundEntry.@NotNull Builder pitch(float pitch) {
         this.pitch = pitch;
         return this;
      }

      public SoundEntry.@NotNull Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      public SoundEntry.@NotNull Builder stream(boolean stream) {
         this.stream = stream;
         return this;
      }

      public SoundEntry.@NotNull Builder attenuationDistance(int attenuationDistance) {
         this.attenuationDistance = attenuationDistance;
         return this;
      }

      public SoundEntry.@NotNull Builder preload(boolean preload) {
         this.preload = preload;
         return this;
      }

      public @NotNull SoundEntry build() {
         return new SoundEntryImpl(this.key, this.volume, this.pitch, this.weight, this.stream, this.attenuationDistance, this.preload, this.type);
      }
   }
}
