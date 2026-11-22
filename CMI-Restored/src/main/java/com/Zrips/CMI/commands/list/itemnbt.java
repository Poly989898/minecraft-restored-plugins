package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class itemnbt implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("jump", "{gcs}[key][type]");
    configReader.get("type", " {gcp}({gcs}[type]{gcp})");
    configReader.get("value", ": {gcs}[value]");
    configReader.get("list", "{gcp}- {gcs}[value]");
    configReader.get("noNbt", "{gcp}Item does not contain any NBT data");
  }

  @CAnnotation(
      info = "Show item NBT information",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = 1,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack != null && itemStack.getType() != Material.AIR) {
      CMINBT cminbt = new CMINBT(itemStack);
      if (cminbt.hasNBT() && !cminbt.getKeys().isEmpty()) {
        CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
        if (cmiItemStack == null) {
          return false;
        }

        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        cmi.getReflectionManager().printItemNBT(itemStack, commandSender);
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        return true;
      } else {
        CMILC.info(this, commandSender, "noNbt");
        return true;
      }
    } else {
      LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
