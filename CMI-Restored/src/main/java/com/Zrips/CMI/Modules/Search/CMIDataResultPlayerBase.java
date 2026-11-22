package com.Zrips.CMI.Modules.Search;

import java.util.UUID;

public class CMIDataResultPlayerBase extends CMIDataResultBase {
  CMIPlayerSearchPlaceType type;
  UUID uuid;

  public CMIDataResultPlayerBase(
      CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType, UUID playerUuid) {
    this.type = cmiPlayerSearchPlaceType;
    this.uuid = playerUuid;
  }

  public CMIPlayerSearchPlaceType getType() {
    return this.type;
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public String getValueOutput() {
    return "";
  }
}
