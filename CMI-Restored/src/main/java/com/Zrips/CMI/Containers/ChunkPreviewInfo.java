package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.util.ArrayList;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChunkPreviewInfo {
  private CMITask scheduleTask = null;
  private int currentId = 1;
  private File[] regionFiles;
  private ArrayList<ChunkInfo> chunks = new ArrayList<>();
  private int chunksChecked = 0;
  private int chunksCkeckedLast = 0;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean autoSpeed = true;
  private boolean messages = true;
  private Player player;
  private String Playername;
  private int checked = 0;
  private World world;
  private int mcaX;
  private int mcaZ;
  private int cX = 0;
  private int cZ = 0;
  private int loaded = 0;
  private int found = 0;
  private long startTime = 0L;
  private int speed = CMI.getInstance().getConfigManager().lfixDefaultSpeed;
  private int range = -1;
  private boolean running = false;

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

  public void setRange(int value) {
    this.range = value;
  }

  public int getRange() {
    return this.range;
  }

  public void setScheduleTask(CMITask cmiTask) {
    this.scheduleTask = cmiTask;
  }

  public CMITask getScheduleTask() {
    return this.scheduleTask;
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

  public void addChunks(ArrayList<ChunkInfo> arrayList) {
    this.chunks = arrayList;
  }

  public void addChunk(ChunkInfo chunkInfo) {
    this.chunks.add(chunkInfo);
  }

  public ArrayList<ChunkInfo> getChunks() {
    return this.chunks;
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

  public void addLoaded() {
    this.loaded++;
  }

  public int getloaded() {
    return this.loaded;
  }

  public void setX(int value) {
    this.mcaX = value;
  }

  public int getX() {
    return this.mcaX;
  }

  public void setZ(int value) {
    this.mcaZ = value;
  }

  public int getZ() {
    return this.mcaZ;
  }

  public void setCX(int value) {
    this.cX = value;
  }

  public void addCX() {
    this.cX++;
  }

  public int getCX() {
    return this.cX;
  }

  public void setCZ(int value) {
    this.cZ = value;
  }

  public void addCZ() {
    this.cZ++;
  }

  public int getCZ() {
    return this.cZ;
  }

  public void addRegionChecked() {
    this.checked++;
  }

  public int getRegionChecked() {
    return this.checked;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
  }

  public World getWorld() {
    return this.world;
  }

  public void setRegionFiles(File[] files) {
    this.regionFiles = files;
  }

  public File[] getRegionFiles() {
    return this.regionFiles;
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
}
