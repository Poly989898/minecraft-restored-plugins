package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class tpbypass implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noSavedTp", "{gcw}There is no saved bad teleport locations");
    configReader.get("feedback", "{gcp}Teleported to unsafe location");
  }

  @CAnnotation(
      info = "Bypass teleportation to unsafe location",
      args = "(playername)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 1)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    } else {
      Location location = cmi.getTeleportHandler().getBadTeleport(player.getUniqueId());
      if (location == null) {
        CMILC.info(this, commandSender, "noSavedTp");
        return null;
      } else if (!player.isOnline()) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        var completableFuture =
            cmi.getTeleportHandler().teleportPlayer(player, location, TeleportCause.COMMAND, false);
        completableFuture.thenAccept(
            object -> {
              Snd snd = new Snd().setSender(commandSender).setTarget(player);
              if (!object.isSuccess()) {
                object.showFeedbackMessage(commandSender, player);
              } else {
                CMILC.info(this, commandSender, "feedback", snd, location);
              }
            });
        return true;
      }
    }
  }
}
