package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.random.UpdatableRandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepRNG implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (!event.getOptions().shouldReRoll()) {
         event.getOldMMOItem().getStats().forEach(stat -> {
            RandomStatData randomStatData = event.getReforger().getTemplate().getBaseItemData().get(stat);
            if (randomStatData != null && randomStatData instanceof UpdatableRandomStatData) {
               StatHistory statHistory = event.getOldMMOItem().computeStatHistory((ItemStat<?, ?>)stat);
               StatData statData = ((UpdatableRandomStatData)randomStatData).reroll(stat, statHistory.getOriginalData(), event.getReforger().getGenerationItemLevel());
               if (statData != null) {
                  StatHistory newHistory = event.getNewMMOItem().computeStatHistory((ItemStat<?, ?>)stat);
                  newHistory.setOriginalData(statData);
               }
            }
         });
      }
   }
}
