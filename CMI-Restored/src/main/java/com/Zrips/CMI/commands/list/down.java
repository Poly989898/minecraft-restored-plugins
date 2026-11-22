package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class down implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notFound", "{gcp}No suitable location");
  }

  @CAnnotation(
      priority = 150,
      info = "Teleport one floor down",
      args = "[playerName] (-s) (max)",
      tab = {"playername", "max"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    boolean maxDrop = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("max")) {
        maxDrop = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CompletableFuture<Location> completableFuture = null;
    if (maxDrop) {
      completableFuture = cmi.getTeleportHandler().getBottomLocation(player.getLocation());
    } else {
      completableFuture = cmi.getTeleportHandler().getDownLocation(player.getLocation());
    }

    var distanceFuture =
        completableFuture.thenComposeAsync(
            object ->
                CompletableFuture.supplyAsync(
                    () -> {
                      if (object == null) {
                        CMIActionBar.send(commandSender, CMILC.getIM(this, "notFound"));
                        return null;
                      } else {
                        Location location = object.clone();
                        location.setX(location.getBlockX());
                        location.setZ(location.getBlockZ());
                        location.add(0.5, 0.0, 0.5);
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());
                        return location;
                      }
                    }));
    boolean silent = flag;
    distanceFuture.thenAccept(
        dropLocation -> {
          var dropDistanceFuture = cmi.getTeleportHandler().getDistanceToDrop(player.getLocation());
          if (dropLocation != null) {
            dropDistanceFuture.thenAccept(
                fallDistance -> {
                  if (player.isOnline() && fallDistance > 3.0) {
                    player.setFallDistance((float) (player.getFallDistance() + fallDistance));
                  }

                  var teleportFuture =
                      cmi.getTeleportHandler()
                          .teleportPlayer(player, dropLocation, true, CMITeleportType.Down);
                  teleportFuture.thenAccept(
                      teleportResult -> {
                        if (teleportResult.isSuccess()) {
                          Snd snd = new Snd().setSender(commandSender).setTarget(player);
                          snd.setSource(player);
                          if (!silent) {
                            CMIActionBar.send(
                                commandSender,
                                CMILC.getIM(
                                    "tppos",
                                    commandSender.getName().equals(player.getName())
                                        ? "feedback"
                                        : "feedbackOther",
                                    teleportResult.getLocation(),
                                    snd));
                          }
                        }
                      });
                });
          }
        });
    return true;
  }
}
