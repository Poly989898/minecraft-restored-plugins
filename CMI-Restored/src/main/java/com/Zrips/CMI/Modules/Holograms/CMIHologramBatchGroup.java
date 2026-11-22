package com.Zrips.CMI.Modules.Holograms;

import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.entity.Player;

public class CMIHologramBatchGroup {
  private CMIHologramLineBase line = null;
  private boolean enabled = true;
  private CMIVector3D offset = new CMIVector3D(0.0, 0.0, 0.0);

  public CMIHologramBatchGroup(CMIHologramLineBase cmiHologramLineBase) {
    this.line = cmiHologramLineBase;
  }

  public CMIHologramLineBase getLine() {
    return this.line;
  }

  public void setLine(CMIHologramLineBase cmiHologramLineBase) {
    this.line = cmiHologramLineBase;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public CMIVector3D getOffset() {
    return this.offset;
  }

  public void setOffset(CMIVector3D cmiVector3D) {
    this.offset = cmiVector3D;
  }

  public void destroy(Player player) {}

  public void show(Player player) {}

  public void setLocation(CMILocation cmiLocation) {}

  public void update(Player player) {}
}
