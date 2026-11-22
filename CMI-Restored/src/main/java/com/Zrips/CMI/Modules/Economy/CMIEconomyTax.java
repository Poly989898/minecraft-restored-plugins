package com.Zrips.CMI.Modules.Economy;

import net.Zrips.CMILib.Container.CMINumber;

public class CMIEconomyTax {
  private CMIEconomyTaxType type = CMIEconomyTaxType.None;
  private double amount = 0.0;
  private double min = 0.0;
  private double max = 0.0;
  private boolean deductOnTop = true;

  public CMIEconomyTax() {}

  public CMIEconomyTax(CMIEconomyTaxType cmiEconomyTaxType, double number) {
    this(cmiEconomyTaxType, number, 0.0, 0.0);
  }

  public CMIEconomyTax(
      CMIEconomyTaxType cmiEconomyTaxType, double number, double minAmount, double maxAmount) {
    this.setType(cmiEconomyTaxType);
    this.setAmount(number);
    this.setMin(minAmount);
    this.setMax(maxAmount);
  }

  public CMIEconomyTaxType getType() {
    return this.type;
  }

  public CMIEconomyTax setType(CMIEconomyTaxType cmiEconomyTaxType) {
    this.type = cmiEconomyTaxType;
    return this;
  }

  public double getAmount() {
    return this.amount;
  }

  public CMIEconomyTax setAmount(double number) {
    this.amount = CMINumber.clamp(number, 0.0, Double.MAX_VALUE);
    return this;
  }

  public double getMin() {
    return this.min;
  }

  public CMIEconomyTax setMin(double number) {
    this.min = CMINumber.clamp(number, 0.0, Double.MAX_VALUE);
    return this;
  }

  public double getMax() {
    return this.max;
  }

  public CMIEconomyTax setMax(double number) {
    this.max = CMINumber.clamp(number, 0.0, Double.MAX_VALUE);
    return this;
  }

  public boolean isDeductOnTop() {
    return this.deductOnTop;
  }

  public void setDeductOnTop(boolean flag) {
    this.deductOnTop = flag;
  }

  public double getTaxAmount(double number) {
    double taxAmount = 0.0;
    switch (this.getType()) {
      case Flat:
        taxAmount = this.getAmount();
        break;
      case Percentage:
        taxAmount = number * (this.getAmount() / 100.0);
    }

    if (this.getMax() > 0.0) {
      taxAmount =
          CMINumber.clamp(
              taxAmount, this.getMin(), this.getMax() == 0.0 ? Double.MAX_VALUE : this.getMax());
    }

    return taxAmount;
  }
}
