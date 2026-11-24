package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepSoulbound implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      SoulboundData data = (SoulboundData)event.getOldMMOItem().getData(ItemStats.SOULBOUND);
      if (data != null && event.getOptions().shouldKeepSoulBind()) {
         event.getNewMMOItem().setData(ItemStats.SOULBOUND, data);
      }
   }
}
