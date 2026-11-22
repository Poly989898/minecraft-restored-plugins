package com.Zrips.CMI.Modules.Display;

import org.bukkit.entity.TextDisplay.TextAlignment;

public enum CMITextAlignment {
  LEFT,
  CENTER,
  RIGHT;

  private TextAlignment bukkitBoard = null;

  public TextAlignment getTextAlignment() {
    return this.bukkitBoard != null ? this.bukkitBoard : TextAlignment.valueOf(this.toString());
  }

  public static CMITextAlignment getByName(String string) {
    CMITextAlignment[] cmiTextAlignments;
    int value = (cmiTextAlignments = values()).length;

    for (int index = 0; index < value; index++) {
      CMITextAlignment cmiTextAlignment = cmiTextAlignments[index];
      if (cmiTextAlignment.toString().equalsIgnoreCase(string)) {
        return cmiTextAlignment;
      }
    }

    return null;
  }

  public CMITextAlignment next() {
    switch (this) {
      case LEFT:
        return CENTER;
      case CENTER:
        return RIGHT;
      case RIGHT:
        return LEFT;
      default:
        return CENTER;
    }
  }
}
