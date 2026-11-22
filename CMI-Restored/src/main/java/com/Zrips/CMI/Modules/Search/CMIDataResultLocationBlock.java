package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Items.CMIMaterial;

public class CMIDataResultLocationBlock extends CMIDataResultLocationBase {
  CMIMaterial type;

  public CMIDataResultLocationBlock(
      CMIMaterial cmiMaterial, CMIVectorInt3D cmiVectorInt3D, int value) {
    super(cmiVectorInt3D, value);
    this.type = cmiMaterial;
  }

  public CMIMaterial getType() {
    return this.type;
  }

  @Override
  public String getValueOutput() {
    return this.type == null ? "Unknown" : this.type.toString();
  }
}
