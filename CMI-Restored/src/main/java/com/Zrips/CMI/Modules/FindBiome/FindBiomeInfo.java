package com.Zrips.CMI.Modules.FindBiome;

import com.Zrips.CMI.Modules.Region.WorldInfo;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;

public class FindBiomeInfo {
  private CMITask task = null;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean messages = true;
  private Player player;
  private long startTime = 0L;
  private int speed = 30000;
  private boolean running = true;
  private WorldInfo RI;
  private int currentId = -1;
  private String biome;
  private int skipped = 0;

  public FindBiomeInfo(Player newPlayer, String string) {
    this.player = newPlayer;
    this.biome = string;
  }

  public int getCurrentId() {
    return this.currentId;
  }

  public void setCurrentId(int value) {
    this.currentId = value;
  }

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

  public void setMessages(boolean flag) {
    this.messages = flag;
  }

  public boolean isMessages() {
    return this.messages;
  }

  public void setShowRegionInfo(boolean flag) {
    this.showInfo = flag;
  }

  public boolean isShowRegionInfo() {
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

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public WorldInfo getRI() {
    return this.RI;
  }

  public void setRI(WorldInfo worldInfo) {
    this.RI = worldInfo;
  }

  public String getBiome() {
    return this.biome;
  }

  public void setBiome(String string) {
    this.biome = string;
  }

  public int getSkipped() {
    return this.skipped;
  }

  public void addSkipped() {
    this.skipped++;
  }

  public void setSkipped(int value) {
    this.skipped = value;
  }
}
