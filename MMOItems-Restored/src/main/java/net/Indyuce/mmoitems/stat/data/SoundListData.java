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

   public SoundListData(@NotNull Map<CustomSound, SoundData> var1) {
      var1.forEach(this.sounds::put);
   }

   public Set<CustomSound> getCustomSounds() {
      return this.sounds.keySet();
   }

   public Map<CustomSound, SoundData> mapData() {
      return this.sounds;
   }

   @Nullable
   public SoundData get(CustomSound var1) {
      return this.sounds.getOrDefault(var1, null);
   }

   public void set(CustomSound var1, SoundData var2) {
      this.sounds.put(var1, var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof SoundListData)) {
         return false;
      }

      if (((SoundListData)var1).getCustomSounds().size() != this.getCustomSounds().size()) {
         return false;
      }

      for (CustomSound var3 : ((SoundListData)var1).getCustomSounds()) {
         if (var3 != null) {
            boolean var4 = true;

            for (CustomSound var6 : this.getCustomSounds()) {
               if (var3.equals(var6)) {
                  var4 = false;
                  break;
               }
            }

            if (var4) {
               return false;
            }
         }
      }

      return true;
   }

   public void mergeWith(SoundListData var1) {
      var1.sounds.forEach(this.sounds::put);
   }

   @NotNull
   public SoundListData clone() {
      return new SoundListData(this.mapData());
   }

   @Override
   public boolean isEmpty() {
      return this.sounds.isEmpty();
   }

   public SoundListData randomize(MMOItemBuilder var1) {
      return new SoundListData(new HashMap<>(this.sounds));
   }
}
