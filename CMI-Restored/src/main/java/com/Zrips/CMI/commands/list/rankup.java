package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.Modules.Ranks.RankManager;
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

public class rankup implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noRank", " {gcp}You don't have any rank associated with you");
    configReader.get(
        "requiresPrevious",
        " {gcp}You cant rankup to this rank. It requires you to have {gcs}[rank] {gcp}rank(`s)");
    configReader.get("list", "{gcp}Pick one rank you want to rank up to: ");
    configReader.get("confirm", "{gcp}Click me to confirm rankup to {gcs}[ranks]");
  }

  @CAnnotation(
      priority = 110,
      info = "Increase your rank",
      args = "(playerName) (rankName) (confirm)",
      tab = "rankname",
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      modules = "ranks",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    CMIRank cmiRank = null;
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("confirm")) {
        flag = true;
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
      CMILC.info(this, commandSender, "noRank");
      return null;
    }

    var list = currentRank.getNextValidRankUps(cmiUser);
    if (list.isEmpty()) {
      if (currentRank.getNextRanks().isEmpty()) {
        CMICommand.performCommand(
            commandSender, "cmi rankinfo " + cmiUser.getName(), CommandType.rank);
      } else {
        CMICommand.performCommand(
            commandSender, "cmi rankinfo " + cmiUser.getName() + " -next", CommandType.rank);
      }

      return null;
    } else if (cmiRank == null) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM(this, "list"));
      if (list.size() > 1) {
        for (CMIRank nextRank : list) {
          rawMessage
              .addText("{gcs}" + nextRank.getDisplayName() + " ")
              .addHover("{gcs}" + nextRank.getDisplayName())
              .addCommand("cmi rankup " + cmiUser.getName() + " " + nextRank.getName());
        }

        rawMessage.show(commandSender);
      } else {
        CMICommand.performCommand(
            commandSender,
            "cmi rankup "
                + cmiUser.getName()
                + " "
                + ((CMIRank) list.get(0)).getName()
                + (flag ? " confirm" : ""),
            CommandType.silent);
      }

      return true;
    } else {
      if (cmiRank.canRankup(cmiUser) != RankManager.rankupFailType.None) {
        CMICommand.performCommand(
            commandSender, "cmi rankinfo " + cmiRank.getName(), CommandType.rank);
        return null;
      }

      Boolean alreadyRanked = false;

      for (CMIRank candidateRank : list) {
        if (candidateRank.getName().equals(cmiRank.getName())) {
          alreadyRanked = true;
          break;
        }
      }

      if (!alreadyRanked) {
        CMILC.info(
            this,
            commandSender,
            "requiresPrevious",
            "[rank]",
            cmiRank.getValidPreviousRanksAsString());
        return null;
      }

      if (cmiRank.isRankUpConfirmation() && !flag) {
        if (cmiUser.isOnline()) {
          String shadowArgs =
              ShadowCommand.addShadowCmd(
                  cmiUser.getPlayer(),
                  CommandsHandler.getLabel()
                      + " "
                      + rankup.class.getSimpleName()
                      + " "
                      + cmiUser.getName()
                      + " "
                      + cmiRank.getName()
                      + " confirm",
                  false,
                  ShadowCommandType.Player);
          String rankupCommand = CommandsHandler.getLabel() + " shadowcmd " + shadowArgs;
          RawMessage listMessage = new RawMessage();
          listMessage
              .addText(
                  CMILC.getIM(
                      this,
                      "confirm",
                      "[ranks]",
                      cmiRank.getDisplayName(),
                      "[rank]",
                      cmiRank.getDisplayName()))
              .addHover("{gcs}" + cmiRank.getDisplayName())
              .addCommand(rankupCommand);
          if (cmiUser.isOnline()) {
            listMessage.show(cmiUser.getPlayer());
          }
        }

        return true;
      } else if ((!cmiRank.isRankUpConfirmation() || !flag) && cmiRank.isRankUpConfirmation()) {
        if (!list.contains(cmiRank)) {
          CMILC.info("rankinfo", commandSender, "noRank");
          return null;
        } else {
          cmiUser.setRank(cmiRank);
          cmiRank.finalizeRankup(cmiUser);
          return true;
        }
      } else {
        cmiUser.setRank(cmiRank);
        cmiRank.finalizeRankup(cmiUser);
        return true;
      }
    }
  }
}
