package net.Indyuce.mmoitems.listener.reforging;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RFGKeepLore implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepLore()) {
         StringListData var2 = (StringListData)var1.getOldMMOItem().getData(ItemStats.LORE);
         if (var2 != null) {
            ArrayList<String> var3 = this.extractLore(var2.getList(), var1.getOptions().getKeepCase());
            if (var3.size() != 0) {
               StatHistory var4 = var1.getNewMMOItem().computeStatHistory(ItemStats.LORE);
               StringListData var5 = (StringListData)var4.getOriginalData();

               for (String var7 : var3) {
                  var5.getList().add(var7);
               }

               var4.setOriginalData(var5);
            }
         }
      }
   }

   @NotNull
   ArrayList<String> extractLore(@NotNull List<String> var1, @NotNull String var2) {
      ArrayList var3 = new ArrayList();

      for (String var5 : var1) {
         if (var5.startsWith(var2)) {
            var3.add(var5);
         }
      }

      return var3;
   }
}
