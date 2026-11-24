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
      UpgradeData newUpgradeData = (UpgradeData)event.getNewMMOItem().getData(ItemStats.UPGRADE);
      if (event.getOptions().shouldKeepUpgrades() && data != null && newUpgradeData != null && newUpgradeData.getMaxUpgrades() > 0) {
         UpgradeData upgradeData = new UpgradeData(
            newUpgradeData.getReference(), newUpgradeData.getTemplateName(), newUpgradeData.isWorkbench(), newUpgradeData.isDestroy(), newUpgradeData.getMax(), newUpgradeData.getMin(), newUpgradeData.getSuccess()
         );
         upgradeData.setLevel(Math.min(data.getLevel(), newUpgradeData.getMaxUpgrades()));
         event.getNewMMOItem().setData(ItemStats.UPGRADE, upgradeData);
      }
   }
}
