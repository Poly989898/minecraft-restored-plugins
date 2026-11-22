package com.Zrips.CMI.events;

import org.bukkit.event.Cancellable;

public class CMICancellableEvent extends CMIEvent implements Cancellable {
  private boolean cancel = false;

  public final void setCancelled(boolean flag) {
    this.cancel = flag;
  }

  public final boolean isCancelled() {
    return this.cancel;
  }
}
