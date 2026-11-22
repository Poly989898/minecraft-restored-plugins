package com.Zrips.CMI.Containers;

import org.bukkit.GameMode;

public class worldFlyState {
  private boolean state = false;
  private boolean temp = true;
  private GameMode mode = GameMode.SURVIVAL;

  public worldFlyState(GameMode gameMode, boolean flag, boolean tempFlag) {
    this.mode = gameMode;
    this.state = flag;
    this.temp = tempFlag;
  }

  public boolean isFlyEnabled() {
    return this.state;
  }

  public void setFlyEnabled(boolean flag) {
    this.state = flag;
  }

  public boolean isTemp() {
    return this.temp;
  }

  public void setTemp(boolean flag) {
    this.temp = flag;
  }

  public GameMode getGameMode() {
    return this.mode;
  }

  public void setMode(GameMode gameMode) {
    this.mode = gameMode;
  }
}
