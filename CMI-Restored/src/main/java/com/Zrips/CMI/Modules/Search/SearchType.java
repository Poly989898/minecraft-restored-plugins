package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Container.CMIText;

public enum SearchType {
  ITEM,
  GM,
  GOD,
  MAXHP,
  FLY;

  private String name;

  SearchType(String string) {
    this.name = string;
  }

  SearchType() {
    this.name = CMIText.firstToUpperCase(this.toString());
  }

  public String getName() {
    return this.name;
  }
}
