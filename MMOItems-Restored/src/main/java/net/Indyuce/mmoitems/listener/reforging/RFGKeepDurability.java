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
   public void onReforge(MMOItemReforgeEvent var1) {
      MMOItem var2 = var1.getNewMMOItem();
      LiveMMOItem var3 = var1.getOldMMOItem();
      if (!var1.getNewMMOItem().hasData(ItemStats.UNBREAKABLE) || !((BooleanData)var1.getNewMMOItem().getData(ItemStats.UNBREAKABLE)).isEnabled()) {
         StatData var4 = var3.getData(ItemStats.CUSTOM_DURABILITY);
         if (var4 != null) {
            var2.setData(ItemStats.CUSTOM_DURABILITY, var4);
         }

         StatData var5 = var3.getData(ItemStats.ITEM_DAMAGE);
         if (var5 != null) {
            var2.setData(ItemStats.ITEM_DAMAGE, var5);
         }
      }
   }
}
