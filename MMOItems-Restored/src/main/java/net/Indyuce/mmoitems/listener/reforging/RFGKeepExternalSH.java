package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepExternalSH implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepExternalSH()) {
         for (StatHistory statHistory : event.getOldMMOItem().getStatHistories()) {
            if (statHistory.getItemStat() != ItemStats.ENCHANTS) {
               StatHistory statHistory2 = event.getNewMMOItem().computeStatHistory(statHistory.getItemStat());

               for (StatData statData : statHistory.getExternalData()) {
                  statHistory2.registerExternalData(statData);
               }
            }
         }
      }
   }
}
