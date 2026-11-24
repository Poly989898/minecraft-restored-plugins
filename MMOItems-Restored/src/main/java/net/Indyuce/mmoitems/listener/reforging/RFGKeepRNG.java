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
         event.getOldMMOItem().getStats().forEach(var1x -> {
            RandomStatData randomStatData = event.getReforger().getTemplate().getBaseItemData().get(var1x);
            if (randomStatData != null && randomStatData instanceof UpdatableRandomStatData) {
               StatHistory statHistory = event.getOldMMOItem().computeStatHistory((ItemStat<?, ?>)var1x);
               StatData statData = ((UpdatableRandomStatData)randomStatData).reroll(var1x, statHistory.getOriginalData(), event.getReforger().getGenerationItemLevel());
               if (statData != null) {
                  StatHistory statHistory2 = event.getNewMMOItem().computeStatHistory((ItemStat<?, ?>)var1x);
                  statHistory2.setOriginalData(statData);
               }
            }
         });
      }
   }
}
