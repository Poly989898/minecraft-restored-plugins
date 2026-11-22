package com.Zrips.CMI.Modules.Skin;

import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class CMISkin {
  private String name;
  private UUID uuid;
  private String skin;
  private String signature;
  private Long lastUpdate = 0L;
  private URL skinUrl = null;

  public CMISkin(String string, UUID skinUuid, String skinValue, String skinSignature) {
    this.name = string;
    this.uuid = skinUuid;
    this.skin = skinValue;
    this.signature = skinSignature;
    this.skinUrl = this.extractUrl();
  }

  private URL extractUrl() {
    String string = new String(Base64.getDecoder().decode(this.getSkin()));
    String urlKey = "\"url\"";
    int value = string.indexOf(urlKey);
    if (value < 0) {
      return null;
    }

    int colonIndex = string.indexOf(58, value);
    int openQuoteIndex = string.indexOf(34, colonIndex + 1);
    int closeQuoteIndex = string.indexOf(34, openQuoteIndex + 1);
    if (openQuoteIndex >= 0 && closeQuoteIndex >= 0) {
      String urlText = string.substring(openQuoteIndex + 1, closeQuoteIndex);

      try {
        return new URL(urlText);
      } catch (Exception exception) {
        return null;
      }
    } else {
      return null;
    }
  }

  public String getSkin() {
    return this.skin;
  }

  public void setSkin(String string) {
    this.skin = string;
  }

  public String getSignature() {
    return this.signature;
  }

  public void setSignature(String string) {
    this.signature = string;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(UUID newUuid) {
    this.uuid = newUuid;
  }

  public Long getLastUpdate() {
    return this.lastUpdate;
  }

  public void setLastUpdate(Long newLastUpdate) {
    this.lastUpdate = newLastUpdate;
  }

  public URL getSkinUrl() {
    return this.skinUrl;
  }
}
