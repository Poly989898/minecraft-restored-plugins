package com.Zrips.CMI.Containers;

public enum Direction {
  SOUTH("South"),
  WEST("West"),
  NORTH("North"),
  EAST("East");

  private String direction;

  Direction(String string) {
    this.direction = string;
  }

  public String getDirection() {
    return this.direction;
  }
}
