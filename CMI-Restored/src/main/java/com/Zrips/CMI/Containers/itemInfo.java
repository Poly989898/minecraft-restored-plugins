package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Material;

public class itemInfo {
  private Material mat = null;
  private int chance = 100;

  @Deprecated
  public itemInfo(CMIMaterial cmiMaterial) {
    this.mat = cmiMaterial.getMaterial();
  }

  public itemInfo(Material material) {
    this.mat = material;
  }

  @Deprecated
  public CMIMaterial getMat() {
    return CMIMaterial.get(this.mat);
  }

  public Material getMaterial() {
    return this.mat;
  }

  @Deprecated
  public void setId(CMIMaterial cmiMaterial) {
    this.mat = cmiMaterial.getMaterial();
  }

  public void setMaterial(Material material) {
    this.mat = material;
  }

  public int getChance() {
    return this.chance;
  }

  public void setChance(int value) {
    this.chance = value;
  }
}
