package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Sheduler.Schedule;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class schedule implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Trigger schedule",
      args = "[scheduleName] (-updatetimer)",
      tab = {"scheduleName", "-updatetimer"},
      regVar = {1, 2},
      consoleVar = {1, 2})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!CMIModule.schedule.isEnabled()) {
      LC.info_ModuleNotEnabled.sendMessage(commandSender, new Object[0]);
    }

    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equals("-updatetimer")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Schedule schedule = cmi.getSchedulerManager().getSchedule(string, true);
    if (schedule == null) {
      CMILC.info_noSchedule.sendMessage(commandSender);
      return null;
    }

    if (flag) {
      schedule.safePerform();
    } else {
      schedule.perform();
    }

    return true;
  }
}
