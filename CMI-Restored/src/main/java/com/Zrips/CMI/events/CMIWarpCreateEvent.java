package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Warps.CmiWarp;

public class CMIWarpCreateEvent extends CMICancellableEvent {
  private CmiWarp warp;

  public CMIWarpCreateEvent(CmiWarp cmiWarp) {
    this.warp = cmiWarp;
  }

  public CmiWarp getWarp() {
    return this.warp;
  }
}
