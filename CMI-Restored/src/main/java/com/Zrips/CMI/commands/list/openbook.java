package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.io.File;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class openbook implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Open book gui",
      args = "(cText) (playerName) (fileName.txt)",
      tab = {"ctext", "playerName"},
      regVar = -66,
      consoleVar = {2, 3, 4},
      paccess = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String playerName = null;
    String fileName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.endsWith(".txt")) {
        fileName = arg;
      } else if (string == null) {
        string = arg;
      } else if (playerName == null) {
        playerName = arg;
      }
    }

    ItemStack itemStack = null;
    Player player = cmi.getTarget(commandSender, playerName, this);
    if (player == null) {
      return null;
    }

    if (fileName != null) {
      File file = new File(cmi.getDataFolder(), "CustomText" + File.separator + fileName);
      if (!file.exists()) {
        return false;
      } else {
        CText cText = cmi.getCTextManager().getCTextFromFile(file);
        if (cText == null) {
          CMILC.info("editctext", commandSender, "noCText", "[name]", fileName);
          return null;
        } else if (cText.isRequirePermission()
            && !PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
                player, cText.getName())) {
          LC.info_NoPermission.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          itemStack = cText.convertToBook(player);
          cmi.getReflectionManager().openBook(itemStack, player);
          return true;
        }
      }
    } else {
      if (string != null) {
        CText customText = cmi.getCTextManager().getCText(string);
        if (customText == null) {
          CMILC.info("editctext", commandSender, "noCText", "[name]", string);
          return null;
        }

        itemStack = customText.convertToBook(player);
      } else {
        itemStack = CMIItemStack.getItemInMainHand(player);
        if (itemStack == null || !itemStack.getType().equals(Material.WRITTEN_BOOK)) {
          itemStack = CMIItemStack.getItemInOffHand(player);
        }

        if (itemStack == null || !itemStack.getType().equals(Material.WRITTEN_BOOK)) {
          return false;
        }
      }

      cmi.getReflectionManager().openBook(itemStack, player);
      return true;
    }
  }
}
