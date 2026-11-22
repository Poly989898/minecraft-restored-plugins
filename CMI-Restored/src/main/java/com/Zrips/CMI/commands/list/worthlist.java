package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class worthlist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("type", "{gcs}[type]");
    configReader.get("worth", "{gcp}Worth: {gcs}[price]");
    configReader.get("title", "{gcp}Worth List");
  }

  @CAnnotation(
      info = "Check list of items with set sell prices",
      args = "(playerName) (-missing)",
      tab = {"playername", "-missing%%"},
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
      if (arg.equalsIgnoreCase("-missing")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      cmi.getWorthManager().openWorthListGui(player, 1, flag);
      return true;
    } else {
      return null;
    }
  }
}
