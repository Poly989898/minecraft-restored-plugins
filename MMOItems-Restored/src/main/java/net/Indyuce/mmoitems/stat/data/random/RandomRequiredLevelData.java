package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;

public class RandomRequiredLevelData extends NumericStatFormula {
   public RandomRequiredLevelData(Object obj) {
      super(obj);
   }

   public RandomRequiredLevelData(double value, double value2, double value3, double value4) {
      super(value, value2, value3, value4);
   }

   public RequiredLevelData randomize(MMOItemBuilder mmoItemBuilder) {
      return new RequiredLevelData(this.calculate(mmoItemBuilder.getLevel()));
   }
}
