package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class compass implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("wrongworld", "{gcw}Your defined world don't exist!");
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[playerDisplayName] {gcp}compass arrow to {gcs}[sourceDisplayName]"
            + " {gcp}location.");
    configReader.get(
        "feedbackCustom",
        "{gcp}You have set {gcs}[playerDisplayName] {gcp}compass arrow to {gcs}[x] [z] [world]"
            + " {gcp}location.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your compass target was set to {gcs}[sourceDisplayName] {gcp}location by"
            + " {gcs}[senderName]{gcp}.");
    configReader.get(
        "targetFeedbackCustom",
        "{gcp}Your compass target was set to {gcs}[x] [z] [world] {gcp}location by"
            + " {gcs}[sourceDisplayName]{gcp}.");
    configReader.get(
        "targetPlayerFeedback",
        "{gcs}[playerDisplayName] {gcp}set their compass towards your location");
    configReader.get("reset", "{gcp}Compass target got reseted");
  }

  @CAnnotation(
      info = "Set players compass point to your location",
      args = "(targetName) (sourceName) (x) (z) (worldname) (reset) (-s)",
      tab = {"playername", "playername"},
      explanation = {
        "Example: /cmi compass Zhax",
        "/cmi compass Zhax Zrips",
        "/cmi compass Zhax LT_Craft 0 0 -s",
        "/cmi compass reset Zhax"
      },
      regVar = {1, 2, 3, 4, 5, 6},
      consoleVar = {2, 3, 4, 5, 6},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    String targetName = null;
    World world = null;
    Double distance = null;
    Double maxDistance = 64.0;
    Double yOffset = null;
    boolean showDistance = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("reset")) {
        showDistance = true;
      } else {
        String mode;
        switch ((mode = arg.toLowerCase()).hashCode()) {
          case 1510:
            if (mode.equals("-s")) {
              if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
                flag = true;
              }
              continue;
            }
        }

        if (distance == null) {
          try {
            distance = Double.parseDouble(arg);
            continue;
          } catch (NumberFormatException numberFormatException) {
          }
        }

        if (yOffset == null) {
          try {
            yOffset = Double.parseDouble(arg);
            continue;
          } catch (NumberFormatException numberFormatException2) {
          }
        }

        if (world == null) {
          world = cmi.getUtilManager().getWorld(arg);
          if (world != null) {
            continue;
          }
        }

        if (string == null) {
          string = arg;
        } else if (targetName == null && world == null && !showDistance) {
          targetName = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    Player targetPlayer = cmi.getTarget(commandSender, targetName, this);
    if (targetPlayer == null && world == null && distance == null && yOffset == null) {
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    snd.setSource(targetPlayer);
    Location location = null;
    boolean hasCustomLocation = false;
    if (world != null && distance != null && yOffset != null) {
      location = new Location(world, distance, maxDistance, yOffset);
      hasCustomLocation = true;
    } else if (targetPlayer != null) {
      location = targetPlayer.getLocation();
    }

    if (location == null) {
      return false;
    }

    if (showDistance) {
      location = location.getWorld().getSpawnLocation();
      hasCustomLocation = true;
    }

    player.setCompassTarget(location);
    cmi.save(player);
    if (!flag) {
      if (!hasCustomLocation) {
        CMILC.info(this, commandSender, "feedback", snd);
      } else {
        CMILC.info(this, commandSender, "feedbackCustom", snd, location);
      }

      if (targetPlayer != null && targetPlayer.isOnline()) {
        CMILC.info(this, targetPlayer, "targetPlayerFeedback", snd, location);
      }
    }

    if (player.getName().equalsIgnoreCase(commandSender.getName())) {
      return true;
    }

    if (!flag) {
      if (!hasCustomLocation) {
        CMILC.info(this, player, "targetFeedback", snd);
      } else {
        CMILC.info(this, player, "targetFeedbackCustom", snd, location);
      }
    }

    return true;
  }
}
