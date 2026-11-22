package com.Zrips.CMI.Modules.Selection;

public class SelectionSides {
  boolean NorthSide = true;
  boolean WestSide = true;
  boolean EastSide = true;
  boolean SouthSide = true;
  boolean TopSide = true;
  boolean BottomSide = true;

  public SelectionSides() {}

  public SelectionSides(
      Boolean north, Boolean west, Boolean east, Boolean south, Boolean top, Boolean bottom) {
    this.NorthSide = north;
    this.WestSide = west;
    this.EastSide = east;
    this.SouthSide = south;
    this.TopSide = top;
    this.BottomSide = bottom;
  }

  public void setNorthSide(boolean flag) {
    this.NorthSide = flag;
  }

  public boolean ShowNorthSide() {
    return this.NorthSide;
  }

  public void setWestSide(boolean flag) {
    this.WestSide = flag;
  }

  public boolean ShowWestSide() {
    return this.WestSide;
  }

  public void setEastSide(boolean flag) {
    this.EastSide = flag;
  }

  public boolean ShowEastSide() {
    return this.EastSide;
  }

  public void setSouthSide(boolean flag) {
    this.SouthSide = flag;
  }

  public boolean ShowSouthSide() {
    return this.SouthSide;
  }

  public void setTopSide(boolean flag) {
    this.TopSide = flag;
  }

  public boolean ShowTopSide() {
    return this.TopSide;
  }

  public void setBottomSide(boolean flag) {
    this.BottomSide = flag;
  }

  public boolean ShowBottomSide() {
    return this.BottomSide;
  }
}
