package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Deprecated
public class CMIDataWatcher {
  private Integer id;
  private Integer secId = null;
  private Object stand;
  private Location loc;
  private CMIItemStack item;
  private CMIHoloLineType type;
  private boolean updatingHead = false;

  public CMIDataWatcher(Integer integer, Object object, CMIHoloLineType cmiHoloLineType) {
    this.stand = object;
    this.id = integer;
    this.type = cmiHoloLineType;
  }

  public CMIDataWatcher(
      Integer integer, Integer secondId, Object object, CMIHoloLineType cmiHoloLineType) {
    this.stand = object;
    this.id = integer;
    this.secId = secondId;
    this.type = cmiHoloLineType;
  }

  public Object getStand() {
    return this.stand;
  }

  public void setStand(Object object) {
    this.stand = object;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer integer) {
    this.id = integer;
  }

  public void remove(Player player) {
    CMI.getInstance().getNMS().removeHologram(player, this.id);
    if (this.secId != null) {
      CMI.getInstance().getNMS().removeHologram(player, this.secId);
    }
  }

  public Location getLoc() {
    return this.loc;
  }

  public void setLoc(Location location) {
    this.loc = location;
  }

  public CMIHoloLineType getType() {
    return this.type;
  }

  public void setType(CMIHoloLineType cmiHoloLineType) {
    this.type = cmiHoloLineType;
  }

  public CMIItemStack getItem() {
    return this.item;
  }

  public void setItem(CMIItemStack cmiItemStack) {
    this.item = cmiItemStack;
  }

  public boolean isUpdatingHead() {
    return this.updatingHead;
  }

  public void setUpdatingHead(boolean flag) {
    this.updatingHead = flag;
  }
}
