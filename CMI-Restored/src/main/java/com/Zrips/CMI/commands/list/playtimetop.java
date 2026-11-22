package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayTimeFormat;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class playtimetop implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("topLine", "{gcp}----- TopPlaytime {gcp}-----");
    configReader.get(
        "scanning", "{gcp}Data is being scanned currently, please wait a minute and try again");
    configReader.get(
        "loading",
        "{gcp}Data is not loaded yet. Scan process will start and you will get result after its"
            + " finished");
    configReader.get("list", "{gcs}[number]{gcp}. {gcs}[playerDisplayName] {gcp}- {gcs}[time]");
    configReader.get("place", "{gcs}[playerDisplayName] {gcp}is in {gcs}[place] {gcp}place");
    configReader.get("ownLine", "{gcu}[number]{gcp}. {gcs}[playerDisplayName] {gcp}- {gcs}[time]");
  }

  @CAnnotation(
      priority = 110,
      info = "Shows top list of player total play time",
      args = "[page]",
      tab = {},
      explanation = {},
      regVar = {0, 1},
      consoleVar = {0, 1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    StatsManager statsManager = cmi.getStatsManager();
    int value = 1;
    CMIUser cmiUser = null;
    if (strings.length > 0) {
      try {
        value = Integer.parseInt(strings[0]);
      } catch (Exception exception) {
        cmiUser = cmi.getPlayerManager().getUser(strings[0]);
      }
    }

    if (statsManager.isScanning()) {
      CMILC.info(this, commandSender, "scanning");
      return null;
    }

    if (!statsManager.isDataLoaded()) {
      CMILC.info(this, commandSender, "loading");
      String string = null;
      if (cmiUser != null) {
        string = cmiUser.getName();
      }

      String argText = string;
      statsManager
          .loadStats()
          .whenComplete(
              (unusedResult, throwable) -> {
                if (commandSender instanceof Player) {
                  Player player = (Player) commandSender;
                  Player onlinePlayer = CMIUser.getOnlinePlayer(player.getUniqueId());
                  if (onlinePlayer == null) {
                    return;
                  }

                  CMIScheduler.runTask(
                      cmi,
                      () ->
                          CMI.getInstance()
                              .performCommand(
                                  onlinePlayer,
                                  "cmi playtimetop" + (argText != null ? " " + argText : "")));
                } else {
                  commandSender.sendMessage("Stats loaded!");
                }
              });
      return true;
    } else {
      var cmiLeaderboard = cmi.getStatsManager().getLeaderboard();
      if (cmiUser != null) {
        Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
        int userPlace = cmiLeaderboard.getRank(cmiUser.getUniqueId());
        CMILC.info(this, commandSender, "place", "[place]", userPlace, snd);
        return true;
      }

      if (value < 1) {
        value = 1;
      }

      PageInfo pageInfo = new PageInfo(10, cmiLeaderboard.getRecordCount(), value);
      List list = cmiLeaderboard.getRange(pageInfo.getStart(), pageInfo.getEnd());
      int userRank = 0;
      cmiUser = cmi.getUser(commandSender, null, this, commandSender instanceof Player, true);
      CMILC.info(this, commandSender, "topLine");
      if (cmiUser != null) {
        userRank = cmiLeaderboard.getRank(cmiUser.getUniqueId());
        if (pageInfo.getStart() >= userRank) {
          Snd userSnd = new Snd().setSender(commandSender).setTarget(cmiUser);
          CMILC.info(
              this,
              commandSender,
              "ownLine",
              "[number]",
              userRank,
              userSnd,
              "[time]",
              CMIPlayTimeFormat.formatPlaytimePlaceholder(cmiUser.getTotalPlayTime()));
        }
      }

      int entryIndex = 0;

      for (Entry entry : ((Iterable<Entry>) list)) {
        entryIndex++;
        CMIUser entryUser = CMIUser.getUser((UUID) entry.getKey());
        if (entryUser != null && entryUser.getName(false) != null) {
          Snd entrySnd = new Snd().setSender(commandSender).setTarget(entryUser);
          String playtimeText =
              CMIPlayTimeFormat.formatPlaytimePlaceholder((Long) entry.getValue());
          if (cmiUser != null && entryUser.getName(false).equals(cmiUser.getName(false))) {
            CMILC.info(
                this,
                commandSender,
                "ownLine",
                "[number]",
                pageInfo.getStart() + entryIndex,
                entrySnd,
                "[time]",
                CMIPlayTimeFormat.formatPlaytimePlaceholder(entryUser.getTotalPlayTime()));
          } else {
            CMILC.info(
                this,
                commandSender,
                "list",
                "[number]",
                pageInfo.getStart() + entryIndex,
                "[time]",
                playtimeText,
                entrySnd);
          }
        }
      }

      if (cmiUser != null && pageInfo.getEnd() + 1 < userRank) {
        Snd rankSnd = new Snd().setSender(commandSender).setTarget(cmiUser);
        CMILC.info(
            this,
            commandSender,
            "ownLine",
            "[number]",
            userRank,
            rankSnd,
            "[time]",
            CMIPlayTimeFormat.formatPlaytimePlaceholder(cmiUser.getTotalPlayTime()));
      }

      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
      return true;
    }
  }
}
