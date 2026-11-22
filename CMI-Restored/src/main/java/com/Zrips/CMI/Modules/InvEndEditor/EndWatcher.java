package com.Zrips.CMI.Modules.InvEndEditor;

import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;

public class EndWatcher {
  Player master = null;
  Player target = null;
  private CMITask saveTask = null;

  public EndWatcher(Player player, Player targetPlayer) {
    this.master = player;
    this.target = targetPlayer;
  }

  public Player getMaster() {
    return this.master;
  }

  public void setMaster(Player player) {
    this.master = player;
  }

  public Player getTarget() {
    return this.target;
  }

  public void setTarget(Player player) {
    this.target = player;
  }

  public CMITask getSaveTask() {
    return this.saveTask;
  }

  public void setSaveTask(CMITask cmiTask) {
    this.saveTask = cmiTask;
  }
}
