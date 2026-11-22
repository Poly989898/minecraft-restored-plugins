package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class patrol implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noPatrol", "{gcp}Currently there is no players you can patrol");
    configReader.get("Patroling", "{gcp}Currently patroling: {gcs}[playerDisplayName]");
    configReader.get("lastPatrol", "{gcp}Last time patroled {gcs}[time]{gcp}ago");
    configReader.get("notes", "{gcp}Has {gcs}[amount] {gcp}notes");
  }

  @CAnnotation(info = "Patrol", regVar = 0, consoleVar = 666, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIUser cmiUser = cmi.getPatrolManager().getNextUser();
    if (cmiUser == null) {
      CMILC.info(this, commandSender, "noPatrol");
      return null;
    }

    if (!cmiUser.isOnline()) {
      return false;
    }

    Player player = cmiUser.getPlayer();
    if (player != null && player.isOnline()) {
      Player patroller = (Player) commandSender;
      Location location = player.getLocation().clone();
      cmi.getTeleportHandler()
          .teleportPlayer(patroller, location, CMITeleportType.Patrol)
          .thenAccept(
              cmiTeleportFeedback -> {
                if (cmiTeleportFeedback.isSuccess()) {
                  if (player.isFlying() && patroller.getAllowFlight()) {
                    patroller.setFlying(true);
                  }
                }
              });
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      CMILC.info(this, commandSender, "Patroling", snd);
      if (cmiUser.getLastPatrol() > 0L) {
        CMILC.info(
            this,
            commandSender,
            "lastPatrol",
            snd,
            "[time]",
            CMITimeManager.to24hourShort(System.currentTimeMillis() - cmiUser.getLastPatrol()));
      }

      if (!cmiUser.getNotes().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(CMILC.getIM(this, "notes", snd, "[amount]", cmiUser.getNotes().size()))
            .addHover(LC.info_Click.getLocale(new Object[0]))
            .addCommand(
                CommandsHandler.getLabel()
                    + " "
                    + note.class.getSimpleName()
                    + " "
                    + player.getName()
                    + " list");
        rawMessage.show(patroller);
      }

      cmiUser.setPatroled();
      return true;
    } else {
      return false;
    }
  }
}
