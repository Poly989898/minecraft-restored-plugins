package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@HasCategory(cat = "template_option")
public class BrowserDisplayIDX extends DoubleStat {
   public BrowserDisplayIDX() {
      super(
         "BROWSER_IDX",
         Material.GHAST_TEAR,
         "Browser Index",
         new String[]{"Used to display similar items together,", "neatly in the GUI §a/mmoitems browse", "", "Items with the same index are grouped."},
         new String[0]
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
   }

   @NotNull
   public static HashMap<Double, ArrayList<MMOItemTemplate>> select(@NotNull Collection<MMOItemTemplate> mmoItemTemplates) {
      HashMap map = new HashMap();

      for (MMOItemTemplate mmoItemTemplate : mmoItemTemplates) {
         if (mmoItemTemplate != null) {
            Double value = null;
            if (mmoItemTemplate.getType().getAvailableStats().contains(ItemStats.BROWSER_DISPLAY_IDX)) {
               NumericStatFormula numericStatFormula = (NumericStatFormula)mmoItemTemplate.getBaseItemData().get(ItemStats.BROWSER_DISPLAY_IDX);
               if (numericStatFormula != null && numericStatFormula.getBase() != 0.0) {
                  value = numericStatFormula.getBase();
               }
            }

            ArrayList list = (ArrayList)map.get(value);
            if (list == null) {
               list = new ArrayList();
            }

            list.add(mmoItemTemplate);
            map.put(value, list);
         }
      }

      return map;
   }

   @Nullable
   public static MMOItemTemplate getAt(int count, @NotNull HashMap<Double, ArrayList<MMOItemTemplate>> map) {
      Entry<Double, ArrayList<MMOItemTemplate>> entry = null;

      for (Entry<Double, ArrayList<MMOItemTemplate>> groupEntry : map.entrySet()) {
         if (groupEntry.getKey() == null) {
            entry = groupEntry;
         } else {
            ArrayList<MMOItemTemplate> mmoItemTemplates = (ArrayList)groupEntry.getValue();

            while (mmoItemTemplates.size() % 4 != 0) {
               mmoItemTemplates.add(null);
            }

            for (MMOItemTemplate mmoItemTemplate : mmoItemTemplates) {
               if (count == 0) {
                  return mmoItemTemplate;
               }

               count--;
            }
         }
      }

      if (entry == null) {
         return null;
      }

      for (MMOItemTemplate mmoItemTemplate : entry.getValue()) {
         if (count == 0) {
            return mmoItemTemplate;
         }

         count--;
      }

      return null;
   }
}
