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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      int count = (int)doubleData.getValue();
      itemStackBuilder.addItemTag(new ItemTag("MMOITEMS_PICKAXE_POWER", count));
      itemStackBuilder.getLore().insert("pickaxe-power", DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), true, false, count));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      Validate.isTrue(doubleData instanceof DoubleData, "Current Data is not Double Data");
      Validate.isTrue(numericStatFormula instanceof NumericStatFormula, "Template Data is not Numeric Stat Formula");
      double value = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
      double value2 = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
      if (!(value2 < 0.0) || this.handleNegativeStats()) {
         if (value < 0.0 && !this.handleNegativeStats()) {
            value = 0.0;
         }

         if (value < numericStatFormula.getBase() - numericStatFormula.getMaxSpread()) {
            value = numericStatFormula.getBase() - numericStatFormula.getMaxSpread();
         }

         if (value2 > numericStatFormula.getBase() + numericStatFormula.getMaxSpread()) {
            value2 = numericStatFormula.getBase() + numericStatFormula.getMaxSpread();
         }

         itemStackBuilder.addItemTag(new ItemTag("MMOITEMS_PICKAXE_POWER", doubleData.getValue()));
         if (value != 0.0 || value2 != 0.0) {
            String text = DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), true, false, Math.floor(value), Math.floor(value2));
            itemStackBuilder.getLore().insert("pickaxe-power", text);
         }
      }
   }
}
