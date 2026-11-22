package com.Zrips.CMI.Components;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CMIComponentAction {
  private CMIComponentActionType action = CMIComponentActionType.none;
  private String clickAction = null;

  public CMIComponentAction() {}

  public CMIComponentAction(CMIComponentActionType cmiComponentActionType, String string) {
    this.action = cmiComponentActionType;
    this.clickAction = string;
  }

  @Nonnull
  public String getClickAction() {
    return this.clickAction;
  }

  @Nonnull
  public CMIComponentActionType getClickActionType() {
    return this.action == null ? CMIComponentActionType.none : this.action;
  }

  public CMIComponentAction setClickAction(
      @Nullable CMIComponentActionType cmiComponentActionType, @Nullable String string) {
    this.action = cmiComponentActionType;
    this.clickAction = string;
    return this;
  }
}
