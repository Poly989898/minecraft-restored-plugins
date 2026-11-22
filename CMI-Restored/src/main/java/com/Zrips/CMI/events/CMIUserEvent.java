package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;

public class CMIUserEvent extends CMIEvent {
  private final CMIUser user;

  public CMIUserEvent(CMIUser cmiUser, boolean flag) {
    super(flag);
    this.user = cmiUser;
  }

  public CMIUserEvent(CMIUser cmiUser) {
    this(cmiUser, false);
  }

  public CMIUser getUser() {
    return this.user;
  }
}
