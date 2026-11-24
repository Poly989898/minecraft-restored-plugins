package net.Indyuce.mmoitems.stat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class PotionEffectListData implements StatData, Mergeable<PotionEffectListData> {
   private final List<PotionEffectData> effects = new ArrayList<>();

   public PotionEffectListData(PotionEffectData... var1) {
      this.add(var1);
   }

   public PotionEffectListData(Collection<PotionEffectData> var1) {
      this.add(var1);
   }

   public void add(PotionEffectData... var1) {
      for (PotionEffectData var5 : var1) {
         this.effects.add(var5);
      }
   }

   public void add(Collection<PotionEffectData> var1) {
      this.effects.addAll(var1);
   }

   @NotNull
   public List<PotionEffectData> getEffects() {
      return this.effects;
   }

   @Override
   public boolean isEmpty() {
      return this.effects.isEmpty();
   }

   public void mergeWith(PotionEffectListData var1) {
      this.effects.addAll(var1.effects);
   }

   @NotNull
   public PotionEffectListData clone() {
      return new PotionEffectListData(this.effects);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PotionEffectListData)) {
         return false;
      }

      if (((PotionEffectListData)var1).getEffects().size() != this.getEffects().size()) {
         return false;
      }

      for (PotionEffectData var3 : ((PotionEffectListData)var1).getEffects()) {
         if (var3 != null) {
            boolean var4 = true;

            for (PotionEffectData var6 : this.getEffects()) {
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
}
