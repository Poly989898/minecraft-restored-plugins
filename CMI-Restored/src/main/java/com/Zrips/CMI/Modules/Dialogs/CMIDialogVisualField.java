package com.Zrips.CMI.Modules.Dialogs;

import java.util.Map;
import javax.annotation.Nonnull;

public class CMIDialogVisualField {
  private CMICustomDialogCondition condition = new CMICustomDialogCondition();

  public Map<String, Object> serialize() {
    return null;
  }

  @Nonnull
  public CMICustomDialogCondition getCondition() {
    return this.condition;
  }

  public CMIDialogVisualField setCondition(
      @Nonnull CMICustomDialogCondition cmiCustomDialogCondition) {
    this.condition = cmiCustomDialogCondition;
    return this;
  }
}
