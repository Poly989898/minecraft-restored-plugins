package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;
import net.Indyuce.mmoitems.stat.data.random.RandomRequiredLevelData;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RequiredLevelStat extends DoubleStat implements ItemRestriction, GemStoneStat {
   @Deprecated
   public RequiredLevelStat(boolean var1, String var2, Material var3, String var4, String[] var5) {
      super(var2, var3, "Required " + var4, var5, new String[]{"!block", "all"});
   }

   public RequiredLevelStat(String var1, Material var2, String var3, String[] var4) {
      super("REQUIRED_" + var1, var2, "Required " + var3, var4, new String[]{"!block", "all"});
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      int var3 = (int)var2.getValue();
      String var4 = this.getGeneralStatFormat().replace("{value}", String.valueOf(var3));
      var1.getLore().insert(this.getPath(), var4);
      var1.addItemTag(new ItemTag(this.getNBTPath(), var3));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
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

         var1.addItemTag(this.getAppliedNBT(var2));
         if (var4 != 0.0 || var6 != 0.0) {
            String var8 = DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), false, false, Math.floor(var4), Math.floor(var6));
            var1.getLore().insert(this.getPath(), var8);
         }
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.getValue()));
      return var2;
   }

   public RandomRequiredLevelData whenInitialized(Object var1) {
      return new RandomRequiredLevelData(var1);
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.DOUBLE));
      }

      RequiredLevelData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public RequiredLevelData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new RequiredLevelData((Double)var2.getValue()) : null;
   }

   public RequiredLevelData getClearStatData() {
      return new RequiredLevelData(0.0);
   }
}
