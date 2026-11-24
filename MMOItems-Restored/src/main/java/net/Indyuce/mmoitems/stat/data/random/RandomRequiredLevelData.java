package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;

public class RandomRequiredLevelData extends NumericStatFormula {
   public RandomRequiredLevelData(Object var1) {
      super(var1);
   }

   public RandomRequiredLevelData(double var1, double var3, double var5, double var7) {
      super(var1, var3, var5, var7);
   }

   public RequiredLevelData randomize(MMOItemBuilder var1) {
      return new RequiredLevelData(this.calculate(var1.getLevel()));
   }
}
