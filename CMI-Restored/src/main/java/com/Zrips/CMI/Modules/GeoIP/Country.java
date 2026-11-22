package com.Zrips.CMI.Modules.GeoIP;

public class Country {
  private String code;
  private String name;

  public Country(String string, String displayName) {
    this.code = string;
    this.name = displayName;
  }

  public String getCode() {
    return this.code;
  }

  public String getName() {
    return this.name;
  }

  public void setCode(String string) {
    this.code = string;
  }

  public void setName(String string) {
    this.name = string;
  }
}
