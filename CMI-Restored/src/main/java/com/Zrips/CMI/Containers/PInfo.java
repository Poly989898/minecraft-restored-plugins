package com.Zrips.CMI.Containers;

public class PInfo {
  private String permission;
  private String desc;
  private boolean cmd = false;
  private boolean others;

  public PInfo(String string, String description, boolean flag) {
    this.permission = string;
    this.desc = description;
    this.cmd = flag;
  }

  public PInfo(String string, String description, boolean flag, boolean othersFlag) {
    this.permission = string;
    this.desc = description;
    this.cmd = flag;
    this.others = othersFlag;
  }

  public PInfo(String string, String description) {
    this.permission = string;
    this.desc = description;
  }

  public String getPermission() {
    return this.permission;
  }

  public void setPermission(String string) {
    this.permission = string;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String string) {
    this.desc = string;
  }

  public boolean isCmd() {
    return this.cmd;
  }

  public void setCmd(boolean flag) {
    this.cmd = flag;
  }

  public boolean isOthers() {
    return this.others;
  }

  public void setOthers(boolean flag) {
    this.others = flag;
  }
}
