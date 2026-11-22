package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.Components.CMIComponentBase;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class CMIHologramLinePortion {
  private CMIHologramLineBase baseLineData = null;
  private CMIHologramLineBase hoverLineData = null;
  private CMIHologramClickAction clickActions = null;
  private short from = 0;
  private short to = 1;

  public CMIHologramLinePortion(CMIHologramLineBase cmiHologramLineBase) {
    this.baseLineData = cmiHologramLineBase;
  }

  public CMIHologramLineBase getBaseLineData() {
    return this.baseLineData;
  }

  public void setBaseLineData(CMIHologramLineBase cmiHologramLineBase) {
    this.baseLineData = cmiHologramLineBase;
  }

  public CMIHologramLineBase getHoverLineData() {
    return this.hoverLineData;
  }

  public void setHoverLineData(CMIHologramLineBase cmiHologramLineBase) {
    this.hoverLineData = cmiHologramLineBase;
  }

  public CMIHologramClickAction getClickActions() {
    return this.clickActions;
  }

  public void setClickActions(CMIHologramClickAction cmiHologramClickAction) {
    this.clickActions = cmiHologramClickAction;
  }

  public void setLineRanges(double number, double rangeTo) {
    this.from = (short) (number * 100.0);
    this.to = (short) (rangeTo * 100.0);
  }

  public boolean isInRange(double number) {
    return number >= this.from / 100.0 && number <= this.to / 100.0;
  }

  public double calculateWidth() {
    if (this.getBaseLineData() == null) {
      return 0.0;
    } else if (this.getBaseLineData() instanceof CMIHologramLineStaticText) {
      CMIHologramLineStaticText cmiHologramLineStaticText =
          (CMIHologramLineStaticText) this.getBaseLineData();
      String string =
          CMIComponentBase.stripMiniMessageTags(cmiHologramLineStaticText.getOriginalText());
      return CMIFont.getWidth(CMIChatColor.stripColor(string));
    } else {
      return this.getBaseLineData() instanceof CMIHologramLineIcon ? 8.0 : 6.0;
    }
  }
}
