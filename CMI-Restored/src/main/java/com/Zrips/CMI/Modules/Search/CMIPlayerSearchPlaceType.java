package com.Zrips.CMI.Modules.Search;

public enum CMIPlayerSearchPlaceType {
  Inventory("inv", "inv%", "cmi inv [player]"),
  InventoryShulker("invS", "invS%", "cmi inv [player]"),
  InventoryBundle("invB", "invB%", "cmi inv [player]"),
  EnderChest("ender", "end%", "cmi ender [player]"),
  EnderChestShulker("enderS", "endS%", "cmi ender [player]"),
  EnderChestBundle("enderB", "endB%", "cmi ender [player]"),
  PlayerVaults("PlayerVault", "Pv%", "pv [player] [nr]"),
  Custom("", "Cs%", "");

  private String place;
  private String pref;
  private String cmd;

  CMIPlayerSearchPlaceType(String string, String prefixText, String commandText) {
    this.place = string;
    this.pref = prefixText;
    this.cmd = commandText;
  }

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
