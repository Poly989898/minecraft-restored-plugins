package com.Zrips.CMI.utils;

import java.util.HashMap;

public class importSettings {
  private HashMap<EssentialsConverter.importType, Boolean> map = new HashMap<>();

  public importSettings set(EssentialsConverter.importType importType, boolean flag) {
    this.map.put(importType, flag);
    return this;
  }

  public boolean is(EssentialsConverter.importType importType) {
    return !this.map.containsKey(importType) ? false : this.map.get(importType);
  }

  public HashMap<EssentialsConverter.importType, Boolean> getMap() {
    return this.map;
  }

  public void setMap(HashMap<EssentialsConverter.importType, Boolean> hashMap) {
    this.map = hashMap;
  }
}
