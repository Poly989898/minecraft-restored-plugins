package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Map.Entry;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class votetop implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("topLine", "{gcp}----- TopVotes {gcp}-----");
    configReader.get("list", "{gcp}[place]. {gcs}[playerDisplayName]{gcp}: {gcs}[votes]");
    configReader.get("ownLine", "{gcu}[place]. {gcs}[playerDisplayName]{gcp}: {gcs}[votes]");
  }

  @CAnnotation(
      info = "Check top vote list",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!CMIHookType.Votifier.isEnabled()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
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

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    var concurrentSkipListMap = cmi.getVotifierManager().getTop();
    PageInfo pageInfo = new PageInfo(10, concurrentSkipListMap.size(), value);
    int entryIndex = 0;

    for (Entry entry : concurrentSkipListMap.entrySet()) {
      entryIndex++;
      if (((CMIUser) entry.getValue()).getName(false) != null
          && ((CMIUser) entry.getValue()).getName(false).equals(cmiUser.getName(false))) {
        break;
      }
    }

    CMILC.info(this, commandSender, "topLine");
    if (pageInfo.getStart() > entryIndex) {
      Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
      CMILC.info(
          this,
          commandSender,
          "ownLine",
          "[place]",
          entryIndex,
          snd,
          "[votes]",
          cmiUser.getVotifierVotes());
    }

    for (Entry voteEntry : concurrentSkipListMap.entrySet()) {
      if (pageInfo.isEntryOk()) {
        if (pageInfo.isBreak()) {
          break;
        }

        if (((CMIUser) voteEntry.getValue()).getName() != null) {
          Snd entrySnd =
              new Snd().setSender(commandSender).setTarget((CMIUser) voteEntry.getValue());
          if (((CMIUser) voteEntry.getValue()).getName(false).equals(cmiUser.getName(false))) {
            CMILC.info(
                this,
                commandSender,
                "ownLine",
                "[place]",
                pageInfo.getPositionForOutput(),
                entrySnd,
                "[votes]",
                ((CMIUser) voteEntry.getValue()).getVotifierVotes());
          } else {
            CMILC.info(
                this,
                commandSender,
                "list",
                "[place]",
                pageInfo.getPositionForOutput(),
                entrySnd,
                "[votes]",
                ((CMIUser) voteEntry.getValue()).getVotifierVotes());
          }
        }
      }
    }

    if (pageInfo.getEnd() + 1 < entryIndex) {
      Snd userSnd = new Snd().setSender(commandSender).setTarget(cmiUser);
      CMILC.info(
          this,
          commandSender,
          "ownLine",
          "[place]",
          entryIndex,
          userSnd,
          "[votes]",
          cmiUser.getVotifierVotes());
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + this.getClass().getSimpleName()
            + " "
            + cmiUser.getName(false));
    return true;
  }
}
