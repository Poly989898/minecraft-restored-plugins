package com.Zrips.CMI.Modules.Homes;

import net.Zrips.CMILib.Items.CMIMaterial;
import org.jetbrains.annotations.Nullable;

public class CMIHomeSettings {
  private boolean privateH = false;
  private boolean bed = false;
  private boolean fav = false;
  private int slot = 0;
  private CMIMaterial material = null;

  public boolean isPrivate() {
    return this.privateH;
  }

  public void setPrivate(boolean flag) {
    this.privateH = flag;
  }

  public boolean isBed() {
    return this.bed;
  }

  public void setBed(boolean flag) {
    this.bed = flag;
  }

  public boolean isFav() {
    return this.fav;
  }

  public void setFav(boolean flag) {
    this.fav = flag;
  }

  public int getSlot() {
    return this.slot;
  }

  public void setSlot(int value) {
    this.slot = value <= 0 ? 0 : value;
  }

  @Nullable
  public CMIMaterial getMaterial() {
    return this.material;
  }

  public void setMaterial(@Nullable CMIMaterial cmiMaterial) {
    this.material = cmiMaterial;
  }
}
