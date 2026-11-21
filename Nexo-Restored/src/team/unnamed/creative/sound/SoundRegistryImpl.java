package team.unnamed.creative.sound;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.Keys;

final class SoundRegistryImpl implements SoundRegistry {
   @Subst("minecraft")
   private final String namespace;
   private final Map<Key, SoundEvent> sounds;

   SoundRegistryImpl(@NotNull String namespace, @NotNull Collection<SoundEvent> sounds) {
      this.namespace = (String)Objects.requireNonNull(namespace, "namespace");
      Objects.requireNonNull(sounds, "sounds");
      this.sounds = new LinkedHashMap();

      for(SoundEvent soundEvent : sounds) {
         this.sounds.put(soundEvent.key(), soundEvent);
      }

      this.validate();
   }

   private void validate() {
      Keys.validateNamespace(this.namespace);

      for(Key key : this.sounds.keySet()) {
         if (!key.namespace().equals(this.namespace)) {
            throw new IllegalArgumentException("Sound events can't have a namespace different from the sound registry namespace!");
         }
      }

   }

   @Pattern("[a-z0-9_\\-.]+")
   public @NotNull String namespace() {
      return this.namespace;
   }

   public @Unmodifiable @NotNull Collection<SoundEvent> sounds() {
      return this.sounds.values();
   }

   public @Nullable SoundEvent sound(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (SoundEvent)this.sounds.get(key);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{namespace=" + this.namespace + ", sounds=" + String.valueOf(this.sounds) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SoundRegistryImpl that = (SoundRegistryImpl)o;
         return this.namespace.equals(that.namespace) && this.sounds.equals(that.sounds);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.namespace, this.sounds});
   }

   static final class BuilderImpl implements SoundRegistry.Builder {
      private String namespace;
      private Set<SoundEvent> sounds;

      public SoundRegistry.@NotNull Builder namespace(@NotNull String namespace) {
         this.namespace = (String)Objects.requireNonNull(namespace, "namespace");
         return this;
      }

      public SoundRegistry.@NotNull Builder sound(@NotNull SoundEvent event) {
         Objects.requireNonNull(event, "event");
         if (this.sounds == null) {
            this.sounds = new LinkedHashSet();
         }

         this.sounds.add(event);
         return this;
      }

      public SoundRegistry.@NotNull Builder sounds(@NotNull Collection<? extends SoundEvent> sounds) {
         Objects.requireNonNull(sounds, "sounds");
         this.sounds = new LinkedHashSet(sounds);
         return this;
      }

      public @NotNull SoundRegistry build() {
         return new SoundRegistryImpl(this.namespace, this.sounds);
      }
   }
}
