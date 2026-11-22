package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class baltop implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("topLine", "{gcp}&m-----{gcs} Balancetop {gcp}&m-----");
    configReader.get("topLineHover", "{gcp}Server Total: {gcn}[total]");
    configReader.get("list", "{gcp}[place]{gcp}. {gcs}[playerDisplayName]: {gcn}[balance]");
    configReader.get("ownLine", "{gcu}[place]{gcp}. {gcs}[playerDisplayName]: {gcn}[balance]");
    configReader.addComment(
        "hover", new String[] {"Set to empty field if you want to not show hover over message"});
    configReader.get("hover", "{gcs}[playerName]");
    configReader.get(
        "recalculating",
        "{gcs}Baltop is being recalculated, please wait couple seconds and try again");
  }

  @CAnnotation(
      priority = 48,
      info = "Check top money list",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!cmi.getEconomyManager().isEnabled()) {
      CMILC.econ_disabled.sendMessage(commandSender);
      return true;
    }

    int value = 1;
    String string = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];

      try {
        value = Integer.parseInt(arg);
      } catch (Exception exception) {
        string = arg;
      }
    }

    CMIUser cmiUser =
        cmi.getUser(commandSender, string, this, commandSender instanceof Player, true);
    if (cmiUser == null && commandSender instanceof Player) {
      return null;
    }

    PageInfo pageInfo =
        new PageInfo(10, cmi.getEconomyManager().getLeaderboard().getRecordCount(), value);
    var list =
        cmi.getEconomyManager().getLeaderboard().getRange(pageInfo.getStart(), pageInfo.getEnd());
    if (cmi.getEconomyManager().isRecalculatingTop() != null && !list.isEmpty()) {
      int userRank = 0;
      if (cmiUser != null) {
        userRank = cmi.getEconomyManager().getLeaderboard().getRank(cmiUser.getUniqueId());
      }

      RawMessage rawMessage = new RawMessage();
      rawMessage
          .addText(CMILC.getIM(this, "topLine"))
          .addHover(
              CMILC.getIM(
                  this,
                  "topLineHover",
                  "[total]",
                  cmi.getEconomyManager().format(cmi.getEconomyManager().getTotalServerMoney())));
      if (cmiUser != null && pageInfo.getStart() > userRank) {
        Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
        rawMessage.addText(
            "\n"
                + CMILC.getIM(
                    this,
                    "ownLine",
                    "[place]",
                    userRank,
                    snd,
                    "[balance]",
                    cmiUser.getFormatedBalance(
                        cmi.getEconomyManager().isBalTopDisplayWithShorts())));
      }

      String hoverText = CMILC.getIM(this, "Hover");
      int entryIndex = 0;

      for (Entry entry : list) {
        entryIndex++;
        CMIUser entryUser = CMIUser.getUser((UUID) entry.getKey());
        if (entryUser.getName(false) != null) {
          Snd entrySnd = new Snd().setSender(commandSender).setTarget(entryUser);
          if (cmiUser != null && entryUser.getName(false).equals(cmiUser.getName(false))) {
            rawMessage.addText(
                "\n"
                    + CMILC.getIM(
                        this,
                        "ownLine",
                        "[place]",
                        pageInfo.getStart() + entryIndex,
                        entrySnd,
                        "[balance]",
                        entryUser.getFormatedBalance(
                            cmi.getEconomyManager().isBalTopDisplayWithShorts())));
          } else {
            rawMessage.addText(
                "\n"
                    + CMILC.getIM(
                        this,
                        "list",
                        "[place]",
                        pageInfo.getStart() + entryIndex,
                        entrySnd,
                        "[balance]",
                        entryUser.getFormatedBalance(
                            cmi.getEconomyManager().isBalTopDisplayWithShorts())));
          }

          if (!hoverText.isEmpty()) {
            rawMessage.addHover(CMILC.getIM(this, "hover", entrySnd));
          }
        }
      }

      if (cmiUser != null && pageInfo.getEnd() + 1 < userRank) {
        Snd userSnd = new Snd().setSender(commandSender).setTarget(cmiUser);
        rawMessage.addText(
            "\n"
                + CMILC.getIM(
                    this,
                    "ownLine",
                    "[place]",
                    userRank,
                    userSnd,
                    "[balance]",
                    cmiUser.getFormatedBalance(
                        cmi.getEconomyManager().isBalTopDisplayWithShorts())));
      }

      rawMessage.show(commandSender);
      if (cmiUser != null) {
        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel()
                + " "
                + baltop.class.getSimpleName()
                + " "
                + cmiUser.getName(false));
      }

      return true;
    } else {
      CMILC.info(this, commandSender, "recalculating");
      cmi.getEconomyManager().getLeaderboard().rebuild();
      return null;
    }
  }
}
