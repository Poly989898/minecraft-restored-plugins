package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.teleportAll;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.io.File;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class tpallworld implements Cmd {
  public teleportAll tpInfo = new teleportAll();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("started", "{gcp}Teleportations started!");
    configReader.get(
        "current",
        "{gcp}Currently teleported {gcs}[amount] {gcp}and {gcs}[files] {gcp}checked files");
    configReader.get(
        "total",
        "{gcp}Teleported a total of {gcs}[amount] {gcp}out of the {gcs}[files] {gcp}users checked");
    configReader.get(
        "WorldNotFound",
        "{gcp}World by given {gcs}[name] {gcp}name is not loaded. Make sure its correct one. We"
            + " will use as it is.");
  }

  @CAnnotation(
      info = "Teleports ALL players from specific world",
      args = "[worldName] (worldName;x;y;z(;yaw;pitch)) (-a)",
      tab = "worlds",
      regVar = {1, 2, 3},
      explanation =
          "-a variable will teleport everyone who are in no longer existing world to target"
              + " location",
      consoleVar = {2, 3},
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMILocation cmiLocation = null;
    boolean flag = false;
    String[] partsSecond = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String string = partsSecond[count];
      if (string.equalsIgnoreCase("cancel") && this.tpInfo.getTask() != null) {
        this.tpInfo.getTask().cancel(false);
        this.tpInfo.setTask(null);
        return true;
      }

      if (string.equalsIgnoreCase("-a")) {
        flag = true;
      } else if (this.tpInfo.getTargetWorld() == null) {
        World world = cmi.getUtilManager().getWorld(string);
        if (world != null) {
          this.tpInfo.setTargetWorld(world.getName());
        } else {
          CMILC.info(this, commandSender, "WorldNotFound", "[name]", string);
          this.tpInfo.setTargetWorld(string);
        }
      } else if (cmiLocation == null) {
        cmiLocation = cmi.getPlayerManager().convertStringToLocation(string, true);
      }
    }

    Location location = null;
    if (cmiLocation != null) {
      location = cmiLocation.clone();
    } else if (commandSender instanceof Player) {
      location = ((Player) commandSender).getLocation();
    }

    if (location == null) {
      return false;
    }

    this.tpInfo.setDestination(location);
    CMILC.info(this, commandSender, "started");

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player
          .getLocation()
          .getWorld()
          .getName()
          .equalsIgnoreCase(this.tpInfo.getTargetWorld())) {
        player.teleport(this.tpInfo.getDestination(), TeleportCause.COMMAND);
        this.tpInfo.addTeleported();
      }
    }

    boolean flagSecond = flag;
    this.tpInfo.setTask(
        CMIScheduler.runTaskAsynchronously(
            cmi,
            () -> {
              File file = new File(this.tpInfo.getTargetFolder() + File.separator + "playerdata");
              if (!file.isDirectory()) {
                LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
              } else {
                File[] files = file.listFiles();
                if (files == null) {
                  LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
                } else {
                  int countSecond = 0;
                  int countThird = 0;
                  int countFourth = 0;
                  File[] filesValue = files;
                  int countFifth = files.length;

                  for (int countSixth = 0; countSixth < countFifth; countSixth++) {
                    File fileSecond = filesValue[countSixth];
                    if (fileSecond.getName().endsWith(".dat")) {
                      if (this.tpInfo.getTask() == null) {
                        break;
                      }

                      String text = cmi.getNMS().getWorldName(fileSecond);
                      countFourth++;
                      if (text == null) {
                        text = getFromUser(fileSecond);
                      }

                      if (flagSecond
                          ? text == null || this.tpInfo.getTargetWorld().equalsIgnoreCase(text)
                          : text != null
                              && !text.isEmpty()
                              && this.tpInfo.getTargetWorld().equalsIgnoreCase(text)) {
                        countThird++;
                        if (countSecond >= 100) {
                          countSecond = 0;
                          CMILC.info(
                              tpallworld.class.getSimpleName(),
                              commandSender,
                              "current",
                              "[amount]",
                              this.tpInfo.getTeleportedCount(),
                              "[files]",
                              countThird);
                        }

                        countSecond++;
                        cmi.getNMS().setMiscLocation(fileSecond, this.tpInfo.getDestination());
                        this.tpInfo.addTeleported();
                      }
                    }
                  }

                  CMILC.info(
                      tpallworld.class.getSimpleName(),
                      commandSender,
                      "total",
                      "[amount]",
                      this.tpInfo.getTeleportedCount(),
                      "[files]",
                      countFourth);
                  this.tpInfo = new teleportAll();
                }
              }
            }));
    return true;
  }

  private static String getFromUser(File file) {
    try {
      UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
      CMIUser cmiUser = CMIUser.getUser(uuid);
      if (cmiUser != null) {
        CMILocation cmiLocation = cmiUser.getCMILogOutLocation();
        if (cmiLocation != null) {
          return cmiLocation.getWorldName();
        }
      }
    } catch (Throwable throwable) {
    }

    return null;
  }
}
