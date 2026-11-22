package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Shadow.ShadowCommand;
import net.Zrips.CMILib.Shadow.ShadowCommandType;
import net.Zrips.CMILib.commands.CMICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class rankdown implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noPrev", "{gcp}Can't find any previous ranks");
    configReader.get("list", "{gcp}Pick one rank you want to {gcs}rankdown {gcp}to: ");
    configReader.get("confirm", "{gcp}Click me to confirm {gcs}rankdown {gcp}to {gcs}[rank]");
    configReader.get("downranked", "{gcp}Ranked down to {gcs}[rank]");
  }

  @CAnnotation(
      priority = 110,
      info = "Decrease your rank",
      args = "(playerName) (rankName) (confirm) (-cmd) (-cost)",
      tab = "rankname",
      explanation = {
        "-cmd will perform command defined for previous rank rankup",
        "-cost will charge player with money, exp or items if defined for previous rank rankup"
      },
      regVar = {0, 1, 2, 3, 4, 5},
      consoleVar = {2, 3, 4, 5},
      alias = true,
      modules = "ranks",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    CMIRank cmiRank = null;
    String string = null;
    Boolean runCommand = false;
    Boolean withCost = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("confirm")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("-cmd")) {
        runCommand = true;
      } else if (arg.equalsIgnoreCase("-cost")) {
        withCost = true;
      } else if (cmi.getRankManager().getRank(arg) != null) {
        cmiRank = cmi.getRankManager().getRank(arg);
      } else {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    CMIRank currentRank = cmiUser.getRank();
    if (currentRank == null) {
      CMILC.info("rankup", commandSender, "noRank");
      return null;
    }

    var list = currentRank.getValidPreviousRanks(cmiUser);
    if (list.isEmpty()) {
      CMILC.info(this, commandSender, "noPrev");
      return null;
    }

    if (cmiRank != null) {
      if (!flag) {
        if (commandSender instanceof Player) {
          String shadowArgs =
              ShadowCommand.addShadowCmd(
                  (Player) commandSender,
                  CommandsHandler.getLabel()
                      + " rankdown "
                      + cmiUser.getName()
                      + " "
                      + cmiRank.getName()
                      + " confirm",
                  false,
                  ShadowCommandType.Player);
          String rankdownCommand = CommandsHandler.getLabel() + " shadowcmd " + shadowArgs;
          RawMessage rawMessage = new RawMessage();
          rawMessage
              .addText(CMILC.getIM(this, "confirm", "[rank]", cmiRank.getDisplayName()))
              .addHover("{gcs}" + cmiRank.getDisplayName())
              .addCommand(rankdownCommand);
          rawMessage.show(commandSender);
        }

        return true;
      } else {
        if (!list.contains(cmiRank)) {
          CMILC.info("rankinfo", commandSender, "noRank");
          return null;
        }

        currentRank.performCommandsOnRankDown(cmiUser);
        cmiRank.finalizeRankup(cmiUser, runCommand, withCost);
        cmiUser.setRank(cmiRank);
        cmiUser.recalculateRank();
        CMILC.info(this, commandSender, "downranked", "[rank]", cmiUser.getRank().getDisplayName());
        if (!cmiRank.getName().equals(cmiUser.getRank().getName())) {
          cmi.consoleMessage(
              "&4Failed to derank &2"
                  + cmiUser.getName()
                  + " &4into &2"
                  + cmiRank.getName()
                  + " &4rank, result was &2"
                  + cmiUser.getRank().getName()
                  + "&4. This can be caused by player having permission access to higher rank");
        }

        cmiUser.addForDelayedSave();
        return true;
      }
    } else {
      RawMessage listMessage = new RawMessage();
      listMessage.addText(CMILC.getIM(this, "list"));
      if (list.size() > 1) {
        for (CMIRank previousRank : list) {
          listMessage
              .addText("{gcs}" + previousRank.getDisplayName() + " ")
              .addHover("{gcs}" + previousRank.getDisplayName())
              .addCommand(
                  CommandsHandler.getLabel()
                      + " rankdown "
                      + cmiUser.getName()
                      + " "
                      + previousRank.getName());
        }

        listMessage.show(commandSender);
      } else {
        CMICommand.performCommand(
            commandSender,
            CommandsHandler.getLabel()
                + " rankdown "
                + cmiUser.getName()
                + " "
                + ((CMIRank) list.get(0)).getName(),
            CommandType.silent);
      }

      return true;
    }
  }
}
