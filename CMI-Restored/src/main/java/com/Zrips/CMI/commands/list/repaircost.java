package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIItemRepair;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class repaircost implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Repair cost: {gcs}[amount]");
    configReader.get("noNeed", "{gcp}No need to repair");
  }

  @CAnnotation(
      info = "Check items repair cost",
      args = "(hand/offhand/armor/all) (playerName)",
      multiTab = "hand,offhand,armor,all [playername]",
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    repair.repairType repairType = repair.repairType.hand;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      repair.repairType parsedType = repair.repairType.get(arg);
      if (parsedType != null) {
        repairType = parsedType;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    var list = repairType.getItems(player);
    double number = CMIItemRepair.getRepairCost(list);
    CMILC.info(this, commandSender, "feedback", "[amount]", cmi.getEconomyManager().format(number));
    return true;
  }
}
