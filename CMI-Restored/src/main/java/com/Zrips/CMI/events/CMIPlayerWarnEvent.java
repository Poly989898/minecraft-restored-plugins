package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;

public class CMIPlayerWarnEvent extends CMIUserCancellableEvent {
  private CMIPlayerWarning warning;

  public CMIPlayerWarnEvent(CMIUser cmiUser, CMIPlayerWarning cmiPlayerWarning) {
    super(cmiUser);
    this.warning = cmiPlayerWarning;
  }

  public CMIPlayerWarning getWarning() {
    return this.warning;
  }
}
