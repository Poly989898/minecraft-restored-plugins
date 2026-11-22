package com.Zrips.CMI.events;

public final class CMIAsyncConsoleMessageEvent extends CMIEvent {
  private String message;
  private long time;

  public CMIAsyncConsoleMessageEvent(String string, long longValue) {
    super(true);
    this.message = string;
    this.time = longValue;
  }

  public String getMessage() {
    return this.message;
  }

  public long getTime() {
    return this.time;
  }
}
