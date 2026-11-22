package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.tehkode.permissions.events.PermissionEntityEvent;

public class PEXListener implements Listener {
  private CMI plugin;

  public PEXListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void BlockPlaceEvent(PermissionEntityEvent value) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(value.getEntityIdentifier());
  }
}
