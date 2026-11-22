/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class voteedit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("currentVotes", "{gcs}[playerDisplayName] {gcp}has {gcs}[votes] {gcp}votes");
    configReader.get(
        "selfFeedback",
        "{gcp}Votes was set to {gcs}[votes] {gcp}for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your vote count was set to {gcs}[votes]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Manage players votes",
      args = "(playerName) [add/set/take/clear] [amount] (-s)",
      multiTab = {
        "[playername] set,add,clear,take,info,trigger 1",
        "set,add,clear,take,info,trigger [playername] 1"
      },
      explanation = {
        "Example:",
        "/voteedit Zrips",
        "/voteedit add 10",
        "/voteedit take 10",
        "/voteedit Zrips clear",
        "/voteedit Zrips add 10"
      },
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      alias = true,
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    boolean bl = false;
    Action action = Action.Info;
    int n = 0;
    String string = null;
    String[] args = stringArray;
    int argCount = stringArray.length;
    int argIndex = 0;
    while (argIndex < argCount) {
      String arg = args[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        case "set":
          {
            action = Action.Set;
            break;
          }
        case "take":
          {
            action = Action.Take;
            break;
          }
        case "trigger":
          {
            action = Action.Trigger;
            break;
          }
        default:
          {
            try {
              n = Integer.parseInt(arg);
              break;
            } catch (NumberFormatException numberFormatException) {
              string = arg;
            }
          }
      }
      ++argIndex;
    }
    int minAmount = 0;
    if (n < minAmount) {
      n = minAmount;
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", minAmount});
    }
    Player player = cMI.getTarget(commandSender, string, this);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (player == null) {
      return true;
    }
    Object object = cMI.getPlayerManager().getUser(player);
    if (stringArray.length == 0
        || stringArray.length == 1 && string != null
        || action.equals((Object) Action.Info)) {
      CMILC.info(
          (Object) this,
          commandSender,
          "currentVotes",
          snd,
          "[votes]",
          ((CMIUser) object).getVotifierVotes());
      return true;
    }
    switch (action) {
      case Add:
        {
          n = ((CMIUser) object).getVotifierVotes() + n;
          break;
        }
      case Take:
        {
          n = ((CMIUser) object).getVotifierVotes() - n;
          break;
        }
      case Clear:
        {
          n = 0;
          break;
        }
      case Set:
      case Info:
        {
          break;
        }
      case Trigger:
        {
          cMI.getVotifierManager().processVote((CMIUser) object, null);
          return true;
        }
    }
    if (n < 0) {
      n = 0;
    }
    ((CMIUser) object).setVotifierVotes(n);
    cMI.getVotifierManager().updateVoteCountList((CMIUser) object);
    cMI.getVotifierManager().updateTopList();
    if (!bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "selfFeedback",
          snd,
          "[votes]",
          ((CMIUser) object).getVotifierVotes());
    }
    ((CMIUser) object).addForDelayedSave();
    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info(
          (Object) this,
          (CommandSender) player,
          "targetFeedback",
          snd,
          "[votes]",
          ((CMIUser) object).getVotifierVotes());
    }
    return true;
  }

  private static enum Action {
    Set,
    Add,
    Clear,
    Take,
    Info,
    Trigger;
  }
}
