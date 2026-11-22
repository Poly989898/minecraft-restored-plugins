package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.PlayerMail;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.list.importfrom;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class EssentialsConverter {
  static int totalToConvert = 0;
  static int converted = 0;
  static int lastBatch = 0;
  static boolean done = false;
  static File ff;

  public static void convert(CommandSender commandSender, importSettings importSettings) {
    if (importSettings.is(EssentialsConverter.importType.warps)) {
      convertWarps(commandSender);
    }

    convertUsers(commandSender, importSettings);
  }

  public static void convertUsers(CommandSender commandSender, importSettings importSettings) {
    ff = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");
    if (ff.isDirectory()) {
      totalToConvert = ff.listFiles().length;
      CMIScheduler.runTaskAsynchronously(
          CMI.getInstance(), () -> proccess(0, commandSender, importSettings));
    }
  }

  private static void convertWarps(CommandSender commandSender) {
    File file = new File("plugins" + File.separator + "Essentials" + File.separator + "warps");
    if (file.isDirectory()) {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(Arrays.asList(file.listFiles()));
      int value = 0;

      for (File fileSecond : ((Iterable<File>) arrayList)) {
        try {
          YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fileSecond);
          String string = yamlConfiguration.getString("world");
          World world = null;
          if (string.length() == 36) {
            world = Bukkit.getWorld(UUID.fromString(string));
          } else {
            world = Bukkit.getWorld(yamlConfiguration.getString("world"));
          }

          if (world == null && yamlConfiguration.isString("world-name")) {
            world = Bukkit.getWorld(yamlConfiguration.getString("world-name"));
          }

          if (world != null) {
            Location location =
                new Location(
                    world,
                    yamlConfiguration.getDouble("x"),
                    yamlConfiguration.getDouble("y"),
                    yamlConfiguration.getDouble("z"));
            location.setPitch(Double.valueOf(yamlConfiguration.getDouble("pitch")).floatValue());
            location.setYaw(Double.valueOf(yamlConfiguration.getDouble("yaw")).floatValue());
            if (yamlConfiguration.isString("name")) {
              String text = yamlConfiguration.getString("name");
              CmiWarp cmiWarp = new CmiWarp(text, new CMILocation(location));
              if (yamlConfiguration.isString("lastowner")) {
                cmiWarp.setCreator(UUID.fromString(yamlConfiguration.getString("lastowner")));
              }

              CMI.getInstance().getWarpManager().addWarp(cmiWarp, false);
              value++;
            }
          }
        } catch (Exception exception) {
          CMIMessages.sendMessage(
              commandSender,
              "&cFailed to load "
                  + fileSecond
                  + " warp file. Inform Dev about this and provide file content for testing");
        }
      }

      CMI.getInstance().getWarpManager().save();
      CMILC.info(importfrom.class, commandSender, "convertedWarps", "[amount]", value);
    }
  }

  private static void proccess(
      int value, CommandSender commandSender, importSettings importSettings) {
    File[] files = ff.listFiles();

    for (int count = 0; count < 20 && value + count < totalToConvert; count++) {
      File file = files[value + count];
      String string = file.getName().substring(0, file.getName().length() - 4);
      if (string.length() != 36) {
        CMI.getInstance().consoleMessage("UUID has wrong size (" + string + ")");
      } else {
        UUID uuid = null;

        try {
          uuid = UUID.fromString(string);
        } catch (IllegalArgumentException illegalArgumentException) {
          CMI.getInstance().consoleMessage("Failed to convert UUID (" + string + ")");
          continue;
        }

        if (uuid == null) {
          CMI.getInstance().consoleMessage("Invalid UUID (" + string + ")");
        } else {
          CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(uuid);
          YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
          if (cmiUser == null && yamlConfiguration.isString("lastAccountName")) {
            cmiUser =
                CMI.getInstance()
                    .getPlayerManager()
                    .getUser(yamlConfiguration.getString("lastAccountName"));
            if (cmiUser != null
                && !cmiUser.getUniqueId().toString().equalsIgnoreCase(uuid.toString())) {
              CMI.getInstance().getDbManager().getDB().updateUUID(cmiUser.getId(), uuid);
            }
          }

          if (cmiUser == null && yamlConfiguration.isString("last-account-name")) {
            cmiUser =
                CMI.getInstance()
                    .getPlayerManager()
                    .getUser(yamlConfiguration.getString("last-account-name"));
            if (cmiUser != null
                && !cmiUser.getUniqueId().toString().equalsIgnoreCase(uuid.toString())) {
              CMI.getInstance().getDbManager().getDB().updateUUID(cmiUser.getId(), uuid);
            }
          }

          boolean flag = false;
          boolean flagSecond = false;
          if (cmiUser == null) {
            cmiUser = new CMIUser(uuid);
            flagSecond = true;
          }

          if (yamlConfiguration.isString("lastAccountName")) {
            cmiUser.setName(yamlConfiguration.getString("lastAccountName"));
            flag = true;
          }

          if (yamlConfiguration.isString("last-account-name")) {
            cmiUser.setName(yamlConfiguration.getString("last-account-name"));
            flag = true;
          }

          try {
            flag = importUser(yamlConfiguration, cmiUser, importSettings);
          } catch (Exception exception) {
            exception.printStackTrace();
          }

          if (flagSecond) {
            CMI.getInstance().getPlayerManager().addUser(cmiUser);
          }

          if (flag) {
            cmiUser.addForDelayedSave();
          }

          converted++;
        }
      }
    }

    lastBatch += 20;
    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          if (value + 1 < totalToConvert) {
            if (lastBatch >= 100) {
              CMILC.info(
                  importfrom.class,
                  commandSender,
                  "converted",
                  "[done]",
                  converted,
                  "[total]",
                  totalToConvert);
              lastBatch = 0;
            }

            proccess(value + 20, commandSender, importSettings);
          } else {
            CMILC.info(
                importfrom.class,
                commandSender,
                "converted",
                "[done]",
                converted,
                "[total]",
                totalToConvert);
            CMILC.info(importfrom.class, commandSender, "savingInProgress");
            if (!CMI.getInstance().getDbManager().isRapidModeEnabled()) {
              CMI.getInstance().getDbManager().startRapidSave();
              CMI.getInstance().getDbManager().start();
            }

            converted = 0;
            totalToConvert = 0;
            lastBatch = 0;
            done = true;
          }
        },
        1L);
  }

  private static boolean importUser(
      YamlConfiguration yamlConfiguration, CMIUser cmiUser, importSettings importSettings) {
    boolean flag = false;
    if (importSettings.is(EssentialsConverter.importType.logoutlocation)
        && yamlConfiguration.isConfigurationSection("logoutlocation")) {
      try {
        World world = Bukkit.getWorld(yamlConfiguration.getString("logoutlocation.world"));
        if (world == null && yamlConfiguration.getString("logoutlocation.world").length() == 36) {
          world =
              Bukkit.getWorld(UUID.fromString(yamlConfiguration.getString("logoutlocation.world")));
        }

        if (world != null) {
          Location location =
              new Location(
                  world,
                  yamlConfiguration.getDouble("logoutlocation.x"),
                  yamlConfiguration.getDouble("logoutlocation.y"),
                  yamlConfiguration.getDouble("logoutlocation.z"));
          location.setPitch(
              Double.valueOf(yamlConfiguration.getDouble("logoutlocation.pitch")).floatValue());
          location.setYaw(
              Double.valueOf(yamlConfiguration.getDouble("logoutlocation.yaw")).floatValue());
          cmiUser.setLogOutLocation(location);
        }
      } catch (Exception exception) {
      }

      flag = true;
    }

    if (importSettings.is(EssentialsConverter.importType.homes)
        && yamlConfiguration.isConfigurationSection("homes")) {
      for (String string : yamlConfiguration.getConfigurationSection("homes").getKeys(false)) {
        String text = "homes." + string;
        String textSecond = "world";
        if (yamlConfiguration.isString("world-name")) {
          textSecond = "world-name";
        }

        CMILocation cmiLocation =
            new CMILocation(
                yamlConfiguration.getString(text + "." + textSecond),
                yamlConfiguration.getDouble(text + ".x"),
                yamlConfiguration.getDouble(text + ".y"),
                yamlConfiguration.getDouble(text + ".z"));
        cmiLocation.setPitch(
            Double.valueOf(yamlConfiguration.getDouble(text + ".pitch", 0.0)).floatValue());
        cmiLocation.setYaw(
            Double.valueOf(yamlConfiguration.getDouble(text + ".yaw", 0.0)).floatValue());
        CMIUser cMIUserSecond = cmiUser;
        if (cmiUser.getHome(string) != null) {
          CMIScheduler.runTask(CMI.getInstance(), () -> cMIUserSecond.removeHome(string));
        }

        CMIScheduler.runTask(
            CMI.getInstance(),
            () -> cMIUserSecond.addHome(new CmiHome(string, cmiLocation), false));
      }

      flag = true;
    }

    if (importSettings.is(EssentialsConverter.importType.nickname)
        && yamlConfiguration.isString("nickname")) {
      String textThird = yamlConfiguration.getString("nickname");
      cmiUser.setNickName(textThird, false);
      flag = true;
    }

    if (importSettings.is(EssentialsConverter.importType.money)) {
      if (yamlConfiguration.isString("money")) {
        String textFourth = yamlConfiguration.getString("money");

        try {
          double number = Double.parseDouble(textFourth);
          cmiUser.getEconomyAccount().setBalance(number);
        } catch (Exception exception2) {
        }

        flag = true;
      }

      if (yamlConfiguration.isDouble("money")) {
        double amount = yamlConfiguration.getDouble("money");

        try {
          cmiUser.getEconomyAccount().setBalance(amount);
        } catch (Exception exception3) {
        }

        flag = true;
      }
    }

    if (importSettings.is(EssentialsConverter.importType.mail)
        && yamlConfiguration.isList("mail")) {
      var list = yamlConfiguration.getStringList("mail");
      int value = 0;

      for (String textFifth : list) {
        value++;
        String textSixth = "Console";
        if (textFifth.contains(" ")) {
          textSixth = textFifth.split(" ")[0];
          textFifth = textFifth.substring(textSixth.length() + 1);
        }

        PlayerMail playerMail =
            new PlayerMail(textSixth, System.currentTimeMillis() - value, textFifth);
        cmiUser.addMail(playerMail, false);
      }
    }

    if (yamlConfiguration.isBoolean("npc") && yamlConfiguration.getBoolean("npc")) {
      cmiUser.setFakeAccount(true);
    }

    return flag;
  }

  public enum importType {
    homes,
    warps,
    nickname,
    logoutlocation,
    money,
    mail;
  }
}
