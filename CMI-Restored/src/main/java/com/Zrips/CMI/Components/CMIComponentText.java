package com.Zrips.CMI.Components;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CMIComponentText extends CMIComponentRoot {
  private String hoverText = null;

  public CMIComponentText() {}

  public CMIComponentText(@Nonnull String string) {
    this.setText(string);
  }

  @Nullable
  public String getHoverText() {
    return this.hoverText;
  }

  public CMIComponentText setHoverText(@Nullable String string) {
    this.hoverText = string;
    return this;
  }
}
