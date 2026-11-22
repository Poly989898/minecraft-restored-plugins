package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.Modules.Teleportations.TpInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpaccept implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noRequest", "{gcp}There is no teleport requests");
    configReader.get("NotOnline", "{gcp}Player is no longer online");
    configReader.get("accepted", "{gcs}[playerDisplayName] {gcp}accepted request");
    configReader.get("youAccepted", "{gcp}Teleportation accepted");
  }

  @CAnnotation(
      info = "Accept teleport request",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    Player playerSecond = null;
    TeleportManager.TpAction tpAction = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String string = parts[count];
      if (playerSecond == null) {
        playerSecond = CMIUser.getOnlinePlayer(string, true);
        if (playerSecond != null) {
          continue;
        }
      }

      if (tpAction == null) {
        tpAction = TeleportManager.TpAction.get(string);
      }
    }

    TpInfo tpInfo = cmi.getTeleportManager().getTeleportInfo(player, playerSecond, tpAction);
    if (tpInfo == null) {
      CMILC.info(this, commandSender, "noRequest");
      return null;
    }

    if (!tpInfo.getWhoOffers().isOnline()) {
      CMILC.info(this, commandSender, "NotOnline");
      return null;
    }

    switch (tpInfo.getAction()) {
      case tpa:
        Player playerThird = tpInfo.getWhoOffers();
        if (cmi.getUtilManager().haveBlackListedItems(playerThird, tpInfo.getAction())) {
          return null;
        } else {
          Location location = player.getLocation();
          if (tpInfo.getLoc() != null) {
            location = tpInfo.getLoc();
          }

          if (cmi.getAnimationManager().isSitting(player)
              || tpInfo.getWhoOffers().getVehicle() != null) {
            location = location.clone().add(0.0, 1.0, 0.0);
          }

          Snd snd = new Snd().setSender(playerThird).setTarget(player);
          CMILC.info(this, player, "youAccepted", snd);
          CMILC.info(this, tpInfo.getWhoOffers(), "accepted", snd);
          if (cmi.getTeleportManager().getTpaMaxDistance() > 0) {
            PermissionInfo permissionInfo =
                PermissionsManager.CMIPerm.command_tpa_max_$1.getPermissionInfo(
                    tpInfo.getWhoOffers());
            int countSecond =
                permissionInfo.getMaxValue(cmi.getTeleportManager().getTpaMaxDistance());
            if (countSecond > 0) {
              double number =
                  CMILocation.getDistance(
                      tpInfo.getWhoOffers().getLocation(), player.getLocation());
              if (number > countSecond) {
                CMILC.info_CantTeleportDistance.sendMessage(commandSender, "[amount]", countSecond);
                return null;
              }
            }
          }

          cmi.getWarmUpManager()
              .startTeleportUsage(tpInfo.getAction(), tpInfo.getWhoOffers(), location);
          return true;
        }
      case tpahere:
        if (cmi.getUtilManager().haveBlackListedItems(player, tpInfo.getAction())) {
          return null;
        } else {
          Location locationSecond = tpInfo.getWhoOffers().getLocation();
          if (tpInfo.getLoc() != null) {
            locationSecond = tpInfo.getLoc();
          }

          if (cmi.getAnimationManager().isSitting(tpInfo.getWhoOffers())
              || tpInfo.getWhoOffers().getVehicle() != null) {
            locationSecond = locationSecond.clone().add(0.0, 1.0, 0.0);
          }

          if (cmi.getTeleportManager().getTpaHereMaxDistance() > 0) {
            PermissionInfo permissionInfoValue =
                PermissionsManager.CMIPerm.command_tpahere_max_$1.getPermissionInfo(
                    tpInfo.getWhoOffers());
            int countThird =
                permissionInfoValue.getMaxValue(cmi.getTeleportManager().getTpaHereMaxDistance());
            if (countThird > 0) {
              double amount =
                  CMILocation.getDistance(
                      tpInfo.getWhoOffers().getLocation(), player.getLocation());
              if (amount > countThird) {
                CMILC.info_CantTeleportDistance.sendMessage(commandSender, "[amount]", countThird);
                return null;
              }
            }
          }

          Snd sndSecond = new Snd().setSender(tpInfo.getWhoOffers()).setTarget(player);
          CMILC.info(this, player, "youAccepted", sndSecond);
          CMILC.info(this, tpInfo.getWhoOffers(), "accepted", sndSecond);
          cmi.getWarmUpManager().startTeleportUsage(tpInfo.getAction(), player, locationSecond);
          return true;
        }
      case tpaall:
        Location locationThird = tpInfo.getWhoOffers().getLocation();
        if (tpInfo.getLoc() != null) {
          locationThird = tpInfo.getLoc();
        }

        if (cmi.getAnimationManager().isSitting(tpInfo.getWhoOffers())
            || tpInfo.getWhoOffers().getVehicle() != null) {
          locationThird = locationThird.clone().add(0.0, 1.0, 0.0);
        }

        cmi.getTeleportHandler().teleportPlayer(player, locationThird, CMITeleportType.TpAll);
        return null;
      default:
        Snd sndThird = new Snd().setSender(commandSender).setTarget(player);
        CMILC.info(this, player, "youAccepted");
        CMILC.info(this, tpInfo.getWhoOffers(), "accepted", sndThird);
        return true;
    }
  }
}
