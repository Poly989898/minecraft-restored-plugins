package com.Zrips.CMI.Modules.GeoIP;

public class IPLocation {
  private String countryCode;
  private String countryName;
  private String region;
  private String city;
  private String postalCode;

  public String getCountryCode() {
    return this.countryCode;
  }

  public void setCountryCode(String string) {
    this.countryCode = string;
  }

  public String getCountryName() {
    return this.countryName;
  }

  public void setCountryName(String string) {
    this.countryName = string;
  }

  public String getRegion() {
    return this.region;
  }

  public void setRegion(String string) {
    this.region = string;
  }

  public String getCity() {
    return this.city == null ? "" : this.city;
  }

  public void setCity(String string) {
    this.city = string;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  public void setPostalCode(String string) {
    this.postalCode = string;
  }
}
