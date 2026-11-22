package com.Zrips.CMI.Modules.FlightCharge;

public class freeFall {
  private Float fallDistance = 0.0F;
  private Boolean jump = true;

  public Float getFallDistance() {
    return this.fallDistance;
  }

  public freeFall setFallDistance(Float newFallDistance) {
    this.fallDistance = newFallDistance;
    return this;
  }

  public Boolean getJump() {
    return this.jump;
  }

  public freeFall setJump(Boolean newJump) {
    this.jump = newJump;
    return this;
  }
}
