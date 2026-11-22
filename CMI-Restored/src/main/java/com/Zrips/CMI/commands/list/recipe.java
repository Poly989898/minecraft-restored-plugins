package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Recipes.CMIRecipeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class recipe implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("type", "{gcs}[type]");
    CMIRecipeType[] cmiRecipeTypes;
    int value = (cmiRecipeTypes = CMIRecipeType.values()).length;

    for (int index = 0; index < value; index++) {
      CMIRecipeType cmiRecipeType = cmiRecipeTypes[index];
      configReader.get(
          "types." + cmiRecipeType.toString(),
          CMIText.firstToUpperCase(cmiRecipeType.toString().replace("_", " ")));
    }

    configReader.get("page", " {gcs}[current]{gcp}/{gcs}[total]");
    configReader.get("noRecipes", "{gcw}Cant find any recipes for this item");
  }

  @CAnnotation(
      info = "Check item recipe",
      args = "(itemName) (-c)",
      tab = {"itemname", "playername", "-c"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIItemStack cmiItemStack = null;
    int value = 1;
    boolean flag = false;
    String string = null;
    String playerName = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length(), arg.length()));
          continue;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }

      if (arg.equalsIgnoreCase("-c")) {
        flag = true;
      } else if (playerName == null && CMIUser.getOnlinePlayer(arg) != null) {
        playerName = arg;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, playerName, this);
    if (player == null) {
      return null;
    }

    if (value < 1) {
      value = 1;
    }

    if (flag
        && !PermissionsManager.CMIPerm.command_recipe_custom.hasPermission(commandSender, true)) {
      flag = false;
    }

    if (string != null) {
      cmiItemStack = CMIItemStack.deserialize(string);
    } else {
      if (flag) {
        return cmi.getRecipeManager().openOnlyCustomRecipeUI(player, value);
      }

      cmiItemStack = new CMIItemStack(CMIItemStack.getItemInMainHand(player));
    }

    if (cmiItemStack == null) {
      LC.info_noItem.sendMessage(player, new Object[0]);
      return null;
    } else if (cmiItemStack.getCMIType().isAir()) {
      LC.info_nothingInHand.sendMessage(player, new Object[0]);
      return null;
    } else {
      return cmi.getRecipeManager().openRecipeUI(player, cmiItemStack, value, flag);
    }
  }
}
