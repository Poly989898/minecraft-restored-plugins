package com.Zrips.CMI.Modules.Homes;

import java.util.ArrayList;
import java.util.List;

public class HomeWorldLimit {
  private List<String> worlds = new ArrayList<>();
  private int limit = 0;

  public List<String> getWorlds() {
    return this.worlds;
  }

  public void setWorlds(List<String> list) {
    this.worlds = list;
  }

  public int getLimit() {
    return this.limit;
  }

  public void setLimit(int value) {
    if (value < -1) {
      value = -1;
    }

    this.limit = value;
  }
}
