package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepSoulbound implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      SoulboundData var2 = (SoulboundData)var1.getOldMMOItem().getData(ItemStats.SOULBOUND);
      if (var2 != null && var1.getOptions().shouldKeepSoulBind()) {
         var1.getNewMMOItem().setData(ItemStats.SOULBOUND, var2);
      }
   }
}
