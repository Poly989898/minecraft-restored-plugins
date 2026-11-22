package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIBanEntry;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class checkban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[playerDisplayName] {gcs}[time]");
    configReader.get("name", "{gcp}Name: {gcs}[playerName]");
    configReader.get("permanent", "{gcn}Permanent");
    configReader.get("bannedUntil", "{gcp}Banned for: {gcs}[time]");
    configReader.get("reason", "{gcp}Reason: {gcs}[reason]");
    configReader.get("bannedBy", "{gcp}Banned by: {gcs}[sourceDisplayName]");
    configReader.get("bannedWhen", "{gcp}Banned at: {gcs}[date]");
  }

  @CAnnotation(
      info = "Check players ban status",
      args = "(playerName)",
      tab = "bannedplayername",
      regVar = {0, 1},
      consoleVar = {0, 1})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    int value = 1;
    String[] parts = strings;
    int count = strings.length;

    for (int countSecond = 0; countSecond < count; countSecond++) {
      String text = parts[countSecond];

      try {
        if (text.startsWith("-p:")) {
          text = text.replace("-p:", "");
          value = Integer.parseInt(text);
          continue;
        }
      } catch (Exception exception) {
      }

      if (string == null) {
        string = text;
      }
    }

    this.showPlayerBan(commandSender, string, value);
    return true;
  }

  private void showPlayerBan(CommandSender commandSender, String string, int value) {
    CMIScheduler.runTaskAsynchronously(
        CMI.getInstance(),
        () -> {
          CMIUser cmiUser = CMIUser.getUser(string);
          if (cmiUser == null && string != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(string);
            cmiUser = CMIUser.getUser(offlinePlayer);
          }

          if (cmiUser == null) {
            this.showPlayerList(commandSender, string, value);
          } else {
            CMIUser cMIUserSecond = cmiUser;
            CMIScheduler.runTask(
                CMI.getInstance(), () -> this.showPlayerBan(commandSender, cMIUserSecond));
          }
        });
  }

  private void showPlayerList(CommandSender commandSender, String string, int value) {
    boolean flag =
        PermissionsManager.CMIPerm.command_checkban_seereason.hasPermission(commandSender);
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    TreeMap<Long, BanEntry> treeMap = new TreeMap(Collections.reverseOrder());
    var banList = CMIBanRecords.getBanEntries();
    if (banList != null) {
      for (Object object : banList.getBanEntries()) {
        BanEntry banEntry = (BanEntry) object;
        if ((string == null || string.equalsIgnoreCase(banEntry.getTarget()))
            && banEntry.getCreated() != null) {
          treeMap.put(banEntry.getCreated().getTime(), banEntry);
        }
      }
    }

    var banListValue = CMIBanRecords.getBanEntriesByIP();
    if (banListValue != null) {
      for (Object valueSecond : banListValue.getBanEntries()) {
        BanEntry banEntryValue = (BanEntry) valueSecond;
        if ((string == null || string.equalsIgnoreCase(banEntryValue.getTarget()))
            && banEntryValue.getCreated() != null) {
          treeMap.put(banEntryValue.getCreated().getTime(), banEntryValue);
        }
      }
    }

    PageInfo pageInfo = new PageInfo(15, treeMap.size(), value);

    for (Entry entry : treeMap.entrySet()) {
      if (pageInfo.isEntryOk()) {
        BanEntry banEntrySecond = (BanEntry) entry.getValue();
        if (banEntrySecond != null) {
          Snd snd = new Snd().setSender(commandSender).setTargetName(banEntrySecond.getTarget());
          RawMessage rawMessage = new RawMessage();
          String text = "";
          if (banEntrySecond.getSource() != null) {
            snd =
                new Snd()
                    .setSourceName(banEntrySecond.getSource())
                    .setSenderName(banEntrySecond.getSource())
                    .setTargetName(banEntrySecond.getTarget());
            text = CMILC.getIM(this, "bannedBy", snd);
          } else {
            text =
                CMILC.getIM(
                    this,
                    "bannedBy",
                    "[sourceDisplayName]",
                    LC.info_Console.getLocale(new Object[0]));
          }

          if (flag) {
            text =
                text + " \n" + CMILC.getIM(this, "reason", "[reason]", banEntrySecond.getReason());
          }

          if (banEntrySecond.getCreated() != null) {
            text =
                text
                    + " \n"
                    + CMILC.getIM(
                        this,
                        "bannedWhen",
                        "[date]",
                        DateFormat.MiliToDate(banEntrySecond.getCreated().getTime()));
          }

          long longValue =
              banEntrySecond.getExpiration() != null
                      && banEntrySecond.getExpiration().getTime() != -1L
                  ? banEntrySecond.getExpiration().getTime() - System.currentTimeMillis()
                  : -1L;
          rawMessage
              .addText(
                  CMILC.getIM(
                      this,
                      "list",
                      "[place]",
                      pageInfo.getPositionForOutput(),
                      snd,
                      "[time]",
                      longValue == -1L
                          ? CMILC.getIM(this, "permanent")
                          : CMITimeManager.to24hourShort(longValue)))
              .addHover(text)
              .addCommand("cmi checkban " + banEntrySecond.getTarget());
          rawMessage.show(commandSender);
        }
      }
    }

    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
  }

  private void showPlayerBan(CommandSender commandSender, final CMIUser cmiUser) {
    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (!cmiUser.isBanned()) {
      CMILC.info("unban", commandSender, "notBanned", snd);
    } else {
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      boolean flag =
          PermissionsManager.CMIPerm.command_checkban_seereason.hasPermission(commandSender);
      boolean flagSecond =
          PermissionsManager.CMIPerm.command_checkban_modify.hasPermission(commandSender);
      CMIBanEntry cmiBanEntry = cmiUser.getBanEntry();
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM(this, "name", snd));
      rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
      rawMessage.addCommand(
          CommandsHandler.getLabel()
              + " "
              + checkban.class.getSimpleName()
              + " "
              + cmiBanEntry.getTarget());
      if (flagSecond) {
        rawMessage.addText(" &7[Unban]");
        RawMessageCommand rawMessageCommand =
            new RawMessageCommand() {
              public void run(CommandSender commandSenderValue) {
                CMIBanRecords.unBan(cmiUser);
                checkban.this.showPlayerList(commandSenderValue, null, 1);
              }
            };
        rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
        rawMessage.addCommand(rawMessageCommand);
      }

      rawMessage.show(commandSender);
      rawMessage = new RawMessage();
      rawMessage.addText(
          CMILC.getIM(
              this,
              "bannedUntil",
              "[time]",
              cmiBanEntry.isPermanent()
                  ? CMILC.getIM(this, "permanent")
                  : CMITimeManager.to24hourShort(cmiBanEntry.getLeftTime())));
      if (flagSecond) {
        rawMessage.addText(" &7[Convert to:");
        this.addTimeModifier(rawMessage, cmiUser, 3600000L);
        this.addTimeModifier(rawMessage, cmiUser, 86400000L);
        this.addTimeModifier(rawMessage, cmiUser, 2592000000L);
        this.addTimeModifier(rawMessage, cmiUser, null);
        rawMessage.addText("]");
      }

      rawMessage.show(commandSender);
      if (flag) {
        rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(this, "reason", "[reason]", cmiUser.getBanEntry().getReason()));
        if (flagSecond) {
          rawMessage.addText(
              " &7" + CMIChatColor.stripColor(LC.modify_editSymbol.getLocale(new Object[0])));
          rawMessage.addHover(LC.modify_editSymbolHover.getLocale(new Object[] {"[text]", ""}));
          RawMessageCommand rawMessageCommandValue =
              new RawMessageCommand() {
                public void run(final CommandSender commandSenderSecond) {
                  ChatMessageEdit chatMessageEdit =
                      new ChatMessageEdit(
                          commandSenderSecond,
                          cmiUser.getBanEntry().getReason() == null
                              ? ""
                              : cmiUser.getBanEntry().getReason()) {
                        public void run(String string) {
                          BanEntry banEntry = CMIBanRecords.getBanEntry(cmiUser);
                          if (!string.isEmpty()
                              && !string.equals("_")
                              && !string.equalsIgnoreCase("delete")) {
                            banEntry.setReason(string);
                          } else {
                            banEntry.setReason(null);
                          }

                          CMIBanRecords.updateBanEntry(cmiUser, banEntry);
                          banEntry.save();
                          checkban.this.showPlayerBan(commandSenderSecond, cmiUser);
                        }

                        public void onCancel() {
                          checkban.this.showPlayerBan(commandSenderSecond, cmiUser);
                        }
                      };
                  chatMessageEdit.setCheckForCancel(true);
                  chatMessageEdit.printMessage();
                }
              };
          rawMessage.addCommand(rawMessageCommandValue);
        }

        rawMessage.show(commandSender);
      }

      if (cmiUser.getBanEntry().getSourceUser() != null) {
        snd = new Snd().setSender(cmiUser.getBanEntry().getSourceUser()).setTarget(cmiUser);
        snd.setSource(cmiUser.getBanEntry().getSourceUser());
        rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM(this, "bannedBy", snd));
        if (flagSecond) {
          rawMessage.addText(" &7[Change to console]");
          RawMessageCommand rawMessageCommandSecond =
              new RawMessageCommand() {
                public void run(CommandSender commandSenderThird) {
                  BanEntry banEntryValue = CMIBanRecords.getBanEntry(cmiUser);
                  banEntryValue.setSource(LC.info_Console.getLocale(new Object[0]));
                  banEntryValue.save();
                  CMIBanRecords.updateBanEntry(cmiUser, banEntryValue);
                  checkban.this.showPlayerBan(commandSenderThird, cmiUser);
                }
              };
          rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
          rawMessage.addCommand(rawMessageCommandSecond);
        }

        rawMessage.show(commandSender);
      } else {
        CMILC.info(
            this,
            commandSender,
            "bannedBy",
            "[sourceDisplayName]",
            LC.info_Console.getLocale(new Object[0]));
      }

      if (cmiUser.getBanEntry().getCreated() != null) {
        rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(
                this,
                "bannedWhen",
                "[date]",
                DateFormat.MiliToDate(cmiUser.getBanEntry().getCreated())));
        rawMessage.show(commandSender);
      }
    }
  }

  private void changeBanTime(CommandSender commandSender, CMIUser cmiUser, Long time) {
    BanEntry banEntry = CMIBanRecords.getBanEntry(cmiUser);
    banEntry.setCreated(new Date(System.currentTimeMillis()));
    if (time == null) {
      banEntry.setExpiration(null);
    } else {
      banEntry.setExpiration(new Date(System.currentTimeMillis() + time));
    }

    banEntry.save();
    CMIBanRecords.updateBanEntry(cmiUser, banEntry);
    this.showPlayerBan(commandSender, cmiUser);
  }

  private void addTimeModifier(RawMessage rawMessage, final CMIUser cmiUser, final Long time) {
    String string =
        time == null
            ? "P"
            : (time == 2592000000L
                ? "1M"
                : (time == 86400000L
                    ? "1D"
                    : (time == 3600000L ? "1H" : CMITimeManager.to24hourShort(time))));
    rawMessage.addText(" " + (time == null ? "P" : string));
    RawMessageCommand rawMessageCommand =
        new RawMessageCommand() {
          public void run(CommandSender commandSender) {
            checkban.this.changeBanTime(commandSender, cmiUser, time);
          }
        };
    rawMessage.addHover(time == null ? "P" : string);
    rawMessage.addCommand(rawMessageCommand);
  }
}
