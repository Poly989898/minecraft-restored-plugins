package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.element.Element;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.util.ElementStatType;
import net.Indyuce.mmoitems.util.Pair;
import org.jetbrains.annotations.NotNull;

public class ElementListData implements StatData, Mergeable<ElementListData> {
   private final Map<Pair<Element, ElementStatType>, Double> stats = new LinkedHashMap<>();

   public double getStat(Element element, ElementStatType elementStatType) {
      Double value = this.stats.get(Pair.of(element, elementStatType));
      return value == null ? 0.0 : value;
   }

   public Set<Pair<Element, ElementStatType>> getKeys() {
      return this.stats.keySet();
   }

   public void setStat(Element element, ElementStatType elementStatType, double value) {
      this.stats.put(Pair.of(element, elementStatType), value);
   }

   public void mergeWith(@NotNull ElementListData elementListData) {
      elementListData.stats.forEach((var1x, value) -> this.stats.put((Pair<Element, ElementStatType>)var1x, value + this.stats.getOrDefault(var1x, 0.0)));
   }

   @NotNull
   public ElementListData clone() {
      ElementListData elementListData = new ElementListData();

      for (Entry entry : this.stats.entrySet()) {
         Pair key = (Pair)entry.getKey();
         Double value = (Double)entry.getValue();
         if (value != 0.0) {
            elementListData.stats.put(key, value);
         }
      }

      return elementListData;
   }

   @Override
   public boolean isEmpty() {
      return this.stats.isEmpty();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ElementListData elementListData = (ElementListData)obj;
         return this.stats.equals(elementListData.stats);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.stats);
   }
}
