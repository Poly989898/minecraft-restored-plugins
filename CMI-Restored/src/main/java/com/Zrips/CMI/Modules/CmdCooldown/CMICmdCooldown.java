package com.Zrips.CMI.Modules.CmdCooldown;

import com.Zrips.CMI.Modules.Particl.ParticleManager;

public class CMICmdCooldown {
  private int cdSeconds = 0;
  private String command;
  private ParticleManager.CMIPresetAnimations animation;

  public CMICmdCooldown(String string, int value) {
    this.command = string;
    this.cdSeconds = value;
  }

  public int getCooldownSeconds() {
    return this.cdSeconds;
  }

  public void setCooldown(int value) {
    this.cdSeconds = value;
  }

  public String getCommand() {
    return this.command;
  }

  public String getSubCommand() {
    return this.command.startsWith("cmi ") ? this.command.substring(4) : this.command;
  }

  public void setCommand(String string) {
    this.command = string;
  }

  public ParticleManager.CMIPresetAnimations getAnimation() {
    return this.animation;
  }

  public void setAnimation(ParticleManager.CMIPresetAnimations cmiPresetAnimations) {
    this.animation = cmiPresetAnimations;
  }
}
