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
   public void onReforge(MMOItemReforgeEvent var1) {
      if (!var1.getOptions().shouldReRoll()) {
         var1.getOldMMOItem().getStats().forEach(var1x -> {
            RandomStatData var2 = var1.getReforger().getTemplate().getBaseItemData().get(var1x);
            if (var2 != null && var2 instanceof UpdatableRandomStatData) {
               StatHistory var3 = var1.getOldMMOItem().computeStatHistory((ItemStat<?, ?>)var1x);
               StatData var4 = ((UpdatableRandomStatData)var2).reroll(var1x, var3.getOriginalData(), var1.getReforger().getGenerationItemLevel());
               if (var4 != null) {
                  StatHistory var5 = var1.getNewMMOItem().computeStatHistory((ItemStat<?, ?>)var1x);
                  var5.setOriginalData(var4);
               }
            }
         });
      }
   }
}
