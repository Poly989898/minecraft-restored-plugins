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
   public RequiredLevelStat(boolean flag, String text, Material material, String text2, String[] texts) {
      super(text, material, "Required " + text2, texts, new String[]{"!block", "all"});
   }

   public RequiredLevelStat(String text, Material material, String text2, String[] texts) {
      super("REQUIRED_" + text, material, "Required " + text2, texts, new String[]{"!block", "all"});
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      int count = (int)doubleData.getValue();
      String text = this.getGeneralStatFormat().replace("{value}", String.valueOf(count));
      itemStackBuilder.getLore().insert(this.getPath(), text);
      itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), count));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      double value = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
      double upperBound = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
      if (!(upperBound < 0.0) || this.handleNegativeStats()) {
         if (value < 0.0 && !this.handleNegativeStats()) {
            value = 0.0;
         }

         if (value < numericStatFormula.getBase() - numericStatFormula.getMaxSpread()) {
            value = numericStatFormula.getBase() - numericStatFormula.getMaxSpread();
         }

         if (upperBound > numericStatFormula.getBase() + numericStatFormula.getMaxSpread()) {
            upperBound = numericStatFormula.getBase() + numericStatFormula.getMaxSpread();
         }

         itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
         if (value != 0.0 || upperBound != 0.0) {
            String text = DoubleStat.formatPath(this.getPath(), this.getGeneralStatFormat(), false, false, Math.floor(value), Math.floor(upperBound));
            itemStackBuilder.getLore().insert(this.getPath(), text);
         }
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), doubleData.getValue()));
      return list;
   }

   public RandomRequiredLevelData whenInitialized(Object obj) {
      return new RandomRequiredLevelData(obj);
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE));
      }

      RequiredLevelData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public RequiredLevelData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new RequiredLevelData((Double)tagAtPath.getValue()) : null;
   }

   public RequiredLevelData getClearStatData() {
      return new RequiredLevelData(0.0);
   }
}
