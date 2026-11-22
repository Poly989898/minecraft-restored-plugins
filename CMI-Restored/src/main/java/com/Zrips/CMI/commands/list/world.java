/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMINumber
 *  net.Zrips.CMILib.Container.CMIWorld
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class world implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("here", "{gcp}Already here");
  }

  @Override
  @CAnnotation(
      info = "Teleports to different world",
      args = "(normal/nether/end/1/2/3...) (playerName) (-s)",
      tab = {"normal%%nether%%end%%worlds"},
      explanation = {"Requires cmi.command.world.[worldName] in addition to base permission node"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    World.Environment environment = null;
    Integer index = null;
    World targetWorld = null;
    String targetName = null;
    boolean bl = false;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "normal":
          {
            environment = World.Environment.NORMAL;
            break;
          }
        case "nether":
          {
            environment = World.Environment.NETHER;
            break;
          }
        case "end":
          {
            environment = World.Environment.THE_END;
            break;
          }
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            try {
              index = Integer.parseInt(arg);
              index = index - 1;
              break;
            } catch (Exception exception) {
              World found = cMI.getUtilManager().getWorld(arg);
              if (targetWorld == null && found != null) {
                targetWorld = found;
                environment = targetWorld.getEnvironment();
                break;
              }
              targetName = arg;
            }
          }
      }
    }
    CMIUser user = cMI.getUser(commandSender, targetName, this);
    if (user == null) {
      return null;
    }
    if (environment == null && index == null) {
      Location location = user.getLocation();
      World currentWorld = location.getWorld();
      int position = 0;
      for (World world : Bukkit.getWorlds()) {
        if (world.equals(currentWorld)) break;
        ++position;
      }
      if (++position > Bukkit.getWorlds().size() - 1) {
        position = 0;
      }
      targetWorld = Bukkit.getWorlds().get(position);
    } else if (environment == null && index != null) {
      if (index < 0) {
        index = 0;
      }
      if (index > Bukkit.getWorlds().size() - 1) {
        return false;
      }
      targetWorld = Bukkit.getWorlds().get(index);
    } else if (environment != null && index == null && targetWorld == null) {
      Location location = user.getLocation();
      World currentWorld = location.getWorld();
      for (World world : Bukkit.getWorlds()) {
        if (!world.getEnvironment().equals(environment)
            || targetWorld != null && targetWorld == currentWorld) continue;
        targetWorld = world;
      }
    }
    if (targetWorld == null) {
      return false;
    }
    World fromWorld = user.getWorld();
    if (targetWorld.equals(fromWorld)) {
      if (!bl) {
        CMILC.info((Object) this, commandSender, "here", new Object[0]);
      }
      return null;
    }
    if (!PermissionsManager.CMIPerm.command_world_$1.hasPermission(
            commandSender, true, targetWorld.getName().toLowerCase())
        && !PermissionsManager.CMIPerm.command_world_$1.hasPermission(commandSender, true, "*")) {
      return null;
    }
    double horizontalScale;
    double verticalScale = 1.0;
    if (fromWorld.getEnvironment().equals(World.Environment.NETHER)
        && (targetWorld.getEnvironment().equals(World.Environment.NORMAL)
            || targetWorld.getEnvironment().equals(World.Environment.THE_END))) {
      horizontalScale = 8.0;
      verticalScale = 2.0;
    } else if ((fromWorld.getEnvironment().equals(World.Environment.NORMAL)
            || targetWorld.getEnvironment().equals(World.Environment.THE_END))
        && targetWorld.getEnvironment().equals(World.Environment.NETHER)) {
      horizontalScale = 0.125;
      verticalScale = 0.5;
    } else {
      horizontalScale = 1.0;
    }
    Location from = user.getLocation().clone();
    from.setY(from.getY() <= 0.0 ? 3.0 : from.getY());
    double y = (double) from.getBlockY() * verticalScale;
    y =
        CMINumber.clamp(
            (double) y,
            (double) (CMIWorld.getMinHeight(targetWorld) + 3),
            (int) CMIWorld.getMaxHeight(targetWorld));
    Location destination =
        new Location(
            targetWorld,
            (double) from.getBlockX() * horizontalScale + 0.5,
            y,
            (double) from.getBlockZ() * horizontalScale + 0.5);
    if (!CMIWorld.insideWorldBorder(destination)) {
      CMILC.info_outsideWorldBorder.sendMessage(commandSender, new Object[0]);
      return null;
    }
    final boolean silent = bl;
    cMI.getTeleportHandler()
        .teleportPlayer(user.getPlayer(), destination, CMITeleportType.World)
        .thenAccept(
            feedback -> {
              if (!feedback.isSuccess()) {
                feedback.showFeedbackMessage(commandSender, user.getPlayer());
                return;
              }
              if (!silent) {
                LC.info_teleported.sendMessage((Object) user.getPlayer(), new Object[0]);
              }
            });
    return true;
  }
}
