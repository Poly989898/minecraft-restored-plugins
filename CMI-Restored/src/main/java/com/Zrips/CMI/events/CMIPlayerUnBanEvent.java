package com.Zrips.CMI.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMIPlayerUnBanEvent extends CMIPlayerCancellableEvent {
  private CommandSender by;

  public CMIPlayerUnBanEvent(CommandSender commandSender, Player player) {
    super(player);
    this.by = commandSender;
  }

  public CommandSender getBannedBy() {
    return this.by;
  }
}
