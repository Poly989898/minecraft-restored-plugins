package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.EventActions.EventActionManager;
import java.util.List;
import org.bukkit.entity.Player;

public class CMIEventCommandEvent extends CMIPlayerCancellableEvent {
  private final EventActionManager.eventAction action;
  private List<String> commands;
  private List<String> sourceCommands;
  private Player source;

  public CMIEventCommandEvent(
      Player player,
      Player sourcePlayer,
      EventActionManager.eventAction eventAction,
      List<String> list,
      List<String> commandList) {
    super(player);
    this.source = sourcePlayer;
    this.action = eventAction;
    this.commands = list;
    this.sourceCommands = commandList;
  }

  public EventActionManager.eventAction getAction() {
    return this.action;
  }

  public List<String> getCommands() {
    return this.commands;
  }

  @Deprecated
  public void setCommands(List<String> list) {
    this.commands.clear();
    this.commands.addAll(list);
  }

  public Player getSource() {
    return this.source;
  }

  public void setSource(Player player) {
    this.source = player;
  }

  public List<String> getSourceCommands() {
    return this.sourceCommands;
  }
}
