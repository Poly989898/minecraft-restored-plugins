package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.MMOItems;

public class RequiredLevelData extends DoubleData {
   public RequiredLevelData(double var1) {
      super(var1);
   }

   @Override
   public void mergeWith(DoubleData var1) {
      boolean var2 = MMOItems.plugin.getConfig().getBoolean("stat-merging.additive-levels", false);
      this.setValue(var2 ? var1.getValue() + this.getValue() : Math.max(var1.getValue(), this.getValue()));
   }

   @Override
   public DoubleData clone() {
      return new RequiredLevelData(this.getValue());
   }
}
