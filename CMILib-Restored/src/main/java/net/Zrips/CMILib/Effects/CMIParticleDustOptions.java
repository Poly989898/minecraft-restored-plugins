package net.Zrips.CMILib.Effects;

import net.Zrips.CMILib.Container.CMINumber;
import org.bukkit.Color;

public class CMIParticleDustOptions extends CMIParticleColor {
  private float size = 1.0F;

  public CMIParticleDustOptions(Color color, float size) {
    super(color);
    this.size = size;
  }

  public float getSize() {
    return this.size;
  }

  public void setSize(float value) {
    this.size = CMINumber.clampValue(value, 0.1F, 4.0F);
  }
}
