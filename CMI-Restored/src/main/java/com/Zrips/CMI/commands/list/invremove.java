package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class invremove implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Remove saved inventories for player",
      args = "(playerName) [id/all/last]",
      tab = "playername",
      regVar = {1, 2},
      consoleVar = 2,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    String string = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("all")) {
        value = -2;
      } else if (arg.equalsIgnoreCase("last")) {
        value = -1;
      } else {
        try {
          value = Integer.parseInt(arg);
        } catch (NumberFormatException numberFormatException) {
          string = arg;
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    cmi.getSavedInventoryManager()
        .removeUserInventory(cmiUser, value)
        .whenComplete(
            (success, throwable) -> {
              if (success) {
                CMILC.info_InventorySave_Deleted.sendMessage(commandSender);
              } else {
                CMILC.info_InventorySave_CantFind.sendMessage(commandSender);
              }
            });
    return true;
  }
}
