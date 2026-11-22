package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class editplaytime implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("cantSet", "{gcw}Failed to change playtime");
    configReader.get("set", "{gcp}Changed from {gcs}[old] {gcp}to {gcs}[new]");
  }

  @CAnnotation(
      info = "Edit players playtime",
      args = "(playerName) [add/take/set] [amount] (-s)",
      tab = {"playername", "add%%take%%set"},
      regVar = {3, 4},
      consoleVar = {3, 4},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    editplaytime.Action action = editplaytime.Action.Add;
    Long playtimeOffset = null;
    String string = null;
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        String timeValue;
        switch ((timeValue = arg.toLowerCase()).hashCode()) {
          case 96417:
            if (timeValue.equals("add")) {
              action = editplaytime.Action.Add;
              continue;
            }
            break;
          case 113762:
            if (timeValue.equals("set")) {
              action = editplaytime.Action.Set;
              continue;
            }
            break;
          case 3552391:
            if (timeValue.equals("take")) {
              action = editplaytime.Action.Take;
              continue;
            }
        }

        try {
          playtimeOffset = timeModifier.getTimeRangeFromString(arg);
          if (playtimeOffset != null) {
            continue;
          }
        } catch (Exception exception) {
        }

        string = arg;
      }
    }

    if (playtimeOffset == null) {
      LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    Long totalPlay = cmiUser.getTotalPlayTime(true);
    Long totalTicks = totalPlay / 50L;
    playtimeOffset = playtimeOffset * 20L;
    boolean dirty = false;
    switch (action) {
      case Set:
        if (CMI.getInstance().getConfigManager().PlayTimeFromStats) {
          dirty =
              cmi.getStatsManager()
                  .changeTo(
                      player, StatsManager.CMIStatistic.PLAY_ONE_TICK, "none", playtimeOffset);
        } else {
          if (playtimeOffset < 20L) {
            playtimeOffset = 20L;
          }

          player.setTicksLived(playtimeOffset.intValue());
          cmi.save(player);
          dirty = true;
        }
        break;
      case Add:
        Long newTicks = totalTicks + playtimeOffset;
        if (CMI.getInstance().getConfigManager().PlayTimeFromStats) {
          dirty =
              cmi.getStatsManager()
                  .changeTo(player, StatsManager.CMIStatistic.PLAY_ONE_TICK, "none", newTicks);
        } else {
          if (newTicks < 20L) {
            newTicks = 20L;
          }

          if (newTicks > 2147483647L) {
            newTicks = 2147483647L;
          }

          player.setTicksLived(newTicks.intValue());
          cmi.save(player);
          dirty = true;
        }
        break;
      case Take:
        Long ticksDiff = totalTicks - playtimeOffset;
        if (CMI.getInstance().getConfigManager().PlayTimeFromStats) {
          dirty =
              cmi.getStatsManager()
                  .changeTo(
                      player,
                      StatsManager.CMIStatistic.PLAY_ONE_TICK,
                      "none",
                      ticksDiff < 0L ? 0L : ticksDiff);
        } else {
          if (ticksDiff < 20L) {
            ticksDiff = 20L;
          }

          player.setTicksLived(ticksDiff.intValue());
          cmi.save(player);
          dirty = true;
        }
    }

    cmiUser.getTotalPlayTime(true);
    Long totalPlayTime = cmiUser.getTotalPlayTime();
    totalPlayTime = totalPlayTime / 1000L;
    Long totalSeconds = totalTicks / 20L;
    if (!flag) {
      if (!dirty) {
        CMILC.info(this, commandSender, "cantSet");
      } else {
        CMILC.info(this, commandSender, "set", "[old]", totalSeconds, "[new]", totalPlayTime);
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
