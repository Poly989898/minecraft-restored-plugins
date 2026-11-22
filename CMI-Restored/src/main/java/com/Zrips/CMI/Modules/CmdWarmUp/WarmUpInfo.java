package com.Zrips.CMI.Modules.CmdWarmUp;

import com.Zrips.CMI.Modules.Particl.CMIVisualEffect;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Location;

public class WarmUpInfo {
  private String cmd;
  private Location loc;
  private CmdWarmUp warmup = new CmdWarmUp("DUMMY", 3L, false);
  private CMITask task = null;
  private CMIVisualEffect effectAnimation;
  private int leftCount = 0;
  private BossBarInfo bar;

  @Deprecated
  public WarmUpInfo(String string, boolean flag) {
    this.cmd = string;
    this.warmup.setMove(flag);
  }

  @Deprecated
  public WarmUpInfo(Location location, boolean flag) {
    this.loc = location;
    this.warmup.setMove(flag);
  }

  public WarmUpInfo(String string, CmdWarmUp cmdWarmUp) {
    this.cmd = string;
    this.setWarmup(cmdWarmUp);
  }

  public WarmUpInfo(Location location, CmdWarmUp cmdWarmUp) {
    this.loc = location;
    this.setWarmup(cmdWarmUp);
  }

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String string) {
    this.cmd = string;
  }

  public boolean isMove() {
    return this.getWarmup() == null ? false : this.getWarmup().isMove();
  }

  @Deprecated
  public void setMove(boolean flag) {}

  public Location getLoc() {
    return this.loc;
  }

  public void setLoc(Location location) {
    this.loc = location;
  }

  public CMIVisualEffect getEffectAnimation() {
    return this.effectAnimation;
  }

  public void setEffectAnimation(CMIVisualEffect cmiVisualEffect) {
    this.effectAnimation = cmiVisualEffect;
  }

  public int getLeftCount() {
    return this.leftCount;
  }

  public void setLeftCount(int value) {
    this.leftCount = value;
  }

  public BossBarInfo getBar() {
    return this.bar;
  }

  public void setBar(BossBarInfo bossBarInfo) {
    this.bar = bossBarInfo;
  }

  public CMITask getTask() {
    return this.task;
  }

  public void setTask(CMITask cmiTask) {
    this.task = cmiTask;
  }

  public CmdWarmUp getWarmup() {
    return this.warmup;
  }

  public void setWarmup(CmdWarmUp cmdWarmUp) {
    this.warmup = cmdWarmUp;
  }
}
