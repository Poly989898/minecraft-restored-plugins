package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Homes.CmiHome;

public class CMIUserHomeRemoveEvent extends CMIUserCancellableEvent {
  private CmiHome home;

  public CMIUserHomeRemoveEvent(CMIUser cmiUser, CmiHome cmiHome) {
    super(cmiUser);
    this.home = cmiHome;
  }

  public CmiHome getHome() {
    return this.home;
  }
}
