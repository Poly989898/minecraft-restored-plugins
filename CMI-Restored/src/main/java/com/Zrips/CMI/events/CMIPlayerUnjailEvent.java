package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;

public class CMIPlayerUnjailEvent extends CMIEvent {
  private CMIUser user;
  private CMIJailCell cell;

  public CMIPlayerUnjailEvent(CMIUser cmiUser, CMIJailCell cmiJailCell) {
    this.user = cmiUser;
    this.cell = cmiJailCell;
  }

  public CMIUser getUser() {
    return this.user;
  }

  public CMIJailCell getCell() {
    return this.cell;
  }
}
