package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;

public class CMIUserBalanceChangeEvent extends CMIUserEvent {
  private CMIUser source;
  private double from;
  private double to;
  private String actionType;

  @Deprecated
  public CMIUserBalanceChangeEvent(CMIUser cmiUser, double number, double toBalance) {
    this(cmiUser, number, toBalance, "Unknown");
  }

  public CMIUserBalanceChangeEvent(
      CMIUser cmiUser, double number, double toBalance, String string, CMIUser sourceUser) {
    super(cmiUser, true);
    this.from = number;
    this.to = toBalance;
    this.actionType = string;
    this.source = sourceUser;
  }

  @Deprecated
  public CMIUserBalanceChangeEvent(
      CMIUser cmiUser, double number, double toBalance, String string) {
    this(cmiUser, number, toBalance, string, null);
  }

  public double getFrom() {
    return this.from;
  }

  public double getTo() {
    return this.to;
  }

  public String getActionType() {
    return this.actionType;
  }

  public void setActionType(String string) {
    this.actionType = string;
  }

  public CMIUser getSource() {
    return this.source;
  }
}
