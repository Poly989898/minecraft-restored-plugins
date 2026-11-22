package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.CMIBungeeType;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;

public class skin implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("changed", "{gcp}Changed skin to {gcs}[skinOwner] {gcp}look");
    configReader.get("failed", "{gcp}Failed to apply skin");
    configReader.get("resetSteve", "{gcp}Removed skin");
    configReader.get("reset", "{gcp}Applied default skin");
    configReader.get("update", "{gcp}Updated skin");
  }

  @CAnnotation(
      info = "Changes players skin",
      args = "[skinName/off/update] (playerName) (-s)",
      tab = {"allPlayerName", "playername", "-s"},
      explanation = {},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true,
      modules = "skin")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmi.getBungeeCordManager().isBungeeCord()
        && cmi.getBungeeCordManager().getNetworkType().equals(CMIBungeeType.BungeeCord)) {
      LC.info_bungeeNoGo.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    String playerName = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("update")) {
        string = arg;
      } else if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (string == null) {
        string = arg;
      } else {
        playerName = arg;
      }
    }

    if (string == null) {
      return false;
    }

    String skinOption = string;
    CMIUser cmiUser = cmi.getUser(commandSender, playerName, this);
    if (cmiUser == null) {
      return null;
    }

    if (skinOption.equalsIgnoreCase("off")) {
      if (cmi.getSkinManager().isSteveOnOff()) {
        if (cmiUser.isOnline()) {
          cmi.getNMS().clearSkin(cmiUser.getPlayer());
        }

        cmiUser.setSkin(null);
        if (cmiUser.isOnline()) {
          cmiUser.getPlayer().updateInventory();
        }

        if (!flag) {
          CMILC.info(this, commandSender, "resetSteve");
        }
      } else {
        if (cmiUser.isOnline()) {
          cmi.getSkinManager().applySkinAsync(cmiUser.getPlayer(), cmiUser.getName());
        }

        cmiUser.setSkin(cmiUser.getName());
        if (!flag) {
          CMILC.info(this, commandSender, "reset");
        }
      }

      cmiUser.addForDelayedSave();
      return true;
    } else if (skinOption.equalsIgnoreCase("update")) {
      String currentSkin = cmiUser.getSkin();
      cmi.getSkinManager().removeSkinFromCache(currentSkin);
      CMILib.getInstance().getSkinManager().removeSkinFromCache(cmiUser.getUniqueId());
      CMILib.getInstance().getSkinManager().removeSkinFromCache(cmiUser.getName());
      if (cmiUser.isOnline()) {
        cmi.getSkinManager().applySkinAsync(cmiUser.getPlayer(), cmiUser.getSkin());
      }

      if (!flag) {
        CMILC.info(this, commandSender, "update");
      }

      return true;
    } else {
      if (cmi.getSkinManager().isRequireSpecificPerm()
          && !PermissionsManager.CMIPerm.command_skin_perm_$1.hasPermission(
              commandSender, true, skinOption.toLowerCase())) {
        return null;
      }

      skin skin = this;
      boolean silent = flag;
      var completableFuture =
          CMI.getInstance().getSkinManager().applySkinAsync(cmiUser.getPlayer(), skinOption);
      completableFuture.whenComplete(
          (success, error) -> {
            if (success) {
              if (!silent) {
                CMILC.info(skin, commandSender, "changed", "[skinOwner]", skinOption);
              }

              cmiUser.setSkin(skinOption);
            } else if (!silent) {
              CMILC.info(skin, commandSender, "failed");
            }
          });
      return true;
    }
  }
}
