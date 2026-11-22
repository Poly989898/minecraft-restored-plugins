package com.Zrips.CMI.Modules.ReplaceBlock;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.itemInfo;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;

public class BRInfo {
  private CMITask task = null;
  private long showedInfo = 0L;
  private boolean showInfo = false;
  private boolean autoSpeed = true;
  private boolean messages = true;
  private Player player;
  private String Playername;
  private long startTime = 0L;
  private int speed = 1;
  private boolean running = false;
  private int y = -1;
  private long replaced = 0L;
  List<itemInfo> itemIds = new ArrayList<>();
  List<itemInfo> replaceIds = new ArrayList<>();
  private double d;
  private WorldInfo RI;

  public void setY(int value) {
    this.y = value;
  }

  public int getY() {
    if (this.y == -1) {
      this.y = this.RI.getWorld().getMaxHeight();
    }

    return this.y;
  }

  public void addReplaced() {
    this.replaced++;
  }

  public long getReplaced() {
    return this.replaced;
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
    Player resolvedPlayer = CMIUser.getOnlinePlayer(this.player.getUniqueId());
    if (resolvedPlayer != null) {
      this.player = resolvedPlayer;
    }

    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.Playername = newPlayer.getName();
    this.player = newPlayer;
  }

  public void addItemInfo(itemInfo itemInfo) {
    this.itemIds.add(itemInfo);
  }

  public List<itemInfo> getItemInfo() {
    return this.itemIds;
  }

  public void addReplaceInfo(itemInfo itemInfo) {
    this.replaceIds.add(itemInfo);
  }

  public List<itemInfo> getReplaceInfo() {
    return this.replaceIds;
  }

  public void setTotal(double number) {
    this.d = number;
  }

  public double getTotal() {
    return this.d;
  }

  public WorldInfo getRI() {
    return this.RI;
  }

  public void setRI(WorldInfo worldInfo) {
    this.RI = worldInfo;
  }
}
