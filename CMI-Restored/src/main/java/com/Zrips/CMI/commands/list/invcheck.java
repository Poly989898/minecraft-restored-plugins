package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class invcheck implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Open saved inventory in preview mode",
      args = "(playerName) [id] (-e) (last)",
      tab = "playerName",
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    int value = 0;
    String string = null;
    boolean flag = true;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("last")) {
        value = -1;
      } else if (arg.equalsIgnoreCase("-e")) {
        if (!PermissionsManager.CMIPerm.command_invcheck_edit.hasPermission(commandSender, true)) {
          return null;
        }

        flag = false;
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
    } else if (value == 0) {
      Bukkit.dispatchCommand(commandSender, "cmi invlist " + cmiUser.getName());
      return true;
    } else {
      this.openSavedInv(player, cmiUser, value, flag);
      return true;
    }
  }

  private void openSavedInv(Player player, CMIUser cmiUser, int value, boolean flag) {
    var completableFuture =
        CMI.getInstance().getSavedInventoryManager().getInventoryById(cmiUser, value);
    completableFuture.whenComplete(
        (object, error) -> {
          if (object == null) {
            CMILC.info_InventorySave_IdDontExist.sendMessage(player);
          } else {
            CMI.getInstance().getSavedInventoryManager().openSavedInv(player, object, flag);
          }
        });
  }
}
