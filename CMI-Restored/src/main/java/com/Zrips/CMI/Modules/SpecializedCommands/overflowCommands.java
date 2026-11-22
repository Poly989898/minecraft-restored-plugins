package com.Zrips.CMI.Modules.SpecializedCommands;

import java.util.HashSet;
import java.util.Set;

public class overflowCommands {
  private long time = 0L;
  private Set<String> command = new HashSet<>();
  private int repeat = 0;

  public overflowCommands(String string) {
    this.command.add(string);
    this.time = System.currentTimeMillis();
    this.repeat++;
  }

  public long getTime() {
    return this.time;
  }

  public void setTime(long longValue) {
    this.time = longValue;
  }

  public Set<String> getCommands() {
    return this.command;
  }

  public void addCommand(String string) {
    this.command.add(string);
  }

  public int getRepeat() {
    return this.repeat;
  }

  public void setRepeat(int value) {
    this.repeat = value;
  }

  public void addRepeat() {
    this.repeat++;
  }
}
