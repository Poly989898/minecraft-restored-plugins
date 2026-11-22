package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class back implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noinfo", "{gcw}There is no saved location to return to!");
    configReader.get(
        "feedback",
        "{gcp}Returned to last saved location"
            + " ({gcs}[worldName]{gcp},{gcs}[x]{gcp},{gcs}[y]{gcp},{gcs}[z]{gcp})!");
  }

  @CAnnotation(
      info = "Teleports back to last saved location",
      args = "(playerName) (-s)",
      tab = {"playername", "-s%%"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      customAlias = "cback",
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
    }

    if (cmi.getBungeeCordManager().isBackToPreviousServer()
        && cmi.getBungeeCordManager().isBungeeCord()
        && cmi.getBungeeCordManager().isCMIBPresent()
        && cmiUser.getBungeeBackLocation() != null
        && cmiUser.getBungeeBackLocation().contains(";")) {
      String bungeeLocationText = cmiUser.getBungeeBackLocation();
      String bungeeServer = bungeeLocationText.split(";", 2)[0];
      CMILocation cmiLocation = CMILocation.fromString(bungeeLocationText.split(";", 2)[1]);
      BungeeCordServer bungeeCordServer = cmi.getBungeeCordManager().getServer(bungeeServer);
      if (bungeeCordServer != null
          && (cmiLocation == null
              || cmiLocation.getWorldName() == null
              || !cmi.getTeleportManager()
                  .getBackBlackList()
                  .contains(cmiLocation.getWorldName().toLowerCase()))) {
        cmi.getBungeeCordManager().connectToServer(cmiUser.getPlayer(false), bungeeServer);
        if (cmiLocation != null && cmiLocation.getWorldName() != null) {
          cmi.getBungeeCordManager()
              .teleporToLocation(
                  bungeeServer,
                  cmiUser.getUniqueId(),
                  bungeeLocationText.split(";", 2)[1],
                  cmiUser.getLocation());
        } else {
          CMIMessages.consoleMessage("Generic sent to server");
        }

        return true;
      } else {
        return this.localTeleport(cmiUser, commandSender, flag);
      }
    } else {
      return this.localTeleport(cmiUser, commandSender, flag);
    }
  }

  private Boolean localTeleport(CMIUser cmiUser, CommandSender commandSender, boolean flag) {
    Location location = cmiUser.getLastTeleportLocation();
    if (location != null && location.getWorld() != null) {
      if (CMI.getInstance()
          .getTeleportManager()
          .getBackBlackList()
          .contains(location.getWorld().getName().toLowerCase())) {
        LC.info_CantTeleportWorld.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
        CMI.getInstance()
            .getTeleportHandler()
            .teleportPlayer(cmiUser.getPlayer(), location, CMITeleportType.Back)
            .thenAccept(
                cmiTeleportFeedback -> {
                  if (!cmiTeleportFeedback.isSuccess()) {
                    cmiTeleportFeedback.showFeedbackMessage(commandSender, cmiUser.getPlayer());
                  } else {
                    if (!flag) {
                      CMILC.info(this, commandSender, "feedback", location, snd);
                    }
                  }
                });
        return true;
      }
    } else {
      CMILC.info(this, commandSender, "noinfo");
      return null;
    }
  }
}
