package net.Zrips.CMILib.Effects;

import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.Color;

public class CMIParticleTrail extends CMIParticleLocation {
  private int value = 20;
  private Color color = Color.fromRGB(200, 0, 50);

  public CMIParticleTrail(CMIVector3D target, Color color, int duration) {
    super(target);
    this.value = duration;
    this.setColor(color);
  }

  public int getDuration() {
    return this.value;
  }

  public void setDuration(int duration) {
    this.value = duration;
  }

  public Color getColor() {
    return this.color;
  }

  public void setColor(Color color) {
    this.color = color == null ? this.color : color;
  }
}
