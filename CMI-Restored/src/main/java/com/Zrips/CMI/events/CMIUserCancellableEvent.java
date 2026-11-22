package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;

public class CMIUserCancellableEvent extends CMICancellableEvent {
  private final CMIUser user;

  public CMIUserCancellableEvent(CMIUser cmiUser) {
    this.user = cmiUser;
  }

  public CMIUser getUser() {
    return this.user;
  }
}
