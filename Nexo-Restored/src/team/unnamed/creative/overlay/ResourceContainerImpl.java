package team.unnamed.creative.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.equipment.EquipmentLayer;
import team.unnamed.creative.equipment.EquipmentLayerType;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.resources.MergeException;
import team.unnamed.creative.resources.MergeStrategy;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.waypoint.WaypointStyle;

@Internal
public class ResourceContainerImpl implements ResourceContainer {
   private final Map<Key, Atlas> atlases = new LinkedHashMap();
   private final Map<Key, BlockState> blockStates = new LinkedHashMap();
   private final Map<Key, Equipment> equipment = new LinkedHashMap();
   private final Map<Key, Font> fonts = new LinkedHashMap();
   private final Map<Key, Item> items = new LinkedHashMap();
   private final Map<Key, Language> languages = new LinkedHashMap();
   private final Map<Key, Model> models = new LinkedHashMap();
   private final Map<String, SoundRegistry> soundRegistries = new LinkedHashMap();
   private final Map<Key, Sound> sounds = new LinkedHashMap();
   private final Map<Key, Texture> textures = new LinkedHashMap();
   private final Map<String, WaypointStyle> waypointStyles = new LinkedHashMap();
   private final Map<String, Writable> files = new LinkedHashMap();

   public void atlas(@NotNull Atlas atlas) {
      Objects.requireNonNull(atlas, "atlas");
      this.atlases.put(atlas.key(), atlas);
   }

   public @Nullable Atlas atlas(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Atlas)this.atlases.get(key);
   }

   public boolean removeAtlas(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.atlases.remove(key) != null;
   }

   public @NotNull Collection<Atlas> atlases() {
      return this.atlases.values();
   }

   public void blockState(@NotNull BlockState state) {
      Objects.requireNonNull(state, "state");
      this.blockStates.put(state.key(), state);
   }

   public @Nullable BlockState blockState(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (BlockState)this.blockStates.get(key);
   }

   public boolean removeBlockState(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.blockStates.remove(key) != null;
   }

   public @NotNull Collection<BlockState> blockStates() {
      return this.blockStates.values();
   }

   public void equipment(@NotNull Equipment equipment) {
      Objects.requireNonNull(equipment, "equipment");
      this.equipment.put(equipment.key(), equipment);
   }

   public @Nullable Equipment equipment(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Equipment)this.equipment.get(key);
   }

   public boolean removeEquipment(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.equipment.remove(key) != null;
   }

   public @NotNull Collection<Equipment> equipment() {
      return this.equipment.values();
   }

   public void font(@NotNull Font font) {
      Objects.requireNonNull(font, "font");
      this.fonts.put(font.key(), font);
   }

   public @Nullable Font font(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Font)this.fonts.get(key);
   }

   public boolean removeFont(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.fonts.remove(key) != null;
   }

   public @NotNull Collection<Font> fonts() {
      return this.fonts.values();
   }

   public void item(@NotNull Item item) {
      Objects.requireNonNull(item, "item");
      this.items.put(item.key(), item);
   }

   public @Nullable Item item(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Item)this.items.get(key);
   }

   public boolean removeItem(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.items.remove(key) != null;
   }

   public @NotNull Collection<Item> items() {
      return this.items.values();
   }

   public void language(@NotNull Language language) {
      Objects.requireNonNull(language, "language");
      this.languages.put(language.key(), language);
   }

   public @Nullable Language language(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Language)this.languages.get(key);
   }

   public boolean removeLanguage(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.languages.remove(key) != null;
   }

   public @NotNull Collection<Language> languages() {
      return this.languages.values();
   }

   public void model(@NotNull Model model) {
      Objects.requireNonNull(model, "model");
      this.models.put(model.key(), model);
   }

   public @Nullable Model model(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Model)this.models.get(key);
   }

   public boolean removeModel(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.models.remove(key) != null;
   }

   public @NotNull Collection<Model> models() {
      return this.models.values();
   }

   public void soundRegistry(@NotNull SoundRegistry soundRegistry) {
      Objects.requireNonNull(soundRegistry, "soundRegistry");
      this.soundRegistries.put(soundRegistry.namespace(), soundRegistry);
   }

   public @Nullable SoundRegistry soundRegistry(@NotNull String namespace) {
      Objects.requireNonNull(namespace, "namespace");
      return (SoundRegistry)this.soundRegistries.get(namespace);
   }

   public boolean removeSoundRegistry(@NotNull String namespace) {
      Objects.requireNonNull(namespace, "namespace");
      return this.soundRegistries.remove(namespace) != null;
   }

   public @NotNull Collection<SoundRegistry> soundRegistries() {
      return this.soundRegistries.values();
   }

   public void sound(@NotNull Sound sound) {
      Objects.requireNonNull(sound, "sound");
      this.sounds.put(sound.key(), sound);
   }

   public @Nullable Sound sound(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Sound)this.sounds.get(key);
   }

   public boolean removeSound(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.sounds.remove(key) != null;
   }

   public @NotNull Collection<Sound> sounds() {
      return this.sounds.values();
   }

   public void texture(@NotNull Texture texture) {
      Objects.requireNonNull(texture, "textures");
      this.textures.put(texture.key(), texture);
   }

   public @Nullable Texture texture(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (Texture)this.textures.get(key);
   }

   public boolean removeTexture(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.textures.remove(key) != null;
   }

   public @NotNull Collection<Texture> textures() {
      return this.textures.values();
   }

   public void waypointStyle(@NotNull WaypointStyle style) {
      Objects.requireNonNull(style, "style");
      this.waypointStyles.put(style.key().asString(), style);
   }

   public @Nullable WaypointStyle waypointStyle(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return (WaypointStyle)this.waypointStyles.get(key.asString());
   }

   public boolean removeWaypointStyle(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return this.waypointStyles.remove(key.asString()) != null;
   }

   public @NotNull Collection<WaypointStyle> waypointStyles() {
      return this.waypointStyles.values();
   }

   public void unknownFile(@NotNull String path, @NotNull Writable data) {
      Objects.requireNonNull(path, "path");
      Objects.requireNonNull(data, "data");
      this.files.put(path, data);
   }

   public @Nullable Writable unknownFile(@NotNull String path) {
      Objects.requireNonNull(path, "path");
      return (Writable)this.files.get(path);
   }

   public boolean removeUnknownFile(@NotNull String path) {
      Objects.requireNonNull(path, "path");
      return this.files.remove(path) != null;
   }

   public @NotNull Map<String, Writable> unknownFiles() {
      return this.files;
   }

   public void merge(@NotNull ResourceContainer other, @NotNull MergeStrategy strategy) {
      boolean override = strategy == MergeStrategy.override();

      for(Atlas atlas : other.atlases()) {
         Atlas oldAtlas = (Atlas)this.atlases.get(atlas.key());
         if (oldAtlas != null && !override) {
            Set<AtlasSource> sources = new LinkedHashSet(oldAtlas.sources());
            sources.addAll(atlas.sources());
            this.atlases.put(atlas.key(), oldAtlas.toBuilder().sources(new ArrayList(sources)).build());
         } else {
            this.atlases.put(atlas.key(), atlas);
         }
      }

      for(BlockState blockState : other.blockStates()) {
         if (this.blockStates.containsKey(blockState.key())) {
            if (strategy == MergeStrategy.override()) {
               this.blockStates.put(blockState.key(), blockState);
            } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
               throw new MergeException("Duplicate block state '" + String.valueOf(blockState.key()) + "': exists in both resource containers");
            }
         } else {
            this.blockStates.put(blockState.key(), blockState);
         }
      }

      for(Equipment equipment : other.equipment()) {
         Equipment oldEquipment = (Equipment)this.equipment.get(equipment.key());
         if (oldEquipment != null && !override) {
            Map<EquipmentLayerType, List<EquipmentLayer>> layersByType = new LinkedHashMap(oldEquipment.layers());

            for(Map.Entry<EquipmentLayerType, List<EquipmentLayer>> entry : equipment.layers().entrySet()) {
               List<EquipmentLayer> oldLayers = (List)layersByType.get(entry.getKey());
               if (oldLayers == null) {
                  layersByType.put((EquipmentLayerType)entry.getKey(), (List)entry.getValue());
               } else {
                  List<EquipmentLayer> newLayers = new ArrayList(oldLayers);
                  newLayers.addAll((Collection)entry.getValue());
                  layersByType.put((EquipmentLayerType)entry.getKey(), newLayers);
               }
            }

            this.equipment.put(equipment.key(), oldEquipment.layers(layersByType));
         } else {
            this.equipment.put(equipment.key(), equipment);
         }
      }

      for(Font font : other.fonts()) {
         Font oldFont = (Font)this.fonts.get(font.key());
         if (oldFont != null && !override) {
            List<FontProvider> providers = new ArrayList(oldFont.providers());
            providers.addAll(font.providers());
            this.fonts.put(font.key(), oldFont.providers(providers));
         } else {
            this.fonts.put(font.key(), font);
         }
      }

      for(Item item : other.items()) {
         if (this.items.containsKey(item.key())) {
            if (override) {
               this.items.put(item.key(), item);
            } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
               throw new MergeException("Duplicated item '" + String.valueOf(item.key()) + "': exists in both resource containers");
            }
         } else {
            this.items.put(item.key(), item);
         }
      }

      for(Language language : other.languages()) {
         Language oldLanguage = (Language)this.languages.get(language.key());
         if (oldLanguage != null && !override) {
            Map<String, String> translations = new LinkedHashMap(oldLanguage.translations());

            for(Map.Entry<String, String> translation : language.translations().entrySet()) {
               String replaced = (String)translations.put((String)translation.getKey(), (String)translation.getValue());
               if (replaced != null && strategy == MergeStrategy.mergeAndFailOnError()) {
                  String var10002 = String.valueOf(language.key());
                  throw new MergeException("Duplicated translation keys in language " + var10002 + ". Translation key: " + (String)translation.getKey() + ". Exists in both resource containers.");
               }
            }

            this.languages.put(language.key(), Language.language(language.key(), translations));
         } else {
            this.languages.put(language.key(), language);
         }
      }

      for(Model model : other.models()) {
         Model oldModel = (Model)this.models.get(model.key());
         if (oldModel != null && strategy != MergeStrategy.override()) {
            Model.Builder oldModelBuilder = oldModel.toBuilder();

            for(ItemOverride itemOverride : model.overrides()) {
               oldModelBuilder.addOverride(itemOverride);
            }

            this.models.put(model.key(), oldModelBuilder.build());
         } else {
            this.models.put(model.key(), model);
         }
      }

      for(SoundRegistry soundRegistry : other.soundRegistries()) {
         SoundRegistry oldSoundRegistry = (SoundRegistry)this.soundRegistries.get(soundRegistry.namespace());
         if (oldSoundRegistry != null && !override) {
            Map<Key, SoundEvent> soundEvents = new LinkedHashMap();

            for(SoundEvent soundEvent : oldSoundRegistry.sounds()) {
               soundEvents.put(soundEvent.key(), soundEvent);
            }

            for(SoundEvent soundEvent : soundRegistry.sounds()) {
               SoundEvent replacedSoundEvent = (SoundEvent)soundEvents.put(soundEvent.key(), soundEvent);
               if (replacedSoundEvent != null && strategy == MergeStrategy.mergeAndFailOnError()) {
                  throw new MergeException("Duplicated sound event '" + String.valueOf(soundEvent) + "': exists in both resource-packs");
               }
            }

            this.soundRegistries.put(soundRegistry.namespace(), SoundRegistry.soundRegistry().namespace(soundRegistry.namespace()).sounds(soundEvents.values()).build());
         } else {
            this.soundRegistries.put(soundRegistry.namespace(), soundRegistry);
         }
      }

      for(Sound sound : other.sounds()) {
         if (this.sounds.containsKey(sound.key())) {
            if (override) {
               this.sounds.put(sound.key(), sound);
            } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
               throw new MergeException("Duplicated sound '" + String.valueOf(sound.key()) + "': exists in both resource containers");
            }
         } else {
            this.sounds.put(sound.key(), sound);
         }
      }

      for(Texture texture : other.textures()) {
         if (this.textures.containsKey(texture.key())) {
            if (override) {
               this.textures.put(texture.key(), texture);
            } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
               throw new MergeException("Duplicated texture '" + String.valueOf(texture.key()) + "': exists in both resource containers");
            }
         } else {
            this.textures.put(texture.key(), texture);
         }
      }

      for(Map.Entry<String, Writable> entry : other.unknownFiles().entrySet()) {
         if (this.files.containsKey(entry.getKey())) {
            if (override) {
               this.files.put((String)entry.getKey(), (Writable)entry.getValue());
            } else if (strategy == MergeStrategy.mergeAndFailOnError()) {
               throw new MergeException("Duplicated unknown file: '" + (String)entry.getKey() + "': exists in both resource containers");
            }
         } else {
            this.files.put((String)entry.getKey(), (Writable)entry.getValue());
         }
      }

   }
}
