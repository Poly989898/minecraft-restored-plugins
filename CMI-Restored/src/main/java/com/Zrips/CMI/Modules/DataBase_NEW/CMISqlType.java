package com.Zrips.CMI.Modules.DataBase_NEW;

public enum CMISqlType {
  SqLite,
  MySQL,
  MariaDB;

  boolean fallback = false;

  public boolean isFallback() {
    return this.fallback;
  }

  public void setFallback(boolean flag) {
    this.fallback = flag;
  }
}
