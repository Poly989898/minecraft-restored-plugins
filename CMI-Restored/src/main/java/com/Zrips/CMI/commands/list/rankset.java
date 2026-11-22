package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class rankset implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "set", "{gcp}Rank changed for {gcs}[playerDisplayName] {gcp}to {gcs}[rankName]");
  }

  @CAnnotation(
      priority = 110,
      info = "Set a players rank",
      args = "(playerName) [rankName] (-cmd) (-cost)",
      tab = {"playername", "rankname", "-cmd%%", "-cost%%"},
      explanation = {
        "-cmd will perform command defined for that rank rankup",
        "-cost will charge player with money, exp or items if defined for that rank rankup",
        "player will get rank even if he doesnt have enough money, exp or required items"
      },
      regVar = {1, 2, 3, 4},
      consoleVar = {2, 3, 4},
      modules = "ranks",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIRank cmiRank = null;
    String string = null;
    Boolean runCommand = false;
    Boolean withCost = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (cmiRank == null) {
        cmiRank = cmi.getRankManager().getRank(arg);
        if (cmiRank != null) {
          continue;
        }
      }

      if (arg.equalsIgnoreCase("-cmd")) {
        runCommand = true;
      } else if (arg.equalsIgnoreCase("-cost")) {
        withCost = true;
      } else {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (cmiRank == null) {
      CMILC.info("rankinfo", commandSender, "noRank");
      return null;
    }

    cmiUser.setRank(cmiRank);
    if (runCommand || withCost) {
      cmiRank.finalizeRankup(cmiUser, runCommand, withCost);
    }

    cmiUser.addForDelayedSave();
    cmiUser.recalculateRank();
    if (!cmiRank.getName().equals(cmiUser.getRank().getName())
        && cmi.getRankManager().isPermissionCheck()) {
      cmi.consoleMessage(
          "&4Failed to derank &2"
              + cmiUser.getName()
              + " &4into &2"
              + cmiRank.getName()
              + " &4rank, result was &2"
              + cmiUser.getRank().getName()
              + "&4. This can be caused by player having permission access to higher rank");
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    CMILC.info(this, commandSender, "set", "[rankName]", cmiRank.getDisplayName(), snd);
    return true;
  }
}
