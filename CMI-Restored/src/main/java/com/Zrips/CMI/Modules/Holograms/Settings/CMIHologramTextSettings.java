package com.Zrips.CMI.Modules.Holograms.Settings;

import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramTextSettings {
  private CMITextAlignment textAlignment = CMITextAlignment.CENTER;
  private CMIChatColor backgroundColor = CMIChatColor.BLACK;
  private int backgroundAlpha = 125;
  private int textAlpha = 255;
  private boolean doubleSided = false;
  private boolean shadowed = true;
  private boolean seeThrough = false;
  private int lineWidth = 1000;
  private int fillerAmount = 245;

  public CMITextAlignment getTextAlignment() {
    return this.textAlignment;
  }

  public CMIHologramTextSettings setTextAlignment(CMITextAlignment cmiTextAlignment) {
    this.textAlignment = cmiTextAlignment;
    return this;
  }

  public CMIChatColor getBackgroundColor() {
    return this.backgroundColor;
  }

  public CMIHologramTextSettings setBackgroundColor(CMIChatColor cmiChatColor) {
    this.backgroundColor = cmiChatColor;
    return this;
  }

  public int getBackgroundAlpha() {
    return this.backgroundAlpha;
  }

  public CMIHologramTextSettings setBackgroundAlpha(int value) {
    this.backgroundAlpha = CMINumber.clamp(value, 0, 255);
    return this;
  }

  public int getTextAlpha() {
    return this.textAlpha;
  }

  public CMIHologramTextSettings setTextAlpha(int value) {
    this.textAlpha = CMINumber.clamp(value, 0, 255);
    return this;
  }

  public boolean isDoubleSided() {
    return this.doubleSided;
  }

  public CMIHologramTextSettings setDoubleSided(boolean flag) {
    this.doubleSided = flag;
    return this;
  }

  public boolean isShadowed() {
    return this.shadowed;
  }

  public CMIHologramTextSettings setShadowed(boolean flag) {
    this.shadowed = flag;
    return this;
  }

  public boolean isSeeThrough() {
    return this.seeThrough;
  }

  public CMIHologramTextSettings setSeeThrough(boolean flag) {
    this.seeThrough = flag;
    return this;
  }

  public int getLineWidth() {
    return this.lineWidth;
  }

  public CMIHologramTextSettings setLineWidth(int value) {
    this.lineWidth = CMINumber.clamp(value, 1, 10000);
    return this;
  }

  public int getFillerAmount() {
    return this.fillerAmount;
  }

  public CMIHologramTextSettings setFillerAmount(int value) {
    this.fillerAmount = CMINumber.clamp(value, 1, 10000);
    return this;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getTextAlignment() != CMITextAlignment.CENTER) {
      hashMap.put("Align", this.getTextAlignment().toString());
    }

    if (this.getBackgroundColor() != CMIChatColor.BLACK) {
      hashMap.put(
          "BColor",
          this.getBackgroundColor().getName() == null
              ? this.getBackgroundColor().getFormatedHex()
              : "{#" + this.getBackgroundColor().getName() + "}");
    }

    if (this.getBackgroundAlpha() != 125) {
      hashMap.put("BAlpha", this.getBackgroundAlpha());
    }

    if (this.getTextAlpha() != 255) {
      hashMap.put("TAlpha", this.getTextAlpha());
    }

    if (this.isDoubleSided()) {
      hashMap.put("Double", true);
    }

    if (!this.isShadowed()) {
      hashMap.put("Shadow", false);
    }

    if (this.isSeeThrough()) {
      hashMap.put("See", true);
    }

    if (this.getLineWidth() != 1000) {
      hashMap.put("Line", this.getLineWidth());
    }

    if (this.getFillerAmount() != 245) {
      hashMap.put("Fill", this.getFillerAmount());
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramTextSettings deserialize(Map<String, Object> map) {
    CMIHologramTextSettings cmiHologramTextSettings = new CMIHologramTextSettings();
    if (map.get("Align") instanceof String) {
      cmiHologramTextSettings.setTextAlignment(
          CMITextAlignment.getByName((String) map.get("Align")));
    }

    if (map.get("BColor") instanceof String) {
      cmiHologramTextSettings.setBackgroundColor(CMIChatColor.getColor((String) map.get("BColor")));
    }

    if (map.get("BAlpha") instanceof Integer) {
      cmiHologramTextSettings.setBackgroundAlpha((Integer) map.get("BAlpha"));
    }

    if (map.get("TAlpha") instanceof Integer) {
      cmiHologramTextSettings.setTextAlpha((Integer) map.get("TAlpha"));
    }

    if (map.get("Double") instanceof Boolean) {
      cmiHologramTextSettings.setDoubleSided((Boolean) map.get("Double"));
    }

    if (map.get("Shadow") instanceof Boolean) {
      cmiHologramTextSettings.setShadowed((Boolean) map.get("Shadow"));
    }

    if (map.get("See") instanceof Boolean) {
      cmiHologramTextSettings.setSeeThrough((Boolean) map.get("See"));
    }

    if (map.get("Line") instanceof Integer) {
      cmiHologramTextSettings.setLineWidth((Integer) map.get("Line"));
    }

    if (map.get("Fill") instanceof Integer) {
      cmiHologramTextSettings.setFillerAmount((Integer) map.get("Fill"));
    }

    return cmiHologramTextSettings;
  }
}
