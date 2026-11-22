package com.Zrips.CMI.Modules.FlightCharge;

import com.Zrips.CMI.CMI;

public class FlightCharge {
  private double charge = 0.0;
  private double lastChange = 0.0;
  private boolean enabled = false;
  private boolean autoRecharge = false;
  private boolean moneyRecharge = false;
  private boolean expRecharge = false;
  private boolean informedMissingExp = false;
  private boolean informedMissingMoney = false;

  public double getCharge() {
    return this.charge;
  }

  @Deprecated
  public double getSafeCharge() {
    return this.charge;
  }

  public void setCharge(double number) {
    if (number < 0.0) {
      number = 0.0;
    }

    this.lastChange = this.charge == 0.0 ? 0.0 : number;
    this.charge = number;
    if (this.charge > CMI.getInstance().getFlightChargeManager().getMaxChargeLevel()) {
      this.charge = CMI.getInstance().getFlightChargeManager().getMaxChargeLevel();
    }
  }

  public void addCharge(double number) {
    if (number < 0.0) {
      number = 0.0;
    }

    this.lastChange = number;
    if (this.charge + number > this.getMax()) {
      this.lastChange = this.getMax() - this.charge;
    }

    this.charge += number;
    if (this.charge > 0.0
        && this.charge > CMI.getInstance().getFlightChargeManager().getMaxChargeLevel()) {
      this.charge = CMI.getInstance().getFlightChargeManager().getMaxChargeLevel();
    }
  }

  public void takeCharge(double number) {
    if (number < 0.0) {
      number = 0.0;
    }

    this.lastChange = -number;
    if (this.charge != 0.0) {
      this.charge -= number;
      if (this.charge < 0.0) {
        this.charge = 0.0;
      }

      if (this.charge > CMI.getInstance().getFlightChargeManager().getMaxChargeLevel()) {
        this.charge = CMI.getInstance().getFlightChargeManager().getMaxChargeLevel();
      }
    }
  }

  public int getMax() {
    return CMI.getInstance().getFlightChargeManager().getMaxChargeLevel();
  }

  public double getLastChange() {
    return this.lastChange;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public boolean isAutoRecharge() {
    return this.autoRecharge;
  }

  public void setAutoRecharge(boolean flag) {
    this.autoRecharge = flag;
  }

  public void setAutoMoneyRecharge(boolean flag) {
    this.moneyRecharge = flag;
  }

  public void setAutoMoneyRecharge() {
    this.moneyRecharge = true;
  }

  public void setAutoExpRecharge(boolean flag) {
    this.expRecharge = flag;
  }

  public void setAutoExpRecharge() {
    this.expRecharge = true;
  }

  public boolean isMoneyAutoRecharge() {
    return this.moneyRecharge;
  }

  public boolean isExpAutoRecharge() {
    return this.expRecharge;
  }

  public boolean isInformedMissingExp() {
    return this.informedMissingExp;
  }

  public void setInformedMissingExp(boolean flag) {
    this.informedMissingExp = flag;
  }

  public boolean isInformedMissingMoney() {
    return this.informedMissingMoney;
  }

  public void setInformedMissingMoney(boolean flag) {
    this.informedMissingMoney = flag;
  }
}
