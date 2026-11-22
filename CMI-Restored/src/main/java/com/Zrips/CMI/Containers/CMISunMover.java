package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class CMISunMover {
  private CMITask task = null;
  private boolean moving = true;

  public CMITask getTimeMoverTask() {
    return this.task;
  }

  public void setTimeMoverTask(CMITask cmiTask) {
    this.task = cmiTask;
  }

  public boolean getMoving() {
    return this.moving;
  }

  public void setMoving(boolean flag) {
    this.moving = flag;
  }
}
