package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class vanish implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("vanished", "{gcp}You have vanished");
    configReader.get("unvanish", "{gcp}You became visible");
    configReader.get("targetVanished", "{gcs}[playerDisplayName] {gcp}have vanished");
    configReader.get("targetUnvanish", "{gcs}[playerDisplayName] {gcp}became visible");
    configReader.get("list", "{gcp}[place]. {gcs}[playerDisplayName] {gcp}({gcs}[offon]{gcp})");
    configReader.get("noVanished", "{gcp}There are no vanished players");
    configReader.get("noMsg", "{gcw}Cant send messages while vanished. Update this in vanishedit");
  }

  @CAnnotation(
      info = "Vanish player",
      args = "(playerName/list) (on/off) (-s)",
      tab = {"playername%%list", "on%%off", "-s"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      customAlias = "v",
      modules = "vanish",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Boolean flagSecond = null;
    boolean flag = false;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (text.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (text.equalsIgnoreCase("on")) {
        flagSecond = true;
      } else if (text.equalsIgnoreCase("off")) {
        flagSecond = false;
      } else {
        if (text.equalsIgnoreCase("list")) {
          if (!PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender, true)) {
            return null;
          }

          if (cmi.getVanishManager().getAllVanished().isEmpty()) {
            CMILC.info(this, commandSender, "noVanished");
            return true;
          }

          int countSecond = 0;

          for (UUID uuid : cmi.getVanishManager().getAllVanished()) {
            countSecond++;
            CMIUser cmiUser = cmi.getPlayerManager().getUser(uuid);
            CMILC.info(
                this,
                commandSender,
                "list",
                "[place]",
                countSecond,
                "[offon]",
                cmiUser.isOnline(),
                cmiUser);
          }

          return true;
        }

        string = text;
      }
    }

    CMIUser cMIUserSecond = cmi.getUser(commandSender, string, this);
    if (cMIUserSecond == null) {
      return null;
    }

    if (flagSecond == null) {
      cMIUserSecond.setVanished(!cMIUserSecond.isVanished());
    } else {
      cMIUserSecond.setVanished(flagSecond);
    }

    cMIUserSecond.addForDelayedSave();
    Snd snd = new Snd().setSender(cMIUserSecond).setTarget(cMIUserSecond);
    if (flag) {
      return true;
    }

    if (cMIUserSecond.getName().equalsIgnoreCase(commandSender.getName())) {
      if (cMIUserSecond.isVanished()) {
        CMILC.info(this, commandSender, "vanished", snd);
      } else {
        CMILC.info(this, commandSender, "unvanish", snd);
      }
    } else if (cMIUserSecond.isVanished()) {
      CMILC.info(this, commandSender, "targetVanished", snd);
    } else {
      CMILC.info(this, commandSender, "targetUnvanish", snd);
    }

    return true;
  }
}
