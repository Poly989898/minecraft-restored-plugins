package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class removewarp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("remove", "{gcd}Click to remove");
    configReader.get("removed", "{gcp}Warp ({gcs}[warp]{gcp}) removed");
    configReader.get("notYours", "{gcd}This warp doesn't belong to you!");
  }

  @CAnnotation(
      info = "Remove warp",
      args = "(warpName)",
      tab = "warps",
      regVar = {0, 1},
      consoleVar = 1,
      customAlias = "delwarp",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    int value = 1;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length()));
          continue;
        } catch (Exception exception) {
        }
      }

      string = arg;
    }

    CmiWarp cmiWarp = cmi.getWarpManager().getWarp(string);
    if (cmiWarp != null && commandSender instanceof Player) {
      UUID uuid =
          commandSender instanceof Player
              ? ((Player) commandSender).getUniqueId()
              : cmi.getServerUUID();
      if (!cmiWarp.getCreator().equals(uuid)
          && !PermissionsManager.CMIPerm.command_removewarp_bypass.hasPermission(
              commandSender, false)) {
        CMILC.info(this, commandSender, "notYours");
        return null;
      }
    }

    if (cmiWarp != null) {
      cmi.getWarpManager().remove(cmiWarp);
      CMILC.info(this, commandSender, "removed", "[warp]", cmiWarp.getName());
      return true;
    }

    HashMap<String, CmiWarp> hashMap = new HashMap();
    boolean flag = false;
    UUID playerUuid =
        commandSender instanceof Player
            ? ((Player) commandSender).getUniqueId()
            : cmi.getServerUUID();
    if (commandSender instanceof Player
        && PermissionsManager.CMIPerm.command_removewarp_bypass.hasPermission(
            commandSender, false)) {
      flag = true;
    }

    for (var entry : cmi.getWarpManager().getWarps().entrySet()) {
      if (flag || ((CmiWarp) entry.getValue()).getCreator().equals(playerUuid)) {
        hashMap.put((String) entry.getKey(), (CmiWarp) entry.getValue());
      }
    }

    if (hashMap.isEmpty()) {
      CMILC.info("warp", commandSender, "noWarps");
      return true;
    }

    PageInfo pageInfo = new PageInfo(10, hashMap.size(), value);

    for (Entry warpEntry : hashMap.entrySet()) {
      if (!pageInfo.isContinue()) {
        if (pageInfo.isBreak()) {
          break;
        }

        RawMessage rawMessage = new RawMessage();

        try {
          rawMessage
              .addText(
                  CMILC.warp_list.getLocale(
                      "[pos]",
                      pageInfo.getPositionForOutput(),
                      "[warpName]",
                      ((CmiWarp) warpEntry.getValue()).getName(),
                      "[worldName]",
                      ((CmiWarp) warpEntry.getValue()).getLoc().getWorldName(),
                      "[x]",
                      ((CmiWarp) warpEntry.getValue()).getLoc().getBlockX(),
                      "[y]",
                      ((CmiWarp) warpEntry.getValue()).getLoc().getBlockY(),
                      "[z]",
                      ((CmiWarp) warpEntry.getValue()).getLoc().getBlockZ()))
              .addHover(CMILC.getIM(this, "remove"))
              .addCommand("cmi removewarp " + (String) warpEntry.getKey());
        } catch (Exception | Error value4) {
        }

        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
    return true;
  }
}
