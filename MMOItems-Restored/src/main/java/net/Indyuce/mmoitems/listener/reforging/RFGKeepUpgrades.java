package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepUpgrades implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      UpgradeData data = (UpgradeData)event.getOldMMOItem().getData(ItemStats.UPGRADE);
      UpgradeData data2 = (UpgradeData)event.getNewMMOItem().getData(ItemStats.UPGRADE);
      if (event.getOptions().shouldKeepUpgrades() && data != null && data2 != null && data2.getMaxUpgrades() > 0) {
         UpgradeData upgradeData = new UpgradeData(
            data2.getReference(), data2.getTemplateName(), data2.isWorkbench(), data2.isDestroy(), data2.getMax(), data2.getMin(), data2.getSuccess()
         );
         upgradeData.setLevel(Math.min(data.getLevel(), data2.getMaxUpgrades()));
         event.getNewMMOItem().setData(ItemStats.UPGRADE, upgradeData);
      }
   }
}
