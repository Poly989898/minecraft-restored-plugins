package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Time.timeState;

public class CMITimeRate {
  private int everyTicks = 1;
  private int addAmount = 1;
  private timeState state = timeState.day;
  private boolean turnCycleOff = false;

  public CMITimeRate(timeState timeState, int value, int addAmountValue) {
    this.state = timeState;
    this.everyTicks = value;
    this.addAmount = addAmountValue;
  }

  public int getAddAmount() {
    return this.addAmount;
  }

  public void setAddAmount(int value) {
    this.addAmount = value;
  }

  public int getEveryTicks() {
    return this.everyTicks;
  }

  public void setEveryTicks(int value) {
    this.everyTicks = value;
  }

  public timeState getState() {
    return this.state;
  }

  public void setState(timeState timeState) {
    this.state = timeState;
  }

  public boolean isTurnCycleOff() {
    return this.turnCycleOff;
  }

  public void setTurnCycleOff(boolean flag) {
    this.turnCycleOff = flag;
  }
}
