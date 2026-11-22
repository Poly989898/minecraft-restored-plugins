package com.Zrips.CMI.Modules.Dialogs;

public enum CMIDialogConditionActionType {
  Equal("=="),
  MoreEqual(">="),
  LessEqual("<="),
  More(">"),
  Less("<");

  private String action;

  CMIDialogConditionActionType(String string) {
    this.action = string;
  }

  public static CMIDialogConditionActionType get(String string) {
    CMIDialogConditionActionType[] cmiDialogConditionActionTypes;
    int value = (cmiDialogConditionActionTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMIDialogConditionActionType cmiDialogConditionActionType =
          cmiDialogConditionActionTypes[index];
      if (string.contains(cmiDialogConditionActionType.getAction())) {
        return cmiDialogConditionActionType;
      }
    }

    return null;
  }

  public String getAction() {
    return this.action;
  }
}
