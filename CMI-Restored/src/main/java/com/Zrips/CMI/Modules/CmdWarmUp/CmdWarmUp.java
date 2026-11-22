package com.Zrips.CMI.Modules.CmdWarmUp;

import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.commands.CommandsHandler;

public class CmdWarmUp {
  private Long warmUpTime = 0L;
  private boolean move = true;
  private double maxMoveDistance = 0.0;
  private String command;
  private ParticleManager.CMIPresetAnimations animation;

  public CmdWarmUp(String string, Long warmUpTimeValue, boolean flag) {
    this.command = string;
    this.warmUpTime = warmUpTimeValue;
    this.move = flag;
  }

  @Deprecated
  public Long getWU() {
    return this.getTime();
  }

  @Deprecated
  public void setWU(Long warmUpTimeValue) {
    this.setTime(warmUpTimeValue);
  }

  public Long getTime() {
    return this.warmUpTime;
  }

  public void setTime(Long warmUpTimeValue) {
    this.warmUpTime = warmUpTimeValue;
  }

  public boolean isMove() {
    return this.move;
  }

  public void setMove(boolean flag) {
    this.move = flag;
  }

  public String getCommand() {
    return this.command;
  }

  public String getSubCommand() {
    return this.command.startsWith(CommandsHandler.getLabel() + " ")
        ? this.command.substring(4)
        : this.command;
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

  public double getMaxMoveDistance() {
    return this.maxMoveDistance;
  }

  public void setMaxMoveDistance(double number) {
    this.maxMoveDistance = number;
  }
}
