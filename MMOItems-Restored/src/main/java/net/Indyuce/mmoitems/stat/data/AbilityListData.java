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

   public AbilityListData(@NotNull AbilityData... var1) {
      this.add(var1);
   }

   public AbilityListData(@NotNull Collection<AbilityData> var1) {
      this.add(var1);
   }

   public void add(@NotNull AbilityData... var1) {
      Collections.addAll(this.abilities, var1);
   }

   public void add(@NotNull Collection<AbilityData> var1) {
      this.abilities.addAll(var1);
   }

   @NotNull
   public List<AbilityData> getAbilities() {
      return this.abilities;
   }

   public void mergeWith(@NotNull AbilityListData var1) {
      this.abilities.addAll(var1.abilities);
   }

   @NotNull
   public AbilityListData clone() {
      return new AbilityListData(this.abilities);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof AbilityListData)) {
         return false;
      }

      if (this.getAbilities().size() != ((AbilityListData)var1).getAbilities().size()) {
         return false;
      }

      for (AbilityData var3 : ((AbilityListData)var1).getAbilities()) {
         if (var3 != null) {
            boolean var4 = true;

            for (AbilityData var6 : this.getAbilities()) {
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

   @Override
   public String toString() {
      return "AbilityListData{abilities=" + this.abilities + '}';
   }

   @Override
   public boolean isEmpty() {
      return this.abilities.isEmpty();
   }
}
