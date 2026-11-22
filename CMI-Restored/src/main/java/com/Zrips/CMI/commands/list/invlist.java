package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class invlist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 230,
      info = "Show saved inventories list",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 666,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    if (strings.length > 0) {
      string = strings[0];
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    cmi.getSavedInventoryManager().InvList(commandSender, cmiUser);
    return true;
  }
}
