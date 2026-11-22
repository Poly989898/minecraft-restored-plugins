package net.Zrips.CMILib.Time;

public class TimeInfo {
  private int hours = -1;
  private int minutes = 0;
  private int seconds = 0;
  private int ticks = -1;
  private boolean byTicks = false;

  public boolean isByTicks() {
    return this.byTicks;
  }

  public void setByTicks() {
    this.byTicks = true;
  }

  public int getHours() {
    return this.hours;
  }

  public void setHours(int hours) {
    this.hours = hours;
  }

  public int getMinutes() {
    return this.minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public int getSeconds() {
    return this.seconds;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  public int getTicks() {
    return this.ticks;
  }

  public void setTicks(int ticks) {
    this.ticks = ticks;
  }
}
