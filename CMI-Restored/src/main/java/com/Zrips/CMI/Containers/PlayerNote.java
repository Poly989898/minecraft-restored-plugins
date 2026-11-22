package com.Zrips.CMI.Containers;

public class PlayerNote {
  private String sender;
  private long time;
  private String note;

  public PlayerNote(String string, Long timeMillis, String noteText) {
    this.sender = string;
    this.time = timeMillis == null ? 0L : timeMillis;
    this.note = noteText;
  }

  public String getSender() {
    return this.sender;
  }

  public void setSender(String string) {
    this.sender = string;
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long timeMillis) {
    this.time = timeMillis == null ? 0L : timeMillis;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(String string) {
    this.note = string;
  }
}
