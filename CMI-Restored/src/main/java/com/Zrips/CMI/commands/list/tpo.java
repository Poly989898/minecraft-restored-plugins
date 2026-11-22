package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Teleports to player's location by force",
      args = "[playerName] (playerName)",
      tab = {"allPlayerName", "playername"},
      regVar = {1, 2},
      consoleVar = 2,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String text = null;
    if (!cmi.getTeleportManager().isSwitchTeleportPlaces()) {
      String[] parts = strings;
      int value = strings.length;

      for (int count = 0; count < value; count++) {
        String textSecond = parts[count];
        if (string == null) {
          string = textSecond;
        } else {
          text = textSecond;
        }
      }
    } else {
      String[] partsSecond = strings;
      int countSecond = strings.length;

      for (int countThird = 0; countThird < countSecond; countThird++) {
        String textThird = partsSecond[countThird];
        if (text == null) {
          text = textThird;
        } else {
          string = textThird;
        }
      }

      if (string == null) {
        string = text;
        text = null;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    Player playerSecond = cmi.getTarget(commandSender, text, this);
    if (player != null && playerSecond != null) {
      if (player.getName().equalsIgnoreCase(commandSender.getName())
          && playerSecond.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info("tp", commandSender, "notAllowed");
        return null;
      }

      CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
      if (cmiUser != null
          && !cmiUser.getOptionState(PlayerOption.acceptingTPA)
          && !PermissionsManager.CMIPerm.command_tptoggle_bypass.hasPermission(commandSender)) {
        CMILC.info("tp", commandSender, "notAllowed");
        return null;
      }

      if (cmi.getUtilManager().haveBlackListedItems(playerSecond, TeleportManager.TpAction.tp)) {
        return null;
      }

      Location location =
          cmi.getAnimationManager().isSitting(player)
              ? player.getLocation().clone().add(0.0, 1.0, 0.0)
              : player.getLocation();
      if (playerSecond.isOnline()) {
        if (Version.isFolia()) {
          CMIScheduler.runAtEntity(
              cmi, playerSecond, () -> cmi.getNMS().forceTeleport(playerSecond, location));
        } else if (!cmi.getNMS().forceTeleport(playerSecond, location)) {
          return null;
        }
      } else {
        cmi.getNMS().setMiscLocation(playerSecond, location);
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player).setSource(player);
      CMILC.info("tp", playerSecond, "feedback", snd, location);
      return true;
    } else {
      return null;
    }
  }
}
