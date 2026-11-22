package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpall implements Cmd {
  private static CMITask sched = null;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have been teleported ({gcs}[world],[x],[y],[z]{gcp}) to {gcs}[playerDisplayName]"
            + " {gcp}location.");
  }

  @CAnnotation(
      info = "Teleports all online players to location",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    if (strings.length > 0) {
      string = strings[0];
    }

    Player player = cmi.getTarget(commandSender, string, this, false);
    if (player == null) {
      return null;
    }

    Location location = player.getLocation();
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (sched != null) {
      sched.cancel();
    }

    ArrayList arrayList = new ArrayList(Bukkit.getOnlinePlayers());
    sched =
        CMIScheduler.scheduleSyncRepeatingTask(
            cmi,
            () -> {
              if (arrayList.isEmpty()) {
                sched.cancel();
                sched = null;
              } else {
                Player playerToTeleport = (Player) arrayList.remove(0);
                if (playerToTeleport.isOnline()
                    && !playerToTeleport.getName().equalsIgnoreCase(commandSender.getName())) {
                  cmi.getTeleportHandler()
                      .teleportPlayer(playerToTeleport, location, CMITeleportType.TpAll)
                      .thenAccept(
                          cmiTeleportFeedback -> {
                            if (cmiTeleportFeedback.isSuccess()) {
                              CMILC.info(tpall.class, playerToTeleport, "feedback", snd, location);
                            }
                          });
                }
              }
            },
            0L,
            2L);
    return true;
  }
}
