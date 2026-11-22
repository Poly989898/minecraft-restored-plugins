package com.Zrips.CMI.Modules.SpawnerCharge;

public class SCharges {
  private String name;
  private int startingCharges = 1;
  private int maxCharges = 1;
  private int cooldown = 3600;
  private int bonus = 0;

  public SCharges() {}

  public SCharges(String string, int value, int maxChargesParam, int cooldownParam) {
    this.name = string;
    this.startingCharges = value;
    this.maxCharges = maxChargesParam;
    this.cooldown = cooldownParam;
  }

  public int getMaxCharges() {
    return this.maxCharges;
  }

  public void setMaxCharges(int value) {
    this.maxCharges = value;
  }

  public int getCooldown() {
    return this.cooldown;
  }

  public void setCooldown(int value) {
    this.cooldown = value;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public int getStartingCharges() {
    return this.startingCharges;
  }

  public void setStartingCharges(int value) {
    this.startingCharges = value;
  }

  public int getBonus() {
    return this.bonus;
  }

  public void setBonus(int value) {
    this.bonus = value;
  }
}
