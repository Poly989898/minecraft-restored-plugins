package net.Indyuce.mmoitems.stat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class PotionEffectListData implements StatData, Mergeable<PotionEffectListData> {
   private final List<PotionEffectData> effects = new ArrayList<>();

   public PotionEffectListData(PotionEffectData... potionEffectDatas) {
      this.add(potionEffectDatas);
   }

   public PotionEffectListData(Collection<PotionEffectData> potionEffectDatas) {
      this.add(potionEffectDatas);
   }

   public void add(PotionEffectData... potionEffectDatas) {
      for (PotionEffectData potionEffectData : potionEffectDatas) {
         this.effects.add(potionEffectData);
      }
   }

   public void add(Collection<PotionEffectData> potionEffectDatas) {
      this.effects.addAll(potionEffectDatas);
   }

   @NotNull
   public List<PotionEffectData> getEffects() {
      return this.effects;
   }

   @Override
   public boolean isEmpty() {
      return this.effects.isEmpty();
   }

   public void mergeWith(PotionEffectListData potionEffectListData) {
      this.effects.addAll(potionEffectListData.effects);
   }

   @NotNull
   public PotionEffectListData clone() {
      return new PotionEffectListData(this.effects);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PotionEffectListData)) {
         return false;
      }

      if (((PotionEffectListData)obj).getEffects().size() != this.getEffects().size()) {
         return false;
      }

      for (PotionEffectData effect : ((PotionEffectListData)obj).getEffects()) {
         if (effect != null) {
            boolean flag = true;

            for (PotionEffectData effect2 : this.getEffects()) {
               if (effect.equals(effect2)) {
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
}
