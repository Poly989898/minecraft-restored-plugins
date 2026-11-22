package com.Zrips.CMI.Modules.CmdCooldown;

import java.util.HashMap;

public class CmdCooldown {
  HashMap<String, CmdCooldown.CMICooldown> list = new HashMap<>();

  public void addCooldown2(String string, Long startTime, Long durationMillis) {
    this.addCooldown(string, startTime, (int) (durationMillis / 1000L));
  }

  public void addCooldown(String string, int value) {
    this.addCooldown(string, Long.valueOf(System.currentTimeMillis()), value);
  }

  public void addCooldown(String string, Long startTime, int value) {
    this.list.put(string.toLowerCase(), new CmdCooldown.CMICooldown(startTime, value));
  }

  public void addCooldown(String string, Long startTime, double number) {
    this.list.put(string.toLowerCase(), new CmdCooldown.CMICooldown(startTime, number));
  }

  public void removeCooldown(String string) {
    this.list.remove(string.toLowerCase());
  }

  public HashMap<String, CmdCooldown.CMICooldown> getList() {
    return this.list;
  }

  public void clear() {
    this.list.clear();
  }

  public class CMICooldown {
    private long initialized = 0L;
    private int cooldownSeconds = 0;

    public CMICooldown(Long startTime, int value) {
      this.initialized = startTime;
      this.cooldownSeconds = value < 0 ? 0 : value;
    }

    public CMICooldown(Long startTime, double number) {
      this.initialized = startTime;
      this.cooldownSeconds = (int) (number < 0.0 ? 0.0 : number);
    }

    public Long getInitialized() {
      return this.initialized;
    }

    public Long getEndTime() {
      return this.initialized + this.cooldownSeconds * 1000L;
    }

    public Long getDelta() {
      long longValue = this.getEndTime() - System.currentTimeMillis();
      return longValue < 0L ? 0L : longValue;
    }

    public void setInitialized(Long startTime) {
      this.initialized = startTime;
    }

    public int getTimePeriod() {
      return this.cooldownSeconds;
    }

    public void setTimePeriod(int value) {
      this.cooldownSeconds = value;
    }
  }
}
