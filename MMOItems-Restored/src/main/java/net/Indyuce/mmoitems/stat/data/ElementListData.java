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

   public double getStat(Element var1, ElementStatType var2) {
      Double var3 = this.stats.get(Pair.of(var1, var2));
      return var3 == null ? 0.0 : var3;
   }

   public Set<Pair<Element, ElementStatType>> getKeys() {
      return this.stats.keySet();
   }

   public void setStat(Element var1, ElementStatType var2, double var3) {
      this.stats.put(Pair.of(var1, var2), var3);
   }

   public void mergeWith(@NotNull ElementListData var1) {
      var1.stats.forEach((var1x, var2) -> this.stats.put((Pair<Element, ElementStatType>)var1x, var2 + this.stats.getOrDefault(var1x, 0.0)));
   }

   @NotNull
   public ElementListData clone() {
      ElementListData var1 = new ElementListData();

      for (Entry var3 : this.stats.entrySet()) {
         Pair var4 = (Pair)var3.getKey();
         Double var5 = (Double)var3.getValue();
         if (var5 != 0.0) {
            var1.stats.put(var4, var5);
         }
      }

      return var1;
   }

   @Override
   public boolean isEmpty() {
      return this.stats.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ElementListData var2 = (ElementListData)var1;
         return this.stats.equals(var2.stats);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.stats);
   }
}
