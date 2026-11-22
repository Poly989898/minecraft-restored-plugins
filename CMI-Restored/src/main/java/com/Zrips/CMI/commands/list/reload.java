package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class reload implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}CMI Configs and locale files reloaded! Took {gcs}[ms]ms");
    configReader.get("failedConfig", "{gcw}Failed to load config file! Check spelling!");
    configReader.get("failedLocale", "{gcw}Failed to load locale file! Check spelling!");
  }

  @CAnnotation(
      info = "Reloads plugins config and locale files",
      regVar = {0, 1},
      multiTab = "dialogs,holograms,tablist",
      consoleVar = {0, 1, 2},
      others = false,
      alias = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings.length > 0) {
      String string;
      switch ((string = strings[0]).hashCode()) {
        case -1552900685:
          if (string.equals("tablist")) {
            cmi.getTabListManager().loadConfig();
            return true;
          }
          break;
        case 1524316786:
          if (string.equals("holograms")) {
            cmi.getHologramManager().reload();
            return true;
          }
          break;
        case 1655024683:
          if (string.equals("dialogs")) {
            cmi.getDialogManager().load();
            return true;
          }
      }
    }

    cmi.getConfigManager().reload(commandSender);
    return true;
  }
}
