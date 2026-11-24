package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepUpgrades implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      UpgradeData var2 = (UpgradeData)var1.getOldMMOItem().getData(ItemStats.UPGRADE);
      UpgradeData var3 = (UpgradeData)var1.getNewMMOItem().getData(ItemStats.UPGRADE);
      if (var1.getOptions().shouldKeepUpgrades() && var2 != null && var3 != null && var3.getMaxUpgrades() > 0) {
         UpgradeData var4 = new UpgradeData(
            var3.getReference(), var3.getTemplateName(), var3.isWorkbench(), var3.isDestroy(), var3.getMax(), var3.getMin(), var3.getSuccess()
         );
         var4.setLevel(Math.min(var2.getLevel(), var3.getMaxUpgrades()));
         var1.getNewMMOItem().setData(ItemStats.UPGRADE, var4);
      }
   }
}
