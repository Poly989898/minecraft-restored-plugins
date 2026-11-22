package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Dialogs.CMICustomDialog;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class dialogs implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notFound", "{gcw}Dialog by this name was not found");
  }

  @CAnnotation(
      info = "Shows specified dialog",
      args = "[dialogName] (playerName) ([variableName]:value) (-s)",
      multiTab = "[dialogname] [playername] -s",
      regVar = -100,
      consoleVar = -100,
      paccess = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R4) && Version.isPaperBranch()) {
      String string = null;
      String playerName = null;
      boolean flag = false;
      HashMap<String, String> hashMap = new HashMap();
      String[] args = strings;
      int value = strings.length;

      for (int argIndex = 0; argIndex < value; argIndex++) {
        String arg = args[argIndex];
        if (arg.equalsIgnoreCase("-s")) {
          flag = true;
        } else if (arg.contains(":")) {
          String[] keyValue = arg.split(":", 2);
          if (keyValue.length == 2) {
            String dialogKey = keyValue[0];
            if (!dialogKey.startsWith("[")) {
              dialogKey = "[" + dialogKey;
            }

            if (!dialogKey.endsWith("]")) {
              dialogKey = dialogKey + "]";
            }

            hashMap.put(dialogKey, keyValue[1]);
          }
        } else if (string == null) {
          string = arg;
        } else {
          playerName = arg;
        }
      }

      Player player = cmi.getTarget(commandSender, playerName, this);
      if (player != null && string != null) {
        CMICustomDialog cmiCustomDialog = CMIDialog.getCustomDialog(player, string);
        if (cmiCustomDialog == null
            || commandSender.equals(player)
                && !cmiCustomDialog.getCondition().isConditionValid(player, cmiCustomDialog)) {
          if (!flag) {
            CMILC.info(this, commandSender, "notFound");
          }

          return null;
        } else {
          cmiCustomDialog.setVariables(hashMap);
          cmiCustomDialog.show(player);
          return true;
        }
      } else {
        return null;
      }
    } else {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
