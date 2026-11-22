package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.List;
import org.bukkit.entity.Player;

public class CounterInfo {
  private String message;
  private List<Player> list;
  private int t;
  private CMI plugin;

  public CounterInfo(String string, List<Player> players, int value, CMI cmi) {
    this.message = string;
    this.list = players;
    this.t = value;
    this.plugin = cmi;
  }

  public String getMessage() {
    return this.message;
  }

  public List<Player> getList() {
    return this.list;
  }

  public int getLeftTime() {
    return this.t;
  }

  public void lowerTime() {
    this.t--;
  }

  public CMI getPlugin() {
    return this.plugin;
  }
}
