package com.Zrips.CMI.events;

import java.util.UUID;
import org.bukkit.command.CommandSender;

public class CMIPlayerBanEvent extends CMIEvent {
  private String reason;
  private Long until;
  private CommandSender by;
  private UUID banned;

  public CMIPlayerBanEvent(CommandSender commandSender, UUID uuid, String string, Long untilTime) {
    this.banned = uuid;
    this.by = commandSender;
    this.reason = string;
    this.until = untilTime;
  }

  public String getReason() {
    return this.reason;
  }

  public Long getUntil() {
    return this.until;
  }

  public CommandSender getBannedBy() {
    return this.by;
  }

  public UUID getBanned() {
    return this.banned;
  }
}
