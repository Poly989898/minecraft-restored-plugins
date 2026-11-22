package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class unbreakable implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You made item [state] {gcp}for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("breakable", "{gcd}breakable");
    configReader.get("unbreakable", "{gcu}unbreakable");
  }

  @CAnnotation(
      info = "Makes item unbreakable",
      args = "(playerName) (true/false) (-o)",
      tab = {"playerName%%true%%false", "true%%false", "-o"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = true;
    boolean mainHand = true;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("true")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("false")) {
        flag = false;
      } else if (arg.equalsIgnoreCase("-o")) {
        mainHand = false;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    ItemStack itemStack =
        mainHand ? CMIItemStack.getItemInMainHand(player) : CMIItemStack.getItemInOffHand(player);
    if (itemStack != null && itemStack.getType() != Material.AIR) {
      itemStack = Util.setUnbreakable(itemStack, flag);
      if (mainHand) {
        CMIItemStack.setItemInMainHand(player, itemStack);
      } else {
        CMIItemStack.setItemInOffHand(player, itemStack);
      }

      cmi.save(player);
      if (player.isOnline()) {
        player.updateInventory();
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      CMILC.info(
          this,
          commandSender,
          "feedback",
          snd,
          "[state]",
          flag ? CMILC.getIM(this, "unbreakable") : CMILC.getIM(this, "breakable"));
      return true;
    } else {
      CMILC.info(this, commandSender, "noitem");
      return false;
    }
  }
}
