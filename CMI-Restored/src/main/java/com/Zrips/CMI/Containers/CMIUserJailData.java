package com.Zrips.CMI.Containers;

import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import java.util.UUID;

public class CMIUserJailData {
  private long jailedForTime = 0L;
  private String jailedReason;
  private UUID jailedBy = null;
  private CMIJailCell cell;

  public long getJailedForTime() {
    return this.jailedForTime;
  }

  public void setJailedForTime(long longValue) {
    this.jailedForTime = longValue;
  }

  public String getJailedReason() {
    return this.jailedReason;
  }

  public void setJailedReason(String string) {
    this.jailedReason = string;
  }

  public UUID getJailedBy() {
    return this.jailedBy;
  }

  public void setJailedBy(UUID uuid) {
    this.jailedBy = uuid;
  }

  public CMIJailCell getCell() {
    return this.cell;
  }

  public void setCell(CMIJailCell cmiJailCell) {
    this.cell = cmiJailCell;
  }
}
