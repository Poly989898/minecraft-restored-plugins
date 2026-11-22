package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Sounds.CMISound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class sound implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "playing",
        "{gcp}Playing {gcs}[sound] {gcp}at [world] [x]:[y]:[z] \n"
            + "{gcp}Pitch: {gcs}[pitch] {gcp}Volume: {gcs}[volume]");
    configReader.get(
        "playingMultiple",
        "{gcp}Playing {gcs}[sound] {gcp}for {gcs}[amount] {gcp}players \n"
            + "{gcp}Pitch: {gcs}[pitch] {gcp}Volume: {gcs}[volume]");
  }

  @CAnnotation(
      priority = 48,
      info = "Play sound at target location",
      args =
          "[sound] (-p:[pitch]) (-v:[volume]) (playerName/-all/-l:playerName) (world;x;y;z)"
              + " (-r:[radius]) (-s)",
      multiTab =
          "[sound] -p:,![currentLocation],[playername],-all,-r:,-l:"
              + " -v:,![currentLocation],![playername],-all ![playername],![currentLocation],-all",
      explanation = {},
      regVar = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
      consoleVar = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMISound cmiSound = null;
    String string = null;
    Float volume = null;
    Float pitch = null;
    World world = null;
    Double x = null;
    Double y = null;
    Double z = null;
    Location location = null;
    Boolean global = false;
    int value = 0;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          global = true;
        }
      } else {
        if (cmiSound == null || cmiSound.getSound() == null) {
          cmiSound = new CMISound(arg);
          if (cmiSound.getSound() != null) {
            continue;
          }
        }

        if (arg.startsWith("-r:")) {
          try {
            value = Integer.parseInt(arg.substring(3));
            continue;
          } catch (Exception exception) {
          }
        }

        if (arg.startsWith("-l:")) {
          try {
            arg = arg.substring("-l:".length(), arg.length());
            Player player = CMIUser.getOnlinePlayer(arg);
            if (player != null && player.getName().equalsIgnoreCase(arg)) {
              location = player.getLocation();
            }
            continue;
          } catch (Exception exception2) {
          }
        }

        if (volume == null && arg.startsWith("-p:")) {
          try {
            volume = Float.parseFloat(arg.substring(3));
            continue;
          } catch (Exception exception3) {
          }
        }

        if (pitch == null && arg.startsWith("-v:")) {
          try {
            pitch = Float.parseFloat(arg.substring(3));
            continue;
          } catch (Exception exception4) {
          }
        }

        CMILocation cmiLocation = CMILocation.fromString(arg);
        if (cmiLocation != null && cmiLocation.isValid()) {
          world = cmiLocation.getWorld();
          x = cmiLocation.getX();
          y = cmiLocation.getY();
          z = cmiLocation.getZ();
        } else if (string == null && CMIUser.getOnlinePlayer(commandSender, arg) != null) {
          string = arg;
        } else {
          if (world == null) {
            world = CMIWorld.getWorld(arg);
            if (world != null) {
              continue;
            }
          }

          if (x == null) {
            try {
              x = Double.parseDouble(arg);
              continue;
            } catch (Exception exception5) {
            }
          }

          if (y == null) {
            try {
              y = Double.parseDouble(arg);
              continue;
            } catch (Exception exception6) {
            }
          }

          if (z == null) {
            try {
              z = Double.parseDouble(arg);
              continue;
            } catch (Exception exception7) {
            }
          }

          if (string == null) {
            string = arg;
          }
        }
      }
    }

    if (cmiSound != null && cmiSound.getSound() != null) {
      if (volume != null) {
        cmiSound.setPitch(volume);
      }

      if (pitch != null) {
        cmiSound.setVolume(pitch);
      }

      if (world != null && x != null && y != null && z != null) {
        location = new Location(world, x, y, z);
      }

      if (location != null || x == null && !(y != null | z != null)) {
        HashSet<Player> hashSet = new HashSet();
        if (location == null) {
          if ((string == null || !string.equalsIgnoreCase("-all")) && string != null) {
            CMIUser cmiUser = cmi.getUser(commandSender, string, this);
            if (cmiUser != null && cmiUser.isOnline()) {
              hashSet.add(cmiUser.getPlayer());
            }

            if (value > 0 && !hashSet.isEmpty()) {
              hashSet.addAll(
                  cmi.getUtilManager()
                      .getPlayersFromRange(
                          ((Player) hashSet.iterator().next()).getLocation(), value));
            }
          } else if (PermissionsManager.CMIPerm.command_$1_others.hasPermission(
              commandSender, this.getClass().getSimpleName())) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
              hashSet.add(onlinePlayer);
            }
          }
        }

        if (location == null) {
          for (Player targetPlayer : hashSet) {
            cmiSound.play(targetPlayer);
          }
        } else if (value > 0) {
          hashSet.addAll(cmi.getUtilManager().getPlayersFromRange(location, value));

          for (Player listedPlayer : hashSet) {
            try {
              cmiSound.play(listedPlayer, location);
            } catch (Throwable throwable) {
              cmiSound.play(listedPlayer);
            }
          }
        } else {
          cmiSound.play(location);
        }

        if (!global) {
          if (hashSet.size() != 1 && location == null) {
            CMILC.info(
                this,
                commandSender,
                "playingMultiple",
                "[sound]",
                cmiSound.getRawName().toLowerCase(),
                "[amount]",
                hashSet.size(),
                "[pitch]",
                cmiSound.getPitch(),
                "[volume]",
                cmiSound.getVolume());
          } else {
            location =
                location != null ? location : ((Player) hashSet.iterator().next()).getLocation();
            CMILC.info(
                this,
                commandSender,
                "playing",
                "[sound]",
                cmiSound.getRawName().toLowerCase(),
                "[world]",
                location.getWorld().getName(),
                "[x]",
                location.getBlockX(),
                "[y]",
                location.getBlockY(),
                "[z]",
                location.getBlockZ(),
                "[pitch]",
                cmiSound.getPitch(),
                "[volume]",
                cmiSound.getVolume());
          }
        }

        return true;
      } else {
        LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
        return null;
      }
    } else {
      return false;
    }
  }
}
