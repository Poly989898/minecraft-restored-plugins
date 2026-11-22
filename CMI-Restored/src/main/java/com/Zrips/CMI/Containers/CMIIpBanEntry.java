package com.Zrips.CMI.Containers;

public class CMIIpBanEntry {
  private String ip;
  private Long banedAt;
  private String Source;
  private Long bannedUntil;
  private String reason;

  public CMIIpBanEntry(
      String string, Long bannedAtTime, String sourceName, Long bannedUntilTime, String banReason) {
    this.ip = string;
    this.banedAt = bannedAtTime;
    this.Source = sourceName;
    this.bannedUntil = bannedUntilTime;
    this.reason = banReason;
  }

  public String getKey() {
    return this.ip;
  }

  public void setKey(String string) {
    this.ip = string;
  }

  public Long getBanedAt() {
    return this.banedAt;
  }

  public void setBanedAt(Long bannedAtTime) {
    this.banedAt = bannedAtTime;
  }

  public String getSource() {
    return this.Source;
  }

  public void setSource(String string) {
    this.Source = string;
  }

  public Long getExpires() {
    return this.bannedUntil;
  }

  public void setExpires(Long bannedUntilTime) {
    this.bannedUntil = bannedUntilTime;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String string) {
    this.reason = string;
  }
}
