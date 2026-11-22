package com.Zrips.CMI.commands;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public interface Cmd {
  Boolean perform(CMI cmi, CommandSender commandSender, String[] strings);

  void getExtra(ConfigReader configReader);
}
