package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ender implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "{gcs}[playerDisplayName] {gcp}Ender Chest");
    configReader.get("feedback", "{gcp}You are editing {gcs}[playerDisplayName] {gcp}enderchest.");
  }

  @CAnnotation(
      info = "Opens players ender chest",
      args = "(playerName) (playerName)",
      tab = {"playerName", "playerName"},
      regVar = {0, 1, 2},
      consoleVar = 2,
      customAlias = {"ce", "ec", "cend", "enderchest", "oe", "echest"},
      others = true,
      explanation = {
        "cmi.command.ender.preventmodify - prevents inventory modifications",
        "cmi.enderedit - allows to edit opened inventory"
      })
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
      } else {
        playerName = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    } else {
      Player targetPlayer = cmi.getTarget(commandSender, playerName, this);
      if (targetPlayer == null) {
        return null;
      } else if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
        targetPlayer.openInventory(targetPlayer.getEnderChest());
        Snd snd = new Snd().setSender(targetPlayer).setTarget(player);
        CMILib.getInstance()
            .getReflectionManager()
            .updateInventoryTitle(targetPlayer, CMILC.getIM("ender", "title", snd));
        return true;
      } else {
        Snd feedbackSnd = new Snd().setSender(commandSender).setTarget(player);
        cmi.getEnderChestManager().openEnderChest(targetPlayer, player);
        CMILC.info(this, commandSender, "feedback", feedbackSnd);
        return true;
      }
    }
  }
}
