package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class purge implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("stopped", "{gcp}Purge stopped.");
  }

  @CAnnotation(
      priority = 300,
      info = "Cleans player data from world folder by inactive days",
      args = "(stop)",
      tab = "stop%%",
      explanation = {},
      regVar = 666,
      consoleVar = {0, 1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings.length == 1) {
      if (strings[0].equalsIgnoreCase("stop")) {
        CMILC.info(this, commandSender, "stopped");
        cmi.getPurgeManager().running = false;
        return true;
      } else {
        cmi.getCommandManager().sendUsage(commandSender, this.getClass().getSimpleName());
        return true;
      }
    } else {
      cmi.getPurgeManager().initialize();
      return true;
    }
  }
}
