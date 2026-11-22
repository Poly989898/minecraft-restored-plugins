package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Worlds.CMIWorld;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class top implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 150,
      info = "Teleport to highest point at your location",
      args = "[playerName] (-s)",
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
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CompletableFuture<Location> completableFuture =
        cmi.getTeleportHandler().getTopLocation(player.getLocation());
    CompletableFuture<Location> locationFuture =
        completableFuture.thenComposeAsync(
            object ->
                CompletableFuture.supplyAsync(
                    () -> {
                      if (object != null
                          && object.getBlockY() > CMIWorld.getMinHeight(object.getWorld())) {
                        Location location = object.clone();
                        location.setX(location.getBlockX() + 0.5);
                        location.setZ(location.getBlockZ() + 0.5);
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());
                        return location;
                      } else {
                        LC.info_nolocation.sendMessage(commandSender, new Object[0]);
                        return null;
                      }
                    }));
    boolean silent = flag;
    locationFuture.thenAccept(
        targetLocation -> {
          if (targetLocation != null) {
            try {
              if (targetLocation.getWorld() == null
                  || player.getLocation().getWorld() == null
                  || targetLocation.getWorld().equals(player.getLocation().getWorld())
                      && abs(targetLocation.getY() - player.getLocation().getY()) < 0.2) {
                LC.info_nolocation.sendMessage(commandSender, new Object[0]);
                return;
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }

            var teleportFuture =
                cmi.getTeleportHandler()
                    .teleportPlayer(player, targetLocation, true, CMITeleportType.Top);
            teleportFuture.thenAccept(
                teleportResult -> {
                  if (teleportResult.isSuccess()) {
                    Snd snd = new Snd().setSender(commandSender).setTarget(player);
                    snd.setSource(player);
                    if (!silent) {
                      if (commandSender.getName().equals(player.getName())) {
                        CMIActionBar.send(
                            commandSender,
                            CMILC.getIM("tppos", "feedback", teleportResult.getLocation(), snd));
                      } else {
                        CMIActionBar.send(
                            commandSender,
                            CMILC.getIM(
                                "tppos", "feedbackOther", teleportResult.getLocation(), snd));
                      }
                    }
                  }
                });
          }
        });
    return true;
  }

  private static <T extends Number> double abs(T value) {
    return value.doubleValue() < 0.0 ? -value.doubleValue() : value.doubleValue();
  }
}
