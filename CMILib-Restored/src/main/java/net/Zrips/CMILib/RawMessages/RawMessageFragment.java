package net.Zrips.CMILib.RawMessages;

import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class RawMessageFragment {
  private CMIChatColor lastColor = null;
  private Set<CMIChatColor> formats = new HashSet<>();
  private String font = null;
  private String text = "";

  public RawMessageFragment() {}

  public RawMessageFragment(RawMessageFragment old) {
    this.lastColor = old.lastColor;
    this.formats = new HashSet<>(old.formats);
    this.font = old.font;
  }

  public CMIChatColor getLastColor() {
    return this.lastColor;
  }

  public void setLastColor(CMIChatColor lastColor) {
    this.lastColor = lastColor;
    this.formats.clear();
  }

  public Set<CMIChatColor> getFormats() {
    return this.formats;
  }

  public void setFormats(Set<CMIChatColor> formats) {
    this.formats = formats;
  }

  public void addFormat(CMIChatColor format) {
    if (format.isReset()) {
      this.formats.clear();
      this.lastColor = null;
    } else {
      this.formats.add(format);
    }
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getFont() {
    return this.font;
  }

  public void setFont(String font) {
    if (font.startsWith("{@") && font.length() > "{@".length()) {
      font = font.substring("{@".length());
    }

    if (font.endsWith("}") && font.length() > "}".length()) {
      font = font.substring(0, font.length() - "}".length());
    }

    this.font = font;
  }
}
