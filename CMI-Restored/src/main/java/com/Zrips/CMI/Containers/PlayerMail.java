package com.Zrips.CMI.Containers;

import org.bukkit.command.CommandSender;

public class PlayerMail {
  private String sender;
  private long time;
  private long keepFor;
  private String message;

  public PlayerMail(String string, Long timeMillis, String messageText) {
    this(string, timeMillis, messageText, 0L);
  }

  public PlayerMail(String string, Long timeMillis, String messageText, Long keepForMillis) {
    this.sender = string;
    this.time = timeMillis == null ? 0L : timeMillis;
    this.message = messageText;
    this.keepFor = keepForMillis == null ? 0L : keepForMillis;
  }

  public PlayerMail(CommandSender commandSender, String string) {
    this(commandSender, System.currentTimeMillis(), string, 0L);
  }

  public PlayerMail(CommandSender commandSender, String string, Long keepForMillis) {
    this(commandSender, System.currentTimeMillis(), string, keepForMillis);
  }

  public PlayerMail(
      CommandSender commandSender, long longValue, String string, Long keepForMillis) {
    this.sender = CMIMail.getSendersName(commandSender);
    this.time = longValue;
    this.message = string;
    this.keepFor = keepForMillis == null ? 0L : keepForMillis;
  }

  public String getSender() {
    return this.sender;
  }

  public void setSender(String string) {
    this.sender = string;
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long timeMillis) {
    this.time = timeMillis == null ? 0L : timeMillis;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }

  public Long getKeepFor() {
    return this.keepFor;
  }

  public void setKeepFor(Long keepForMillis) {
    this.keepFor = keepForMillis == null ? 0L : keepForMillis;
  }
}
