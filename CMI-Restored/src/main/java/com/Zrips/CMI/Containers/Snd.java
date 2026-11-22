package com.Zrips.CMI.Containers;

import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Snd {
  private SndUser sender = null;
  private SndUser target = null;
  private SndUser source = null;
  private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\\\n");

  public Snd() {}

  public Snd(Player player, Player targetPlayer) {
    this.getSender().set(player);
    this.getTarget().set(targetPlayer);
  }

  private String filterNewLine(String string) {
    return string != null && !string.isEmpty()
        ? NEWLINE_PATTERN.matcher(string).replaceAll("\n")
        : string;
  }

  public String updateLocale(String string) {
    return this.updateLocale(string, null);
  }

  public String updateLocale(String string, @Nullable CommandSender commandSender) {
    if (string == null) {
      return this.filterNewLine(string);
    }

    if (!string.contains("[")) {
      return this.filterNewLine(string);
    }

    if (this.sender != null) {
      string =
          this.getSender()
              .updateLocale(
                  string,
                  commandSender == null
                      ? (this.sender == null ? null : this.sender.getCommandSender())
                      : commandSender);
    }

    if (string != null && string.contains("[")) {
      if (this.target != null) {
        string =
            this.getTarget()
                .updateLocale(
                    string,
                    commandSender == null
                        ? (this.target == null ? null : this.target.getCommandSender())
                        : commandSender);
      }

      if (string != null && string.contains("[")) {
        if (this.source != null) {
          string =
              this.getSource()
                  .updateLocale(
                      string,
                      commandSender == null
                          ? (this.source == null ? null : this.source.getCommandSender())
                          : commandSender);
        }

        return this.filterNewLine(string);
      } else {
        return this.filterNewLine(string);
      }
    } else {
      return this.filterNewLine(string);
    }
  }

  public SndUser getSender() {
    if (this.sender == null) {
      this.sender = new SndUser("sender");
    }

    return this.sender;
  }

  public SndUser getTarget() {
    if (this.target == null) {
      this.target = new SndUser("", "player", "target");
    }

    return this.target;
  }

  public SndUser getSource() {
    if (this.source == null) {
      this.source = new SndUser("source");
    }

    return this.source;
  }

  public Player getPlayerSender() {
    return this.getSender().getPlayer();
  }

  public ConsoleCommandSender getConsoleSender() {
    return this.getSender().getCommandSender() instanceof ConsoleCommandSender
        ? (ConsoleCommandSender) this.getSender().getCommandSender()
        : null;
  }

  public Snd setSender(Player player) {
    this.getSender().set(player);
    return this;
  }

  public Snd setSender(CMIUser cmiUser) {
    this.getSender().set(cmiUser);
    return this;
  }

  public Snd setSender(CommandSender commandSender) {
    this.getSender().set(commandSender);
    return this;
  }

  public Snd resetSender() {
    this.getSender().reset();
    return this;
  }

  public Snd setSender(ConsoleCommandSender consoleCommandSender) {
    this.getSender().set(consoleCommandSender);
    return this;
  }

  public Player getPlayerTarget() {
    return this.getTarget().getPlayer();
  }

  public ConsoleCommandSender getConsoleTarget() {
    return this.getTarget().getCommandSender() instanceof ConsoleCommandSender
        ? (ConsoleCommandSender) this.getTarget().getCommandSender()
        : null;
  }

  public Snd resetTarget() {
    this.getTarget().reset();
    return this;
  }

  public Snd setTarget(Player player) {
    this.getTarget().set(player);
    return this;
  }

  public Snd setTarget(CMIUser cmiUser) {
    this.getTarget().set(cmiUser);
    return this;
  }

  public Snd setTarget(CommandSender commandSender) {
    this.getTarget().set(commandSender);
    return this;
  }

  public Snd setTarget(ConsoleCommandSender consoleCommandSender) {
    this.getTarget().set(consoleCommandSender);
    return this;
  }

  public Player getPlayerSource() {
    return this.getSource().getPlayer();
  }

  public ConsoleCommandSender getConsoleSource() {
    return this.getSource().getCommandSender() instanceof ConsoleCommandSender
        ? (ConsoleCommandSender) this.getSource().getCommandSender()
        : null;
  }

  public Snd resetSource() {
    this.getSource().reset();
    return this;
  }

  public Snd setSource(Player player) {
    this.getSource().set(player);
    return this;
  }

  public Snd setSource(CMIUser cmiUser) {
    this.getSource().set(cmiUser);
    return this;
  }

  public Snd setSource(CommandSender commandSender) {
    this.getSource().set(commandSender);
    return this;
  }

  public Snd setSource(ConsoleCommandSender consoleCommandSender) {
    this.getSource().set(consoleCommandSender);
    return this;
  }

  public CMIUser getSenderUser() {
    return this.getSender().getUser();
  }

  public void setSenderUser(CMIUser cmiUser) {
    this.getSender().set(cmiUser);
  }

  public CMIUser getTargetUser() {
    return this.getTarget().getUser();
  }

  public void setTargetUser(CMIUser cmiUser) {
    this.getTarget().set(cmiUser);
  }

  public CMIUser getSourceUser() {
    return this.getSource().getUser();
  }

  public void setSourceUser(CMIUser cmiUser) {
    this.getSource().set(cmiUser);
  }

  public String getSenderName() {
    return this.getSender().getName();
  }

  public Snd setSenderName(String string) {
    this.getSender().setName(string);
    return this;
  }

  public String getTargetName() {
    return this.getTarget().getName();
  }

  public Snd setTargetName(String string) {
    this.getTarget().setName(string);
    return this;
  }

  public String getSourceName() {
    return this.getSource().getName();
  }

  public Snd setSourceName(String string) {
    this.getSource().setName(string);
    return this;
  }
}
