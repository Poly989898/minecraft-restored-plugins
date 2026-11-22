package com.Zrips.CMI.Modules.Dialogs;

public class CMIDialogButtonUrl extends CMIDialogButton {
  public CMIDialogButtonUrl(String string, String urlValue) {
    super(string);
    this.setAction(new CMIDialogAction(urlValue, CMIDialogButtonType.URL));
  }
}
