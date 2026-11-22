package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIDirection;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class distance implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}Distance: {gcs}[dist] {gcp}x:{gcs}[x] {gcp}y:{gcs}[y] {gcp}z:{gcs}[z]"
            + " {gcp}Direction:{gcs}[dir]");
  }

  @CAnnotation(
      priority = 40,
      info = "Check distance between 2 points",
      args = "(playerName) (playerName)",
      tab = {"playername", "playername"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String secondName = null;
    boolean flag = true;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String candidate = args[argIndex];
      if (string == null && cmi.getPlayerManager().getUserByAproxName(candidate) != null) {
        string = cmi.getPlayerManager().getUserByAproxName(candidate).getName();
      } else if (secondName == null
          && cmi.getPlayerManager().getUserByAproxName(candidate) != null) {
        secondName = cmi.getPlayerManager().getUserByAproxName(candidate).getName();
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    Player secondPlayer = cmi.getTarget(commandSender, secondName, this);
    if (player != null && secondPlayer != null) {
      Location location = player.getLocation();
      if (flag && player.equals(secondPlayer)) {
        Block block = cmi.getUtilManager().getTargetBlock(player, 200);
        if (block != null) {
          location = block.getLocation().clone().add(0.5, 0.5, 0.5);
        }
      }

      Location secondLocation = secondPlayer.getLocation();
      int dx = location.getBlockX() - secondLocation.getBlockX();
      dx = dx < 0 ? -dx : dx;
      int dy = location.getBlockY() - secondLocation.getBlockY();
      dy = dy < 0 ? -dy : dy;
      int dz = location.getBlockZ() - secondLocation.getBlockZ();
      dz = dz < 0 ? -dz : dz;
      if (!location.getWorld().equals(secondLocation.getWorld())) {
        LC.info_differentWorld.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        CMILC.info(
            this,
            commandSender,
            "feedback",
            "[dist]",
            (int) (location.distance(secondLocation) * 100.0) / 100.0,
            "[x]",
            dx,
            "[z]",
            dz,
            "[y]",
            dy,
            "[dir]",
            CMIDirection.getFromLocations(location, secondLocation));
        return true;
      }
    } else {
      return null;
    }
  }
}
