package com.Zrips.CMI.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class CMIAfkLeaveEvent extends CMIPlayerEvent {
  private List<String> leaveTrigerCommands = new ArrayList<>();
  private long time;
  private String reason;

  public CMIAfkLeaveEvent(Player player) {
    super(player);
  }

  @Deprecated
  public CMIAfkLeaveEvent(Player player, List<String> list) {
    super(player);
    this.leaveTrigerCommands = list;
  }

  public CMIAfkLeaveEvent(Player player, List<String> list, long longValue, String string) {
    super(player);
    this.leaveTrigerCommands = list;
    this.time = longValue;
    this.reason = string;
  }

  public List<String> getLeaveTrigerCommands() {
    return this.leaveTrigerCommands;
  }

  public void setLeaveTrigerCommands(List<String> list) {
    this.leaveTrigerCommands = list;
  }

  public long getTime() {
    return this.time;
  }

  public String getReason() {
    return this.reason;
  }
}
