package com.Zrips.CMI.Modules.Placeholders;

public class PlaceholderCache {
  private long validUntil = 0L;
  private String value = null;

  public long getValidUntil() {
    return this.validUntil;
  }

  public void setValidUntil(long longValue) {
    this.validUntil = longValue;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String string) {
    this.value = string;
  }
}
