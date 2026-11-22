package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class statsedit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("cantSet", "{gcw}Can't set this type of statistics");
    configReader.get("reqSubType", "{gcw}This statistic requires defined {gcs}[type] {gcw}subtype");
    configReader.get(
        "set",
        "{gcp}Changed from {gcs}[old] {gcp}to {gcs}[new] {gcp}value for {gcs}[statistic]"
            + " {gcp}({gcs}[extra]{gcp})");
  }

  @CAnnotation(
      priority = 257,
      info = "Edit players statistics",
      args = "(playerName) [add/take/set] [statistic] (subType) [amount] (-s)",
      tab = {"playername", "add%%take%%set", "statstype", "statssubtype"},
      explanation = {},
      regVar = {3, 4, 5, 6},
      consoleVar = {3, 4, 5, 6},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    statsedit.Action action = statsedit.Action.Add;
    Long amount = null;
    String string = null;
    StatsManager.CMIStatistic cmiStatistic = null;
    String statName = "none";
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        String valueText;
        switch ((valueText = arg.toLowerCase()).hashCode()) {
          case 96417:
            if (valueText.equals("add")) {
              action = statsedit.Action.Add;
              continue;
            }
            break;
          case 113762:
            if (valueText.equals("set")) {
              action = statsedit.Action.Set;
              continue;
            }
            break;
          case 3552391:
            if (valueText.equals("take")) {
              action = statsedit.Action.Take;
              continue;
            }
        }

        try {
          amount = Long.parseLong(arg);
        } catch (Exception exception) {
          if (cmiStatistic == null) {
            cmiStatistic = cmi.getStatsManager().getStatisticByName(arg);
            if (cmiStatistic != null) {
              continue;
            }
          }

          if (cmiStatistic != null && statName.equals("none")) {
            statName = arg;
          } else {
            string = arg;
          }
        }
      }
    }

    if (cmiStatistic == null || amount == null) {
      return false;
    }

    if (!cmiStatistic.getSubType().equals(StatsManager.CMIType.None)
        && cmiStatistic.getBukkitStat() != null
        && statName.equals("none")) {
      CMILC.info(this, commandSender, "reqSubType", "[type]", cmiStatistic.getSubType().toString());
      return null;
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    if (player == null) {
      return null;
    }

    if (!player.isOnline()) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    Long current = cmiUser.getStatistic(cmiStatistic, statName);
    player = cmiUser.getPlayer();
    if (cmiStatistic.getType() == StatsManager.svt.Time) {
      current = current / 50L;
      amount = amount * 20L;
    }

    boolean changed = false;
    switch (action) {
      case Set:
        changed = cmi.getStatsManager().changeTo(player, cmiStatistic, statName, amount);
        break;
      case Add:
        Long added = current + amount;
        changed = cmi.getStatsManager().changeTo(player, cmiStatistic, statName, added);
        break;
      case Take:
        Long subtracted = current - amount;
        changed =
            cmi.getStatsManager()
                .changeTo(player, cmiStatistic, statName, subtracted < 0L ? 0L : subtracted);
    }

    cmiUser.getTotalPlayTime(true);
    Long statValue = cmiUser.getStatistic(cmiStatistic, statName);
    if (cmiStatistic.getType() == StatsManager.svt.Time) {
      statValue = statValue / 1000L;
      current = current / 20L;
    }

    if (!flag) {
      if (!changed) {
        CMILC.info(this, commandSender, "cantSet");
      } else {
        CMILC.info(
            this,
            commandSender,
            "set",
            "[old]",
            current,
            "[new]",
            statValue,
            "[statistic]",
            CMILC.getIM("stats", "Statistics." + cmiStatistic.toString()),
            "[extra]",
            statName.equals("none") ? "" : statName);
      }
    }

    return true;
  }

  private enum Action {
    Set,
    Add,
    Take;
  }
}
