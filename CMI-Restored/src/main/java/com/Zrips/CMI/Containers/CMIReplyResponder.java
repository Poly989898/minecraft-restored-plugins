package com.Zrips.CMI.Containers;

public class CMIReplyResponder {
  private String name;
  private Long time;

  public CMIReplyResponder(String string, Long newTime) {
    this.name = string;
    this.time = newTime;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long newTime) {
    this.time = newTime;
  }
}
