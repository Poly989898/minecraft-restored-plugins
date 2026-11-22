package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;

public class editwarnings implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "list",
        "{gcs}[place]{gcp}. {gcs}[time] {gcp}by {gcs}[warnedBy] {gcp}for: {gcs}[warnedFor]");
    configReader.get("hover", "{gcp}Expires in: {gcs}[time]");
    configReader.get("cleared", "{gcp}All warnings from player got cleared");
    configReader.get("allCleared", "{gcp}All warnings from ALL players got cleared");
  }

  @CAnnotation(
      priority = 48,
      info = "Check player warnings",
      args = "(playerName/clearall) (clear)",
      tab = {"playername%%clearall", "clear%%"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    int value = 1;
    Integer integer = null;
    editwarnings.actions actionsValue = null;
    String[] parts = strings;
    int count = strings.length;

    continueSecond:
    for (int countSecond = 0; countSecond < count; countSecond++) {
      String text = parts[countSecond];
      if (text.startsWith("-p:")) {
        try {
          value = Integer.parseInt(text.substring("-p:".length()));
        } catch (Exception exception) {
        }
      } else if (text.startsWith("-id:")) {
        try {
          integer = Integer.parseInt(text.substring("-id:".length()));
        } catch (Exception exception2) {
        }
      } else {
        if (actionsValue == null) {
          editwarnings.actions[] actionses;
          int countThird = (actionses = editwarnings.actions.values()).length;

          for (int countFourth = 0; countFourth < countThird; countFourth++) {
            editwarnings.actions actionsSecond = actionses[countFourth];
            if (actionsSecond.name().equalsIgnoreCase(text)) {
              actionsValue = actionsSecond;
              continue continueSecond;
            }
          }
        }

        if (string == null) {
          string = text;
        }
      }
    }

    if (actionsValue == null) {
      actionsValue = editwarnings.actions.list;
    }

    switch (actionsValue) {
      case delete:
        CMIUser cmiUser = cmi.getUser(commandSender, string, this);
        if (cmiUser == null) {
          return null;
        }

        if (integer == null) {
          return false;
        }

        if (cmiUser.getWarnings() != null && cmiUser.getWarnings().size() >= integer) {
          cmiUser.getWarnings().remove(integer - 1);
        }

        cmiUser.addForDelayedSave();
        cmi.performCommand(
            commandSender,
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " "
                + cmiUser.getName()
                + " list");
        break;
      case clear:
        CMIUser cMIUserSecond = cmi.getUser(commandSender, string, this);
        if (cMIUserSecond == null) {
          return null;
        }

        if (cMIUserSecond.getWarnings() == null) {
          CMILC.info("warnings", commandSender, "noWarnings");
          return true;
        }

        cMIUserSecond.getWarnings().clear();
        cMIUserSecond.addForDelayedSave();
        CMILC.info(this, commandSender, "cleared");
        break;
      case clearall:
        for (var entry : cmi.getPlayerManager().getAllUsers().entrySet()) {
          if (((CMIUser) entry.getValue()).getWarnings() != null) {
            ((CMIUser) entry.getValue()).getWarnings().clear();
            ((CMIUser) entry.getValue()).addForDelayedSave();
          }
        }

        CMILC.info(this, commandSender, "allCleared");
        break;
      case list:
        CMIUser cMIUserThird = cmi.getUser(commandSender, string, this);
        if (cMIUserThird == null) {
          return null;
        }

        if (cMIUserThird.getWarnings() == null) {
          CMILC.info("warnings", commandSender, "noWarnings");
          return true;
        }

        PageInfo pageInfo = new PageInfo(10, cMIUserThird.getWarnings().size(), value);

        for (CMIPlayerWarning cmiPlayerWarning : cMIUserThird.getWarnings()) {
          if (pageInfo.isBreak()) {
            break;
          }

          if (!pageInfo.isContinue()) {
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[place]",
                    pageInfo.getPositionForOutput(),
                    "[time]",
                    DateFormat.MiliToDate(cmiPlayerWarning.getGivenAt()),
                    "[warnedBy]",
                    cmiPlayerWarning.getGivenBy(),
                    "[warnedFor]",
                    cmiPlayerWarning.getReason() == null
                        ? cmiPlayerWarning.getCategory().getDefaultReason()
                        : cmiPlayerWarning.getReason()));
            rawMessage.addHover(
                CMILC.getIM(
                    this,
                    "hover",
                    "[time]",
                    CMITimeManager.to24hourShort(
                        cmiPlayerWarning.getGivenAt()
                            + cmiPlayerWarning.getCategory().getLifeTime()
                            - System.currentTimeMillis())));
            rawMessage.build();
            rawMessage.addText(LC.modify_deleteSymbol.getLocale(new Object[0]));
            rawMessage.addHover(LC.modify_deleteSymbolHover.getLocale(new Object[] {"[text]", ""}));
            rawMessage.addCommand(
                CommandsHandler.getLabel()
                    + " "
                    + this.getClass().getSimpleName()
                    + " delete "
                    + cMIUserThird.getName()
                    + " -id:"
                    + pageInfo.getPositionForOutput());
            rawMessage.build();
            rawMessage.show(commandSender);
          }
        }

        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(),
            "-p:");
    }

    return true;
  }

  private enum actions {
    delete,
    clear,
    clearall,
    list;
  }
}
