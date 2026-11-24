package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.MMOItems;

public class RequiredLevelData extends DoubleData {
   public RequiredLevelData(double value) {
      super(value);
   }

   @Override
   public void mergeWith(DoubleData doubleData) {
      boolean flag = MMOItems.plugin.getConfig().getBoolean("stat-merging.additive-levels", false);
      this.setValue(flag ? doubleData.getValue() + this.getValue() : Math.max(doubleData.getValue(), this.getValue()));
   }

   @Override
   public DoubleData clone() {
      return new RequiredLevelData(this.getValue());
   }
}
