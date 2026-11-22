package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.RandomTeleport;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.io.File;
import java.io.IOException;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class setrt implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "list",
        "{gcs}[place]. {gcp}[world] [enabled] {gcs}[centerX]{gcp}:{gcs}[centerZ]"
            + " [maxDist]{gcp}/{gcs}[minDist] [type]");
  }

  @CAnnotation(
      priority = 40,
      info = "Set random teleport bounds",
      args =
          "(worldName) (center:[x]:[z]) (min:[range]) (max:[range]) (square/circle)"
              + " (enabled/disabled)",
      tab = {"worlds", "center:%%", "min:%%", "max:%%", "square%%circle", "enabled%%disabled"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4, 5, 6},
      consoleVar = {0, 1, 2, 3, 4, 5, 6},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings.length == 0) {
      int value = 0;
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);

      for (World world : Bukkit.getWorlds()) {
        RandomTeleport randomTeleport =
            cmi.getRandomTeleportationManager().getRandomTeleport(world, true);
        value++;
        RawMessage rawMessage = new RawMessage();
        if (randomTeleport == null) {
          randomTeleport = new RandomTeleport(world);
          randomTeleport.setEnabled(false);
        }

        rawMessage.addText(
            CMILC.getIM(
                this,
                "list",
                "[place]",
                value,
                "[world]",
                randomTeleport.getCenter().getWorld().getName(),
                "[enabled]",
                randomTeleport.isEnabled()
                    ? LC.modify_enabled.getLocale(new Object[0])
                    : LC.modify_disabled.getLocale(new Object[0]),
                "[centerX]",
                randomTeleport.getCenter().getBlockX(),
                "[centerZ]",
                randomTeleport.getCenter().getBlockZ(),
                "[maxDist]",
                randomTeleport.getMaxDistance(),
                "[minDist]",
                randomTeleport.getMinDistance(),
                "[type]",
                randomTeleport.isCircle()
                    ? LC.info_circle.getLocale(new Object[0])
                    : LC.info_square.getLocale(new Object[0])));
        rawMessage.addHover(LC.modify_editSymbolHover.getLocale(new Object[0]));
        rawMessage.addSuggestion(
            "/"
                + CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " "
                + world.getName()
                + " center:"
                + randomTeleport.getCenter().getBlockX()
                + ":"
                + randomTeleport.getCenter().getBlockZ()
                + " min:"
                + randomTeleport.getMinDistance()
                + " max:"
                + randomTeleport.getMaxDistance()
                + (randomTeleport.isCircle() ? " circle" : " square")
                + (randomTeleport.isEnabled() ? " enabled" : " disabled"));
        rawMessage.show(commandSender);
      }

      return true;
    } else {
      World targetWorld = null;
      Integer integer = null;
      Integer minY = null;
      Integer maxY = null;
      Integer radius = null;
      Boolean circle = null;
      Boolean surfaceOnly = null;
      String[] argsCopy = strings;
      int argCount = strings.length;

      for (int argIndex = 0; argIndex < argCount; argIndex++) {
        String string = argsCopy[argIndex];
        if (string.equalsIgnoreCase("circle")) {
          circle = true;
        } else if (string.equalsIgnoreCase("square")) {
          circle = false;
        } else if (string.equalsIgnoreCase("enabled")) {
          surfaceOnly = true;
        } else if (string.equalsIgnoreCase("disabled")) {
          surfaceOnly = false;
        } else {
          if (targetWorld == null) {
            targetWorld = cmi.getUtilManager().getWorld(string);
            if (targetWorld != null) {
              continue;
            }
          }

          if (string.toLowerCase().startsWith("center:")) {
            string = string.substring("center:".length());
            if (string.contains(":")) {
              String[] pairs = string.split(":");

              try {
                integer = Integer.parseInt(pairs[0]);
                minY = Integer.parseInt(pairs[1]);
              } catch (Exception exception) {
                return false;
              }
            }
          } else if (string.toLowerCase().startsWith("min:")) {
            string = string.substring("min:".length());

            try {
              radius = Integer.parseInt(string);
            } catch (Exception exception2) {
              return false;
            }
          } else if (string.toLowerCase().startsWith("max:")) {
            string = string.substring("max:".length());

            try {
              maxY = Integer.parseInt(string);
            } catch (Exception exception3) {
              return false;
            }
          }
        }
      }

      if (targetWorld == null && commandSender instanceof Player) {
        targetWorld = ((Player) commandSender).getWorld();
      }

      if (targetWorld == null) {
        return false;
      }

      RandomTeleport teleport =
          cmi.getRandomTeleportationManager().getRandomTeleport(targetWorld, true);
      String configPrefix =
          "RandomTeleportation.Worlds." + targetWorld.getName().replace(".", "_") + ".";
      if (teleport == null) {
        teleport = new RandomTeleport(targetWorld);
      }

      if (integer != null) {
        CMILocation cmiLocation = new CMILocation(teleport.getCenter().clone());
        cmiLocation.setX(integer.intValue());
        teleport.setCenter(cmiLocation);
      }

      if (minY != null) {
        CMILocation cmiLocation = new CMILocation(teleport.getCenter().clone());
        cmiLocation.setZ(minY.intValue());
        teleport.setCenter(cmiLocation);
      }

      if (maxY != null) {
        teleport.setMaxDistance(maxY);
      }

      if (radius != null) {
        teleport.setMinDistance(radius);
      }

      if (surfaceOnly != null) {
        teleport.setEnabled(surfaceOnly);
      }

      if (circle != null) {
        teleport.setCircle(circle);
      }

      File file = new File(cmi.getDataFolder(), "RandomTeleportations.yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      yamlConfiguration.options().copyDefaults(true);
      yamlConfiguration.set(configPrefix + "Enabled", teleport.isEnabled());
      yamlConfiguration.set(configPrefix + "MaxRange", teleport.getMaxDistance());
      yamlConfiguration.set(configPrefix + "MinRange", teleport.getMinDistance());
      yamlConfiguration.set(configPrefix + "CenterX", teleport.getCenter().getBlockX());
      yamlConfiguration.set(configPrefix + "CenterZ", teleport.getCenter().getBlockZ());
      yamlConfiguration.set(configPrefix + "Circle", teleport.isCircle());

      try {
        yamlConfiguration.save(file);
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

      this.perform(cmi, commandSender, new String[0]);
      return true;
    }
  }
}
