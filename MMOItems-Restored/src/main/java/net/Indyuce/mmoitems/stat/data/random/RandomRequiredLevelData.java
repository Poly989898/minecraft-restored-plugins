package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;

public class RandomRequiredLevelData extends NumericStatFormula {
   public RandomRequiredLevelData(Object obj) {
      super(obj);
   }

   public RandomRequiredLevelData(double value, double scale, double spread, double maxSpread) {
      super(value, scale, spread, maxSpread);
   }

   public RequiredLevelData randomize(MMOItemBuilder mmoItemBuilder) {
      return new RequiredLevelData(this.calculate(mmoItemBuilder.getLevel()));
   }
}
