package com.Zrips.CMI.Modules.Search;

import java.util.UUID;

public class CMIDataResultItem extends CMIDataResultValue {
  String identifier;

  public CMIDataResultItem(
      CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType, UUID playerUuid, int value) {
    super(cmiPlayerSearchPlaceType, playerUuid, value);
  }

  public String getSubIdentifier() {
    return this.identifier;
  }

  public void setSubIdentifier(String string) {
    this.identifier = string;
  }

  @Override
  public String getValueOutput() {
    return String.valueOf(this.amount);
  }
}
