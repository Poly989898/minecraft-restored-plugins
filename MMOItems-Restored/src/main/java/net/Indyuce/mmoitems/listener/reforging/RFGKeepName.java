package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepName implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepName()) {
         NameData nameData;
         if (event.getOldMMOItem().hasData(ItemStats.NAME)) {
            NameData data = (NameData)event.getOldMMOItem().getData(ItemStats.NAME);
            nameData = new NameData(data.getMainName());
         } else {
            if (!event.getReforger().getStack().getItemMeta().hasDisplayName()) {
               return;
            }

            nameData = new NameData(event.getReforger().getStack().getItemMeta().getDisplayName());
         }

         StatHistory statHistory = event.getNewMMOItem().computeStatHistory(ItemStats.NAME);
         ((NameData)statHistory.getOriginalData()).setString(nameData.getMainName());
      }
   }
}
