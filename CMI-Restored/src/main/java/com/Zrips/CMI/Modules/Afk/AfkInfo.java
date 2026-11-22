package com.Zrips.CMI.Modules.Afk;

import com.Zrips.CMI.events.CMIAfkEnterEvent;
import net.Zrips.CMILib.Container.CMIVectorInt3D;

public class AfkInfo {
  private long afkFrom = 0L;
  private int kicksPerformed = 0;
  private CMIAfkEnterEvent.AfkType type;
  private String reason;
  private long playtimeAtAfkStart = 0L;
  private CMIVectorInt3D afkAt = null;
  private int kickOutIn = 0;

  public long getAfkFrom() {
    return this.afkFrom;
  }

  public void setAfkFrom(Long afkFromMillis) {
    this.afkFrom = afkFromMillis == null ? 0L : afkFromMillis;
  }

  public int getKicksPerformed() {
    return this.kicksPerformed;
  }

  public void setKicksPerformed(int value) {
    this.kicksPerformed = value;
  }

  public void addKicksPerformed() {
    this.kicksPerformed++;
  }

  public CMIAfkEnterEvent.AfkType getType() {
    return this.type == null ? CMIAfkEnterEvent.AfkType.auto : this.type;
  }

  public void setType(CMIAfkEnterEvent.AfkType afkType) {
    this.type = afkType;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String string) {
    this.reason = string;
  }

  public long getPlaytimeAtAfkStart() {
    return this.playtimeAtAfkStart;
  }

  public void setPlaytimeAtAfkStart(Long playtimeMillis) {
    this.playtimeAtAfkStart = playtimeMillis == null ? 0L : playtimeMillis / 1000L * 1000L;
  }

  public int getKickOutInSec() {
    return this.kickOutIn;
  }

  public void setKickOutInSec(int value) {
    this.kickOutIn = value;
  }

  public CMIVectorInt3D getAfkAt() {
    return this.afkAt;
  }

  public void setAfkAt(CMIVectorInt3D cmiVectorInt3D) {
    this.afkAt = cmiVectorInt3D;
  }
}
