package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class checkaccount implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("place", "{gcs}[place]{gcp}. {gcs}");
    configReader.get("ip", "{gcp}[ip]{gcs}: ");
    configReader.get("name", "{gcs}[name]{gcp}({gcs}[times]{gcp})");
    configReader.get("nonIpList", "{gcp}Accounts: {gcs}");
    configReader.get("nonIpName", "{gcs}[name]");
    configReader.get("click", "{gcp}Click to check {gcs}[name] {gcp}account");
    configReader.get("noData", "{gcw}There is no records for this user");
  }

  @CAnnotation(
      info = "Search for a players other accounts",
      args = "(playerName/ip)",
      tab = "playername",
      explanation = "Extra permission: cmi.command.checkaccount.showip",
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    HashMap<String, Integer> hashMap = new HashMap();
    if (strings.length == 1 && cmi.getIpManager().ipIsOk(strings[0])) {
      hashMap.put(strings[0], 0);
    } else {
      String string = null;
      if (strings.length == 1) {
        string = strings[0];
      }

      CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
      if (cmiUser == null) {
        return true;
      }

      if (Config.checkAccountExlusions.contains(cmiUser.getName().toLowerCase())) {
        Snd snd = new Snd();
        snd.setSource(cmiUser);
        snd.setTarget(cmiUser);
        LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[] {snd});
        return true;
      }

      hashMap = cmiUser.getIps();
    }

    int value = 0;
    HashSet<CMIUser> hashSet = new HashSet();
    boolean flag = false;
    if (!PermissionsManager.CMIPerm.command_checkaccount_showip.hasPermission(commandSender)) {
      flag = true;
    }

    boolean flagSecond = false;
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (Entry entry : hashMap.entrySet()) {
      if (!((String) entry.getKey()).equalsIgnoreCase("127.0.0.1")) {
        value++;
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM(this, "place", "[place]", value));
        String text = (String) entry.getKey();
        if (!Config.checkAccountExlusions.contains(text)) {
          rawMessage
              .addText(CMILC.getIM(this, "ip", "[ip]", text))
              .addHover(CMILC.getIM(this, "click", "[name]", text));
          rawMessage.addCommand("cmi checkaccount " + text).addInsertion(text);
          var set = cmi.getIpManager().getUsers(text);
          if (set != null) {
            for (CMIUser cMIUserSecond : set) {
              if (cMIUserSecond.getName() != null) {
                if (flag) {
                  hashSet.add(cMIUserSecond);
                } else {
                  Integer integer = cMIUserSecond.getIps().get(text);
                  if (integer != null
                      && !Config.checkAccountExlusions.contains(
                          cMIUserSecond.getName().toLowerCase())) {
                    rawMessage
                        .addText(
                            CMILC.getIM(
                                    this,
                                    "name",
                                    cMIUserSecond,
                                    "[name]",
                                    cMIUserSecond.getName(),
                                    "[times]",
                                    integer)
                                + " ")
                        .addHover(CMILC.getIM(this, "click", cMIUserSecond))
                        .addCommand("cmi checkaccount " + cMIUserSecond.getName())
                        .addInsertion(cMIUserSecond.getName());
                  }
                }
              }
            }
          }

          if (!flag) {
            rawMessage.show(commandSender);
            flagSecond = true;
          }
        }
      }
    }

    if (flag) {
      String textSecond = "";
      RawMessage rawMessageSecond = new RawMessage();
      rawMessageSecond.addText(CMILC.getIM(this, "nonIpList"));

      for (CMIUser cMIUserThird : hashSet) {
        if (!Config.checkAccountExlusions.contains(cMIUserThird.getName().toLowerCase())) {
          if (!textSecond.isEmpty()) {
            textSecond = textSecond + LC.info_ListSpliter.getLocale(new Object[0]);
          }

          textSecond = textSecond + cMIUserThird.getName();
          rawMessageSecond
              .addText(CMILC.getIM(this, "nonIpName", "[name]", cMIUserThird.getName() + " "))
              .addHover(CMILC.getIM(this, "click", cMIUserThird))
              .addCommand("cmi checkaccount " + cMIUserThird.getName());
          flagSecond = true;
        }
      }

      rawMessageSecond.show(commandSender);
    }

    if (!flagSecond) {
      LC.info_NoInformation.sendMessage(commandSender, new Object[0]);
    }

    return true;
  }
}
