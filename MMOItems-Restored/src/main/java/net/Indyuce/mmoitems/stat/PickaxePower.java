package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class PickaxePower extends DoubleStat {
   public PickaxePower() {
      super(
         "PICKAXE_POWER",
         Material.IRON_PICKAXE,
         "Pickaxe Power",
         new String[]{"The breaking strength of the", "item when mining custom blocks."},
         new String[]{"tool"}
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      int var3 = (int)var2.getValue();
      var1.addItemTag(new ItemTag("MMOITEMS_PICKAXE_POWER", var3));
      var1.getLore().insert("pickaxe-power", DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), true, false, var3));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
      Validate.isTrue(var2 instanceof DoubleData, "Current Data is not Double Data");
      Validate.isTrue(var3 instanceof NumericStatFormula, "Template Data is not Numeric Stat Formula");
      double var4 = var3.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
      double var6 = var3.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
      if (!(var6 < 0.0) || this.handleNegativeStats()) {
         if (var4 < 0.0 && !this.handleNegativeStats()) {
            var4 = 0.0;
         }

         if (var4 < var3.getBase() - var3.getMaxSpread()) {
            var4 = var3.getBase() - var3.getMaxSpread();
         }

         if (var6 > var3.getBase() + var3.getMaxSpread()) {
            var6 = var3.getBase() + var3.getMaxSpread();
         }

         var1.addItemTag(new ItemTag("MMOITEMS_PICKAXE_POWER", var2.getValue()));
         if (var4 != 0.0 || var6 != 0.0) {
            String var8 = DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), true, false, Math.floor(var4), Math.floor(var6));
            var1.getLore().insert("pickaxe-power", var8);
         }
      }
   }
}
