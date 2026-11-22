package net.Zrips.CMILib.Effects;

import net.Zrips.CMILib.Items.CMIMaterial;

public class CMIParticleBlockData extends CMIParticleMaterial {
  public CMIParticleBlockData(CMIMaterial material) {
    super(material != null && material.isBlock() ? material : CMIMaterial.STONE);
  }

  @Override
  public void setMaterial(CMIMaterial material) {
    if (material == null || !material.isBlock()) {
      material = CMIMaterial.STONE;
    }

    super.setMaterial(material);
  }
}
