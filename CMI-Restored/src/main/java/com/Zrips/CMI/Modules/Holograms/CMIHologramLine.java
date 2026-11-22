package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Images.CMIImage;
import net.Zrips.CMILib.Version.Version;

public class CMIHologramLine {
  private String originalFulltext;
  private String command;
  private CMIHologramTextSettings settings = null;
  private List<CMIHologramLinePortion> portions = new ArrayList<>();
  private CMIHoloLineType type = CMIHoloLineType.unknown;
  private double width = 0.0;
  static Pattern pattern = Pattern.compile("<T>(.*?)(?=<T>|$)", 32);

  public CMIHologramLine clone() {
    CMIHologramLine cmiHologramLine = new CMIHologramLine();
    cmiHologramLine.command = this.command;
    cmiHologramLine.portions = new ArrayList<>(this.portions);
    cmiHologramLine.settings = this.settings;
    cmiHologramLine.type = this.type;
    cmiHologramLine.width = this.width;
    return cmiHologramLine;
  }

  public CMIHologramLine(CMIHologramTextSettings cmiHologramTextSettings, String string) {
    this.settings = cmiHologramTextSettings;
    this.setText(string);
  }

  public CMIHologramLine() {}

  private void recheck() {
    String string = this.getText();
    this.portions.clear();
    if (string.startsWith(CMIImage.imageIndicator)
        && Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      this.portions.add(new CMIHologramLinePortion(new CMIHologramLineImage(string)));
      this.setType(CMIHoloLineType.image);
    } else {
      if (string.equals("_")) {
        string = "";
      }

      boolean flag =
          CMI.getInstance()
              .getPlaceholderAPIManager()
              .containsPlaceHolder(CMIChatColor.stripColor(string));
      if (!string.startsWith("SICON:") && !string.startsWith("ICON:")) {
        this.setType(CMIHoloLineType.plainText);
        if (string.startsWith("<T>")) {
          for (String block : splitBlocks(string)) {
            String blockCopy = block;
            String rawBlock = null;
            String clickActionRaw = null;

            try {
              String rawLine = null;
              if (block.contains("<H>") && block.contains("</H>")) {
                rawLine = block.replaceAll(".*\\<H>|\\</H>.*", "");
              }

              String clickActionLine = null;
              if (block.contains("<C>") && block.contains("</C>")) {
                clickActionLine = block.replaceAll(".*\\<C>|\\</C>.*", "");
              }

              String plainText = block.replaceAll(".*\\<T>|\\</T>.*", "");
              if (rawLine != null && !rawLine.isEmpty()) {
                rawBlock = rawLine;
              }

              if (clickActionLine != null && !clickActionLine.isEmpty()) {
                clickActionRaw = clickActionLine;
              }

              if (plainText != null && !plainText.isEmpty()) {
                blockCopy = plainText;
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }

            CMIHologramLineBase cmiHologramLineBase = null;
            if (flag) {
              cmiHologramLineBase = new CMIHologramLineDynamicText(this.settings, blockCopy);
            } else {
              cmiHologramLineBase = new CMIHologramLineStaticText(this.settings, blockCopy);
            }

            CMIHologramLinePortion cmiHologramLinePortion =
                new CMIHologramLinePortion(cmiHologramLineBase);
            if (rawBlock != null) {
              if (flag) {
                cmiHologramLineBase = new CMIHologramLineDynamicText(this.settings, rawBlock);
              } else {
                cmiHologramLineBase = new CMIHologramLineStaticText(this.settings, rawBlock);
              }

              cmiHologramLinePortion.setHoverLineData(cmiHologramLineBase);
            }

            if (clickActionRaw != null) {
              cmiHologramLinePortion.setClickActions(new CMIHologramClickAction(clickActionRaw));
            }

            this.portions.add(cmiHologramLinePortion);
          }
        } else {
          CMIHologramLineStaticText cmiHologramLineStaticText = null;
          if (flag) {
            cmiHologramLineStaticText = new CMIHologramLineDynamicText(this.settings, string);
          } else {
            cmiHologramLineStaticText = new CMIHologramLineStaticText(this.settings, string);
          }

          cmiHologramLineStaticText.setDynamic(flag);
          this.portions.add(new CMIHologramLinePortion(cmiHologramLineStaticText));
        }
      } else {
        CMIHoloLineType cmiHoloLineType = CMIHoloLineType.staticIcon;
        if (string.startsWith("SICON:")) {
          string = string.substring("SICON:".length());
        } else if (string.startsWith("ICON:")) {
          string = string.substring("ICON:".length());
          cmiHoloLineType = CMIHoloLineType.icon;
        }

        CMIHologramLineBase lineBase = new CMIHologramLineIcon(string).setType(cmiHoloLineType);
        lineBase.setDynamic(flag);
        this.portions.add(new CMIHologramLinePortion(lineBase));
        this.setType(cmiHoloLineType);
      }
    }
  }

  private static List<String> splitBlocks(String string) {
    ArrayList<String> arrayList = new ArrayList();
    Matcher matcher = pattern.matcher(string);

    while (matcher.find()) {
      arrayList.add("<T>" + matcher.group(1));
    }

    return arrayList;
  }

  public List<CMIHologramLinePortion> getLinePortions() {
    return this.portions;
  }

  public String getCommand() {
    return this.command;
  }

  public String getText() {
    return this.originalFulltext;
  }

  public CMIHologramLine setText(String string) {
    this.originalFulltext = string;
    this.recheck();
    this.calculateLineWidths();
    return this;
  }

  private void calculateLineWidths() {
    this.width = 0.0;
    ArrayList<Double> arrayList = new ArrayList();

    for (CMIHologramLinePortion cmiHologramLinePortion : this.portions) {
      double number = cmiHologramLinePortion.calculateWidth();
      this.width += number;
      arrayList.add(number);
    }

    double x = 0.0;

    for (int value = 0; value < this.portions.size(); value++) {
      CMIHologramLinePortion portion = this.portions.get(value);
      double portionWidth = (Double) arrayList.get(value) / this.width;
      portion.setLineRanges(x, x + portionWidth);
      x += portionWidth;
    }

    this.width = this.width * CMIFont.multy;
  }

  public int getParts() {
    return this.portions.size();
  }

  public CMIHologramLinePortion getPortionByRange(
      double number, CMIHologramPage cmiHologramPage, CMITextAlignment cmiTextAlignment) {
    if (this.settings != null) {
      double width = cmiHologramPage.getWidth();
      double originalWidth = this.getWidth();
      switch (cmiTextAlignment) {
        case LEFT:
          number = number * width / originalWidth;
          break;
        case CENTER:
          double centerOffset = (width - originalWidth) / 2.0;
          number = (number * width - centerOffset) / originalWidth;
          break;
        case RIGHT:
          number = (number * width - (width - originalWidth)) / originalWidth;
      }
    }

    number = CMINumber.clamp(number, -0.01, 1.01);
    if (this.portions.isEmpty()) {
      return null;
    }

    if (this.portions.size() == 1) {
      return this.portions.get(0);
    }

    for (CMIHologramLinePortion cmiHologramLinePortion : this.portions) {
      if (cmiHologramLinePortion.isInRange(number)) {
        return cmiHologramLinePortion;
      }
    }

    return null;
  }

  public CMIHoloLineType getType() {
    return this.type;
  }

  private void setType(CMIHoloLineType cmiHoloLineType) {
    this.type = cmiHoloLineType;
  }

  public double getWidth() {
    return this.width;
  }
}
