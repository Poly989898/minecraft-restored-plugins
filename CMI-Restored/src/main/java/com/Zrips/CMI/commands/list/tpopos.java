package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Random;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpopos implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have been teleported ({gcs}[world],[x],[y],[z]{gcp}).");
    configReader.get(
        "feedbackOther",
        "{gcs}[playerDisplayName] {gcp}have been teleported ({gcs}[world],[x],[y],[z]{gcp}).");
  }

  @CAnnotation(
      info = "Teleports to location by force",
      args = "(-p:playerName) [x] [y] [z] (world) (pitch) (yaw) (-rng:[range])",
      tab = {"currentX", "currentY", "currentZ", "worlds", "currentPitch", "currentYaw", "%%-rng:"},
      regVar = -100,
      consoleVar = {-1, -100},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Double x = null;
    boolean flag = false;
    Double y = null;
    boolean hasX = false;
    Double z = null;
    boolean hasY = false;
    World world = null;
    Float yaw = null;
    Float pitch = null;
    Integer integer = null;
    Integer xRange = null;
    Integer rangeFactor = null;
    boolean hasZ = false;
    String[] argsCopy = strings;
    int value = strings.length;

    label173:
    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-p:")) {
        string = arg.substring("-p:".length());
      } else {
        try {
          if (x == null) {
            CMILocation cmiLocation = CMILocation.fromString(arg);
            if (cmiLocation != null && cmiLocation.isValid()) {
              x = cmiLocation.getX();
              y = cmiLocation.getY();
              z = cmiLocation.getZ();
              world = cmiLocation.getWorld();
              yaw = cmiLocation.getPitch();
              pitch = cmiLocation.getYaw();
              continue;
            }

            if (arg.startsWith("~")) {
              x = Double.parseDouble(arg.substring(1));
              flag = true;
            } else {
              x = Double.parseDouble(arg);
            }
            continue;
          }

          if (y == null) {
            if (arg.startsWith("~")) {
              y = Double.parseDouble(arg.substring(1));
              hasX = true;
            } else {
              y = Double.parseDouble(arg);
            }
            continue;
          }

          if (z == null) {
            if (arg.startsWith("~")) {
              z = Double.parseDouble(arg.substring(1));
              hasY = true;
            } else {
              z = Double.parseDouble(arg);
            }
            continue;
          }

          if (yaw == null) {
            yaw = Float.parseFloat(arg);
            continue;
          }

          if (pitch == null) {
            pitch = Float.parseFloat(arg);
            continue;
          }

          if (integer == null) {
            integer = Integer.parseInt(arg);
            continue;
          }

          if (xRange == null) {
            xRange = Integer.parseInt(arg);
            continue;
          }
        } catch (Exception exception) {
        }

        if (world == null) {
          for (World candidateWorld : Bukkit.getWorlds()) {
            if (candidateWorld.getName().equalsIgnoreCase(arg)) {
              world = candidateWorld;
              continue label173;
            }
          }
        }

        if (arg.startsWith("-rng:")) {
          try {
            rangeFactor = Integer.parseInt(arg.substring("-rng:".length(), arg.length()));
            continue;
          } catch (Exception exception2) {
          }
        }

        if (arg.equalsIgnoreCase("-s")) {
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
            hasZ = true;
          }
        } else if (!arg.equalsIgnoreCase("true") && !arg.equalsIgnoreCase("false")) {
          string = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (world == null) {
      world = player.getWorld();
    }

    if (world == null) {
      LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Location location = null;
    if (x != null && y != null && z != null) {
      location =
          new Location(
              world,
              flag ? player.getLocation().getX() + x : x,
              hasX ? player.getLocation().getY() + y : y,
              hasY ? player.getLocation().getZ() + z : z);
    }

    if (location == null) {
      commandSender.sendMessage(cmi.getLM().getDefaultMessage("info.IncorrectLocation"));
      return null;
    }

    if (rangeFactor != null && rangeFactor > 0) {
      Random random = new Random(System.currentTimeMillis());
      double number = (random.nextInt(rangeFactor * 10 * 2) - rangeFactor * 10) / 10.0;
      double randomOffset = (random.nextInt(rangeFactor * 10 * 2) - rangeFactor * 10) / 10.0;
      location.add(number, 0.0, randomOffset);
    }

    if (yaw != null) {
      location.setPitch(yaw);
    } else {
      location.setPitch(player.getLocation().getPitch());
    }

    if (pitch != null) {
      location.setYaw(pitch);
    } else {
      location.setYaw(player.getLocation().getYaw());
    }

    cmi.getTeleportHandler()
        .showParticleEffect(player, CMITeleportType.TpoPos, player.getLocation(), location);
    cmi.getNMS().forceTeleport(player, location);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (!hasZ) {
      if (commandSender.getName().equals(player.getName())) {
        CMIActionBar.send(commandSender, CMILC.getIM(this, "feedback", location, snd));
      } else {
        CMIActionBar.send(commandSender, CMILC.getIM(this, "feedbackOther", location, snd));
      }
    }

    return true;
  }
}
