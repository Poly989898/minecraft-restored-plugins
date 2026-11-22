package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.Modules.Display.CMIDisplay;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.entity.Player;

public class CMIHologramBatchGroupDisplay extends CMIHologramBatchGroup {
  private CMIDisplay display = null;

  public CMIHologramBatchGroupDisplay(
      CMIDisplay cmiDisplay, CMIHologramLineBase cmiHologramLineBase) {
    super(cmiHologramLineBase);
    this.display = cmiDisplay;
  }

  public CMIDisplay getDisplay() {
    return this.display;
  }

  public void setDisplay(CMIDisplay cmiDisplay) {
    this.display = cmiDisplay;
  }

  @Override
  public void destroy(Player player) {
    if (this.display != null) {
      this.display.destroy(player);
    }
  }

  @Override
  public void show(Player player) {
    if (this.display != null) {
      this.display.show(player);
    }
  }

  @Override
  public void setLocation(CMILocation cmiLocation) {
    if (this.display != null) {
      this.display.setLocation(cmiLocation);
    }
  }

  @Override
  public void update(Player player) {
    if (this.display != null) {
      this.display.update(player);
    }
  }
}
