package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepName implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepName()) {
         NameData var2;
         if (var1.getOldMMOItem().hasData(ItemStats.NAME)) {
            NameData var3 = (NameData)var1.getOldMMOItem().getData(ItemStats.NAME);
            var2 = new NameData(var3.getMainName());
         } else {
            if (!var1.getReforger().getStack().getItemMeta().hasDisplayName()) {
               return;
            }

            var2 = new NameData(var1.getReforger().getStack().getItemMeta().getDisplayName());
         }

         StatHistory var4 = var1.getNewMMOItem().computeStatHistory(ItemStats.NAME);
         ((NameData)var4.getOriginalData()).setString(var2.getMainName());
      }
   }
}
