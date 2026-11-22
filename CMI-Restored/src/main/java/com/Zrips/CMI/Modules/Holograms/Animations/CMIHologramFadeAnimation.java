package com.Zrips.CMI.Modules.Holograms.Animations;

public class CMIHologramFadeAnimation extends CMIHologramAnimation {
  public CMIHologramFadeAnimation(CMIHologramValueChanger cmiHologramValueChanger) {
    this.changers.put(CMIHologramAnimationType.Base, cmiHologramValueChanger);
  }
}
