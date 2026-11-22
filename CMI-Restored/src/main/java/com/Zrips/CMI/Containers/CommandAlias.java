package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAlias {
  private boolean state;
  private String cmiCommandName;
  private List<String> commands = new ArrayList<>();
  private String alias = null;
  private boolean requiresPerm = false;
  private boolean tabComplete = true;
  private CommandAliasType type;
  private boolean exact = false;
  private boolean overrideTab = false;
  private boolean addTabs = false;
  private boolean disableBase = false;
  private List<String> customTabList = null;
  private CMITabComplete customTab = null;
  private Boolean containsDynamic = null;
  private String fileName = null;
  private Set<String> alternatives = new HashSet<>();
  private boolean alternative = false;
  Pattern patern = Pattern.compile("[$?][\\d][-]?");

  @Deprecated
  public CommandAlias(String string, List<String> list, boolean flag) {
    this(string, list, flag, null);
  }

  @Deprecated
  public CommandAlias(
      String string, List<String> list, boolean flag, CommandAliasType commandAliasType) {
    this(string, list, flag, commandAliasType, null);
  }

  @Deprecated
  public CommandAlias(
      String string,
      List<String> list,
      boolean flag,
      CommandAliasType commandAliasType,
      String text) {
    this.commands.addAll(list);
    this.state = flag;
    this.alias = CMIChatColor.stripColor(CMIChatColor.deColorize(string));
    this.type = commandAliasType;
    this.cmiCommandName = text;
  }

  public CommandAlias(String string, String text, List<String> list, boolean flag) {
    this(string, text, list, flag, null);
  }

  public CommandAlias(
      String string,
      String text,
      List<String> list,
      boolean flag,
      CommandAliasType commandAliasType) {
    this(string, text, list, flag, commandAliasType, null);
  }

  public CommandAlias(
      String string,
      String text,
      List<String> list,
      boolean flag,
      CommandAliasType commandAliasType,
      String textSecond) {
    this.commands.addAll(list);
    this.state = flag;
    this.alias = CMIChatColor.stripColor(CMIChatColor.deColorize(string));
    this.type = commandAliasType;
    this.cmiCommandName = textSecond;
    this.fileName = text;
  }

  public boolean getState() {
    return this.state;
  }

  public void setState(boolean flag) {
    this.state = flag;
  }

  public String getCommand() {
    return this.commands.isEmpty() ? "" : this.commands.get(0);
  }

  public String getCleanCommand() {
    if (this.commands.isEmpty()) {
      return "";
    }

    String string = this.getCommand();
    ArrayList<String> arrayList = new ArrayList();
    if (string.contains(" ")) {
      arrayList.addAll(Arrays.asList(string.split(" ")));
    } else {
      arrayList.add(string);
    }

    String text = "";

    for (String textSecond : arrayList) {
      if (textSecond.startsWith("$") || textSecond.startsWith("[")) {
        break;
      }

      if (!text.isEmpty()) {
        text = text + " ";
      }

      text = text + textSecond;
    }

    return text;
  }

  public String getCommand(CommandSender commandSender, List<String> list) {
    if (this.commands.isEmpty()) {
      return "";
    }

    if (!(commandSender instanceof Player)) {
      return this.commands.get(0);
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(commandSender);
    String string = this.getCommand();
    ArrayList<String> arrayList = new ArrayList();
    if (string.contains(" ")) {
      arrayList.addAll(Arrays.asList(string.split(" ")));
    } else {
      arrayList.add(string);
    }

    Iterator iterator = arrayList.iterator();

    while (true) {
      boolean flag;
      Integer integer;
      while (true) {
        if (!iterator.hasNext()) {
          return CMI.getInstance().getLM().updateSnd(snd, string);
        }

        String text = (String) iterator.next();
        flag = false;
        if (text.startsWith("$")) {
          Object object = null;

          try {
            if (text.endsWith("-")) {
              flag = true;
            }

            integer = Integer.parseInt(text.replace("$", "").replace("-", ""));
            break;
          } catch (Exception exception) {
          }
        }
      }

      String textSecond = null;
      if (integer < list.size()) {
        if (!flag) {
          textSecond = (String) list.get(integer);
        } else {
          textSecond = "";

          for (int value = integer; value < list.size(); value++) {
            if (!textSecond.isEmpty()) {
              textSecond = textSecond + " ";
            }

            textSecond = textSecond + (String) list.get(value);
          }
        }
      }

      if (textSecond == null) {
        textSecond = "";
      }

      if (string.contains("$" + integer)) {
        string = string.replace("$" + integer + "-", textSecond);
        string = string.replace("$" + integer, textSecond);
      }
    }
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public String getCommandsForLore() {
    String string = "";

    for (String text : this.commands) {
      if (!string.isEmpty()) {
        string = string + " \n";
      }

      string =
          string
              + (text.startsWith("delay! ")
                  ? CMILC.getIM("aliaseditor", "commandListDelay", "[command]", text)
                  : CMILC.getIM("aliaseditor", "commandList", "[command]", text));
    }

    return string;
  }

  public String getAlias() {
    return this.alias;
  }

  public boolean containsDynamicVariables() {
    if (this.containsDynamic == null) {
      this.recalculateDynamicVariables();
    }

    return this.containsDynamic == null ? false : this.containsDynamic;
  }

  public void recalculateDynamicVariables() {
    this.containsDynamic = null;

    for (String string : this.commands) {
      Matcher matcher = this.patern.matcher(string);
      if (matcher.find()) {
        this.containsDynamic = true;
        break;
      }
    }

    if (this.containsDynamic == null) {
      this.containsDynamic = false;
    }
  }

  public String getAliasBaseCommand() {
    return this.alias.contains(" ") ? this.alias.split(" ")[0] : this.alias;
  }

  public String getAliasAsOneWord() {
    return this.alias.replace(" ", "__");
  }

  public String getAliasAsOneWordNS() {
    return this.alias.replace(" ", "");
  }

  public void setAlias(String string) {
    this.alias = string;
  }

  public void setCommands(List<String> list) {
    this.commands = list;
  }

  public boolean isRequiresPerm() {
    return this.requiresPerm;
  }

  public void setRequiresPerm(boolean flag) {
    this.requiresPerm = flag;
  }

  public CommandAliasType getType() {
    return this.type == null ? CommandAliasType.custom : this.type;
  }

  public String getCmiCommandName() {
    return this.cmiCommandName;
  }

  public boolean isTabComplete() {
    return this.tabComplete;
  }

  public void setTabComplete(boolean flag) {
    this.tabComplete = flag;
  }

  public List<String> getCustomTabRawList() {
    return this.customTabList;
  }

  public CMITabComplete getCustomTab() {
    return this.customTab;
  }

  public List<Object> getTabCompleteList(String[] strings) {
    return this.customTab == null ? new ArrayList<>() : this.customTab.getTabCompleteList(strings);
  }

  public void recheckTabCompletes() {
    this.customTab = new CMITabComplete();

    for (String string : this.customTabList) {
      this.customTab.addTabComplete(string);
    }
  }

  public void setCustomTab(List<String> list) {
    this.customTab = new CMITabComplete();
    this.customTabList = list;
    this.recheckTabCompletes();
  }

  public boolean isDisableBase() {
    return this.disableBase;
  }

  public void setDisableBase(boolean flag) {
    this.disableBase = flag;
  }

  public boolean isOverrideTab() {
    return this.overrideTab;
  }

  public void setOverrideTab(boolean flag) {
    this.overrideTab = flag;
  }

  public boolean isAddTabs() {
    return this.addTabs;
  }

  public void setAddTabs(boolean flag) {
    this.addTabs = flag;
  }

  public String getFileName() {
    return this.fileName == null ? "CustomAlias" : this.fileName;
  }

  public void setFileName(String string) {
    this.fileName = string;
  }

  public Set<String> getAlternatives() {
    return this.alternatives;
  }

  public void setAlternatives(Set<String> set) {
    this.alternatives = set;
  }

  public void addAlternative(String string) {
    this.alternatives.add(string);
  }

  public boolean isAlternative() {
    return this.alternative;
  }

  public void setAlternative(boolean flag) {
    this.alternative = flag;
  }

  public boolean isExact() {
    return this.exact;
  }

  public void setExact(boolean flag) {
    this.exact = flag;
  }
}
