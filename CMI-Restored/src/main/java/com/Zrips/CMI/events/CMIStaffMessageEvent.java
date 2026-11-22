package com.Zrips.CMI.events;

import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMIStaffMessageEvent extends CMICancellableEvent {
  private CommandSender by;
  private Set<Player> receivers;
  private String message;

  public CMIStaffMessageEvent(CommandSender commandSender, Set<Player> set, String string) {
    this.by = commandSender;
    this.receivers = set;
    this.message = string;
  }

  public CommandSender getSentBy() {
    return this.by;
  }

  public Set<Player> getReceivers() {
    return this.receivers;
  }

  public void setReceivers(Set<Player> set) {
    this.receivers = set;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }
}
