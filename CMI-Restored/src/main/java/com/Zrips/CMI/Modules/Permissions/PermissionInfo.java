package com.Zrips.CMI.Modules.Permissions;

import java.util.HashSet;
import java.util.Set;

public class PermissionInfo {
  private String permission;
  private boolean enabled = false;
  private Long delay = 1000L;
  private Long lastChecked = null;
  private Double maxValue = null;
  private Double minValue = null;
  private Set<String> values = new HashSet<>();

  public PermissionInfo(String string, Long delayValue) {
    this.permission = string;
    if (delayValue != null) {
      this.delay = delayValue;
    }
  }

  public boolean isTimeToRecalculate() {
    return this.lastChecked == null || this.delay + this.lastChecked < System.currentTimeMillis();
  }

  public String getPermission() {
    return this.permission;
  }

  public void setPermission(String string) {
    this.permission = string;
  }

  public Long getDelay() {
    return this.delay;
  }

  public void setDelay(long longValue) {
    this.delay = longValue;
  }

  public Long getLastChecked() {
    if (this.lastChecked == null) {
      this.lastChecked = System.currentTimeMillis();
    }

    return this.lastChecked;
  }

  public void setLastChecked(long longValue) {
    this.lastChecked = longValue;
  }

  public Double getMaxValue() {
    return this.maxValue;
  }

  public Double getMaxValue(double number) {
    return this.maxValue == null ? number : (this.maxValue > number ? this.maxValue : number);
  }

  public int getMaxValue(int value) {
    return this.maxValue == null
        ? value
        : (this.maxValue > value ? this.maxValue.intValue() : value);
  }

  public void setMaxValue(Double newMaxValue) {
    this.maxValue = newMaxValue;
  }

  public Double getMinValue() {
    return this.minValue;
  }

  public Double getMinValue(double number) {
    return this.minValue == null ? number : (this.minValue < number ? this.minValue : number);
  }

  public int getMinValue(int value) {
    return this.minValue == null
        ? value
        : (this.minValue < value ? this.minValue.intValue() : value);
  }

  public void setMinValue(Double newMinValue) {
    this.minValue = newMinValue;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public Set<String> getValues() {
    return this.values;
  }

  public void addValue(String string) {
    this.values.add(string);
  }
}
