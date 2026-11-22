package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class lockip implements Cmd {
  private Pattern pattern = null;
  private static final String IPADDRESS_PATTERN =
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

  public boolean isValid(String string) {
    if (this.pattern == null) {
      this.pattern =
          Pattern.compile(
              "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    }

    Matcher matcher = this.pattern.matcher(string);
    return matcher.matches();
  }

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("CantLogin", "{gcw}You can't login from this ip address");
    configReader.get("add", "{gcp}New ip ({gcs}[ip]{gcp}) added for {gcs}[playerDisplayName]");
    configReader.get("remove", "{gcp}Ip ({gcs}[ip]{gcp}) removed from {gcs}[playerDisplayName]");
    configReader.get("clear", "{gcp}All ip's removed from {gcs}[playerDisplayName]");
    configReader.get("ipNotFound", "&cIp not found");
    configReader.get("empty", "&cList is empty");
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[ip]");
  }

  @CAnnotation(
      priority = 240,
      info = "Prevents logging into account from different ip",
      args = "(playername) [add/remove/list/clear] [ip]",
      tab = {"playername", "add%%remove%%list%%clear"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "add";
    String playerName = null;
    String ip = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("add")) {
        string = "add";
      } else if (arg.equalsIgnoreCase("remove")) {
        string = "remove";
      } else if (arg.equalsIgnoreCase("list")) {
        string = "list";
      } else if (arg.equalsIgnoreCase("clear")) {
        string = "clear";
      } else if (this.isValid(arg)) {
        ip = arg;
      } else {
        playerName = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, playerName, this);
    if (cmiUser == null) {
      return null;
    }

    if (!cmiUser.getName().equalsIgnoreCase(commandSender.getName())
        && !PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, true, this.getClass().getSimpleName())) {
      return true;
    }

    if (!string.equals("list")) {
      if (ip == null && cmiUser.isOnline()) {
        ip = cmiUser.getPlayer().getAddress().getAddress().getHostAddress();
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
      if (string.equals("add")) {
        if (ip == null) {
          return false;
        }

        cmiUser.addLockedIps(ip);
        CMILC.info(this, commandSender, "add", "[ip]", ip, snd);
        return true;
      } else if (string.equals("remove")) {
        if (cmiUser.removeLockedIps(ip)) {
          CMILC.info(this, commandSender, "remove", "[ip]", ip, snd);
        } else {
          CMILC.info(this, commandSender, "ipNotFound");
        }

        return true;
      } else if (string.equals("clear")) {
        cmiUser.getLockedIps().clear();
        cmiUser.saveIfOffline();
        CMILC.info(this, commandSender, "clear", snd);
        return true;
      } else {
        return true;
      }
    } else {
      if (!cmiUser.getLockedIps().isEmpty()) {
        int placeNumber = 0;
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);

        for (String lockedIp : cmiUser.getLockedIps()) {
          CMILC.info(this, commandSender, "list", "[place]", ++placeNumber, "[ip]", lockedIp);
        }

        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      } else {
        CMILC.info(this, commandSender, "empty");
      }

      return true;
    }
  }
}
