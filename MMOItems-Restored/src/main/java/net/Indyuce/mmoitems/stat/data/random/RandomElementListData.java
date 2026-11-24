package net.Indyuce.mmoitems.stat.data.random;

import io.lumine.mythic.lib.element.Element;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.ElementListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.ElementStatType;
import net.Indyuce.mmoitems.util.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class RandomElementListData implements RandomStatData<ElementListData>, UpdatableRandomStatData<ElementListData> {
   private final Map<Pair<Element, ElementStatType>, NumericStatFormula> stats = new LinkedHashMap<>();

   public RandomElementListData(ConfigurationSection var1) {
      Validate.notNull(var1, "Config cannot be null");

      for (Element var3 : Element.values()) {
         for (ElementStatType var7 : ElementStatType.values()) {
            String var8 = var7.getConcatenatedConfigPath(var3);
            if (var1.contains(var8)) {
               this.stats.put(Pair.of(var3, var7), new NumericStatFormula(var1.get(var8)));
            }
         }
      }
   }

   public boolean hasStat(Element var1, ElementStatType var2) {
      return this.stats.containsKey(Pair.of(var1, var2));
   }

   @NotNull
   public NumericStatFormula getStat(Element var1, ElementStatType var2) {
      return this.stats.getOrDefault(Pair.of(var1, var2), NumericStatFormula.ZERO);
   }

   public Set<Pair<Element, ElementStatType>> getKeys() {
      return this.stats.keySet();
   }

   public void setStat(Element var1, ElementStatType var2, NumericStatFormula var3) {
      this.stats.put(Pair.of(var1, var2), var3);
   }

   public ElementListData randomize(MMOItemBuilder var1) {
      ElementListData var2 = new ElementListData();
      this.stats.forEach((var2x, var3) -> var2.setStat(var2x.getKey(), var2x.getValue(), var3.calculate(var1.getLevel())));
      return var2;
   }

   @NotNull
   public ElementListData reroll(@NotNull ItemStat var1, @NotNull ElementListData var2, int var3) {
      ElementListData var4 = new ElementListData();
      ElementListData var5 = var2;

      for (Element var7 : Element.values()) {
         for (ElementStatType var11 : ElementStatType.values()) {
            NumericStatFormula var12 = this.getStat(var7, var11);
            DoubleData var13 = new DoubleData(var5.getStat(var7, var11));
            DoubleData var14 = var12.reroll(var1, var13, var3);
            var4.setStat(var7, var11, var14.getValue());
         }
      }

      return var4;
   }
}
