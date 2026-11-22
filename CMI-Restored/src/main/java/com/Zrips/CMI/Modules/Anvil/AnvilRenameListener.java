package com.Zrips.CMI.Modules.Anvil;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIAnvilItemRenameEvent;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AnvilRenameListener implements Listener {
  private CMI plugin;

  public AnvilRenameListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void CMIItemRenameEvent(CMIAnvilItemRenameEvent cmiAnvilItemRenameEvent) {
    if (this.plugin
        .getUtilManager()
        .blockedItemFromRenaming(
            cmiAnvilItemRenameEvent.getPlayer(),
            cmiAnvilItemRenameEvent.getItemFrom(),
            cmiAnvilItemRenameEvent.getItemTo(),
            cmiAnvilItemRenameEvent.getItemTo().getItemMeta().getDisplayName())) {
      LC.info_cantRename.sendMessage(cmiAnvilItemRenameEvent.getPlayer(), new Object[0]);
      cmiAnvilItemRenameEvent.setCancelled(true);
    }
  }
}
