package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class dback implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noinfo", "{gcw}There is no saved location to return to!");
    configReader.get("feedback", "{gcp}Returned to last saved location ([world],[x],[y],[z])!");
  }

  @CAnnotation(
      info = "Returns to death location",
      args = "(playerName) (-s)",
      tab = {"playername", "-s%%"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    } else {
      Location location = cmiUser.getDeathLoc();
      if (location == null || location.getWorld() == null) {
        CMILC.info(this, commandSender, "noinfo");
        return null;
      } else if (cmi.getTeleportManager()
          .getDBackBlackList()
          .contains(location.getWorld().getName().toLowerCase())) {
        LC.info_CantTeleportWorld.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
        boolean silent = flag;
        cmi.getTeleportHandler()
            .teleportPlayer(cmiUser.getPlayer(), location, false, CMITeleportType.DBack)
            .thenAccept(
                cmiTeleportFeedback -> {
                  if (!cmiTeleportFeedback.isSuccess()) {
                    cmiTeleportFeedback.showFeedbackMessage(commandSender, cmiUser.getPlayer());
                  } else {
                    if (!silent) {
                      CMILC.info(
                          this, commandSender, "feedback", snd, cmiTeleportFeedback.getLocation());
                    }
                  }
                });
        return true;
      }
    }
  }
}
