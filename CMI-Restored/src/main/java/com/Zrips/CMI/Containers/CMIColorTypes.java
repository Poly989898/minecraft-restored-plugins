package com.Zrips.CMI.Containers;

public enum CMIColorTypes {
  publicmessage(true),
  privatemessage(true),
  me(true),
  nickname(true),
  signs(false),
  books(true),
  itemname(true),
  itemlore(true);

  private boolean clean;

  CMIColorTypes(boolean flag) {
    this.clean = flag;
  }

  public boolean isClean() {
    return this.clean;
  }

  public void setClean(boolean flag) {
    this.clean = flag;
  }
}
