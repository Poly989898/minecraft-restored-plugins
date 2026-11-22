package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class inv implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You are editing {gcs}[playerDisplayName] {gcp}inventory.");
  }

  @CAnnotation(
      info = "Opens players inventory",
      args = "[playerName] (playerName)",
      multiTab = "![playername] ![playername]",
      explanation = {
        "cmi.command.inv.preventmodify - prevents inventory editing",
        "cmi.invedit - allows to edit inventory",
        "cmi.command.inv.location - shows players location"
      },
      regVar = {1, 2},
      consoleVar = 2,
      customAlias = {"cinv", "invsee", "oi"},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String playerName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else if (playerName == null) {
        playerName = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
    if (cmiUser == null) {
      return null;
    } else {
      Player player = cmi.getTarget(commandSender, playerName, this);
      if (player == null) {
        return null;
      } else {
        Player targetPlayer = cmiUser.getPlayer(true);
        if (targetPlayer == null) {
          LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
          return null;
        } else if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
          CMILC.info_Same.sendMessage(commandSender);
          return null;
        } else if ((!Config.InvBlackList.contains(targetPlayer.getName().toLowerCase())
                || player.isOp())
            && (!cmi.getConfigManager().isInvDisableOffline() || targetPlayer.isOnline())) {
          Snd snd = new Snd().setSender(player).setTarget(cmiUser);
          cmi.getRegChestManager().openInventory(player, targetPlayer);
          CMILC.info(this, player, "feedback", snd);
          return true;
        } else {
          Snd feedbackSnd = new Snd();
          feedbackSnd.setTarget(targetPlayer);
          LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[] {feedbackSnd});
          return null;
        }
      }
    }
  }
}
