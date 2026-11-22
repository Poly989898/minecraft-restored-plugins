package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class warpgroups implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcp}[number]. {gcs}[group] {gcp}({gcs}[amount]{gcp})");
  }

  @CAnnotation(
      info = "Lists warp groups",
      args = "",
      tab = {},
      explanation = {},
      regVar = 0,
      consoleVar = 0,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    HashMap<String, Integer> hashMap = new HashMap();

    for (var entry : cmi.getWarpManager().getWarps().entrySet()) {
      String string =
          ((CmiWarp) entry.getValue()).getGroup() == null
              ? "NULL"
              : ((CmiWarp) entry.getValue()).getGroup();
      Integer integer = (Integer) hashMap.get(string);
      if (integer == null) {
        integer = 0;
      }

      integer = integer + 1;
      hashMap.put(string, integer);
    }

    int value = 0;
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (Entry groupEntry : hashMap.entrySet()) {
      value++;
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(
          CMILC.getIM(
              this,
              "list",
              "[number]",
              value,
              "[group]",
              groupEntry.getKey() == "NULL" ? "N/A" : groupEntry.getKey(),
              "[amount]",
              groupEntry.getValue()));
      rawMessage.addHover(LC.info_Click);
      rawMessage.addCommand(
          CommandsHandler.getLabel()
              + " "
              + warp.class.getSimpleName()
              + (((String) groupEntry.getKey()).equalsIgnoreCase("NULL")
                  ? ""
                  : " -g:" + (String) groupEntry.getKey()));
      rawMessage.show(commandSender);
    }

    return true;
  }
}
