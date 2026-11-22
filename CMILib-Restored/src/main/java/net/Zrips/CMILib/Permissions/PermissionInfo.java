package net.Zrips.CMILib.Permissions;

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

  public PermissionInfo(String permission, Long delay) {
    this.permission = permission;
    if (delay != null) {
      this.delay = delay;
    }
  }

  public boolean isTimeToRecalculate() {
    return this.lastChecked == null || this.delay + this.lastChecked < System.currentTimeMillis();
  }

  public String getPermission() {
    return this.permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Long getDelay() {
    return this.delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public Long getLastChecked() {
    if (this.lastChecked == null) {
      this.lastChecked = System.currentTimeMillis();
    }

    return this.lastChecked;
  }

  public void setLastChecked(long lastChecked) {
    this.lastChecked = lastChecked;
  }

  public Double getMaxValue() {
    return this.maxValue;
  }

  public Double getMaxValue(double defaultV) {
    return this.maxValue == null ? defaultV : (this.maxValue > defaultV ? this.maxValue : defaultV);
  }

  public int getMaxValue(int defaultV) {
    return this.maxValue == null
        ? defaultV
        : (this.maxValue > defaultV ? this.maxValue.intValue() : defaultV);
  }

  public void setMaxValue(Double maxValue) {
    this.maxValue = maxValue;
  }

  public Double getMinValue() {
    return this.minValue;
  }

  public Double getMinValue(double defaultV) {
    return this.minValue == null ? defaultV : (this.minValue < defaultV ? this.minValue : defaultV);
  }

  public int getMinValue(int defaultV) {
    return this.minValue == null
        ? defaultV
        : (this.minValue < defaultV ? this.minValue.intValue() : defaultV);
  }

  public void setMinValue(Double minValue) {
    this.minValue = minValue;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Set<String> getValues() {
    return this.values;
  }

  public void addValue(String value) {
    this.values.add(value);
  }
}
