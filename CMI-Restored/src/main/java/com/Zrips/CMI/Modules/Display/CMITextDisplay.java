package com.Zrips.CMI.Modules.Display;

import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentPaper;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;

public class CMITextDisplay extends CMIDisplay {
  private TextDisplay td;

  public CMITextDisplay(Location location) {
    super(CMIDisplayType.Text, location);
    if (location != null) {
      try {
        this.td = (TextDisplay) this.display;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      this.setBillboard(Billboard.CENTER);
      this.setRange(32);
      this.setWidth(1.0);
      this.setHeight(1.0);
      this.setLocation(location);
    }
  }

  public String getText() {
    return this.td.getText();
  }

  public static String linesIntoString(List<String> list) {
    StringBuilder stringBuilder = new StringBuilder();
    list.forEach(
        string -> {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append("\n§r");
          }

          stringBuilder.append(string);
        });
    String textContent = stringBuilder.toString();
    if (textContent.length() >= 15000) {
      CMIMessages.consoleMessage(
          "&4Hologram text too long. Max 15000 characters, got " + textContent.length());
      textContent = textContent.substring(0, 15000);
    }

    return textContent;
  }

  public boolean setText(List<String> list) {
    return this.setText(linesIntoString(list));
  }

  public boolean setText(String string) {
    if (string == null || string.isEmpty()) {
      return false;
    }

    if (this.td == null) {
      return false;
    }

    try {
      this.td.setText(CMIChatColor.translate(string));
      return true;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return false;
    }
  }

  public boolean setText(CMIComponentBase cmiComponentBase) {
    if (this.td == null) {
      return false;
    }

    this.updateText(cmiComponentBase);
    return true;
  }

  private void updateText(CMIComponentBase cmiComponentBase) {
    if (CMIComponentBase.isValidVersion()) {
      this.td.text(((CMIComponentPaper) cmiComponentBase).getCombinedByPage());
    } else {
      this.td.setText(CMIChatColor.translate(cmiComponentBase.getTextOnly()));
    }
  }

  public int getLineWidth() {
    return this.td.getLineWidth();
  }

  public void setLineWidth(int value) {
    this.td.setLineWidth(value);
  }

  public byte getTextOpacity() {
    return this.td.getTextOpacity();
  }

  public void setTextOpacity(byte b) {
    this.td.setTextOpacity(b);
  }

  public TextAlignment getAligment() {
    return this.td.getAlignment();
  }

  public void setAligment(TextAlignment textAlignment) {
    this.td.setAlignment(textAlignment);
  }

  public void setDefaultBackground(boolean flag) {
    this.td.setDefaultBackground(flag);
  }

  public boolean isDefaultBackground() {
    return this.td.isDefaultBackground();
  }

  public void setBackgroundColor(Color color) {
    this.td.setBackgroundColor(color);
  }

  public void setBackgroundColor(CMIChatColor cmiChatColor) {
    this.td.setBackgroundColor(cmiChatColor.getRGBColor());
  }

  public Color getBackgroundColor() {
    return this.td.getBackgroundColor();
  }

  public void setFacing(double number, double yaw) {
    this.getLocation().setPitch((float) number + 1.0E-10F);
    this.getLocation().setYaw((float) yaw + 1.0E-10F);
  }

  public float getDisplayHeight() {
    return this.td.getDisplayHeight();
  }

  public void setShadowed(boolean flag) {
    if (this.td != null) {
      this.td.setShadowed(flag);
    }
  }

  public boolean isShadowed() {
    return this.td.isShadowed();
  }

  public void setSeeThrough(boolean flag) {
    this.td.setSeeThrough(flag);
  }

  public boolean isSeeThrough() {
    return this.td.isSeeThrough();
  }
}
