package com.Zrips.CMI.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class CMIAfkKickEvent extends CMIPlayerCancellableEvent {
  private List<String> kickTrigerCommands = new ArrayList<>();

  public CMIAfkKickEvent(Player player, List<String> list) {
    super(player);
    this.kickTrigerCommands = list;
  }

  public List<String> getKickTrigerCommands() {
    return this.kickTrigerCommands;
  }

  public void setKickTrigerCommands(List<String> list) {
    this.kickTrigerCommands = list;
  }
}
