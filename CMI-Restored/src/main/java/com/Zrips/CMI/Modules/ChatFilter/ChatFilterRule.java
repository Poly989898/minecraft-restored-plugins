package com.Zrips.CMI.Modules.ChatFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Chat.ChatFilterBlockType;

public class ChatFilterRule {
  private String replaceWith;
  private ChatFilterBlockType blockType;
  private String ruleName;
  private List<Pattern> pattern = new ArrayList<>();
  private String messageToStaff;
  private List<String> commands = new ArrayList<>();
  private String group;
  private boolean informConsole = false;
  private boolean applyToPrivateMessages = true;

  public ChatFilterRule(
      String string,
      String groupName,
      List<String> list,
      String replacement,
      ChatFilterBlockType chatFilterBlockType,
      String staffMessage,
      List<String> commandList) {
    this.group = groupName;
    this.ruleName = string;
    this.commands = commandList;
    this.setPattern(list);
    this.messageToStaff = staffMessage;
    this.replaceWith = replacement;
    this.blockType = chatFilterBlockType;
  }

  public ChatFilterRule(String string, List<String> list) {
    this.ruleName = string;
    this.setPattern(list);
    this.blockType = ChatFilterBlockType.None;
  }

  public ChatFilterRule() {}

  public List<Pattern> getPattern() {
    return this.pattern;
  }

  public ChatFilterRule setPattern(String string) {
    this.setPattern(Arrays.asList(string));
    return this;
  }

  public void setPattern(List<String> list) {
    this.pattern.clear();

    for (String string : list) {
      this.pattern.add(Pattern.compile(string, 64));
    }
  }

  public String getReplaceWith() {
    return this.replaceWith;
  }

  public void setReplaceWith(String string) {
    this.replaceWith = string;
  }

  public ChatFilterBlockType getBlockType() {
    return this.blockType;
  }

  public void setBlockType(ChatFilterBlockType chatFilterBlockType) {
    this.blockType = chatFilterBlockType;
  }

  public String getRuleName() {
    return this.ruleName;
  }

  public void setRuleName(String string) {
    this.ruleName = string;
  }

  public Matcher getMatcher(String string) {
    Matcher matcher = null;

    for (Pattern compiledPattern : this.pattern) {
      if (compiledPattern.matcher(string).find()) {
        matcher = compiledPattern.matcher(string);
        break;
      }
    }

    return matcher;
  }

  public String getMessageToStaff() {
    return this.messageToStaff;
  }

  public void setMessageToStaff(String string) {
    this.messageToStaff = string;
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void setCommands(List<String> list) {
    this.commands = list;
  }

  public String getGroup() {
    return this.group;
  }

  public void setGroup(String string) {
    this.group = string;
  }

  public boolean isInformConsole() {
    return this.informConsole;
  }

  public void setInformConsole(boolean flag) {
    this.informConsole = flag;
  }

  public boolean isApplyToPrivateMessages() {
    return this.applyToPrivateMessages;
  }

  public void setApplyToPrivateMessages(boolean flag) {
    this.applyToPrivateMessages = flag;
  }
}
