package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIIpUnBanEvent;
import com.Zrips.CMI.events.CMIPlayerUnBanEvent;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class unban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("unBaned", "!broadcast!{gcs}[playerDisplayName] {gcp}unbanned");
    configReader.get("unBanedSilent", "&7[&8Silent&7]{gcs}[playerDisplayName] {gcp}unbanned");
    configReader.get("notBanned", "{gcs}[playerDisplayName] {gcp}not banned");
  }

  @CAnnotation(
      info = "Unban player or ip",
      args = "[playerName/ip] (-s)",
      tab = "playername",
      explanation = {},
      regVar = {1, 2},
      consoleVar = {1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (string == null) {
        string = text;
      } else if (text.equalsIgnoreCase("-s")) {
        flag = true;
      }
    }

    String textSecond = null;
    if (strings.length >= 1 && cmi.getIpManager().ipIsOk(string)) {
      textSecond = string;
    }

    CMIUser cmiUser = CMIUser.getUser(string);
    if (cmiUser == null && textSecond == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[] {string});
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (textSecond != null) {
      String textThird = textSecond;
      if (textThird != null && textThird.contains(".")) {
        StringBuilder stringBuilder = new StringBuilder();
        int countSecond = 0;
        String[] partsSecond;
        int countThird = (partsSecond = textThird.split("\\.")).length;

        for (int countFourth = 0; countFourth < countThird; countFourth++) {
          String textFourth = partsSecond[countFourth];
          countSecond++;
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(".");
          }

          if (countSecond < 3) {
            stringBuilder.append(textFourth);
          } else {
            for (int countFifth = 0; countFifth < textFourth.length(); countFifth++) {
              stringBuilder.append(CMILC.getIM("ipban", "replacer"));
            }
          }
        }

        textThird = stringBuilder.toString();
      }

      snd.setTargetName(textThird);
    }

    if (cmiUser != null && !cmiUser.isBanned()) {
      if (cmiUser.getName() == null) {
        LC.info_NoInformation.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (CMIBanRecords.getBanEntry(cmiUser) == null) {
        cmiUser.unBan();
        CMILC.info(this, commandSender, "notBanned", snd);
        return true;
      }
    }

    if (cmiUser == null && textSecond != null) {
      BanEntry banEntry = CMIBanRecords.getBanEntryByIP(textSecond);
      if (banEntry == null) {
        CMILC.info(this, commandSender, "notBanned", snd);
        return true;
      }
    }

    if (cmiUser != null) {
      cmiUser.unBan();
      CMIPlayerUnBanEvent cmiPlayerUnBanEvent =
          new CMIPlayerUnBanEvent(commandSender, cmiUser.getPlayer());
      Bukkit.getServer().getPluginManager().callEvent(cmiPlayerUnBanEvent);
    } else if (textSecond != null) {
      CMIBanRecords.pardonByIP(textSecond);
      CMIIpUnBanEvent cmiIpUnBanEvent = new CMIIpUnBanEvent(commandSender, textSecond);
      Bukkit.getServer().getPluginManager().callEvent(cmiIpUnBanEvent);
    }

    if (!flag) {
      CMILC.info(this, commandSender, "unBaned", snd);
    } else {
      CMILC.info(this, commandSender, "unBanedSilent", snd);
    }

    return true;
  }
}
