package team.unnamed.creative.overlay;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.resources.MergeStrategy;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.waypoint.WaypointStyle;

@NonExtendable
public interface ResourceContainer {
   void atlas(@NotNull Atlas var1);

   @Nullable Atlas atlas(@NotNull Key var1);

   boolean removeAtlas(@NotNull Key var1);

   @NotNull Collection<Atlas> atlases();

   void blockState(@NotNull BlockState var1);

   @Nullable BlockState blockState(@NotNull Key var1);

   boolean removeBlockState(@NotNull Key var1);

   @NotNull Collection<BlockState> blockStates();

   void equipment(@NotNull Equipment var1);

   @Nullable Equipment equipment(@NotNull Key var1);

   boolean removeEquipment(@NotNull Key var1);

   @NotNull Collection<Equipment> equipment();

   void font(@NotNull Font var1);

   @Nullable Font font(@NotNull Key var1);

   boolean removeFont(@NotNull Key var1);

   @NotNull Collection<Font> fonts();

   default void font(@NotNull Key key, @NotNull FontProvider... providers) {
      this.font(Font.font(key, providers));
   }

   default void font(@NotNull Key key, @NotNull List<FontProvider> providers) {
      this.font(Font.font(key, providers));
   }

   void item(@NotNull Item var1);

   @Nullable Item item(@NotNull Key var1);

   boolean removeItem(@NotNull Key var1);

   @NotNull Collection<Item> items();

   void language(@NotNull Language var1);

   @Nullable Language language(@NotNull Key var1);

   boolean removeLanguage(@NotNull Key var1);

   @NotNull Collection<Language> languages();

   void model(@NotNull Model var1);

   @Nullable Model model(@NotNull Key var1);

   boolean removeModel(@NotNull Key var1);

   @NotNull Collection<Model> models();

   void soundRegistry(@NotNull SoundRegistry var1);

   @Nullable SoundRegistry soundRegistry(@NotNull String var1);

   boolean removeSoundRegistry(@NotNull String var1);

   @NotNull Collection<SoundRegistry> soundRegistries();

   default void soundEvent(@NotNull SoundEvent soundEvent) {
      Objects.requireNonNull(soundEvent, "soundEvent");
      String namespace = soundEvent.key().namespace();
      SoundRegistry soundRegistry = this.soundRegistry(namespace);
      Set<SoundEvent> soundEvents;
      if (soundRegistry == null) {
         soundEvents = new HashSet();
      } else {
         soundEvents = new HashSet(soundRegistry.sounds());
      }

      soundEvents.add(soundEvent);
      this.soundRegistry(SoundRegistry.soundRegistry(namespace, soundEvents));
   }

   default @Nullable SoundEvent soundEvent(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      SoundRegistry registry = this.soundRegistry(key.namespace());
      return registry == null ? null : registry.sound(key);
   }

   default boolean removeSoundEvent(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      SoundRegistry registry = this.soundRegistry(key.namespace());
      if (registry == null) {
         return false;
      } else {
         Set<SoundEvent> sounds = new HashSet(registry.sounds());
         boolean removed = sounds.removeIf((sound) -> sound.key().equals(key));
         this.soundRegistry(SoundRegistry.soundRegistry(key.namespace(), sounds));
         return removed;
      }
   }

   default @NotNull Collection<SoundEvent> soundEvents() {
      Collection<SoundEvent> soundEvents = new HashSet();

      for(SoundRegistry soundRegistry : this.soundRegistries()) {
         soundEvents.addAll(soundRegistry.sounds());
      }

      return soundEvents;
   }

   void sound(@NotNull Sound var1);

   @Nullable Sound sound(@NotNull Key var1);

   boolean removeSound(@NotNull Key var1);

   @NotNull Collection<Sound> sounds();

   default void sound(@NotNull Key key, @NotNull Writable data) {
      this.sound(Sound.sound(key, data));
   }

   void texture(@NotNull Texture var1);

   @Nullable Texture texture(@NotNull Key var1);

   boolean removeTexture(@NotNull Key var1);

   @NotNull Collection<Texture> textures();

   default void texture(@NotNull Key key, @NotNull Writable data) {
      this.texture(Texture.texture(key, data));
   }

   default void texture(@NotNull Key key, @NotNull Writable data, @NotNull Metadata meta) {
      this.texture(Texture.texture(key, data, meta));
   }

   void waypointStyle(@NotNull WaypointStyle var1);

   @Nullable WaypointStyle waypointStyle(@NotNull Key var1);

   boolean removeWaypointStyle(@NotNull Key var1);

   @NotNull Collection<WaypointStyle> waypointStyles();

   default void part(@NotNull ResourcePackPart part) {
      Objects.requireNonNull(part, "part");
      part.addTo(this);
   }

   void unknownFile(@NotNull String var1, @NotNull Writable var2);

   @Nullable Writable unknownFile(@NotNull String var1);

   boolean removeUnknownFile(@NotNull String var1);

   @NotNull Map<String, Writable> unknownFiles();

   void merge(@NotNull ResourceContainer var1, @NotNull MergeStrategy var2);
}
