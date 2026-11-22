package com.Zrips.CMI.Modules.Alias;

import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandReg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Allias extends CommandReg {
  public Allias(String string, CommandAlias commandAlias) {
    super(string, commandAlias);
  }

  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String string, String[] strings) {
    return true;
  }
}
