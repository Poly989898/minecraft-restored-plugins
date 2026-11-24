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

   public RandomElementListData(ConfigurationSection section) {
      Validate.notNull(section, "Config cannot be null");

      for (Element element : Element.values()) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            String concatenatedConfigPath = elementStatType.getConcatenatedConfigPath(element);
            if (section.contains(concatenatedConfigPath)) {
               this.stats.put(Pair.of(element, elementStatType), new NumericStatFormula(section.get(concatenatedConfigPath)));
            }
         }
      }
   }

   public boolean hasStat(Element element, ElementStatType elementStatType) {
      return this.stats.containsKey(Pair.of(element, elementStatType));
   }

   @NotNull
   public NumericStatFormula getStat(Element element, ElementStatType elementStatType) {
      return this.stats.getOrDefault(Pair.of(element, elementStatType), NumericStatFormula.ZERO);
   }

   public Set<Pair<Element, ElementStatType>> getKeys() {
      return this.stats.keySet();
   }

   public void setStat(Element element, ElementStatType elementStatType, NumericStatFormula numericStatFormula) {
      this.stats.put(Pair.of(element, elementStatType), numericStatFormula);
   }

   public ElementListData randomize(MMOItemBuilder mmoItemBuilder) {
      ElementListData elementListData = new ElementListData();
      this.stats.forEach((var2x, numericStatFormula) -> elementListData.setStat(var2x.getKey(), var2x.getValue(), numericStatFormula.calculate(mmoItemBuilder.getLevel())));
      return elementListData;
   }

   @NotNull
   public ElementListData reroll(@NotNull ItemStat itemStat, @NotNull ElementListData elementListData, int count) {
      ElementListData elementListData2 = new ElementListData();
      ElementListData elementListData3 = elementListData;

      for (Element element : Element.values()) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            NumericStatFormula stat = this.getStat(element, elementStatType);
            DoubleData doubleData = new DoubleData(elementListData3.getStat(element, elementStatType));
            DoubleData doubleData2 = stat.reroll(itemStat, doubleData, count);
            elementListData2.setStat(element, elementStatType, doubleData2.getValue());
         }
      }

      return elementListData2;
   }
}
