package net.Indyuce.mmoitems.stat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class AbilityListData implements StatData, Mergeable<AbilityListData> {
   private final List<AbilityData> abilities = new ArrayList<>();

   public AbilityListData(@NotNull AbilityData... abilityDatas) {
      this.add(abilityDatas);
   }

   public AbilityListData(@NotNull Collection<AbilityData> abilityDatas) {
      this.add(abilityDatas);
   }

   public void add(@NotNull AbilityData... abilityDatas) {
      Collections.addAll(this.abilities, abilityDatas);
   }

   public void add(@NotNull Collection<AbilityData> abilityDatas) {
      this.abilities.addAll(abilityDatas);
   }

   @NotNull
   public List<AbilityData> getAbilities() {
      return this.abilities;
   }

   public void mergeWith(@NotNull AbilityListData abilityListData) {
      this.abilities.addAll(abilityListData.abilities);
   }

   @NotNull
   public AbilityListData clone() {
      return new AbilityListData(this.abilities);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof AbilityListData)) {
         return false;
      }

      if (this.getAbilities().size() != ((AbilityListData)obj).getAbilities().size()) {
         return false;
      }

      for (AbilityData ability : ((AbilityListData)obj).getAbilities()) {
         if (ability != null) {
            boolean flag = true;

            for (AbilityData abilityEntry : this.getAbilities()) {
               if (ability.equals(abilityEntry)) {
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

   @Override
   public String toString() {
      return "AbilityListData{abilities=" + this.abilities + '}';
   }

   @Override
   public boolean isEmpty() {
      return this.abilities.isEmpty();
   }
}
