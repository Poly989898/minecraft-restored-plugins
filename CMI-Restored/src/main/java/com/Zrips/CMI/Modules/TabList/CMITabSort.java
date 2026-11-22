package com.Zrips.CMI.Modules.TabList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMITabSort {
  CMITabSortingType sortingType = CMITabSortingType.Weight;
  CMITabSortingOrder sortingOrder = CMITabSortingOrder.ASC;

  public CMITabSort(
      @NotNull CMITabSortingType cmiTabSortingType,
      @NotNull CMITabSortingOrder cmiTabSortingOrder) {
    this.sortingType = cmiTabSortingType;
    this.sortingOrder = cmiTabSortingOrder;
  }

  @NotNull
  public CMITabSortingType getSortingType() {
    return this.sortingType;
  }

  @NotNull
  public CMITabSortingOrder getSortingOrder() {
    return this.sortingOrder;
  }

  @NotNull
  @Override
  public String toString() {
    return this.sortingType.toString() + "-" + this.sortingOrder.toString();
  }

  @Nullable
  public static CMITabSort fromString(String string) {
    String[] strings = string.split("-");
    CMITabSortingType cmiTabSortingType = null;
    CMITabSortingOrder cmiTabSortingOrder = null;
    cmiTabSortingType = CMITabSortingType.getByName(strings[0]);
    if (strings.length > 1) {
      cmiTabSortingOrder = CMITabSortingOrder.getByName(strings[1]);
    }

    if (cmiTabSortingType == null) {
      return null;
    }

    if (cmiTabSortingOrder == null) {
      cmiTabSortingOrder = CMITabSortingOrder.ASC;
    }

    return new CMITabSort(cmiTabSortingType, cmiTabSortingOrder);
  }
}
