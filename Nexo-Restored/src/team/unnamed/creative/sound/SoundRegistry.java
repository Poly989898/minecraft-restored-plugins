package team.unnamed.creative.sound;

import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Namespaced;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

public interface SoundRegistry extends ResourcePackPart, Namespaced {
   static @NotNull SoundRegistry soundRegistry(@NotNull String namespace, @NotNull Set<SoundEvent> sounds) {
      return new SoundRegistryImpl(namespace, sounds);
   }

   static @NotNull SoundRegistry soundRegistry(@NotNull String namespace, @NotNull Collection<SoundEvent> sounds) {
      return new SoundRegistryImpl(namespace, sounds);
   }

   static @NotNull Builder soundRegistry() {
      return new SoundRegistryImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull SoundRegistry of(@NotNull String namespace, @NotNull Set<SoundEvent> sounds) {
      return soundRegistry(namespace, sounds);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return soundRegistry();
   }

   @Pattern("[a-z0-9_\\-.]+")
   @Subst("minecraft")
   @NotNull String namespace();

   @Unmodifiable @NotNull Collection<SoundEvent> sounds();

   @Nullable SoundEvent sound(@NotNull Key var1);

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.soundRegistry(this);
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder namespace(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull Builder sound(@NotNull SoundEvent var1);

      @Contract("_ -> this")
      @NotNull Builder sounds(@NotNull Collection<? extends SoundEvent> var1);

      @Contract("-> new")
      @NotNull SoundRegistry build();
   }
}
