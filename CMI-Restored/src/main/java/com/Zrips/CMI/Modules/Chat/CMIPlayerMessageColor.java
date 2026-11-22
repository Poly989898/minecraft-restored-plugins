package com.Zrips.CMI.Modules.Chat;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class CMIPlayerMessageColor {
  private CMIChatColor color;
  private Set<CMIChatColor> formats = null;

  public CMIPlayerMessageColor(CMIChatColor cmiChatColor) {
    this(cmiChatColor, null);
  }

  public CMIPlayerMessageColor(CMIChatColor cmiChatColor, @Nullable Set<CMIChatColor> set) {
    this.color = cmiChatColor;
    this.formats = set == null ? null : (set.isEmpty() ? null : set);
  }

  public CMIChatColor getColor() {
    return this.color;
  }

  public void setColor(CMIChatColor cmiChatColor) {
    this.color = cmiChatColor;
  }

  public Set<CMIChatColor> getFormats() {
    return this.formats;
  }

  public void setFormat(Set<CMIChatColor> set) {
    this.formats = set;
  }

  @Override
  public String toString() {
    return this.color.getBukkitColorCode() + this.formatsToString();
  }

  public String formatsToString() {
    return this.formats != null && !this.formats.isEmpty()
        ? this.formats.stream()
            .<CharSequence>map(CMIChatColor::getBukkitColorCode)
            .collect(Collectors.joining())
        : "";
  }
}
