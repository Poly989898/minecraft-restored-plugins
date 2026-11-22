package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class item implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Give item to yourself",
      args = "[itemdata/hand] (-slot:[number]) (unstack) (-s)",
      tab = {"itemname", "itemname%%hand"},
      regVar = -100,
      consoleVar = -100,
      customAlias = "i")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    return cmi.getCommandManager()
        .performCMICommand(
            commandSender, give.class, CMIArray.addFirst(strings, commandSender.getName()));
  }
}
