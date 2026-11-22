package com.Zrips.CMI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class CMIPlayerCancellableEvent extends CMIPlayerEvent implements Cancellable {
  private boolean cancel = false;

  public CMIPlayerCancellableEvent(Player player, boolean flag) {
    super(player, flag);
  }

  public CMIPlayerCancellableEvent(Player player) {
    super(player);
  }

  public final void setCancelled(boolean flag) {
    this.cancel = flag;
  }

  public final boolean isCancelled() {
    return this.cancel;
  }
}
