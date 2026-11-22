package com.Zrips.CMI.Modules.Holograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Container.CMINumber;

public class CMIHologramSchedulerData {
  int tickInterval = 1;
  int currentTick = 0;
  List<CMIHologramUpdateType> updateTypes = new ArrayList<>();

  public CMIHologramSchedulerData(int value) {
    this.tickInterval = CMINumber.clamp(value, 1, Integer.MAX_VALUE);
  }

  public CMIHologramSchedulerData(int value, CMIHologramUpdateType... cmiHologramUpdateTypes) {
    this.tickInterval = CMINumber.clamp(value, 1, Integer.MAX_VALUE);
    this.updateTypes.addAll(Arrays.asList(cmiHologramUpdateTypes));
  }

  public int getTickInterval() {
    return this.tickInterval;
  }

  public int getCurrentTick() {
    return this.currentTick;
  }

  public void setCurrentTick(int value) {
    this.currentTick = value;
  }

  public void incrementCurrentTick() {
    this.currentTick++;
  }

  public void resetCurrentTick() {
    this.currentTick = 0;
  }

  public void setTickInterval(int value) {
    this.tickInterval = value;
  }

  public boolean cycleTick() {
    this.incrementCurrentTick();
    boolean flag = this.getCurrentTick() >= this.getTickInterval();
    if (flag) {
      this.resetCurrentTick();
    }

    return flag;
  }

  public List<CMIHologramUpdateType> getUpdateTypes() {
    return this.updateTypes;
  }

  public CMIHologramSchedulerData setUpdateTypes(List<CMIHologramUpdateType> list) {
    this.updateTypes = list;
    return this;
  }
}
