package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.SavedInv.CMIInventory;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.DateFormat;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class invsave implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 200,
      info = "Save inventory",
      args = "(playerName) (id) (-s)",
      tab = {"playername", "-s%%"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Integer integer = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];

      try {
        integer = Integer.parseInt(arg);
      } catch (Exception exception) {
        if (arg.equalsIgnoreCase("-s")
            && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        } else {
          string = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    boolean silent = flag;
    CMIInventory cmiInventory = new CMIInventory(player.getName(), player.getUniqueId());
    cmi.getSavedInventoryManager().generateCMIInventory(cmiInventory, null, null);
    cmi.getSavedInventoryManager()
        .saveInventory(cmiInventory, integer)
        .whenComplete(
            (savedInventory, throwable) -> {
              if (!silent) {
                CMILC.info_InventorySave_saved.sendMessage(
                    commandSender,
                    "[time]",
                    DateFormat.MiliToDate(System.currentTimeMillis()),
                    "[id]",
                    savedInventory.getId());
                if (commandSender instanceof Player) {
                  cmi.getSavedInventoryManager().InvList(commandSender, player);
                }
              }
            });
    return true;
  }
}
