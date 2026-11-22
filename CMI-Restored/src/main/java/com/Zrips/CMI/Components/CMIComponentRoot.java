package com.Zrips.CMI.Components;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Version;

public class CMIComponentRoot {
  private String text = null;
  private Color shadowColor = null;
  protected CMIComponentAction action = new CMIComponentAction();
  private static final Pattern SHADOW =
      Pattern.compile("<shadow:((#)([a-zA-Z0-9]+))(?::(\\d+.?(\\d+)?))?>");

  public CMIComponentRoot(@Nullable String string) {
    this.setText(string);
  }

  public CMIComponentRoot() {}

  @Nonnull
  public String getText() {
    return this.text == null ? "" : this.text;
  }

  public static String removeShadow(String string) {
    return !string.contains("<shadow:") ? string : SHADOW.matcher(string).replaceAll("");
  }

  private static Color parseShadow(String string) {
    if (!string.contains("<shadow:")) {
      return null;
    }

    Matcher matcher = SHADOW.matcher(string);
    if (!matcher.find()) {
      return null;
    }

    String hexDigits = matcher.group(3);
    if (hexDigits == null) {
      return null;
    }

    CMIChatColor cmiChatColor = CMIChatColor.getColor(hexDigits);
    if (cmiChatColor == null) {
      return null;
    }

    String numberText = matcher.group(4);
    double number = 0.5;
    if (hexDigits.length() > 7 && numberText == null) {
      numberText = hexDigits.substring(7, 9);

      try {
        number = Integer.parseInt(numberText, 16);
        number = CMINumber.clamp(number / 255.0, 0.0, 1);
      } catch (NumberFormatException numberFormatException) {
      }
    } else if (numberText != null) {
      try {
        number = Double.parseDouble(numberText);
        number = CMINumber.clamp(number, 0.0, 1);
      } catch (NumberFormatException numberFormatException2) {
      }
    }

    Color color = cmiChatColor.getJavaColor();
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (number * 255.0));
  }

  public CMIComponentRoot setText(@Nonnull String string) {
    if (string == null) {
      this.text = null;
      return this;
    }

    if (!Version.isPaperBranch()) {
      string = removeShadow(string);
    }

    this.text = string;
    return this;
  }

  public CMIComponentAction getAction() {
    return this.action;
  }

  public CMIComponentRoot setClickAction(
      @Nullable CMIComponentActionType cmiComponentActionType, @Nullable String string) {
    this.getAction().setClickAction(cmiComponentActionType, string);
    return this;
  }

  public Color getShadowColor() {
    return this.shadowColor;
  }

  public CMIComponentRoot setShadowColor(Color color) {
    this.shadowColor = color;
    return this;
  }
}
