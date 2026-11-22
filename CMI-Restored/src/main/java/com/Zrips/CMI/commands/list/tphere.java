package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tphere implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback.offline",
        "{gcp}You have set ({gcs}[world],[x],[y],[z]{gcp}) spawn location for"
            + " {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "feedback.online",
        "{gcp}You have teleported {gcs}[playerDisplayName] {gcp}to your location");
    configReader.get(
        "feedback.player", "{gcp}You got teleported to {gcs}[sourceDisplayName] {gcp}location");
    configReader.get("notAllowed", "{gcp}Can't teleport this player");
  }

  @CAnnotation(
      info = "Teleports player to your location",
      args = "[playerName] (playerName) (-s)",
      tab = {"allPlayerName", "playername", "-s"},
      regVar = {1, 2},
      consoleVar = 2,
      others = true,
      redirectClass = tpahere.class)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String targetName = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (string == null) {
        string = arg;
      } else {
        targetName = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    Player targetPlayer = cmi.getTarget(commandSender, targetName, this);
    if (player != null && targetPlayer != null) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      snd.setSource(targetPlayer);
      CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
      if (!cmiUser.getOptionState(PlayerOption.acceptingTPA)
          && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
        CMILC.info(this, commandSender, "notAllowed");
        return null;
      }

      Location location = targetPlayer.getLocation();
      if (cmi.getAnimationManager().isSitting(targetPlayer) || targetPlayer.getVehicle() != null) {
        location = location.clone().add(0.0, 1.0, 0.0);
      }

      boolean silent = flag;
      cmi.getTeleportHandler()
          .teleportPlayer(player, location, CMITeleportType.TpHere)
          .thenAccept(
              cmiTeleportFeedback -> {
                if (cmiTeleportFeedback.isSuccess() && !silent) {
                  if (player.isOnline()) {
                    CMILC.info(
                        this,
                        commandSender,
                        "feedback.online",
                        snd,
                        cmiTeleportFeedback.getLocation());
                    CMILC.info(this, player, "feedback.player", snd);
                  } else {
                    CMILC.info(
                        this,
                        commandSender,
                        "feedback.offline",
                        snd,
                        cmiTeleportFeedback.getLocation());
                  }
                }
              });
      return true;
    } else {
      return null;
    }
  }
}
