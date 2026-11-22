package com.Zrips.CMI.Modules.Recipes;

import net.Zrips.CMILib.Recipes.CMIRecipeCraftData;

public class recipeInformation {
  private boolean exact = true;
  private String customId = null;
  private CMIRecipeCraftData craftData = null;

  public boolean isExact() {
    return this.exact;
  }

  public void setExact(boolean flag) {
    this.exact = flag;
  }

  public String getCustomId() {
    return this.customId;
  }

  public void setCustomId(String string) {
    this.customId = string;
  }

  public CMIRecipeCraftData getCraftData() {
    return this.craftData;
  }

  public void setCraftData(CMIRecipeCraftData cmiRecipeCraftData) {
    this.craftData = cmiRecipeCraftData;
  }
}
