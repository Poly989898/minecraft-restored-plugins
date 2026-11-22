package com.Zrips.CMI.Modules.TabList;

public enum CMITabSortingOrder {
  ASC,
  DESC;

  public static CMITabSortingOrder getByName(String string) {
    CMITabSortingOrder[] cmiTabSortingOrders;
    int value = (cmiTabSortingOrders = values()).length;

    for (int index = 0; index < value; index++) {
      CMITabSortingOrder cmiTabSortingOrder = cmiTabSortingOrders[index];
      if (cmiTabSortingOrder.toString().equalsIgnoreCase(string)) {
        return cmiTabSortingOrder;
      }
    }

    return null;
  }
}
