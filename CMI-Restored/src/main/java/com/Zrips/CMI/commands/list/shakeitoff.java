package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class shakeitoff implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("shaked", "{gcp}You have shaken off: {gcs}[entity]");
    configReader.get("nothing", "{gcp}Nothing to shake off");
  }

  @CAnnotation(
      priority = 47,
      info = "Dismount any entity riding you or target player",
      args = "(playerName) (-s)",
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
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    var completableFuture = cmi.getAnimationManager().removePassengers(player, string);
    boolean silent = flag;
    completableFuture.thenAccept(
        object -> {
          if (object.isEmpty()) {
            if (!silent) {
              CMILC.info(this, commandSender, "nothing");
            }
          } else {
            if (!silent) {
              StringBuilder stringBuilder = new StringBuilder();

              for (Entity entity : object) {
                if (!stringBuilder.toString().isEmpty()) {
                  stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
                }

                stringBuilder.append(new CMIEntity(entity).getCustomName());
              }

              CMILC.info(this, commandSender, "shaked", "[entity]", stringBuilder.toString());
            }
          }
        });
    return true;
  }
}
