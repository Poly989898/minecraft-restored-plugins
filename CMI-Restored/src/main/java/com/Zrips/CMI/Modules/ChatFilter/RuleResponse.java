package com.Zrips.CMI.Modules.ChatFilter;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.Chat.ChatFilterBlockType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RuleResponse {
  private String message;
  private String updatedMessage;
  private HashMap<String, ChatFilterRule> rules = new HashMap<>();
  private List<String> bypass = new ArrayList<>();

  public RuleResponse(String string) {
    this.message = string;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }

  public HashMap<String, ChatFilterRule> getRules() {
    return this.rules;
  }

  public String getRulesNamesAsString() {
    String string = "";

    for (var entry : this.rules.entrySet()) {
      if (!string.isEmpty()) {
        string = string + ", ";
      }

      string = string + (String) entry.getKey();
    }

    return string;
  }

  public void addRule(ChatFilterRule chatFilterRule) {
    if (chatFilterRule != null && !this.rules.containsKey(chatFilterRule.getRuleName())) {
      this.rules.put(chatFilterRule.getRuleName(), chatFilterRule);
    }
  }

  public ChatFilterBlockType getMaxFilterBlockType() {
    ChatFilterBlockType chatFilterBlockType = ChatFilterBlockType.None;

    for (var entry : this.rules.entrySet()) {
      if (!this.isBypass(((ChatFilterRule) entry.getValue()).getGroup())
          && ((ChatFilterRule) entry.getValue()).getBlockType().getId()
              < chatFilterBlockType.getId()) {
        chatFilterBlockType = ((ChatFilterRule) entry.getValue()).getBlockType();
      }
    }

    return chatFilterBlockType;
  }

  public boolean isInformConsole() {
    boolean flag = false;

    for (var entry : this.rules.entrySet()) {
      if (!this.isBypass(((ChatFilterRule) entry.getValue()).getGroup())
          && ((ChatFilterRule) entry.getValue()).isInformConsole()) {
        flag = true;
      }
    }

    return flag;
  }

  public boolean isBypass(String string) {
    return this.bypass.contains(string.toLowerCase());
  }

  public void addBypass(String string) {
    if (!this.bypass.contains(string.toLowerCase())) {
      this.bypass.add(string.toLowerCase());
    }
  }

  public String getUpdatedMessage() {
    return this.updatedMessage == null ? this.message : this.updatedMessage;
  }

  public void setUpdatedMessage(String string) {
    this.updatedMessage = string;
  }

  public void informStaff(Player player) {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      if (PermissionsManager.CMIPerm.chatfilter_inform.hasPermission(viewer, 10000L)) {
        for (var entry : this.getRules().entrySet()) {
          if (!this.isBypass(((ChatFilterRule) entry.getValue()).getGroup())) {
            ChatFilterRule chatFilterRule = (ChatFilterRule) entry.getValue();
            if (chatFilterRule.getMessageToStaff() != null
                && !chatFilterRule.getMessageToStaff().isEmpty()) {
              CMIMessages.sendMessage(
                  viewer,
                  CMIChatColor.translate(
                          CMI.getInstance()
                              .getLM()
                              .updateSnd(
                                  new Snd().setSender(viewer).setTarget(player),
                                  chatFilterRule.getMessageToStaff()))
                      .replace("[message]", this.getMessage()));
            }
          }
        }
      }
    }

    if (this.isInformConsole()) {
      CMI.getInstance()
          .consoleMessage(
              CMILC.info_spamConsoleInform.getLocale(
                  "[playerName]",
                  player.getName(),
                  "[rules]",
                  this.getRulesNamesAsString(),
                  "[message]",
                  this.getMessage()));
    }
  }

  public void performCommands(Player player) {
    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          Snd snd = new Snd().setSender(player).setTarget(player);

          for (var entry : this.getRules().entrySet()) {
            for (String string : ((ChatFilterRule) entry.getValue()).getCommands()) {
              if (!this.isBypass(((ChatFilterRule) entry.getValue()).getGroup())) {
                String command = CMI.getInstance().getLM().updateSnd(snd, string);
                command = command.replace("[message]", this.getMessage());
                command = command.replace("[updatedmessage]", this.getUpdatedMessage());
                CMI.getInstance().getSpecializedCommandManager().executeCmd(command, null);
              }
            }
          }
        },
        1L);
  }
}
