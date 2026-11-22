package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class hologrampages implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Change hologram page",
      args = "[holoName] (playerName) (next/prev/[pageNumber])",
      tab = {"holograms", "playername", "next%%prev%%"},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    hologrampages.pageAction action = null;
    Integer integer = null;
    CMIHologram cmiHologram = null;
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (cmiHologram == null) {
        cmiHologram = cmi.getHologramManager().getByName(arg);
      } else {
        action = hologrampages.pageAction.getByName(arg);
        if (action == null) {
          CMIUser cmiUser = cmi.getPlayerManager().getUser(arg);
          if (cmiUser != null && cmiUser.isOnline()) {
            string = arg;
          } else {
            try {
              integer = Integer.parseInt(arg);
              action = hologrampages.pageAction.exact;
            } catch (Exception exception) {
              string = arg;
            }
          }
        }
      }
    }

    if (cmiHologram == null) {
      return false;
    }

    CMIUser targetUser = cmi.getUser(commandSender, string, this);
    if (targetUser == null) {
      return null;
    }

    if (action == null) {
      action = hologrampages.pageAction.next;
    }

    switch (action) {
      case next:
        cmiHologram.goToNextPage(targetUser.getUniqueId());
        break;
      case prev:
        cmiHologram.goToPrevPage(targetUser.getUniqueId());
        break;
      case exact:
        if (integer == null) {
          return false;
        }

        cmiHologram.goToPage(targetUser.getUniqueId(), integer);
    }

    if (targetUser.isOnline()) {
      cmiHologram.requestUpdate(targetUser.getPlayer());
    }

    return null;
  }

  private enum pageAction {
    next,
    prev,
    exact;

    public static hologrampages.pageAction getByName(String string) {
      hologrampages.pageAction[] pageActions;
      int value = (pageActions = values()).length;

      for (int index = 0; index < value; index++) {
        hologrampages.pageAction candidate = pageActions[index];
        if (candidate.name().equalsIgnoreCase(string)) {
          return candidate;
        }
      }

      return null;
    }
  }
}
