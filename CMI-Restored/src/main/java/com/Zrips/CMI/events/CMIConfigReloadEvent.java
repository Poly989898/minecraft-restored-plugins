package com.Zrips.CMI.events;

import org.bukkit.command.CommandSender;

public class CMIConfigReloadEvent extends CMIEvent {
  private final CommandSender sender;

  public CMIConfigReloadEvent(CommandSender commandSender) {
    this.sender = commandSender;
  }

  public CommandSender getSender() {
    return this.sender;
  }
}
