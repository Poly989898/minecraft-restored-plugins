package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;

public class CMIPlayerJailEvent extends CMIUserCancellableEvent {
  private CMIJailCell cell;

  public CMIPlayerJailEvent(CMIUser cmiUser, CMIJailCell cmiJailCell) {
    super(cmiUser);
    this.cell = cmiJailCell;
  }

  public CMIJailCell getCell() {
    return this.cell;
  }

  public void setCell(CMIJailCell cmiJailCell) {
    this.cell = cmiJailCell;
  }
}
