package com.Zrips.CMI.Modules.SpecializedCommands;

public class SpecCMDCooldown {
  private double cooldown = 0.0;
  private String cmd;

  @Deprecated
  public long getCooldown() {
    return (long) this.cooldown;
  }

  public double getCD() {
    return this.cooldown;
  }

  @Deprecated
  public SpecCMDCooldown setCooldown(long longValue) {
    this.cooldown = longValue;
    return this;
  }

  public SpecCMDCooldown setCooldown(double number) {
    this.cooldown = number;
    return this;
  }

  public String getCmd() {
    return this.cmd;
  }

  public String getBaseCmd() {
    return this.cmd.contains(" ") ? this.cmd.split(" ")[0] : this.cmd;
  }

  public SpecCMDCooldown setCmd(String string) {
    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    this.cmd = string;
    return this;
  }

  public long untilNextUse(Long lastUse) {
    return (long) (lastUse.longValue() + this.getCD() * 1000.0 - System.currentTimeMillis());
  }
}
