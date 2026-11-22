package com.Zrips.CMI.Modules.Warnings;

public class CMIWarningCategory {
  private String name;
  private Long lifeTime = 1000L;
  private int points = 1;
  private String defaultReason = "&7Violated server rules";

  public CMIWarningCategory(String string) {
    this.name = string;
  }

  public Long getLifeTime() {
    return this.lifeTime;
  }

  public void setLifeTime(Long newLifeTime) {
    this.lifeTime = newLifeTime;
  }

  public int getPoints() {
    return this.points;
  }

  public void setPoints(int value) {
    this.points = value;
  }

  public String getDefaultReason() {
    return this.defaultReason;
  }

  public void setDefaultReason(String string) {
    this.defaultReason = string;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }
}
