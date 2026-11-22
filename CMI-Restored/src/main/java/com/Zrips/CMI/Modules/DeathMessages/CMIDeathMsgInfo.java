package com.Zrips.CMI.Modules.DeathMessages;

import java.util.UUID;

class CMIDeathMsgInfo {
  private UUID uuid = null;
  private CMIKillerInfo killerInfo;
  private CMIKillerInfo PreviousDamageInfo;
  private String prefixPath = null;
  private String suffixPath = null;
  private String basePath = null;
  private String message = "";

  public CMIDeathMsgInfo(UUID newUuid, CMIKillerInfo cmiKillerInfo) {
    this.uuid = newUuid;
    this.killerInfo = cmiKillerInfo;
  }

  public CMIKillerInfo getKillerInfo() {
    return this.killerInfo;
  }

  public void setKillerInfo(CMIKillerInfo cmiKillerInfo) {
    this.killerInfo = cmiKillerInfo;
  }

  public String getPrefixPath() {
    return this.prefixPath;
  }

  public void setPrefixPath(String string) {
    this.prefixPath = string;
  }

  public String getSuffixPath() {
    return this.suffixPath;
  }

  public void setSuffixPath(String string) {
    this.suffixPath = string;
  }

  public CMIKillerInfo getPreviousDamageInfo() {
    return this.PreviousDamageInfo;
  }

  public void setPreviousDamageInfo(CMIKillerInfo cmiKillerInfo) {
    this.PreviousDamageInfo = cmiKillerInfo;
  }

  public String getBasePath() {
    return this.basePath;
  }

  public void setBasePath(String string) {
    this.basePath = string;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }

  public UUID getUniqueId() {
    return this.uuid;
  }
}
