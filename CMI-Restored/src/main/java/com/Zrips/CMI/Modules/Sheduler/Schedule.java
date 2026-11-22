package com.Zrips.CMI.Modules.Sheduler;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Schedule {
  private boolean enabled = false;
  private String name;
  private long performOn = 0L;
  private boolean performed = false;
  private List<String> commands = new ArrayList<>();
  private List<String> tempCommands = new ArrayList<>();
  private List<String> minPlayerCommands = new ArrayList<>();
  private boolean repeatable = false;
  private boolean randomize = false;
  private boolean duplicateRandomize = false;
  private boolean singleLinear = false;
  private long delay = 0L;
  private long lastPerformedOn = 0L;
  private List<SchedTime> time = new ArrayList<>();
  private int MinPlayer = 0;
  private int MaxPlayer = 0;
  private boolean playerAmountFeedback = true;
  private double commandDelay = 0.0;
  private boolean dontTranslatePlaceholders = true;
  private boolean randomPlayer = false;
  Pattern placeholderKeepPatern = Pattern.compile("(\\{\\%)([^\\\"^\\%^ ]+)(\\%\\})(\\B)");

  public Schedule(String string) {
    this.name = string;
  }

  public void reset() {
    this.performOn = 0L;
    this.performed = false;
    this.commands = new ArrayList<>();
    this.repeatable = false;
    this.delay = 0L;
    this.randomize = false;
    this.duplicateRandomize = false;
    this.singleLinear = false;
    this.MinPlayer = 0;
    this.MaxPlayer = 0;
  }

  public String getDuplicatedRandomCommand() {
    this.tempCommands.clear();
    this.tempCommands.addAll(this.commands);
    if (this.tempCommands.isEmpty()) {
      return null;
    }

    Collections.shuffle(this.tempCommands);
    return this.tempCommands.get(0) != null ? this.tempCommands.get(0) : null;
  }

  public String getRandomCommand() {
    if (this.tempCommands.isEmpty()) {
      this.tempCommands.addAll(this.commands);
      Collections.shuffle(this.tempCommands);
    }

    if (this.tempCommands.isEmpty()) {
      return null;
    } else {
      return this.tempCommands.get(0) != null ? this.tempCommands.remove(0) : null;
    }
  }

  public String getSingleLinearCommand() {
    if (this.tempCommands.isEmpty()) {
      this.tempCommands.addAll(this.commands);
    }

    if (this.tempCommands.isEmpty()) {
      return null;
    } else {
      return this.tempCommands.get(0) != null ? this.tempCommands.remove(0) : null;
    }
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void setCommands(List<String> list) {
    this.commands = list;
    this.tempCommands.clear();

    for (int value = 0; value < this.commands.size(); value++) {
      Object object = list.get(value);
      if (!(object instanceof LinkedHashMap)) {
        list.set(value, (String) list.get(value));
      } else {
        LinkedHashMap linkedHashMap = (LinkedHashMap) object;
        String string = "";

        for (Entry entry : ((java.util.Map<?, ?>) linkedHashMap).entrySet()) {
          string = string + (String) entry.getKey() + ": " + (String) entry.getValue();
        }

        list.set(value, string);
      }
    }

    for (String listedCommand : list) {
      if (listedCommand.contains("[randomPlayer]")) {
        this.randomPlayer = true;
        break;
      }
    }

    double number = 0.0;

    for (String commandLine : this.commands) {
      if (commandLine.startsWith("delay!")) {
        String[] strings = commandLine.split(" ");

        try {
          number += Double.parseDouble(strings[1]);
        } catch (Exception exception) {
          try {
            number +=
                Double.parseDouble(
                    CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(strings[1]));
          } catch (Exception exception2) {
            exception2.printStackTrace();
          }
        }
      }
    }

    this.commandDelay = number;
  }

  public boolean isPerformed() {
    return this.performed;
  }

  public void setPerformed(boolean flag) {
    this.performed = flag;
  }

  public boolean isPlayerAmountOk() {
    return this.isPlayerMinOk() && this.isPlayerMaxOk();
  }

  public boolean isPlayerMinOk() {
    return this.getMinPlayer() == 0 || Bukkit.getOnlinePlayers().size() >= this.getMinPlayer();
  }

  public boolean isPlayerMaxOk() {
    return this.getMaxPlayer() == 0 || Bukkit.getOnlinePlayers().size() <= this.getMaxPlayer();
  }

  public boolean itsTimeToPerform() {
    long longValue = System.currentTimeMillis();
    boolean flag = false;
    if (this.performOn != 0L && this.performOn - 100L < longValue) {
      if (!this.isPerformed()) {
        return true;
      }

      if (this.repeatable) {
        this.setPerformed(false);
        this.recalculateNext();
      }
    }

    if (!this.time.isEmpty()) {
      for (SchedTime schedTime : this.time) {
        if (schedTime.isNow()) {
          flag = true;
          if (!this.isPerformed()) {
            if (!this.repeatable) {
              schedTime.setDone(true);
            }

            return true;
          }
        }
      }

      if (!flag) {
        this.setPerformed(false);
      }

      return false;
    } else {
      if (this.repeatable) {
        this.setPerformed(false);
        this.recalculateNext();
      }

      return false;
    }
  }

  public void recalculateNext() {
    if (this.delay != 0L) {
      this.updateNextPerform();
    } else if (!this.time.isEmpty()) {
      Long nextPerformTime = Long.MAX_VALUE;

      for (SchedTime schedTime : this.time) {
        if (schedTime.getNextMili() - System.currentTimeMillis() > 0L
            && nextPerformTime > schedTime.getNextMili()) {
          nextPerformTime = schedTime.getNextMili();
        }
      }

      if (nextPerformTime != Long.MAX_VALUE) {
        this.performOn = nextPerformTime;
      }
    }
  }

  public Long getPerformOn() {
    this.recalculateNext();
    return this.performOn;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public boolean isRepeatable() {
    return this.repeatable;
  }

  public void setRepeatable(boolean flag) {
    this.repeatable = flag;
  }

  public long getDelay() {
    return this.delay;
  }

  public void setDelay(long longValue) {
    this.delay = longValue;
  }

  public Long getLastPerformedOn() {
    return this.lastPerformedOn;
  }

  public void setLastPerformedOn() {
    this.setLastPerformedOn(System.currentTimeMillis());
    if (!this.time.isEmpty()) {
      this.recalculateNext();
    }
  }

  public void setLastPerformedOn(Long newLastPerformed) {
    this.lastPerformedOn = newLastPerformed;
    this.updateNextPerform();
  }

  private void updateNextPerform() {
    this.performOn =
        (this.lastPerformedOn == 0L ? System.currentTimeMillis() : this.lastPerformedOn)
            + this.delay * 1000L;
  }

  public List<SchedTime> getTime() {
    return this.time;
  }

  public void setTime(List<SchedTime> list) {
    this.time = list;
  }

  public Integer getMinPlayer() {
    return this.MinPlayer;
  }

  public void setMinPlayer(Integer integer) {
    this.MinPlayer = integer;
  }

  public Integer getMaxPlayer() {
    return this.MaxPlayer;
  }

  public void setMaxPlayer(Integer integer) {
    this.MaxPlayer = integer;
  }

  public boolean isRandomize() {
    return this.randomize;
  }

  public void setRandomize(boolean flag) {
    this.randomize = flag;
  }

  public void safePerform() {
    this.setLastPerformedOn();
    this.recalculateNext();
    this.setPerformed(true);
    if (!this.isPlayerAmountOk()) {
      if (this.isPlayerAmountFeedback()) {
        CMIMessages.consoleMessage("Skipping schedule " + this.getName() + " due to player amount");
      }

      if (!this.isPlayerMinOk()) {
        CMI.getInstance().getSpecializedCommandManager().processCmds(this.getMinPlayerCommands());
      }
    } else {
      this.perform();
    }
  }

  public void perform() {
    Player player = null;
    if (this.randomPlayer) {
      player = this.getRandomPlayer();
    }

    if (this.isSingleLinear()) {
      this.processCmd(this.getSingleLinearCommand(), player);
    } else if (this.isRandomize()) {
      this.processCmd(this.getRandomCommand(), player);
    } else if (this.isDuplicateRandomize()) {
      this.processCmd(this.getDuplicatedRandomCommand(), player);
    } else {
      ArrayList arrayList = new ArrayList();

      for (String string : this.getCommands()) {
        arrayList.addAll(
            string.contains(";;") ? Arrays.asList(string.split(";;")) : Arrays.asList(string));
      }

      this.performCmd(arrayList, player);
    }
  }

  private void processCmd(String string, Player player) {
    if (string != null) {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(
          string.contains(";;") ? Arrays.asList(string.split(";;")) : Arrays.asList(string));
      this.performCmd(arrayList, player);
    }
  }

  private void performCmd(List<String> list, Player player) {
    list = this.updateSchedCmds(list, player);
    CMI.getInstance().getSpecializedCommandManager().processCmds(list);
  }

  public List<String> updateSchedCmds(List<String> list, Player player) {
    for (int value = 0; value < list.size(); value++) {
      String string = (String) list.get(value);
      string = CMIChatColor.translate(string);
      if (player != null) {
        string = string.replace("[randomPlayer]", player.getName());
      }

      if (!string.startsWith("allPlayers!") || !this.isDontTranslatePlaceholders()) {
        string = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
      }

      list.set(value, string);
    }

    return list;
  }

  public Player getRandomPlayer() {
    Player player = null;
    if (this.randomPlayer) {
      ArrayList<Player> arrayList = new ArrayList();

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        arrayList.add(onlinePlayer);
      }

      int value = 0;
      Collections.shuffle(arrayList, new Random(System.currentTimeMillis()));

      while (value < arrayList.size()) {
        player = (Player) arrayList.get(value);
        value++;
        if (!PermissionsManager.CMIPerm.scheduler_exclude.hasSetPermission(player)) {
          break;
        }

        player = null;
      }
    }

    return player;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public Boolean isPlayerAmountFeedback() {
    return this.playerAmountFeedback;
  }

  public void setPlayerAmountFeedback(Boolean enabled) {
    this.playerAmountFeedback = enabled;
  }

  public Double getTotalCommandDelay() {
    return this.commandDelay;
  }

  public boolean isSingleLinear() {
    return this.singleLinear;
  }

  public void setSingleLinear(boolean flag) {
    this.singleLinear = flag;
  }

  public boolean isDuplicateRandomize() {
    return this.duplicateRandomize;
  }

  public void setDuplicateRandomize(boolean flag) {
    this.duplicateRandomize = flag;
  }

  public List<String> getMinPlayerCommands() {
    return this.minPlayerCommands;
  }

  public void setMinPlayerCommands(List<String> list) {
    this.minPlayerCommands = list;
  }

  public boolean isDontTranslatePlaceholders() {
    return this.dontTranslatePlaceholders;
  }

  public void setDontTranslatePlaceholders(boolean flag) {
    this.dontTranslatePlaceholders = flag;
  }
}
