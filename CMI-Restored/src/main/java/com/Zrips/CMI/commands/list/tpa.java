package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
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

public class tpa implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("requestSent", "{gcp}Request sent to {gcs}[playerDisplayName]");
    configReader.get("gotRequest", "{gcs}[senderDisplayName] {gcp}requests teleportation to you!");
    configReader.get("time", "{gcp}You have {gcs}[time]{gcp}to accept this request");
    configReader.get("canceled", "{gcp}Teleportation request has been cancelled");
    configReader.addComment(
        "senderFeedbackMessageFormat",
        new String[] {
          "This line cant contain anything else then default variables ([message][cancelButton]),"
              + " you can switch them around to your liking tho"
        });
    configReader.get("senderFeedbackMessageFormat", "[message][cancelButton]");
    configReader.get(
        "alreadyRequesting",
        "{gcp}You are already requesting that {gcs}[playerDisplayName] {gcp}accepts your"
            + " teleportation request");
    configReader.get(
        "banned",
        "{gcp}You can't send a teleportation request to {gcs}[playerDisplayName] {gcp}for the next"
            + " {gcs}[time]");
  }

  @CAnnotation(
      info = "Ask the player if you can teleport to them",
      args = "[playerName] (playerName) (-c)",
      tab = {"allPlayerName", "playername", "-c"},
      regVar = {1, 2},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (string.equalsIgnoreCase("-cancel-") || string.equalsIgnoreCase("-c")) {
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

    Player target = CMIUser.getOnlinePlayer(strings[0], true);
    boolean tpaCurrentLoc = cmi.getTeleportManager().isTpaCurrentLoc();
    if (strings.length > 1
        && strings[1].equalsIgnoreCase("-c")
        && PermissionsManager.CMIPerm.teleport_currentlocation.hasPermission(commandSender)) {
      tpaCurrentLoc = !tpaCurrentLoc;
    }

    if (target != null && (!target.isOnline() || player.canSee(target))) {
      Snd snd = new Snd().setSender(player).setTarget(target);
      if (commandSender.getName().equals(target.getName())) {
        Player tempPlayer = target;
        target = player;
        player = tempPlayer;
        snd = new Snd().setSender(player).setTarget(target);
        if (commandSender.getName().equals(target.getName())) {
          CMILC.info("tp", commandSender, "notAllowed", snd);
          return null;
        }
      }

      if (flag) {
        if (cmi.getTeleportManager().removeTpRequest(player, target)) {
          CMILC.info("tpa", commandSender, "canceled");
        }

        return true;
      } else {
        if (cmi.getUtilManager().haveBlackListedItems(player, TeleportManager.TpAction.tpa)) {
          return null;
        }

        if (cmi.getTeleportManager()
            .isAlreadyInRequest(player, target, TeleportManager.TpAction.tpa)) {
          CMILC.info(this, commandSender, "alreadyRequesting", snd);
          return null;
        }

        if (cmi.getTeleportManager()
            .isBlockedRequest(player, target, TeleportManager.TpAction.tpa)) {
          long longValue =
              cmi.getTeleportManager().getBlockTime(player, target, TeleportManager.TpAction.tpa);
          CMILC.info(
              this,
              commandSender,
              "banned",
              snd,
              "[time]",
              CMITimeManager.to24hourShort(longValue));
          return null;
        }

        if (player.getName().equals(target.getName())) {
          CMILC.info("tp", commandSender, "notAllowed", snd);
          return null;
        }

        CMIUser cmiUser = cmi.getPlayerManager().getUser(target);
        if (cmiUser.isIgnoring(player.getUniqueId())
            && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
          CMILC.info("ignore", commandSender, "ignoringYou", snd);
          return null;
        }

        if (cmiUser.isSilenceMode()
            && !PermissionsManager.CMIPerm.command_silence_bypass.hasPermission(commandSender)) {
          CMILC.info("silence", commandSender, "inMode");
          return null;
        }

        if (!cmiUser.getOptionState(PlayerOption.acceptingTPA)
            && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
          CMILC.info("tp", commandSender, "notAllowed");
          return null;
        }

        if (cmi.getTeleportManager().getTpaMaxDistance() > 0) {
          PermissionInfo permissionInfo =
              PermissionsManager.CMIPerm.command_tpa_max_$1.getPermissionInfo(player, "");
          int maxDistance =
              permissionInfo.getMaxValue(cmi.getTeleportManager().getTpaMaxDistance());
          if (maxDistance > 0) {
            double number = CMILocation.getDistance(player.getLocation(), target.getLocation());
            if (number > maxDistance) {
              CMILC.info_CantTeleportDistance.sendMessage(commandSender, "[amount]", maxDistance);
              return null;
            }
          }
        }

        if (!cmi.getTeleportManager()
            .addTpRequest(player, target, TeleportManager.TpAction.tpa, tpaCurrentLoc)) {
          return null;
        }

        RawMessage rawMessage = new RawMessage();
        String requestMessage = CMILC.getIM(this, "requestSent", snd);
        requestMessage = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, requestMessage);
        if (new CMIMultiMessage(requestMessage).isMultiType()) {
          CMILC.sendMessage(commandSender, requestMessage);
        } else {
          String senderFeedbackFormat = CMILC.getIM(this, "senderFeedbackMessageFormat");

          for (int pass = 0; pass < 2; pass++) {
            if (senderFeedbackFormat.startsWith("[message]")) {
              rawMessage.addText(requestMessage);
              senderFeedbackFormat = senderFeedbackFormat.substring("[message]".length());
            } else if (senderFeedbackFormat.startsWith("[cancelButton]")) {
              rawMessage
                  .addText(LC.modify_cancelSymbol)
                  .addHover(LC.modify_cancelSymbolHover)
                  .addCommand("cmi tpa " + target.getName() + " -cancel-");
              senderFeedbackFormat = senderFeedbackFormat.substring("[cancelButton]".length());
            }
          }

          rawMessage.show(commandSender);
        }

        rawMessage = new RawMessage();
        rawMessage
            .addText(CMILC.getIM(this, "gotRequest", snd))
            .addHover(
                CMILC.getIM(
                    this,
                    "time",
                    "[time]",
                    CMITimeManager.to24hourShort(
                        (long) (cmi.getTeleportManager().getTpaTime() * 1000))));
        CMISoundPreset.TpaRequest.play(target);
        if (cmi.getTeleportManager().isDenyConfirm()) {
          rawMessage
              .addText(LC.modify_acceptSymbol)
              .addHover(LC.modify_acceptSymbolHover)
              .addCommand("cmi tpaccept " + player.getName() + " tpa");
          rawMessage
              .addText(LC.modify_denySymbol)
              .addHover(LC.modify_denySymbolHover)
              .addCommand("cmi tpdeny " + player.getName());
        }

        rawMessage.show(target);
        return true;
      }
    } else {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
