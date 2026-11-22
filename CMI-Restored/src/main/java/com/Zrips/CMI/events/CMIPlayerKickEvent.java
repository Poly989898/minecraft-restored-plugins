package com.Zrips.CMI.events;

import java.util.UUID;
import org.bukkit.command.CommandSender;

public class CMIPlayerKickEvent extends CMICancellableEvent {
  private String reason;
  private CommandSender by;
  private UUID banned;

  public CMIPlayerKickEvent(CommandSender commandSender, UUID uuid, String string) {
    this.banned = uuid;
    this.by = commandSender;
    this.reason = string;
  }

  public String getReason() {
    return this.reason;
  }

  public CommandSender getBannedBy() {
    return this.by;
  }

  public UUID getBanned() {
    return this.banned;
  }
}
