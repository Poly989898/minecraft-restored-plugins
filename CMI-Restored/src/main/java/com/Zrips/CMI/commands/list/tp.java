package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportHandler;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have been teleported to {gcp}[sourceDisplayName] {gcp}location.");
    configReader.get("bad.msg", "{gcw}Teleport location is unsafe. {gcs}[reason]");
    configReader.get(
        "bad.extraMsg", "{gcp}Use {gcs}/tpbypass {gcp}to bypass this. You have {gcs}[time]");
    configReader.get("bad.badfloor", "{gcw}Bad floor ({gcs}[material]{gcw})");
    configReader.get("bad.void", "{gcw}Void");
    configReader.get("bad.fall", "{gcw}Fall ({gcs}[distance]{gcw})");
    configReader.get("bad.suffocation", "{gcw}Suffocation");
    configReader.get("bad.unknown", "{gcw}Unknown");
    configReader.get("bad.plugin", "{gcw}3rd party plugin");
    configReader.get("notAllowed", "{gcw}Can't teleport to this player");
    configReader.get("prevented", "{gcw}Teleportation has been prevented by another plugin");
  }

  @CAnnotation(
      info = "Teleports to player's location",
      args = "[playerName] (playerName)",
      tab = {"allPlayerName", "playername"},
      regVar = {1, 2},
      consoleVar = 2,
      others = true,
      redirectClass = tpa.class)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String text = null;
    boolean flag = false;
    if (!cmi.getTeleportManager().isSwitchTeleportPlaces()) {
      String[] parts = strings;
      int value = strings.length;

      for (int count = 0; count < value; count++) {
        String textSecond = parts[count];
        if (textSecond.equalsIgnoreCase("-s")) {
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
            flag = true;
          }
        } else if (string == null) {
          string = textSecond;
        } else {
          text = textSecond;
        }
      }
    } else {
      String[] partsSecond = strings;
      int countSecond = strings.length;

      for (int countThird = 0; countThird < countSecond; countThird++) {
        String textThird = partsSecond[countThird];
        if (textThird.equalsIgnoreCase("-s")) {
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
            flag = true;
          }
        } else if (text == null) {
          text = textThird;
        } else {
          string = textThird;
        }
      }

      if (string == null) {
        string = text;
        text = null;
      }
    }

    CMILocation cmiLocation = CMILocation.fromString(string);
    if (cmiLocation != null && cmiLocation.isValid()) {
      cmi.getCommandManager()
          .onCommand(
              commandSender, null, null, CMIArray.addFirst(strings, tppos.class.getSimpleName()));
      return null;
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    Player playerSecond = cmi.getTarget(commandSender, text, this);
    if (player != null && playerSecond != null) {
      if (player.getName().equalsIgnoreCase(commandSender.getName())
          && playerSecond.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info(this, commandSender, "notAllowed");
        return null;
      }

      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null
          && !cmiUser.getOptionState(PlayerOption.acceptingTPA)
          && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
        CMILC.info(this, commandSender, "notAllowed");
        return null;
      }

      if (cmi.getUtilManager().haveBlackListedItems(playerSecond, TeleportManager.TpAction.tp)) {
        return null;
      }

      Location location = player.getLocation();
      if (cmi.getAnimationManager().isSitting(player)) {
        location = location.clone().add(0.0, 1.0, 0.0);
      }

      if (player.isOnline()
          && playerSecond.isOnline()
          && !playerSecond.canSee(player)
          && cmiUser != null
          && commandSender.getName().equals(playerSecond.getName())) {
        location = cmiUser.getLogOutLocation();
      }

      boolean flagSecond = flag;
      cmi.getTeleportHandler()
          .teleportPlayer(
              playerSecond,
              location,
              TeleportHandler.hasSafeTeleport(playerSecond),
              CMITeleportType.Tp)
          .thenAccept(
              cmiTeleportFeedback -> {
                if (cmiTeleportFeedback.isSuccess()) {
                  Snd snd = new Snd().setSender(commandSender).setTarget(player).setSource(player);
                  if (!flagSecond) {
                    CMILC.info(
                        this, playerSecond, "feedback", snd, cmiTeleportFeedback.getLocation());
                  }
                }
              });
      return true;
    } else {
      return null;
    }
  }
}
