package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.SpawnUtil;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spawn implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notset", "{gcw}Spawn point is not set!");
    configReader.get("feedback", "{gcp}You were teleported to spawn.");
    configReader.get("feedbackother", "{gcs}[playerDisplayName] {gcp}was teleported to spawn.");
  }

  @CAnnotation(
      info = "Teleports back to spawn location",
      args = "(playerName) (-s)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMILocation cmiLocation = SpawnUtil.getSpawnPoint(player);
    Location location = player.getLocation();
    if (cmiLocation == null) {
      CMILC.info(this, commandSender, "notset");
      return null;
    }

    if (cmi.getUtilManager().haveBlackListedItems(player, TeleportManager.TpAction.spawn)) {
      return null;
    }

    boolean silent = flag;
    cmi.getTeleportHandler()
        .teleportPlayer(player, cmiLocation, false, CMITeleportType.Spawn)
        .thenAccept(
            cmiTeleportFeedback -> {
              if (!cmiTeleportFeedback.isSuccess()) {
                cmiTeleportFeedback.showFeedbackMessage(commandSender, player);
              } else {
                Snd snd = new Snd();
                snd.setSender(commandSender);
                snd.setTarget(player);
                if (!silent) {
                  CMISoundPreset.TeleportSpawn.play(cmiLocation);
                  CMISoundPreset.TeleportSpawn.play(location);
                  if (commandSender.getName().equals(player.getName())) {
                    CMILC.info(this, commandSender, "feedback", snd);
                  } else {
                    CMILC.info(this, commandSender, "feedbackother", snd);
                  }
                }
              }
            });
    return true;
  }
}
