package com.Zrips.CMI.Modules.Search;

import java.util.UUID;

public class CMIDataResultValue extends CMIDataResultPlayerBase {
  int amount = 0;

  public CMIDataResultValue(
      CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType, UUID playerUuid, int value) {
    super(cmiPlayerSearchPlaceType, playerUuid);
    this.amount = value;
  }

  public int getAmount() {
    return this.amount;
  }

  public CMIDataResultValue addAmount(int value) {
    this.amount += value;
    return this;
  }

  @Override
  public String getValueOutput() {
    return String.valueOf(this.amount);
  }
}
