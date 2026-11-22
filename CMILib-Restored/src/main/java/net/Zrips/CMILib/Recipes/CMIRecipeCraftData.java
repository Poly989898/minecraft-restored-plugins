package net.Zrips.CMILib.Recipes;

import net.Zrips.CMILib.Version.Version;

public class CMIRecipeCraftData {
  private float exp = 0.0F;
  private int cookingTime = 20;

  public CMIRecipeCraftData(float exp, int cookingTime) {
    this.exp = exp;
    this.cookingTime = cookingTime;
  }

  public float getExp() {
    return this.exp;
  }

  public void setExp(float exp) {
    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1) && exp > 2.0F) {
      exp = 2.0F;
    } else if (!Version.isCurrentEqualOrHigher(Version.v1_17_R1) && exp > 1.0F) {
      exp = 1.0F;
    }

    if (exp < 0.0F) {
      exp = 0.0F;
    }

    this.exp = exp;
  }

  public int getCookingTime() {
    return this.cookingTime;
  }

  public void setCookingTime(int cookingTime) {
    this.cookingTime = cookingTime;
  }
}
