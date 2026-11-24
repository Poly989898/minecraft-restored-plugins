package net.Indyuce.mmoitems.stat.data.random;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.AbilityListData;

public class RandomAbilityListData implements RandomStatData<AbilityListData> {
   private final Set<RandomAbilityData> abilities = new LinkedHashSet<>();

   public RandomAbilityListData(RandomAbilityData... randomAbilityDatas) {
      this.add(randomAbilityDatas);
   }

   public void add(RandomAbilityData... randomAbilityDatas) {
      this.abilities.addAll(Arrays.asList(randomAbilityDatas));
   }

   public Set<RandomAbilityData> getAbilities() {
      return this.abilities;
   }

   public AbilityListData randomize(MMOItemBuilder mmoItemBuilder) {
      AbilityListData abilityListData = new AbilityListData();
      this.abilities.forEach(var2x -> abilityListData.add(var2x.randomize(mmoItemBuilder)));
      return abilityListData;
   }
}
