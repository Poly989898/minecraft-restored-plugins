package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpohere implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Teleports player to your location by force",
      args = "[playerName] (playerName)",
      tab = {"allPlayerName", "playername"},
      regVar = {1, 2},
      consoleVar = 2,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String targetName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else {
        targetName = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    Player targetPlayer = cmi.getTarget(commandSender, targetName, this);
    if (player != null && targetPlayer != null) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      snd.setSource(targetPlayer);
      CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
      if (!cmiUser.getOptionState(PlayerOption.acceptingTPA)
          && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
        CMILC.info("tphere", commandSender, "notAllowed");
        return null;
      }

      Location location = targetPlayer.getLocation();
      if (player.isOnline()) {
        boolean flag = cmi.getNMS().forceTeleport(cmiUser.getPlayer(), location);
        CMILC.info("tphere", commandSender, "feedback.online", snd, location);
        CMILC.info("tphere", player, "feedback.player", snd);
      } else {
        cmi.getNMS().setMiscLocation(player, location);
        CMILC.info("tphere", commandSender, "feedback.offline", snd, location);
      }

      return true;
    } else {
      return null;
    }
  }
}
