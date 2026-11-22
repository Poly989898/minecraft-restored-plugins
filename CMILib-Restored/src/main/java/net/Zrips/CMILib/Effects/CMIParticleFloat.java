package net.Zrips.CMILib.Effects;

public class CMIParticleFloat extends CMIParticleOptions {
  private float value = 0.0F;

  public CMIParticleFloat(float value) {
    this.value = value;
  }

  public float getValue() {
    return this.value;
  }

  public void setValue(float value) {
    this.value = value;
  }
}
