package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.entity.Player;

public class CMIHologramBatchGroupArmorStand extends CMIHologramBatchGroup {
  private FakeEntityInfo display = null;

  public CMIHologramBatchGroupArmorStand(
      FakeEntityInfo fakeEntityInfo, CMIHologramLineBase cmiHologramLineBase) {
    super(cmiHologramLineBase);
    this.display = fakeEntityInfo;
  }

  public FakeEntityInfo getDisplay() {
    return this.display;
  }

  public void setDisplay(FakeEntityInfo fakeEntityInfo) {
    this.display = fakeEntityInfo;
  }

  @Override
  public void destroy(Player player) {
    if (this.display != null) {
      CMI.getInstance().getNMS().removeHologram(player, this.display.getId());
    }
  }

  @Override
  public void show(Player player) {
    if (this.display != null) {
      ;
    }
  }

  @Override
  public void setLocation(CMILocation cmiLocation) {
    if (this.display != null) {
      ;
    }
  }

  @Override
  public void update(Player player) {
    if (this.display != null) {
      ;
    }
  }
}
