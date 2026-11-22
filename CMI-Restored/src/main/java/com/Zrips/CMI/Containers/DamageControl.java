package com.Zrips.CMI.Containers;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageControl {
  private String node;
  private DamageCause cause;
  private double mult;

  public DamageControl(String string, DamageCause damageCause, double number) {
    this.node = string;
    this.cause = damageCause;
    this.mult = number;
  }

  public String getNode() {
    return this.node;
  }

  public void setNode(String string) {
    this.node = string;
  }

  public DamageCause getCause() {
    return this.cause;
  }

  public void setCause(DamageCause damageCause) {
    this.cause = damageCause;
  }

  public double getMult() {
    return this.mult;
  }

  public void setMult(double number) {
    this.mult = number;
  }
}
