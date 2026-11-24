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
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
   }

   @NotNull
   public static HashMap<Double, ArrayList<MMOItemTemplate>> select(@NotNull Collection<MMOItemTemplate> var0) {
      HashMap var1 = new HashMap();

      for (MMOItemTemplate var3 : var0) {
         if (var3 != null) {
            Double var4 = null;
            if (var3.getType().getAvailableStats().contains(ItemStats.BROWSER_DISPLAY_IDX)) {
               NumericStatFormula var5 = (NumericStatFormula)var3.getBaseItemData().get(ItemStats.BROWSER_DISPLAY_IDX);
               if (var5 != null && var5.getBase() != 0.0) {
                  var4 = var5.getBase();
               }
            }

            ArrayList var6 = (ArrayList)var1.get(var4);
            if (var6 == null) {
               var6 = new ArrayList();
            }

            var6.add(var3);
            var1.put(var4, var6);
         }
      }

      return var1;
   }

   @Nullable
   public static MMOItemTemplate getAt(int var0, @NotNull HashMap<Double, ArrayList<MMOItemTemplate>> var1) {
      Entry<Double, ArrayList<MMOItemTemplate>> var2 = null;

      for (Entry<Double, ArrayList<MMOItemTemplate>> var4 : var1.entrySet()) {
         if (var4.getKey() == null) {
            var2 = var4;
         } else {
            ArrayList<MMOItemTemplate> var5 = (ArrayList)var4.getValue();

            while (var5.size() % 4 != 0) {
               var5.add(null);
            }

            for (MMOItemTemplate var7 : var5) {
               if (var0 == 0) {
                  return var7;
               }

               var0--;
            }
         }
      }

      if (var2 == null) {
         return null;
      }

      for (MMOItemTemplate var10 : var2.getValue()) {
         if (var0 == 0) {
            return var10;
         }

         var0--;
      }

      return null;
   }
}
