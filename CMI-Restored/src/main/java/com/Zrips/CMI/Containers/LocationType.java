package com.Zrips.CMI.Containers;

public enum LocationType {
  INVENTORY("Inventory"),
  ENDERCHEST("Enderchest");

  private String name;

  LocationType(String string) {
    this.name = string;
  }

  public String getName() {
    return this.name;
  }
}
