package net.Indyuce.mmoitems.stat.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class SoundListData implements StatData, Mergeable<SoundListData>, RandomStatData<SoundListData> {
   private final Map<CustomSound, SoundData> sounds = new LinkedHashMap<>();

   public SoundListData() {
   }

   public SoundListData(@NotNull Map<CustomSound, SoundData> map) {
      map.forEach(this.sounds::put);
   }

   public Set<CustomSound> getCustomSounds() {
      return this.sounds.keySet();
   }

   public Map<CustomSound, SoundData> mapData() {
      return this.sounds;
   }

   @Nullable
   public SoundData get(CustomSound customSound) {
      return this.sounds.getOrDefault(customSound, null);
   }

   public void set(CustomSound customSound, SoundData soundData) {
      this.sounds.put(customSound, soundData);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof SoundListData)) {
         return false;
      }

      if (((SoundListData)obj).getCustomSounds().size() != this.getCustomSounds().size()) {
         return false;
      }

      for (CustomSound customSound : ((SoundListData)obj).getCustomSounds()) {
         if (customSound != null) {
            boolean flag = true;

            for (CustomSound soundEntry : this.getCustomSounds()) {
               if (customSound.equals(soundEntry)) {
                  flag = false;
                  break;
               }
            }

            if (flag) {
               return false;
            }
         }
      }

      return true;
   }

   public void mergeWith(SoundListData soundListData) {
      soundListData.sounds.forEach(this.sounds::put);
   }

   @NotNull
   public SoundListData clone() {
      return new SoundListData(this.mapData());
   }

   @Override
   public boolean isEmpty() {
      return this.sounds.isEmpty();
   }

   public SoundListData randomize(MMOItemBuilder mmoItemBuilder) {
      return new SoundListData(new HashMap<>(this.sounds));
   }
}
