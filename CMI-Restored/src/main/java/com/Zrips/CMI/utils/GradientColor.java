package com.Zrips.CMI.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Colors.CMICustomColors;

public class GradientColor {
  private String startingColor = null;
  private String startingTextFormat = null;
  private String endingColor = null;
  private String nextChar = null;
  private String prevHexColor = null;
  private double prevDistance = Double.MAX_VALUE;
  private double prevDelta = Double.MAX_VALUE;
  private StringBuilder modifiedText = new StringBuilder();
  private StringBuilder toBeReplacedText = new StringBuilder();
  private String replaceWith = null;
  private static Pattern gradientDetectionPattern =
      Pattern.compile(
          "(?=(\\{#([0-9A-Fa-f]{6}|[a-zA-Z_]{3,})\\}((?:&[0-9a-fk-or])*)(.)\\{#([0-9A-Fa-f]{6}|[a-zA-Z_]{3,})\\}((?:&[0-9a-fk-or])*)(.)))");
  private static Pattern cleanupPattern = Pattern.compile("\\{#([0-9A-Za-z_]{3,})<\\}\\{#\\1>\\}");

  public GradientColor(String string, String endColor) {
    this.startingColor = string;
    this.startingTextFormat = endColor;
  }

  public String getStartingColor() {
    return this.startingColor;
  }

  public GradientColor setStartingColor(String string) {
    this.startingColor = string;
    return this;
  }

  public String getStartingTextFormat() {
    return this.startingTextFormat;
  }

  public GradientColor setStartingTextFormat(String string) {
    this.startingTextFormat = string;
    return this;
  }

  public String getPrevHexColor() {
    return this.prevHexColor;
  }

  public GradientColor setPrevHexColor(String string) {
    this.prevHexColor = string;
    return this;
  }

  public double getPrevDistance() {
    return this.prevDistance;
  }

  public GradientColor setPrevDistance(double number) {
    this.prevDistance = number;
    return this;
  }

  public String getModifiedText() {
    return this.modifiedText.toString();
  }

  public GradientColor addToModifiedText(String string) {
    this.modifiedText.append(string);
    return this;
  }

  public StringBuilder getReplaceWhat() {
    return this.toBeReplacedText;
  }

  public GradientColor addReplaceWhat(String string) {
    this.toBeReplacedText.append(string);
    return this;
  }

  public String getEndingColor() {
    return this.endingColor;
  }

  public GradientColor setEndingColor(String string) {
    this.endingColor = string;
    return this;
  }

  public String getNextChar() {
    return this.nextChar;
  }

  public GradientColor setNextChar(String string) {
    this.nextChar = string;
    return this;
  }

  public String getReplaceWith() {
    return this.replaceWith;
  }

  public GradientColor setReplaceWith(String string) {
    this.replaceWith = string;
    return this;
  }

  public void calculateFinalString() {
    CMIChatColor cmiChatColor = CMIChatColor.getColor(this.getStartingColor());
    CMIChatColor endColorCode = CMIChatColor.getColor(this.getEndingColor());
    this.addReplaceWhat(
        "{#"
            + this.getEndingColor()
            + "}"
            + (this.getStartingTextFormat() == null ? "" : this.getStartingTextFormat())
            + this.getNextChar());
    this.addToModifiedText(this.getNextChar());
    StringBuilder stringBuilder = new StringBuilder();
    StringBuilder builder = new StringBuilder();
    if (this.getModifiedText().length() > 1) {
      builder.append("{#");
      builder.append(
          cmiChatColor.getName() == null
              ? cmiChatColor.getHex()
              : cmiChatColor.getName().toLowerCase().replace("_", ""));
      builder.append(">");
      builder.append("}");
      if (this.getStartingTextFormat() != null) {
        builder.append(this.getStartingTextFormat());
      }

      builder.append(this.getModifiedText());
      builder.append("{#");
      builder.append(
          endColorCode.getName() == null
              ? endColorCode.getHex()
              : endColorCode.getName().toLowerCase().replace("_", ""));
      builder.append("<");
      builder.append("}");
    }

    stringBuilder.append(builder);
    this.setReplaceWith(stringBuilder.toString());
  }

  public double getPreviousDelta() {
    return this.prevDelta;
  }

  public GradientColor setPreviousDelta(double number) {
    this.prevDelta = number;
    return this;
  }

  public static String deconvert(String string) {
    string = convertLegacyToHex(string);
    String text = string;
    string = cycleOver(string);
    if (!CMIChatColor.translate(string).equals(CMIChatColor.translate(text))) {
      string = text;
    }

    if (string.contains("<}")) {
      Matcher matcher = cleanupPattern.matcher(string);

      while (matcher.find()) {
        String match = matcher.group();
        String group = matcher.group(1);
        string = string.replace(match, "{#" + group + "<>" + "}");
      }
    }

    return string;
  }

  private static String cycleOver(String string) {
    if (!string.contains("{#")) {
      return string;
    }

    String text = string;
    Matcher matcher = gradientDetectionPattern.matcher(string);
    ArrayList<GradientColor> arrayList = new ArrayList();
    GradientColor gradientColor = null;

    while (matcher.find()) {
      String group2Text = matcher.group(2);
      String group3Text = matcher.group(3);
      String group4Text = matcher.group(4);
      String group5Text = matcher.group(5);
      String group7Text = matcher.group(7);
      CMIChatColor cmiChatColor = CMIChatColor.getColor(group2Text);
      CMIChatColor endColor = CMIChatColor.getColor(group5Text);
      if (gradientColor == null) {
        gradientColor = new GradientColor(group2Text, group3Text);
      }

      int redDelta = cmiChatColor.getRed() - endColor.getRed();
      int greenDelta = cmiChatColor.getGreen() - endColor.getGreen();
      int blueDelta = cmiChatColor.getBlue() - endColor.getBlue();
      double number =
          Math.sqrt(redDelta * redDelta + greenDelta * greenDelta + blueDelta * blueDelta);
      if (gradientColor.getPrevDistance() == Double.MAX_VALUE) {
        gradientColor.setPrevDistance(number);
      }

      double distanceDelta = Math.abs(gradientColor.getPrevDistance() - number);
      if (gradientColor.getPreviousDelta() == Double.MAX_VALUE) {
        gradientColor.setPreviousDelta(distanceDelta);
      }

      if (distanceDelta > 2.0) {
        gradientColor.calculateFinalString();
        arrayList.add(gradientColor);
        gradientColor = null;
        break;
      }

      gradientColor.addReplaceWhat(
          "{#"
              + group2Text
              + "}"
              + (gradientColor.getStartingTextFormat() == null
                  ? ""
                  : gradientColor.getStartingTextFormat())
              + group4Text);
      gradientColor.addToModifiedText(group4Text);
      gradientColor.setEndingColor(group5Text);
      gradientColor.setNextChar(group7Text);
      gradientColor.setPreviousDelta(distanceDelta);
      gradientColor.setPrevDistance(number);
    }

    if (gradientColor != null) {
      gradientColor.calculateFinalString();
      arrayList.add(gradientColor);
    }

    for (GradientColor finishedGradient : arrayList) {
      string = string.replace(finishedGradient.getReplaceWhat(), finishedGradient.getReplaceWith());
    }

    if (!arrayList.isEmpty() && !text.equals(string)) {
      string = deconvert(string);
    }

    return string;
  }

  private static String legacyCodeToHex(char ch) {
    switch (Character.toLowerCase(ch)) {
      case '0':
        return CMICustomColors.Black.getName();
      case '1':
        return CMICustomColors.Dark_Blue.getName();
      case '2':
        return CMICustomColors.Dark_Green.getName();
      case '3':
        return CMICustomColors.Dark_Aqua.getName();
      case '4':
        return CMICustomColors.Dark_Red.getName();
      case '5':
        return CMICustomColors.Dark_Purple.getName();
      case '6':
        return CMICustomColors.Gold.getName();
      case '7':
        return CMICustomColors.Gray.getName();
      case '8':
        return CMICustomColors.Dark_Gray.getName();
      case '9':
        return CMICustomColors.Blue.getName();
      case 'a':
        return CMICustomColors.Green.getName();
      case 'b':
        return CMICustomColors.Aqua.getName();
      case 'c':
        return CMICustomColors.Red.getName();
      case 'd':
        return CMICustomColors.Light_Purple.getName();
      case 'e':
        return CMICustomColors.Yellow.getName();
      case 'f':
        return CMICustomColors.White.getName();
      default:
        return null;
    }
  }

  private static boolean isHexDigit(char ch) {
    return ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
  }

  private static String convertLegacyToHex(String string) {
    StringBuilder stringBuilder = new StringBuilder();

    for (int value = 0; value < string.length(); value++) {
      char ch = string.charAt(value);
      if (ch == '&') {
        if (value + 13 < string.length()
            && (string.charAt(value + 1) == 'x' || string.charAt(value + 1) == 'X')) {
          StringBuilder hexBuilder = new StringBuilder();
          boolean flag = true;

          for (int digitIndex = 0; digitIndex < 6; digitIndex++) {
            int digitPos = value + 2 + digitIndex * 2;
            if (string.charAt(digitPos) != '&') {
              flag = false;
              break;
            }

            char hexDigit = string.charAt(digitPos + 1);
            if (!isHexDigit(hexDigit)) {
              flag = false;
              break;
            }

            hexBuilder.append(hexDigit);
          }

          if (flag) {
            stringBuilder.append("{#").append(hexBuilder).append("}");
            value += 13;
            continue;
          }
        }

        if (value + 1 < string.length()) {
          char legacyChar = string.charAt(value + 1);
          String hexCode = legacyCodeToHex(legacyChar);
          if (hexCode != null) {
            stringBuilder.append("{#").append(hexCode).append("}");
            value++;
            continue;
          }
        }
      }

      stringBuilder.append(ch);
    }

    return stringBuilder.toString();
  }
}
