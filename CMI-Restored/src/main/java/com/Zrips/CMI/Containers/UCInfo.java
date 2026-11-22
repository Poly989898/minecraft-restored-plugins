package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

public class UCInfo {
  private CMITask scheduleId = null;
  private int currentId = 1;
  private List<Chunk> chunks = new ArrayList<>();
  private int chunksChecked = 0;
  private int chunksCkeckedLast = 0;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean autoSpeed = true;
  private boolean messages = true;
  private CommandSender sender;
  private String sendername;
  private int checked = 0;
  private int found = 0;
  private long startTime = 0L;
  private int speed = 100;
  private boolean running = false;

  public void setChunks(List<Chunk> list) {
    this.chunks = list;
  }

  public List<Chunk> getChunks() {
    return this.chunks;
  }

  public int getCurrentId() {
    return this.currentId;
  }

  public void setCurrentId(int value) {
    this.currentId = value;
  }

  public void setChunksCkeckedLast(int value) {
    this.chunksCkeckedLast = value;
  }

  public int getChunksCkeckedLast() {
    return this.chunksCkeckedLast;
  }

  public void setScheduleTask(CMITask cmiTask) {
    this.scheduleId = cmiTask;
  }

  public CMITask getScheduleTask() {
    return this.scheduleId;
  }

  public void setShowInfo(long longValue) {
    this.showedInfo = longValue;
  }

  public long getShowInfo() {
    return this.showedInfo;
  }

  public void addChunkCkecked() {
    this.chunksChecked++;
  }

  public int getChunkChecked() {
    return this.chunksChecked;
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

  public void setShowRegionInfo(boolean flag) {
    this.showInfo = flag;
  }

  public boolean isShowRegionInfo() {
    return this.showInfo;
  }

  public void addFound() {
    this.found++;
  }

  public int getFound() {
    return this.found;
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

  public void addRegionChecked() {
    this.checked++;
  }

  public int getRegionChecked() {
    return this.checked;
  }

  public String getPlayerName() {
    return this.sendername;
  }

  public CommandSender getSender() {
    return this.sender;
  }

  public void setSender(CommandSender commandSender) {
    this.sendername = commandSender.getName();
    this.sender = commandSender;
  }
}
