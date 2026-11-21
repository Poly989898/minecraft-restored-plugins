package com.nexomc.nexo.configs;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.Reloadable;
import com.nexomc.nexo.utils.jukebox_playable.JukeboxPlayable;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0014\u001a\u00020\u0015H\u0016J&\u0010\u0016\u001a\u0012\u0012\u0004\u0012\u00020\u00130\u0017j\b\u0012\u0004\u0012\u00020\u0013`\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001aH\u0002J&\u0010\u0011\u001a\"\u0012\f\u0012\n \u001c*\u0004\u0018\u00010\u00130\u00130\u0017j\u0010\u0012\f\u0012\n \u001c*\u0004\u0018\u00010\u00130\u0013`\u0018R\u0014\u0010\u0004\u001a\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u000e8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/configs/SoundManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "soundConfig", "Lorg/bukkit/configuration/file/YamlConfiguration;", "getSoundConfig", "()Lorg/bukkit/configuration/file/YamlConfiguration;", "jukeboxPlayables", "", "Lcom/nexomc/nexo/utils/jukebox_playable/JukeboxPlayable;", "getJukeboxPlayables", "()Ljava/util/List;", "generateSounds", "", "getGenerateSounds", "()Z", "customSoundRegistries", "", "Lteam/unnamed/creative/sound/SoundRegistry;", "reload", "", "parseCustomSounds", "Ljava/util/LinkedHashSet;", "Lkotlin/collections/LinkedHashSet;", "sounds", "", "Lorg/bukkit/configuration/ConfigurationSection;", "kotlin.jvm.PlatformType", "com.nexomc:core"}
)
public final class SoundManager implements Reloadable {
   @NotNull
   private final List<JukeboxPlayable> jukeboxPlayables = (List)(new ArrayList());
   @NotNull
   private Set<SoundRegistry> customSoundRegistries = (Set)(new LinkedHashSet());

   public SoundManager() {
      this.reload();
   }

   private final YamlConfiguration getSoundConfig() {
      return NexoPlugin.Companion.instance().getConfigsManager$com_nexomc_core().getSounds();
   }

   @NotNull
   public final List<JukeboxPlayable> getJukeboxPlayables() {
      return this.jukeboxPlayables;
   }

   private final boolean getGenerateSounds() {
      return this.getSoundConfig().getBoolean("settings.generate_sounds");
   }

   public void reload() {
      this.customSoundRegistries.clear();
      NexoConverter.INSTANCE.processSounds(this.getSoundConfig());
      if (this.getGenerateSounds()) {
         CollectionsKt.addAll((Collection)this.customSoundRegistries, (Iterable)this.parseCustomSounds(NexoYamlKt.sectionList((ConfigurationSection)this.getSoundConfig(), "sounds")));
      }

   }

   private final LinkedHashSet<SoundRegistry> parseCustomSounds(List<? extends ConfigurationSection> sounds) {
      java.lang.String str;
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      LinkedHashSet linkedHashSet2 = linkedHashSet;
      int value = 0;
      Iterable iterable = (Iterable)sounds;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value2 = 0;
         ConfigurationSection soundEntry = (ConfigurationSection)element;
         int value3 = 0;
         Key key = NexoYamlKt.key(soundEntry, "id");
         SoundEvent soundEvent;
         if (key == null) {
            int value9 = 0;
            Logs.logWarn("Skipping sound entry with missing ID", false);
            soundEvent = null;
         } else {
            block: {
               Key soundId = key;
               Iterable iterable2 = (Iterable)NexoYamlKt.keyListOrSingle(soundEntry, "sound", (String)null);
               Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

               for(Object item : iterable2) {
                  Key it = (Key)item;
                  int value4 = 0;
                  collection.add(KeyUtils.INSTANCE.removeSuffix(it, ".ogg"));
               }

               List soundKeys = (List)collection;
               Object obj4 = soundEntry.get("type");
               Object obj = obj4;
               if (!(obj4 instanceof String)) {
                  obj = null;
               }

               block2: {
                  String str2 = (String)obj;
                  if (str2 != null) {
                     str = str2.toUpperCase(Locale.ROOT);
                     if (str != null) {
                        break block2;
                     }
                  }

                  str = "FILE";
               }

               String type = str;
               List list;
               if (Intrinsics.areEqual(type, "FILE")) {
                  Iterable iterable3 = (Iterable)soundKeys;
                  Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

                  for(Object obj5 : iterable3) {
                     Key soundKey = (Key)obj5;
                     int value5 = 0;
                     SoundEntry.Builder builder = SoundEntry.soundEntry().key(soundKey).stream(soundEntry.getBoolean("stream"));
                     Float number = NexoYamlKt.floatOrNull(soundEntry, "volume");
                     builder = builder.volume(number != null ? RangesKt.coerceAtLeast((float)(number), 0.0F) : 1.0F);
                     number = NexoYamlKt.floatOrNull(soundEntry, "pitch");
                     builder = builder.pitch(number != null ? RangesKt.coerceAtLeast((float)(number), 0.0F) : 1.0F);
                     Integer number2 = NexoYamlKt.intOrNull(soundEntry, "weight");
                     builder = builder.weight(number2 != null ? number2 : 1).preload(soundEntry.getBoolean("preload"));
                     number2 = NexoYamlKt.intOrNull(soundEntry, "attenuation_distance");
                     collection2.add(builder.attenuationDistance(number2 != null ? number2 : 16).build());
                  }

                  list = (List)collection2;
               } else {
                  if (!Intrinsics.areEqual(type, "EVENT")) {
                     Logs.logWarn("Skipping unknown sound type " + type + " for " + soundId, false);
                     soundEvent = null;
                     break block;
                  }

                  Key referenceId = NexoYamlKt.key(soundEntry, "reference_id");
                  if (referenceId == null) {
                     Logs.logWarn("Skipping EVENT type sound entry without reference ID for " + soundId, false);
                     soundEvent = null;
                     break block;
                  }

                  list = CollectionsKt.listOf(SoundEntry.soundEntry().key(referenceId).build());
               }

               List soundEntries = list;
               ConfigurationSection configurationSection = NexoYamlKt.section(soundEntry, "jukebox_playable");
               if (configurationSection != null) {
                  ConfigurationSection it = configurationSection;
                  ((Collection)this.jukeboxPlayables).add(new JukeboxPlayable(it, soundId));
               }

               soundEvent = SoundEvent.soundEvent().key(soundId).sounds(soundEntries).replace(soundEntry.getBoolean("replace", false)).subtitle(soundEntry.getString("subtitle")).build();
            }
         }

         if (soundEvent != null) {
            Object obj6 = soundEvent;
            int value6 = 0;
            destination.add(obj6);
         }
      }

      iterable = (Iterable)((List)destination);
      Map map = (Map)(new LinkedHashMap());

      for(Object obj7 : iterable) {
         SoundEvent it = (SoundEvent)obj7;
         int value7 = 0;
         Object obj8 = it.key().namespace();
         Object obj9 = map.get(obj8);
         Object obj2;
         if (obj9 == null) {
            int referenceId = 0;
            Object answer = (List)(new ArrayList());
            map.put(obj8, answer);
            obj2 = answer;
         } else {
            obj2 = obj9;
         }

         List list2 = (List)obj2;
         list2.add(obj7);
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value8 = 0;
         Object obj3 = entry.getKey();
         String namespace = (String)obj3;
         List soundEvents = (List)entry.getValue();
         ((Collection)linkedHashSet2).add(SoundRegistry.soundRegistry(namespace, (Collection)soundEvents));
      }

      return linkedHashSet;
   }

   @NotNull
   public final LinkedHashSet<SoundRegistry> customSoundRegistries() {
      return new LinkedHashSet((Collection)this.customSoundRegistries);
   }
}
