package com.Zrips.CMI.Modules.PlayTimeRewards;

public class PTRRepeat extends PlayTimeReward {
  private boolean stackRewards = true;

  public PTRRepeat(String string) {
    super(string);
  }

  public Long getPayEvery() {
    return this.getRange();
  }

  public void setPayEvery(Long newPayEvery) {
    this.setRange(newPayEvery * 1000L);
  }

  public boolean isStackRewards() {
    return this.stackRewards;
  }

  public void setStackRewards(boolean flag) {
    this.stackRewards = flag;
  }
}
