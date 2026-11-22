package com.Zrips.CMI.Modules.Holograms.Settings;

import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Container.CMINumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramAnimationSettings {
  private int fadeInAnimation = 0;
  private int fadeOutAnimation = 0;
  private int autoRotate = 0;

  public boolean isFadeInAnimation() {
    return this.fadeInAnimation > 0;
  }

  public int getFadeInAnimationSpeed() {
    return this.fadeInAnimation;
  }

  public CMIHologramAnimationSettings setFadeInAnimation(int value) {
    this.fadeInAnimation = CMINumber.clamp(value, 0, 600);
    return this;
  }

  public boolean isFadeOutAnimation() {
    return this.fadeOutAnimation > 0;
  }

  public int getFadeOutAnimationSpeed() {
    return this.fadeOutAnimation;
  }

  public CMIHologramAnimationSettings setFadeOutAnimation(int value) {
    this.fadeOutAnimation = CMINumber.clamp(value, 0, 600);
    return this;
  }

  public int getAutoRotate() {
    return this.autoRotate;
  }

  public CMIHologramAnimationSettings setAutoRotate(int value) {
    this.autoRotate = value % 1080;
    return this;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap<String, Object> hashMap = new HashMap<>();
    if (this.getAutoRotate() != 0) {
      hashMap.put("Rotate", this.getAutoRotate());
    }

    if (this.isFadeInAnimation()) {
      hashMap.put("In", this.getFadeInAnimationSpeed());
    }

    if (this.isFadeOutAnimation()) {
      hashMap.put("Out", this.getFadeOutAnimationSpeed());
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramAnimationSettings deserialize(Map<String, Object> map) {
    CMIHologramAnimationSettings cmiHologramAnimationSettings = new CMIHologramAnimationSettings();
    if (map.get("Rotate") instanceof Integer) {
      cmiHologramAnimationSettings.setAutoRotate((Integer) map.get("Rotate"));
    }

    if (map.get("In") instanceof Integer) {
      cmiHologramAnimationSettings.setFadeInAnimation((Integer) map.get("In"));
    }

    if (map.get("Out") instanceof Integer) {
      cmiHologramAnimationSettings.setFadeOutAnimation((Integer) map.get("Out"));
    }

    return cmiHologramAnimationSettings;
  }
}
