package com.Zrips.CMI.Modules.TagName;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class TagNameManager {
  private CMI plugin;

  public TagNameManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {}

  private static String Color(String string) {
    return CMIChatColor.translate(string);
  }

  public enum TeamAction {
    CREATE,
    UPDATE,
    DESTROY;
  }
}
