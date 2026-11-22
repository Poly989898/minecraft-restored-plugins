package com.Zrips.CMI.Modules.Mirror;

public class CMIRotationAngle {
  private float pitch;
  private float yaw;

  public CMIRotationAngle(float floatValue, float yaw) {
    this.pitch = floatValue;
    this.yaw = yaw;
  }

  public float getPitch() {
    return this.pitch;
  }

  public float getYaw() {
    return this.yaw;
  }

  public void setPitch(float floatValue) {
    this.pitch = floatValue;
  }

  public void setYaw(float floatValue) {
    this.yaw = floatValue;
  }
}
