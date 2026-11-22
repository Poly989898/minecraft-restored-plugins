package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMILeaderboard;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Time.CMITimeManager;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class lastonline implements Cmd {
  private static CMILeaderboard<Long> leaderboard = new CMILeaderboard<>(1200L);
  private static boolean init = false;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "searching", "{gcp}Searching {gcs}[checked]{gcp}/{gcs}[total] {gcp}({gcs}[found]{gcp})");
    configReader.get("List", " {gcs}[place]{gcp}. {gcs}[name] {gcp}-> {gcs}[time]");
    configReader.get(
        "timeFrame",
        "{gcp}Last hour ({gcs}[time]{gcp}), 24 hours ({gcs}[timeSecond]{gcp}), 7 days"
            + " ({gcs}[timeThird]{gcp}), 30 days ({gcs}[timeFourth]{gcp})");
  }

  private static int getSizeByTime(int value) {
    long longValue = System.currentTimeMillis() - value * 1000L;
    return leaderboard.countSince(longValue);
  }

  public static void addLogOff(CMIUser cmiUser) {
    if (init) {
      long longValue =
          cmiUser.isOnline()
              ? cmiUser.getLastLogin()
              : (cmiUser.getLastLogoff() == 0L
                  ? (cmiUser.getLastLogin() == 0L ? 0L : cmiUser.getLastLogin())
                  : cmiUser.getLastLogoff());
      leaderboard.setValue(cmiUser.getUniqueId(), longValue);
    }
  }

  @CAnnotation(
      info = "Show played players from last x minutes",
      args = "(-p:[page])",
      regVar = {0, 1},
      consoleVar = {0, 1},
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 1;
    int count = 10;
    String[] parts = strings;
    int countSecond = strings.length;

    for (int countThird = 0; countThird < countSecond; countThird++) {
      String string = parts[countThird];
      if (string.startsWith("-p:")) {
        try {
          value = Integer.parseInt(string.substring("-p:".length()));
          continue;
        } catch (Exception exception) {
        }
      }

      if (string.startsWith("-list:")) {
        try {
          count = Integer.parseInt(string.substring("-list:".length()));
          continue;
        } catch (Exception exception2) {
        }
      }

      try {
        value = Integer.parseInt(string);
      } catch (Exception exception3) {
      }
    }

    if (value < 1) {
      value = 1;
    }

    int countFourth = leaderboard.getRecordCount();
    if (countFourth == 0) {
      int countFifth = value;
      final int value3Final = count;
      recalculate()
          .thenRun(
              () ->
                  leaderboard
                      .rebuild()
                      .thenRun(() -> this.show(commandSender, countFifth, value3Final)));
    } else {
      this.show(commandSender, value, count);
    }

    return true;
  }

  private static CompletableFuture<Void> recalculate() {
    init = true;
    return CompletableFuture.runAsync(
        () -> {
          for (CMIUser cmiUser : CMIUser.getUsers()) {
            addLogOff(cmiUser);
          }
        });
  }

  private void show(CommandSender commandSender, int value, int count) {
    var list = leaderboard.getRange((value - 1) * count, (value - 1) * count + count);
    int countSecond = leaderboard.getRecordCount();
    int countThird = countSecond / count + 1;
    value = value > countThird ? countThird : value;
    PageInfo pageInfo = new PageInfo(count, leaderboard.getRecordCount(), value);
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    Snd snd = new Snd();
    snd.setSender(commandSender);
    CMILC.info(
        this,
        commandSender,
        "timeFrame",
        "[time]",
        getSizeByTime(Config.lastonlineTimers.get(0)),
        "[timeSecond]",
        getSizeByTime(Config.lastonlineTimers.get(1)),
        "[timeThird]",
        getSizeByTime(Config.lastonlineTimers.get(2)),
        "[timeFourth]",
        getSizeByTime(Config.lastonlineTimers.get(3)));
    int countFourth = 0;

    for (Entry entry : list) {
      UUID uuid = (UUID) entry.getKey();
      long longValue = (Long) entry.getValue();
      countFourth++;
      RawMessage rawMessage = new RawMessage();
      CMIUser cmiUser = CMIUser.getUser(uuid);
      if (cmiUser != null) {
        snd.setTarget(cmiUser);
        if (longValue < 1000000L) {
          longValue = 0L;
        }

        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "List",
                    "[place]",
                    (value - 1) * count + countFourth,
                    "[name]",
                    cmiUser.getName(),
                    "[time]",
                    longValue == 0L
                        ? LC.info_TimeNotRecorded.getLocale(new Object[0])
                        : CMITimeManager.to24hourAproximateShort(
                            System.currentTimeMillis() - longValue, true),
                    snd))
            .addHover(LC.info_ClickToPaste.getLocale(new Object[0]))
            .addSuggestion(cmiUser.getName());
        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
  }
}
