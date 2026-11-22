package com.Zrips.CMI.Containers;

public class CMIUserAlert {
  private long until = 0L;
  private String reason = null;

  public CMIUserAlert(long longValue) {
    this(longValue, null);
  }

  public CMIUserAlert(long longValue, String string) {
    this.until = longValue;
    this.reason = string;
  }

  public long getUntil() {
    return this.until;
  }

  public void setUntil(long longValue) {
    this.until = longValue;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String string) {
    this.reason = string;
  }
}
