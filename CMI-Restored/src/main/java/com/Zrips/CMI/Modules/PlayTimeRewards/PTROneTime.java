package com.Zrips.CMI.Modules.PlayTimeRewards;

public class PTROneTime extends PlayTimeReward {
  public PTROneTime(String string) {
    super(string);
  }

  public Long getPayFor() {
    return super.getRange();
  }

  public void setPayFor(Long newPayFor) {
    this.setRange(newPayFor * 1000L);
  }
}
