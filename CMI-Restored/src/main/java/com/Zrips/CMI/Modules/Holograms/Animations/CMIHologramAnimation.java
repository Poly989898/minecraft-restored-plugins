package com.Zrips.CMI.Modules.Holograms.Animations;

import java.util.HashMap;
import net.Zrips.CMILib.Container.CMINumber;
import org.bukkit.Color;

public class CMIHologramAnimation {
  HashMap<CMIHologramAnimationType, CMIHologramValueChanger> changers = new HashMap<>();

  public double getTarget(CMIHologramAnimationType cmiHologramAnimationType) {
    return this.changers
        .computeIfAbsent(cmiHologramAnimationType, key -> new CMIHologramValueChanger())
        .getTarget();
  }

  public void setTarget(CMIHologramAnimationType cmiHologramAnimationType, double number) {
    this.changers
        .computeIfAbsent(cmiHologramAnimationType, key -> new CMIHologramValueChanger())
        .setTarget(number);
  }

  public double getCurrent(CMIHologramAnimationType cmiHologramAnimationType) {
    return this.changers
        .computeIfAbsent(cmiHologramAnimationType, key -> new CMIHologramValueChanger())
        .getCurrent();
  }

  public void setCurrent(CMIHologramAnimationType cmiHologramAnimationType, double number) {
    this.changers
        .computeIfAbsent(cmiHologramAnimationType, key -> new CMIHologramValueChanger())
        .setCurrent(CMINumber.clamp(number, 0.0, Double.MAX_VALUE));
  }

  public void update() {
    for (CMIHologramValueChanger cmiHologramValueChanger : this.changers.values()) {
      cmiHologramValueChanger.updateValue();
    }
  }

  public CMIHologramValueChanger get(CMIHologramAnimationType cmiHologramAnimationType) {
    return this.changers.computeIfAbsent(
        cmiHologramAnimationType, key -> new CMIHologramValueChanger());
  }

  public static Color applyAlpha(Color color, int value) {
    return Color.fromARGB(
        CMINumber.clamp(value, 0, 255), color.getRed(), color.getGreen(), color.getBlue());
  }

  public boolean finished() {
    for (CMIHologramValueChanger cmiHologramValueChanger : this.changers.values()) {
      if (!cmiHologramValueChanger.finished()) {
        return false;
      }
    }

    return true;
  }

  public void onFinish() {}
}
