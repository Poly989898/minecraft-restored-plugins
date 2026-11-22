package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class CMIHologramPage {
  private List<CMIHologramLine> lines = Collections.synchronizedList(new ArrayList<>());
  private List<String> commands = Collections.synchronizedList(new ArrayList<>());
  private double height = 0.0;
  private double width = 0.0;
  private int lineCount = 0;

  public List<CMIHologramLine> getLines() {
    return this.lines;
  }

  @Nullable
  public CMIHologramLine getLine(int value) {
    return value > this.lines.size() - 1 ? null : this.lines.get(value);
  }

  public void setLines(List<CMIHologramLine> list) {
    this.lines = list;
    this.calculateWidth();
  }

  public void addLine(int value, CMIHologramLine cmiHologramLine) {
    if (value > this.lines.size() - 1) {
      this.lines.add(cmiHologramLine);
    } else {
      this.lines.set(value, cmiHologramLine);
    }
  }

  public void addLine(CMIHologramLine cmiHologramLine) {
    this.lines.add(cmiHologramLine);
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void trimToLimit(int value) {
    synchronized (this.lines) {
      if (this.lines.size() > value) {
        this.lines.subList(value, this.lines.size()).clear();
      }
    }
  }

  public void setCommands(List<String> list) {
    this.commands = list;
  }

  public double getHeight() {
    return this.height;
  }

  public void setHeight(double number) {
    this.height = number;
  }

  public double getWidth() {
    return this.width;
  }

  @Deprecated
  public void setWidth(int value) {
    this.width = value;
  }

  public void setWidth(double number) {
    this.width = number;
  }

  public void calculateWidth() {
    double number = 0.0;

    for (CMIHologramLine cmiHologramLine : new ArrayList<>(this.getLines())) {
      switch (cmiHologramLine.getType()) {
        case plainText:
        case image:
          StringBuilder stringBuilder = new StringBuilder();

          for (CMIHologramLinePortion cmiHologramLinePortion : cmiHologramLine.getLinePortions()) {
            stringBuilder.append(cmiHologramLinePortion.getBaseLineData().getOriginalText());
          }

          String string =
              CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .replacePlaceHolders(CMIChatColor.stripColor(stringBuilder.toString()), "____");
          string = CMIComponentBase.stripMiniMessageTags(string);
          double textWidth = CMIFont.getWidth(string);
          if (textWidth > number) {
            number = textWidth;
          }
          break;
        case icon:
          if (15.0 > number) {
            number = 15.0;
          }
        case staticIcon:
      }
    }

    this.width = number * CMIFont.multy;
  }

  public int getLineCount() {
    return this.lineCount;
  }

  public CMIHologramPage setLineCount(int value) {
    this.lineCount = value;
    return this;
  }
}
