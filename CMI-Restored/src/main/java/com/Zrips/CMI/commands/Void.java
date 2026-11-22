package com.Zrips.CMI.commands;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class Void implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    return true;
  }
}
