package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;

public class CMIHologramLineDynamicText extends CMIHologramLineStaticText {
  public CMIHologramLineDynamicText clone() {
    return new CMIHologramLineDynamicText(this.settings, this.getOriginalText());
  }

  public CMIHologramLineDynamicText(
      CMIHologramTextSettings cmiHologramTextSettings, String string) {
    super(cmiHologramTextSettings, string);
    this.setType(CMIHoloLineType.plainText);
    this.setDynamic(true);
  }
}
