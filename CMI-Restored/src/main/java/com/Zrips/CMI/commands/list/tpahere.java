package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMultiMessage;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpahere implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("requestSent", "{gcp}Request sent to {gcs}[playerDisplayName]");
    configReader.get("gotRequest", "{gcs}[senderDisplayName] {gcp}requests teleportation to them!");
    configReader.get("accept", "{gcu}Click to accept teleportation request!");
  }

  @CAnnotation(
      info = "Asks player to accept teleportation to your location",
      args = "[playerName] (playerName) (-c)",
      tab = {"allPlayerName", "playername", "-c"},
      explanation = {},
      regVar = {1, 2},
      consoleVar = {2, 3})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (string.equalsIgnoreCase("-cancel-")) {
        flag = true;
      }
    }

    Player player = null;
    if (strings.length > 1
        && !strings[1].equalsIgnoreCase("-c")
        && !strings[1].equalsIgnoreCase("-cancel-")) {
      player = cmi.getTarget(commandSender, strings[1], this);
      if (player == null) {
        return null;
      }
    } else if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    if (player == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Player targetPlayer = CMIUser.getOnlinePlayer(strings[0], true);
    boolean currentLoc = cmi.getTeleportManager().isTpahereCurrentLoc();
    if (strings.length > 1
        && strings[1].equalsIgnoreCase("-c")
        && PermissionsManager.CMIPerm.teleport_currentlocation.hasPermission(commandSender)) {
      currentLoc = !currentLoc;
    }

    if (targetPlayer != null && player.canSee(targetPlayer)) {
      Snd snd = new Snd().setSender(player).setTarget(targetPlayer);
      if (commandSender.getName().equals(targetPlayer.getName())) {
        Player targetCopy = targetPlayer;
        targetPlayer = player;
        player = targetCopy;
        snd = new Snd().setSender(player).setTarget(targetPlayer);
        if (commandSender.getName().equals(targetPlayer.getName())) {
          CMILC.info("tp", commandSender, "notAllowed", snd);
          return null;
        }
      }

      if (flag) {
        if (cmi.getTeleportManager().removeTpRequest(player, targetPlayer)) {
          CMILC.info("tpa", commandSender, "canceled");
        }

        return true;
      } else {
        if (cmi.getTeleportManager()
            .isAlreadyInRequest(player, targetPlayer, TeleportManager.TpAction.tpahere)) {
          CMILC.info("tpa", commandSender, "alreadyRequesting", targetPlayer);
          return null;
        }

        if (cmi.getTeleportManager()
            .isBlockedRequest(player, targetPlayer, TeleportManager.TpAction.tpahere)) {
          long longValue =
              cmi.getTeleportManager()
                  .getBlockTime(player, targetPlayer, TeleportManager.TpAction.tpahere);
          CMILC.info(
              "tpa",
              commandSender,
              "banned",
              new Snd().setSender(commandSender).setTarget(targetPlayer),
              "[time]",
              CMITimeManager.to24hourShort(longValue));
          return null;
        }

        CMIUser cmiUser = cmi.getPlayerManager().getUser(targetPlayer);
        if (cmiUser != null
            && !cmiUser.getOptionState(PlayerOption.acceptingTPA)
            && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
          CMILC.info("tp", commandSender, "notAllowed");
          return null;
        }

        if (cmiUser != null
            && cmiUser.isIgnoring(player.getUniqueId())
            && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
          CMILC.info("ignore", commandSender, "ignoringYou", snd);
          return null;
        }

        if (cmi.getTeleportManager().getTpaHereMaxDistance() > 0) {
          PermissionInfo permissionInfo =
              PermissionsManager.CMIPerm.command_tpahere_max_$1.getPermissionInfo(player, "");
          int maxDistance =
              permissionInfo.getMaxValue(cmi.getTeleportManager().getTpaHereMaxDistance());
          if (maxDistance > 0) {
            double number =
                CMILocation.getDistance(player.getLocation(), targetPlayer.getLocation());
            if (number > maxDistance) {
              CMILC.info_CantTeleportDistance.sendMessage(commandSender, "[amount]", maxDistance);
              return null;
            }
          }
        }

        if (!cmi.getTeleportManager()
            .addTpRequest(player, targetPlayer, TeleportManager.TpAction.tpahere, currentLoc)) {
          return null;
        }

        RawMessage rawMessage = new RawMessage();
        String senderFeedback = CMILC.getIM(this, "requestSent", snd);
        if (new CMIMultiMessage(senderFeedback).isMultiType()) {
          CMILC.sendMessage(commandSender, senderFeedback);
        } else {
          String requestFormat = CMILC.getIM("tpa", "senderFeedbackMessageFormat");

          for (int index = 0; index < 2; index++) {
            if (requestFormat.startsWith("[message]")) {
              rawMessage.addText(senderFeedback);
              requestFormat = requestFormat.substring("[message]".length());
            } else if (requestFormat.startsWith("[cancelButton]")) {
              rawMessage
                  .addText(LC.modify_cancelSymbol)
                  .addHover(LC.modify_cancelSymbolHover)
                  .addCommand("cmi tpahere " + targetPlayer.getName() + " -cancel-");
              requestFormat = requestFormat.substring("[cancelButton]".length());
            }
          }

          rawMessage.show(commandSender);
        }

        rawMessage = new RawMessage();
        rawMessage
            .addText(CMILC.getIM(this, "gotRequest", snd))
            .addHover(
                CMILC.getIM(
                    "tpa",
                    "time",
                    "[time]",
                    CMITimeManager.to24hourShort(
                        (long) (cmi.getTeleportManager().getTpaTime() * 1000))));
        if (cmi.getTeleportManager().isDenyConfirm()) {
          rawMessage
              .addText(LC.modify_acceptSymbol)
              .addHover(LC.modify_acceptSymbolHover)
              .addCommand("cmi tpaccept " + player.getName() + " tpahere");
          rawMessage
              .addText(LC.modify_denySymbol)
              .addHover(LC.modify_denySymbolHover)
              .addCommand("cmi tpdeny " + player.getName());
        }

        rawMessage.show(targetPlayer);
        return true;
      }
    } else {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
