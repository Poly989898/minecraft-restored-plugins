package com.Zrips.CMI.Modules.ConsoleFilter;

public class CMIConsoleRecord {
  String message;
  long time;

  public CMIConsoleRecord(String string, long longValue) {
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
