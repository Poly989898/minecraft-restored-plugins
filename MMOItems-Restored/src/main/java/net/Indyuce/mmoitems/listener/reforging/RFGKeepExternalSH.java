package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepExternalSH implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepExternalSH()) {
         for (StatHistory var3 : var1.getOldMMOItem().getStatHistories()) {
            if (var3.getItemStat() != ItemStats.ENCHANTS) {
               StatHistory var4 = var1.getNewMMOItem().computeStatHistory(var3.getItemStat());

               for (StatData var6 : var3.getExternalData()) {
                  var4.registerExternalData(var6);
               }
            }
         }
      }
   }
}
