package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import java.util.Arrays;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;

public class warnings implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "totalPoints",
        "{gcp}---- {gcs}[playerDisplayName] {gcp}total violation points: {gcs}[totalPoints]"
            + " {gcp}----");
    configReader.get("noWarnings", "{gcp}Player doesn't have any warnings");
    configReader.get(
        "list", "{gcp}[place]. {gcs}[time] {gcp}by [warnedBy] {gcn}for: {gcp}[warnedFor]");
    configReader.get(
        "hover",
        Arrays.asList("{gcp}Expires in: {gcs}[time]", "{gcp}Violation points: {gcs}[points]"));
  }

  @CAnnotation(
      info = "Check player warnings",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    int value = 1;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length()));
        } catch (Exception exception) {
        }
      } else if (string == null) {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (cmiUser.getWarnings() == null) {
      CMILC.info(this, commandSender, "noWarnings");
      return true;
    }

    PageInfo pageInfo = new PageInfo(10, cmiUser.getWarnings().size(), value);
    Snd snd = new Snd();
    snd.setTarget(cmiUser);
    snd.setSender(commandSender);
    CMILC.info(
        this, commandSender, "totalPoints", "[totalPoints]", cmiUser.getWarningPoints(), snd);

    for (CMIPlayerWarning cmiPlayerWarning : cmiUser.getWarnings()) {
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
        var list =
            CMILC.getIML(
                this,
                "hover",
                "[time]",
                CMITimeManager.to24hourShort(
                    cmiPlayerWarning.getGivenAt()
                        + cmiPlayerWarning.getCategory().getLifeTime()
                        - System.currentTimeMillis()),
                "[points]",
                cmiPlayerWarning.getCategory().getPoints());
        rawMessage.addHover(list);
        rawMessage.build();
        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + this.getClass().getSimpleName()
            + " "
            + cmiUser.getName(),
        "-p:");
    return true;
  }
}
