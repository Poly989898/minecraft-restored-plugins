package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class entitynbt implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Check entity nbt information",
      args = "(playername) (-console)",
      multiTab = "[playername] -console",
      regVar = {0, 1, 2},
      consoleVar = 1,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-console")) {
        flag = true;
      } else if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    Entity entity = cmi.getUtilManager().getTargetEntity(player);
    if (entity == null) {
      LC.info_lookAtEntity.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (flag) {
      LC.info_Spliter.sendMessage(Bukkit.getConsoleSender(), new Object[0]);
      cmi.getReflectionManager().printEntityNBT(entity, Bukkit.getConsoleSender());
      LC.info_Spliter.sendMessage(Bukkit.getConsoleSender(), new Object[0]);
    } else {
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      cmi.getReflectionManager().printEntityNBT(entity, commandSender);
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    }

    return true;
  }
}
