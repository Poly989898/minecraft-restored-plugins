package com.Zrips.CMI.Modules.LightFix;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;

public class LightFixInfo {
  private CMITask task = null;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean autoSpeed = true;
  private boolean messages = true;
  private Player player;
  private String Playername;
  private long startTime = 0L;
  private int speed = CMI.getInstance().getConfigManager().lfixDefaultSpeed;
  private boolean running = false;
  private WorldInfo RI;

  public void setScheduleTask(CMITask cmiTask) {
    this.task = cmiTask;
  }

  public CMITask getScheduleTask() {
    return this.task;
  }

  public void setShowInfo(long longValue) {
    this.showedInfo = longValue;
  }

  public long getShowInfo() {
    return this.showedInfo;
  }

  public void setAutoSpeed(boolean flag) {
    this.autoSpeed = flag;
  }

  public boolean isAutoSpeed() {
    return this.autoSpeed;
  }

  public void setMessages(boolean flag) {
    this.messages = flag;
  }

  public boolean isMessages() {
    return this.messages;
  }

  public void setShowInfo(boolean flag) {
    this.showInfo = flag;
  }

  public boolean isShowInfo() {
    return this.showInfo;
  }

  public void setSpeed(int value) {
    this.speed = value;
  }

  public int getSpeed() {
    return this.speed;
  }

  public void setRunning(boolean flag) {
    this.running = flag;
  }

  public boolean isRunning() {
    return this.running;
  }

  public void setStartTime() {
    this.startTime = System.currentTimeMillis();
  }

  public long getStartTime() {
    return this.startTime;
  }

  public String getPlayerName() {
    return this.Playername;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.Playername = newPlayer.getName();
    this.player = newPlayer;
  }

  public WorldInfo getRI() {
    return this.RI;
  }

  public void setRI(WorldInfo worldInfo) {
    this.RI = worldInfo;
  }
}
