package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Kits.Kit;

public class CMIUserKitAcquireEvent extends CMIUserCancellableEvent {
  private Kit kit;
  private boolean giveItems;

  public CMIUserKitAcquireEvent(CMIUser cmiUser, Kit kit, boolean flag) {
    super(cmiUser);
    this.kit = kit;
    this.giveItems = flag;
  }

  public Kit getKit() {
    return this.kit;
  }

  public boolean isGiveItems() {
    return this.giveItems;
  }

  public void setGiveItems(boolean flag) {
    this.giveItems = flag;
  }
}
