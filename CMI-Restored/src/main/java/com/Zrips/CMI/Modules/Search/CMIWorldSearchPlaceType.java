package com.Zrips.CMI.Modules.Search;

public enum CMIWorldSearchPlaceType {
  ContainerHolder,
  Unknown;

  private String place;
  private String pref;
  private String cmd;

  public String getPlace() {
    return this.place;
  }

  public String getPref() {
    return this.pref;
  }

  public String getCmd() {
    return this.cmd;
  }
}
