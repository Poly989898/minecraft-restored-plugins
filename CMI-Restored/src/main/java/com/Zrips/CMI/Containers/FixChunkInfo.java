package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

public class FixChunkInfo {
  private CMITask scheduleTask = null;
  private int currentId = 1;
  private List<File> regionFiles = new ArrayList<>();
  private RegionFiles currentRegion = null;
  private Vector center;
  private ArrayList<ChunkInfo> chunks = new ArrayList<>();
  private boolean onlyChunks = false;
  private double chunksChecked = 0.0;
  private double chunksCkeckedLast = 0.0;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean autoSpeed = true;
  private boolean messages = true;
  private UUID sender;
  private double checked = 0.0;
  private World world;
  private int mcaX;
  private int mcaZ;
  private int loaded = 0;
  private double total = 0.0;
  private int found = 0;
  private long startTime = 0L;
  private int speed = 1;
  private int range = -1;
  private boolean running = false;
  private ArrayList<ChunkInfo> chunksResult = new ArrayList<>();

  public List<ChunkInfo> getChunksResult() {
    return this.chunksResult;
  }

  public void addChunkResult(ChunkInfo chunkInfo) {
    this.chunksResult.add(chunkInfo);
  }

  public int getCurrentId() {
    return this.currentId;
  }

  public void setCurrentId(int value) {
    this.currentId = value;
  }

  public void setChunksCkeckedLast(double number) {
    this.chunksCkeckedLast = number;
  }

  public double getChunksCkeckedLast() {
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

  public double getChunkChecked() {
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

  public void setOnlyChunks(boolean flag) {
    this.onlyChunks = flag;
  }

  public boolean isOnlyChunks() {
    return this.onlyChunks;
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

  public void addRegionChecked() {
    this.checked++;
  }

  public double getRegionChecked() {
    return this.checked;
  }

  public void setWorld(World world) {
    this.world = world;
  }

  public World getWorld() {
    return this.world;
  }

  public void setRegionFiles(List<File> list) {
    this.regionFiles = list;
  }

  public List<File> getRegionFiles() {
    return this.regionFiles;
  }

  public CommandSender getSender() {
    return (CommandSender)
        (this.sender.equals(CMI.getInstance().getServerUUID())
            ? Bukkit.getConsoleSender()
            : CMIUser.getOnlinePlayer(this.sender));
  }

  public void setSender(UUID uuid) {
    this.sender = uuid;
  }

  public double getTotal() {
    return this.total;
  }

  public void setTotal(double number) {
    this.total = number;
  }

  public Vector getCenter() {
    return this.center;
  }

  public void setCenter(Vector vector) {
    this.center = vector;
  }

  public RegionFiles getCurrentRegion() {
    return this.currentRegion;
  }

  public void setCurrentRegion(RegionFiles region) {
    this.currentRegion = region;
  }
}
