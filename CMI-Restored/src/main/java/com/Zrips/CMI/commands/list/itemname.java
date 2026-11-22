package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class itemname implements Cmd {
  public static final String indicator = "ModifiedName";

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have renamed item to {gcs}[itemname] {gcp}for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("noitem", "{gcd}Not holding any item in hand.");
    configReader.get("removed", "{gcp}Item name was removed for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("blocked", "{gcp}This item is blocked from renaming");
    configReader.get(
        "maxLength", "{gcw}New value is to long. Max length {gcs}[length] {gcw}characters");
  }

  @CAnnotation(
      info = "Rename items",
      args = "(-p:[playerName]) [remove/your new item name]",
      multiTab = "[currentItemName]",
      regVar = -100,
      consoleVar = -100,
      alias = true,
      customAlias = {"!iname", "rename"},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    String playerName = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.startsWith("-p:")) {
        playerName = arg.replace("-p:", "");
      } else {
        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + arg;
      }
    }

    Player player = cmi.getTarget(commandSender, playerName, this);
    if (player == null) {
      return null;
    }

    if (string.isEmpty()) {
      return false;
    }

    if (string.equalsIgnoreCase("remove")) {
      flag = true;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
      CMILC.info(this, commandSender, "noitem");
      return null;
    }

    if (cmi.getUtilManager().blockedItemFromRenaming(commandSender, itemStack, itemStack, string)) {
      CMILC.info(this, commandSender, "blocked");
      return null;
    }

    CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
    string = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, string);
    if (Config.ItemRenamingMaxLength > 0
        && CMIChatColor.stripColor(string).length() > Config.ItemRenamingMaxLength) {
      CMILC.info(this, commandSender, "maxLength", "[length]", Config.ItemRenamingMaxLength);
      return null;
    }

    string =
        CMITextCleaner.cleanFromColorCodes(commandSender, string, CMIColorTypes.itemname, true);
    if (CMIChatColor.getColor(string) == null) {
      string = "&f" + string;
    }

    cmiItemStack.setDisplayName(flag ? null : string);
    if (cmi.getConfigManager().isItemNameMarkChanged()) {
      cmiItemStack.setNbt("ModifiedName", true);
    }

    cmi.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (!string.equalsIgnoreCase("remove")) {
      CMILC.info(this, commandSender, "feedback", "[itemname]", string, snd);
    } else {
      CMILC.info(this, commandSender, "removed", snd);
    }

    return true;
  }
}
