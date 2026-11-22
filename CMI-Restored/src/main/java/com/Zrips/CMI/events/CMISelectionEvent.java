package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Portals.CuboidArea;
import org.bukkit.entity.Player;

public class CMISelectionEvent extends CMIPlayerEvent {
  private CuboidArea area;

  public CMISelectionEvent(Player player, CuboidArea cuboidArea) {
    super(player);
    this.area = cuboidArea;
  }

  public CuboidArea getArea() {
    return this.area;
  }

  public void setArea(CuboidArea cuboidArea) {
    this.area = cuboidArea;
  }
}
