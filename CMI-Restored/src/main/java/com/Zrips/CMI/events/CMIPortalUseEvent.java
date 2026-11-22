package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Portals.CMIPortal;
import org.bukkit.entity.Player;

public class CMIPortalUseEvent extends CMIPlayerCancellableEvent {
  private final CMIPortal portal;

  public CMIPortalUseEvent(Player player, CMIPortal cmiPortal) {
    super(player);
    this.portal = cmiPortal;
  }

  public CMIPortal getPortal() {
    return this.portal;
  }
}
