package com.Zrips.CMI.Modules.Ranks;

public class InformTimer {
  private Long nextCheck;
  private Long timesInformed = 0L;

  public InformTimer() {}

  public InformTimer(long longValue) {
    this.nextCheck = longValue;
  }

  public Long getNextCheck() {
    return this.nextCheck;
  }

  public void setNextCheck(Long newNextCheck) {
    this.nextCheck = newNextCheck;
  }

  public Long getTimesInformed() {
    return this.timesInformed;
  }

  public void resetTimesInformed() {
    this.timesInformed = 0L;
  }

  public void addTimesInformed() {
    this.timesInformed = this.timesInformed + 1L;
  }
}
