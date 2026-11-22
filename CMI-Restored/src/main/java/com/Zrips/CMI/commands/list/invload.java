package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class invload implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Load saved inventory",
      args = "(sourceName) (targetName) [id/last]",
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      alias = true,
      others = true,
      multiTab = "[playerName],!last,1 [playerName],!last,1 !last,1")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    String string = null;
    String playerName = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("last")) {
        value = -1;
      } else {
        try {
          value = Integer.parseInt(arg);
        } catch (NumberFormatException numberFormatException) {
          if (string == null) {
            string = arg;
          } else {
            playerName = arg;
          }
        }
      }
    }

    if (value == 0) {
      LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
      return null;
    } else {
      Player player = cmi.getTarget(commandSender, string, this);
      Player targetPlayer = cmi.getTarget(commandSender, playerName, this);
      if (targetPlayer != null && player != null) {
        CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
        var completableFuture = cmi.getSavedInventoryManager().getInventoryById(cmiUser, value);
        completableFuture.whenComplete(
            (object, error) -> {
              CMIUser targetUser = cmi.getPlayerManager().getUser(targetPlayer);

              try {
                cmi.getSavedInventoryManager().loadInv(commandSender, targetUser, object);
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            });
        return true;
      } else {
        return null;
      }
    }
  }
}
