package com.Zrips.CMI.events;

import org.bukkit.command.CommandSender;

public class CMIIpBanEvent extends CMICancellableEvent {
  private CommandSender by;
  private String ip;
  private Long until;
  private String reason;

  public CMIIpBanEvent(
      CommandSender commandSender, String string, String reasonText, Long untilTime) {
    this.by = commandSender;
    this.ip = string;
    this.reason = reasonText;
    this.until = untilTime;
  }

  public CommandSender getBannedBy() {
    return this.by;
  }

  public String getIp() {
    return this.ip;
  }

  public Long getUntil() {
    return this.until;
  }

  public String getReason() {
    return this.reason;
  }
}
