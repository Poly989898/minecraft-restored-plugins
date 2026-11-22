package com.Zrips.CMI.Modules.InteractiveCommand;

import java.util.UUID;

public class CMINPC {
  private UUID uuid = null;
  private Integer id = null;
  private UUID secondaryUuid = null;

  public CMINPC(int value) {
    this.id = value;
  }

  public CMINPC(UUID newUuid) {
    this.uuid = newUuid;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public CMINPC setUuid(UUID newUuid) {
    this.uuid = newUuid;
    return this;
  }

  public Integer getId() {
    return this.id;
  }

  public CMINPC setId(Integer integer) {
    this.id = integer;
    return this;
  }

  public UUID getSecondaryUUID() {
    return this.secondaryUuid;
  }

  public CMINPC setSecondaryUUID(UUID newSecondaryUuid) {
    this.secondaryUuid = newSecondaryUuid;
    return this;
  }
}
