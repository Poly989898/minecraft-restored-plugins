package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Random;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tppos implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have been teleported ({gcs}[world],[x],[y],[z]{gcp}).");
    configReader.get(
        "feedbackOther",
        "{gcs}[playerDisplayName] {gcp}have been teleported ({gcs}[world],[x],[y],[z]{gcp}).");
  }

  @CAnnotation(
      info = "Teleports to location",
      args = "(-p:playerName) [x] [y] [z] (world) (pitch) (yaw) (-rng:[range])",
      multiTab = "[currentX] [currentY] [currentZ] [worlds] [currentPitch] [currentYaw] -rng:",
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
    boolean hasZ = false;
    Float pitch = null;
    boolean hasYaw = false;
    Integer integer = null;
    boolean hasPitch = false;
    String[] argsCopy = strings;
    int value = strings.length;

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
              if (arg.length() == 1) {
                x = 0.0;
              } else {
                x = Double.parseDouble(arg.substring(1));
              }

              flag = true;
            } else {
              x = Double.parseDouble(arg);
              if (!arg.contains(".") && !arg.contains(",")) {
                x = x + 0.5;
              }
            }
            continue;
          }

          if (y == null) {
            if (arg.startsWith("~")) {
              if (arg.length() == 1) {
                y = 0.0;
              } else {
                y = Double.parseDouble(arg.substring(1));
              }

              hasX = true;
            } else {
              y = Double.parseDouble(arg);
            }
            continue;
          }

          if (z == null) {
            if (arg.startsWith("~")) {
              if (arg.length() == 1) {
                z = 0.0;
              } else {
                z = Double.parseDouble(arg.substring(1));
              }

              hasY = true;
            } else {
              z = Double.parseDouble(arg);
              if (!arg.contains(".") && !arg.contains(",")) {
                z = z + 0.5;
              }
            }
            continue;
          }

          if (world == null) {
            world = CMIWorld.getWorld(arg);
            if (world != null) {
              continue;
            }
          }

          if (yaw == null) {
            if (arg.startsWith("~")) {
              if (arg.length() == 1) {
                yaw = 0.0F;
              } else {
                yaw = Float.parseFloat(arg.substring(1));
              }

              hasZ = true;
            } else {
              yaw = Float.parseFloat(arg);
            }
            continue;
          }

          if (pitch == null) {
            if (arg.startsWith("~")) {
              if (arg.length() == 1) {
                pitch = 0.0F;
              } else {
                pitch = Float.parseFloat(arg.substring(1));
              }

              hasYaw = true;
            } else {
              pitch = Float.parseFloat(arg);
            }
            continue;
          }
        } catch (Exception exception) {
        }

        if (world == null) {
          world = CMIWorld.getWorld(arg);
          if (world != null) {
            continue;
          }
        }

        if (arg.startsWith("-rng:")) {
          try {
            integer = Integer.parseInt(arg.substring("-rng:".length(), arg.length()));
            continue;
          } catch (Exception exception2) {
          }
        }

        if (arg.equalsIgnoreCase("-s")) {
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
            hasPitch = true;
          }
        } else if (!arg.equalsIgnoreCase("true") && !arg.equalsIgnoreCase("false")) {
          string = arg;
        }
      }
    }

    boolean pitchFlag = hasPitch;
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
      LC.info_IncorrectLocation.sendMessage(commandSender, new Object[0]);
      return null;
    }

    label194:
    if (integer != null && integer > 0) {
      Random random = new Random(System.currentTimeMillis());
      int attempts = 0;

      Location targetLocation;
      while (true) {
        if (attempts >= 10) {
          break label194;
        }

        targetLocation = location.clone();
        double number = (random.nextInt(integer * 10 * 2) - integer * 10) / 10.0;
        double randomOffset = (random.nextInt(integer * 10 * 2) - integer * 10) / 10.0;
        targetLocation.add(number, 0.0, randomOffset);

        try {
          var collection =
              targetLocation.getWorld().getNearbyEntities(targetLocation, 2.0, 2.0, 2.0);
          if (collection.isEmpty()) {
            break;
          }
        } catch (Throwable throwable) {
          location = targetLocation.clone();
          break label194;
        }

        attempts++;
      }

      location = targetLocation.clone();
    }

    if (yaw != null) {
      if (hasZ) {
        yaw = player.getLocation().getPitch() + yaw;
      }

      location.setPitch(yaw);
    } else {
      location.setPitch(player.getLocation().getPitch());
    }

    if (pitch != null) {
      if (hasYaw) {
        pitch = player.getLocation().getYaw() + pitch;
      }

      location.setYaw(pitch);
    } else {
      location.setYaw(player.getLocation().getYaw());
    }

    int worldLimit = 30000000;
    if (!(location.getX() > worldLimit)
        && !(location.getX() < -worldLimit)
        && !(location.getZ() > worldLimit)
        && !(location.getZ() < -worldLimit)) {
      if (location.getY() > CMIWorld.getMaxHeight(location.getWorld())) {
        location.setY(CMIWorld.getMaxHeight(location.getWorld()));
      }

      var completableFuture =
          cmi.getTeleportHandler().teleportPlayer(player, location, CMITeleportType.TpPos);
      completableFuture.thenAccept(
          object -> {
            if (!object.isSuccess()) {
              if (!pitchFlag) {
                object.showFeedbackMessage(commandSender, player);
              }
            } else {
              Snd snd = new Snd().setSender(commandSender).setTarget(player);
              if (!pitchFlag) {
                CMILC.info(
                    this,
                    commandSender,
                    commandSender.getName().equals(player.getName()) ? "feedback" : "feedbackOther",
                    object.getLocation(),
                    snd);
              }
            }
          });
      return true;
    } else {
      CMILC.info_mapLimit.sendMessage(commandSender);
      return false;
    }
  }
}
