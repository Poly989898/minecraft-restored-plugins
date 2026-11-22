package com.Zrips.CMI.events;

import org.bukkit.command.CommandSender;

public class CMIIpUnBanEvent extends CMICancellableEvent {
  private CommandSender by;
  private String ip;

  public CMIIpUnBanEvent(CommandSender commandSender, String string) {
    this.by = commandSender;
    this.ip = string;
  }

  public CommandSender getBannedBy() {
    return this.by;
  }

  public String getIp() {
    return this.ip;
  }
}
