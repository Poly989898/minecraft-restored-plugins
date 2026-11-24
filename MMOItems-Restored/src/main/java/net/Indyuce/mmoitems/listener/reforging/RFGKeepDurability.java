package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepDurability implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      MMOItem newMMOItem = event.getNewMMOItem();
      LiveMMOItem oldMMOItem = event.getOldMMOItem();
      if (!event.getNewMMOItem().hasData(ItemStats.UNBREAKABLE) || !((BooleanData)event.getNewMMOItem().getData(ItemStats.UNBREAKABLE)).isEnabled()) {
         StatData data = oldMMOItem.getData(ItemStats.CUSTOM_DURABILITY);
         if (data != null) {
            newMMOItem.setData(ItemStats.CUSTOM_DURABILITY, data);
         }

         StatData data2 = oldMMOItem.getData(ItemStats.ITEM_DAMAGE);
         if (data2 != null) {
            newMMOItem.setData(ItemStats.ITEM_DAMAGE, data2);
         }
      }
   }
}
