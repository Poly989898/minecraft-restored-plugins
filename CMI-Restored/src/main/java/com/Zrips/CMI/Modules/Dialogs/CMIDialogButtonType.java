package com.Zrips.CMI.Modules.Dialogs;

import javax.annotation.Nullable;
import net.Zrips.CMILib.Container.CMIText;

public enum CMIDialogButtonType {
  CUSTOM(null),
  COMMANDS,
  URL,
  OPENDIALOG("OpenDialog"),
  SUGGESTION;

  String configEntry = null;

  CMIDialogButtonType() {
    this.configEntry = CMIText.firstToUpperCase(this.toString());
  }

  CMIDialogButtonType(String string) {
    this.configEntry = string;
  }

  @Nullable
  public String getConfigEntry() {
    return this.configEntry;
  }
}
