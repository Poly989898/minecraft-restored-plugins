package com.Zrips.CMI.Modules.Holograms;

import org.bukkit.entity.Player;

public class CMIHologramLineBase {
  protected String originalText = "";
  private CMIHoloLineType type = CMIHoloLineType.unknown;
  private boolean dynamic = false;

  public CMIHologramLineBase clone() {
    return new CMIHologramLineBase();
  }

  public CMIHologramLineBase(String string) {
    this.originalText = string;
  }

  public CMIHologramLineBase() {}

  public CMIHoloLineType getType() {
    return this.type;
  }

  public CMIHologramLineBase setType(CMIHoloLineType cmiHoloLineType) {
    this.type = cmiHoloLineType;
    return this;
  }

  public String getOriginalText() {
    return this.originalText;
  }

  public String getLineOutput(Player player) {
    return this.getOriginalText();
  }

  public boolean isDynamic() {
    return this.dynamic;
  }

  public CMIHologramLineBase setDynamic(boolean flag) {
    this.dynamic = flag;
    return this;
  }
}
