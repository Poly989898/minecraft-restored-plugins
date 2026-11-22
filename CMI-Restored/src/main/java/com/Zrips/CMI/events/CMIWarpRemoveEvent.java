package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Warps.CmiWarp;

public class CMIWarpRemoveEvent extends CMIEvent {
  private CmiWarp warp;

  public CMIWarpRemoveEvent(CmiWarp cmiWarp) {
    this.warp = cmiWarp;
  }

  public CmiWarp getWarp() {
    return this.warp;
  }
}
