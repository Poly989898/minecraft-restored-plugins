package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ranklist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "list", "{gcs}[place]{gcp}. [currentRankMarker]{gcs}[rankName][currentRankMarker]");
    configReader.get("currentRankMarker", "{gcu}+");
    configReader.get("hoverNext", "{gcp}Next: \n{gcs}[rankList]");
    configReader.get("hoverLast", "{gcu}Last rank");
  }

  @CAnnotation(
      info = "List of possible ranks",
      regVar = {0, 1},
      consoleVar = {0, 1},
      modules = "ranks",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 1;
    if (strings.length > 0) {
      try {
        value = Integer.parseInt(strings[0]);
      } catch (Exception exception) {
      }
    }

    CMIRank cmiRank = null;
    if (commandSender instanceof Player) {
      cmiRank = cmi.getPlayerManager().getUser((Player) commandSender).getRank();
    }

    ArrayList<CMIRank> arrayList = new ArrayList();

    for (var entry : cmi.getRankManager().getRanks().entrySet()) {
      if (!cmi.getRankManager().isListSamePathOnly()
          || cmiRank == null
          || cmiRank.isOnSamePathWith((CMIRank) entry.getValue())
          || cmiRank == entry.getValue()) {
        arrayList.add((CMIRank) entry.getValue());
      }
    }

    PageInfo pageInfo =
        new PageInfo(
            commandSender instanceof Player ? 10 : arrayList.size(), arrayList.size(), value);
    Snd snd = new Snd();
    snd.setSender(commandSender);
    snd.setTarget(commandSender);
    snd.setSource(commandSender);

    for (CMIRank listedRank : arrayList) {
      if (!pageInfo.isContinue()) {
        if (pageInfo.isBreak()) {
          break;
        }

        String string = "";
        if (cmiRank != null && cmiRank.getName().equals(listedRank.getName())) {
          string = CMILC.getIM(this, "currentRankMarker");
        }

        String nextRanksText = "";

        for (CMIRank nextRank : listedRank.getNextRanks()) {
          if (!nextRanksText.isEmpty()) {
            nextRanksText = nextRanksText + " \n";
          }

          nextRanksText = nextRanksText + " " + nextRank.getDisplayName();
        }

        if (!nextRanksText.isEmpty()) {
          nextRanksText = CMILC.getIM(this, "hoverNext", "[rankList]", nextRanksText, snd);
        } else {
          nextRanksText = CMILC.getIM(this, "hoverLast", snd);
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[place]",
                    pageInfo.getPositionForOutput(),
                    "[currentRankMarker]",
                    string,
                    "[rankName]",
                    listedRank.getDisplayName(),
                    snd))
            .addHover(nextRanksText)
            .addCommand("cmi rankinfo " + listedRank.getName());
        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
    return true;
  }
}
