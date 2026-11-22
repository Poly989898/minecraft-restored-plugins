package com.Zrips.CMI.Modules.Economy;

public enum CMIEconomyTaxType {
  Flat,
  Percentage,
  None;

  public static CMIEconomyTaxType get(String string) {
    CMIEconomyTaxType[] cmiEconomyTaxTypes;
    int value = (cmiEconomyTaxTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMIEconomyTaxType cmiEconomyTaxType = cmiEconomyTaxTypes[index];
      if (cmiEconomyTaxType.name().equalsIgnoreCase(string)) {
        return cmiEconomyTaxType;
      }
    }

    return None;
  }
}
