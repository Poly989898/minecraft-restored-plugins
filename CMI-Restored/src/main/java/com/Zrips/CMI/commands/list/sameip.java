package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class sameip implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]. {gcp}");
  }

  @CAnnotation(
      info = "List players logged in from same ip",
      regVar = 0,
      consoleVar = 0,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    HashMap<String, Set> hashMap = new HashMap();

    for (Player player : Bukkit.getOnlinePlayers()) {
      String string = player.getAddress().getAddress().getHostAddress();
      Set set = (Set) hashMap.get(string);
      if (set == null) {
        set = new HashSet();
        hashMap.put(string, set);
      }

      set.add(player);
    }

    int value = 0;

    for (Entry entry : hashMap.entrySet()) {
      if (((Set) entry.getValue()).size() >= 2) {
        value++;
      }
    }

    if (value > 0) {
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    }

    value = 0;

    for (Entry ipEntry : hashMap.entrySet()) {
      if (((Set) ipEntry.getValue()).size() >= 2) {
        value++;
        String separator = "";
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM(this, "list", "[place]", value));

        for (Player ipPlayer : (Set<Player>) ipEntry.getValue()) {
          CMIUser cmiUser = cmi.getPlayerManager().getUser(ipPlayer);
          if (!separator.isEmpty()) {
            rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          rawMessage
              .addText("&r" + cmiUser.getDisplayName())
              .addHover(cmiUser.getName())
              .addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + sameip.class.getSimpleName()
                      + " "
                      + cmiUser.getName());
          separator = "s";
        }

        rawMessage.show(commandSender);
      }
    }

    if (value == 0) {
      LC.info_nothingToShow.sendMessage(commandSender, new Object[0]);
    }

    return true;
  }
}
