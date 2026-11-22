package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayDay;
import com.Zrips.CMI.Modules.PlayTime.PlayTimeManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cplaytime implements Cmd {
  static int c = 0;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("moreDetails", "{gcp}Click for more details");
    configReader.get("title", "&8[playerDisplayName] {gcp}playtime");
    configReader.get("date", "&8[date]");
    configReader.get("time", "{gcs}[hour]:00{gcn}-{gcs}[nextHour]:00");
    PlayTimeManager.PlaytimeRange[] playtimeRanges;
    int value = (playtimeRanges = PlayTimeManager.PlaytimeRange.values()).length;

    for (int index = 0; index < value; index++) {
      PlayTimeManager.PlaytimeRange playtimeRange = playtimeRanges[index];
      switch (playtimeRange) {
        case thishour:
          configReader.get(playtimeRange.name(), "{gcp}Current hour");
          break;
        case hourback:
          configReader.get(playtimeRange.name(), "{gcp}Previous hour");
          break;
        case today:
          configReader.get(playtimeRange.name(), "{gcp}Today");
          break;
        case yesterday:
          configReader.get(playtimeRange.name(), "{gcp}Yesterday");
          break;
        case thisweek:
          configReader.get(playtimeRange.name(), "{gcp}Current week");
          break;
        case week:
          configReader.get(playtimeRange.name(), "{gcp}7 day range");
          break;
        case thismonth:
          configReader.get(playtimeRange.name(), "{gcp}Current month");
          break;
        case month:
          configReader.get(playtimeRange.name(), "{gcp}30 day range");
          break;
        case thisyear:
          configReader.get(playtimeRange.name(), "{gcp}Current year");
          break;
        case year:
          configReader.get(playtimeRange.name(), "{gcp}365 day range");
          break;
        case total:
          configReader.get(playtimeRange.name(), "{gcp}Total");
      }
    }
  }

  @CAnnotation(
      info = "Detailed playtime",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1, 2, 3},
      consoleVar = 666,
      modules = "cmiplaytime",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!cmi.getConfigManager().isCMIPlayTimeTracking()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player player = (Player) commandSender;
    String string = null;
    PlayTimeManager.PlaytimeRange playtimeRange = null;
    Integer integer = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int index = 0; index < value; index++) {
      String arg = argsCopy[index];
      if (playtimeRange == null) {
        playtimeRange = PlayTimeManager.PlaytimeRange.getByName(arg);
        if (playtimeRange != null) {
          continue;
        }
      }

      if (integer == null) {
        try {
          integer = Integer.parseInt(arg);
          continue;
        } catch (Exception exception) {
        }
      }

      string = arg;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    cmiUser.getCMIPlayTime().updatePlayTime();
    Snd snd = new Snd();
    snd.setSender(commandSender);
    snd.setTarget(cmiUser);
    if (playtimeRange == null) {
      CMIGui cmiGui = new CMIGui(player);
      cmiGui.setTitle(CMILC.getIM(this, "title", snd));
      cmiGui.setInvSize(GUIRows.r6);
      PlayTimeManager.PlaytimeRange[] playtimeRanges;
      int rangeCount = (playtimeRanges = PlayTimeManager.PlaytimeRange.values()).length;

      for (int rangeIndex = 0; rangeIndex < rangeCount; rangeIndex++) {
        PlayTimeManager.PlaytimeRange range = playtimeRanges[rangeIndex];
        CMIGuiButton cmiGuiButton = new CMIGuiButton(range.getColor().newItemStack());
        cmiGuiButton.setName(CMILC.getIM(this, range.name()));
        cmiGuiButton.setSlot(range.getSlot());
        cmiGuiButton.addLore(
            CMITimeManager.to24hourShort(cmiUser.getCMIPlayTime().getPlayTime(range)));
        if (range.isDetailed()) {
          cmiGuiButton.addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + this.getClass().getSimpleName()
                  + " "
                  + range.name()
                  + " "
                  + cmiUser.getName(),
              CommandType.silent);
          cmiGuiButton.addLore(CMILC.getIM(this, "moreDetails"));
        }

        cmiGui.addButton(cmiGuiButton);
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
      return true;
    } else {
      switch (playtimeRange) {
        case thishour:
        case hourback:
        case thisyear:
        case year:
        case total:
        default:
          break;
        case today:
        case yesterday:
          CMIGui gui = new CMIGui(player);
          gui.setTitle(CMILC.getIM(this, "title", snd));
          CMIPlayDay cmiPlayDay = cmiUser.getCMIPlayTime().getPlayDayOfToday();
          if (playtimeRange.equals(PlayTimeManager.PlaytimeRange.yesterday)) {
            cmiPlayDay =
                cmiUser
                    .getCMIPlayTime()
                    .getPlayDay(PlayTimeManager.PlaytimeRange.yesterday.getStartTime());
          } else if (integer != null) {
            cmiPlayDay = cmiUser.getCMIPlayTime().getPlayDay(integer);
          }

          int totalMinutes = 0;
          int maxRows = 10;
          int rowCount = 0;

          for (int hour = 0; hour < 24; hour++) {
            Long hourPlaytime = cmiPlayDay.getHourPlaytime(hour);
            CMIMaterial cmiMaterial = CMIMaterial.BLACK_WOOL;
            if (hourPlaytime == 0L) {
              cmiMaterial = CMIMaterial.BLACK_WOOL;
            } else if (hourPlaytime < 900000L) {
              cmiMaterial = CMIMaterial.GRAY_WOOL;
            } else if (hourPlaytime < 1800000L) {
              cmiMaterial = CMIMaterial.LIGHT_GRAY_WOOL;
            } else if (hourPlaytime < 2700000L) {
              cmiMaterial = CMIMaterial.WHITE_WOOL;
            } else if (hourPlaytime < 3000000L) {
              cmiMaterial = CMIMaterial.YELLOW_WOOL;
            } else if (hourPlaytime < 3600000L) {
              cmiMaterial = CMIMaterial.LIME_WOOL;
            } else {
              cmiMaterial = CMIMaterial.GREEN_WOOL;
            }

            CMIGuiButton dayButton = new CMIGuiButton(cmiMaterial.newItemStack());
            dayButton.setName(CMILC.getIM(this, "time", "[hour]", hour, "[nextHour]", hour + 1));
            dayButton.addLore(CMITimeManager.to24hourShort(hourPlaytime));
            dayButton.setSlot(maxRows);
            gui.addButton(dayButton);
            totalMinutes++;
            maxRows++;
            if (totalMinutes > 2) {
              rowCount++;
              totalMinutes = 0;
              maxRows++;
              if (rowCount > 1) {
                rowCount = 0;
                maxRows++;
              }
            }
          }

          CMIGuiButton blankButton = new CMIGuiButton(CMIMaterial.BROWN_WOOL.newItemStack());
          blankButton.setName(LC.info_Back.getLocale(new Object[0]));
          blankButton.setSlot(49);
          blankButton.addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + this.getClass().getSimpleName()
                  + " "
                  + cmiUser.getName(),
              CommandType.silent);
          gui.addButton(blankButton);
          gui.fillEmptyButtons();
          gui.open();
          break;
        case thisweek:
        case week:
          CMIGui dayGui = new CMIGui(player);
          dayGui.setTitle(CMILC.getIM(this, "title", snd));
          Integer rangeStart = playtimeRange.getStartTime();
          int dayRow = 0;
          int dayMax = 10;

          for (int dayOffset = 7; dayOffset > -7; dayOffset--) {
            Integer dayDate = cmi.getPlayTimeManager().getDaysBackDate(dayOffset);
            if ((!playtimeRange.equals(PlayTimeManager.PlaytimeRange.week) || dayDate > rangeStart)
                && (!playtimeRange.equals(PlayTimeManager.PlaytimeRange.thisweek)
                    || dayDate >= rangeStart)) {
              Long dayTotal = cmiUser.getCMIPlayTime().getPlayDay(dayDate).getTotalTime();
              CMIMaterial blackMaterial = CMIMaterial.BLACK_WOOL;
              if (dayTotal == 0L) {
                blackMaterial = CMIMaterial.BLACK_WOOL;
              } else if (dayTotal < 2700000L) {
                blackMaterial = CMIMaterial.GRAY_WOOL;
              } else if (dayTotal < 5400000L) {
                blackMaterial = CMIMaterial.LIGHT_GRAY_WOOL;
              } else if (dayTotal < 8100000L) {
                blackMaterial = CMIMaterial.WHITE_WOOL;
              } else if (dayTotal < 9000000L) {
                blackMaterial = CMIMaterial.YELLOW_WOOL;
              } else if (dayTotal < 10800000L) {
                blackMaterial = CMIMaterial.LIME_WOOL;
              } else {
                blackMaterial = CMIMaterial.GREEN_WOOL;
              }

              CMIGuiButton dayCellButton = new CMIGuiButton(blackMaterial.newItemStack());
              dayCellButton.setName(
                  CMILC.getIM(
                      this, "date", "[date]", cmi.getPlayTimeManager().formatDate(dayDate)));
              dayCellButton.addLore(CMITimeManager.to24hourShort(dayTotal));
              dayCellButton.addLore(CMILC.getIM(this, "moreDetails"));
              dayCellButton.addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " "
                      + cmiUser.getName()
                      + " "
                      + PlayTimeManager.PlaytimeRange.today.name()
                      + " "
                      + dayDate,
                  CommandType.silent);
              dayCellButton.setSlot(dayMax);
              dayGui.addButton(dayCellButton);
              dayRow++;
              dayMax++;
              if (dayRow > 6) {
                break;
              }
            }
          }

          CMIGuiButton brownButton = new CMIGuiButton(CMIMaterial.BROWN_WOOL.newItemStack());
          brownButton.setName(LC.info_Back.getLocale(new Object[0]));
          brownButton.setSlot(49);
          brownButton.addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + this.getClass().getSimpleName()
                  + " "
                  + cmiUser.getName(),
              CommandType.silent);
          dayGui.addButton(brownButton);
          dayGui.fillEmptyButtons();
          dayGui.open();
          break;
        case thismonth:
        case month:
          CMIGui monthGui = new CMIGui(player);
          monthGui.setTitle(CMILC.getIM(this, "title", snd));
          Integer monthRangeStart = playtimeRange.getStartTime();
          int monthRow = 0;
          int monthCol = 0;

          for (int monthOffset = 30; monthOffset > -31; monthOffset--) {
            Integer monthDate = cmi.getPlayTimeManager().getDaysBackDate(monthOffset);
            if ((!playtimeRange.equals(PlayTimeManager.PlaytimeRange.month)
                    || monthDate > monthRangeStart)
                && (!playtimeRange.equals(PlayTimeManager.PlaytimeRange.thismonth)
                    || monthDate >= monthRangeStart)) {
              if (playtimeRange.equals(PlayTimeManager.PlaytimeRange.thismonth)
                  && monthDate - monthRangeStart > 32) {
                break;
              }

              Long monthTotal = cmiUser.getCMIPlayTime().getPlayDay(monthDate).getTotalTime();
              CMIMaterial monthBlackMaterial = CMIMaterial.BLACK_WOOL;
              if (monthTotal == 0L) {
                monthBlackMaterial = CMIMaterial.BLACK_WOOL;
              } else if (monthTotal < 2700000L) {
                monthBlackMaterial = CMIMaterial.GRAY_WOOL;
              } else if (monthTotal < 5400000L) {
                monthBlackMaterial = CMIMaterial.LIGHT_GRAY_WOOL;
              } else if (monthTotal < 8100000L) {
                monthBlackMaterial = CMIMaterial.WHITE_WOOL;
              } else if (monthTotal < 9000000L) {
                monthBlackMaterial = CMIMaterial.YELLOW_WOOL;
              } else if (monthTotal < 10800000L) {
                monthBlackMaterial = CMIMaterial.LIME_WOOL;
              } else {
                monthBlackMaterial = CMIMaterial.GREEN_WOOL;
              }

              CMIGuiButton monthButton = new CMIGuiButton(monthBlackMaterial.newItemStack());
              monthButton.setName(
                  CMILC.getIM(
                      this, "date", "[date]", cmi.getPlayTimeManager().formatDate(monthDate)));
              monthButton.addLore(CMITimeManager.to24hourShort(monthTotal));
              monthButton.addLore(CMILC.getIM(this, "moreDetails"));
              monthButton.addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " "
                      + cmiUser.getName()
                      + " "
                      + PlayTimeManager.PlaytimeRange.today.name()
                      + " "
                      + monthDate,
                  CommandType.silent);
              monthButton.setSlot(monthCol);
              monthGui.addButton(monthButton);
              monthRow++;
              monthCol++;
              if (monthRow > 30) {
                break;
              }
            }
          }

          CMIGuiButton monthBrownButton = new CMIGuiButton(CMIMaterial.BROWN_WOOL.newItemStack());
          monthBrownButton.setName(LC.info_Back.getLocale(new Object[0]));
          monthBrownButton.setSlot(49);
          monthBrownButton.addCommand(
              CommandsHandler.getLabel()
                  + " "
                  + this.getClass().getSimpleName()
                  + " "
                  + cmiUser.getName(),
              CommandType.silent);
          monthGui.addButton(monthBrownButton);
          monthGui.fillEmptyButtons();
          monthGui.open();
      }

      return true;
    }
  }
}
