package com.Zrips.CMI.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class CMIAfkEnterEvent extends CMIPlayerCancellableEvent {
  private List<String> awayTrigerCommands = new ArrayList<>();
  private CMIAfkEnterEvent.AfkType type;

  public CMIAfkEnterEvent(Player player, List<String> list, CMIAfkEnterEvent.AfkType afkType) {
    super(player);
    this.awayTrigerCommands = list;
    this.type = afkType;
  }

  public List<String> getAwayTrigerCommands() {
    return this.awayTrigerCommands;
  }

  public void setAwayTrigerCommands(List<String> list) {
    this.awayTrigerCommands = list;
  }

  public CMIAfkEnterEvent.AfkType getType() {
    return this.type;
  }

  public enum AfkType {
    auto,
    manual;
  }
}
