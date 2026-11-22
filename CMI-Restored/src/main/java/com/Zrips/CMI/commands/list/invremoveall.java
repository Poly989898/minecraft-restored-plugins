package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.SavedInv.SavedInventories;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Map.Entry;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class invremoveall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("removed", "{gcp}All saved inventories removed ({gcs}[count]{gcp})");
  }

  @CAnnotation(
      info = "Removes all saved inventories",
      args = "[confirmed]",
      tab = "%%confirmed",
      regVar = 666,
      consoleVar = 1)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    if (strings[0].equalsIgnoreCase("confirmed")) {
      flag = true;
    }

    if (!flag) {
      return false;
    }

    var concurrentHashMap = cmi.getSavedInventoryManager().getMap();
    int value = 0;

    for (Entry entry : concurrentHashMap.entrySet()) {
      CMIUser cmiUser = ((SavedInventories) entry.getValue()).getOwner();
      if (cmiUser != null) {
        value += ((SavedInventories) entry.getValue()).getInventories().size();
        cmi.getDbManager().addForSave(cmiUser, "");
        ((SavedInventories) entry.getValue()).getInventories().clear();
      }
    }

    cmi.getDbManager().saveBatchAsync(true);
    CMILC.info(this, commandSender, "removed", "[count]", value);
    return true;
  }
}
