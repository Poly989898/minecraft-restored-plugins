package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class removehome implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("remove", "{gcd}Click to remove");
    configReader.get("removed", "{gcp}Home ({gcs}[home]{gcp}) removed");
    configReader.get("list", "{gcs}[home]");
  }

  @CAnnotation(
      priority = 115,
      info = "Remove home",
      args = "(homeName) (playerName)",
      tab = {"homes", "playername"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = 2,
      customAlias = "delhome",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    if (strings.length > 0) {
      string = strings[0];
    }

    String targetName = null;
    if (strings.length > 1) {
      targetName = strings[1];
    }

    CMIUser cmiUser = cmi.getUser(commandSender, targetName, this);
    if (cmiUser == null) {
      return null;
    }

    CmiHome cmiHome = cmiUser.getHome(string);
    if (cmiHome == null) {
      if (!cmiUser.getHomes().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM("home", "list"));
        String pluralSuffix = "";

        for (var entry : cmiUser.getHomes().entrySet()) {
          if (!pluralSuffix.isEmpty()) {
            rawMessage.addText(LC.info_ListSpliter);
          }

          rawMessage
              .addText(CMILC.getIM(this, "list", "[home]", ((CmiHome) entry.getValue()).getName()))
              .addHover(CMILC.getIM(this, "remove"))
              .addCommand("cmi removehome " + (String) entry.getKey() + " " + cmiUser.getName());
          pluralSuffix = "s";
        }

        rawMessage.show(commandSender);
        return true;
      } else {
        CMILC.info("home", commandSender, "noHomes");
        return true;
      }
    } else {
      cmiUser.removeHome(cmiHome);
      cmiUser.saveIfOffline();
      CMILC.info(this, commandSender, "removed", "[home]", cmiHome.getName());
      return true;
    }
  }
}
