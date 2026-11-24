package net.Indyuce.mmoitems.stat.data.random;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.AbilityListData;

public class RandomAbilityListData implements RandomStatData<AbilityListData> {
   private final Set<RandomAbilityData> abilities = new LinkedHashSet<>();

   public RandomAbilityListData(RandomAbilityData... var1) {
      this.add(var1);
   }

   public void add(RandomAbilityData... var1) {
      this.abilities.addAll(Arrays.asList(var1));
   }

   public Set<RandomAbilityData> getAbilities() {
      return this.abilities;
   }

   public AbilityListData randomize(MMOItemBuilder var1) {
      AbilityListData var2 = new AbilityListData();
      this.abilities.forEach(var2x -> var2.add(var2x.randomize(var1)));
      return var2;
   }
}
