package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.events.CMIPlayerWarpEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class warp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcp}Warps: {gcs}");
    configReader.get("click", "{gcp}Click to teleport to {gcs}[warp]");
    configReader.get("teleported", "{gcp}Teleported to warp: {gcs}[warp]");
    configReader.get("noWarps", "{gcw}There is no saved warps");
    configReader.get("autoLoreName", "{gcs}[warpName]");
    configReader.get(
        "autoLoreFormat", Arrays.asList("{gcs}[worldName]", "{gcs}[x]:[y]:[z]", "{gcs}[creator]"));
    configReader.get("creator", "{gcp}Creator: {gcs}[creator]");
    configReader.get("noWarpByName", "{gcw}There is no warps by this name");
    configReader.get("noAccess", "{gcw}No access to this warp");
    configReader.get("guiTitle", "{gcp}Warps");
    configReader.get("pageTitles", Arrays.asList("&2Warps 1", "&8Warps 2", "&0Warps 3"));
  }

  @CAnnotation(
      info = "Teleports to warp location",
      args = "(warpName) (playerName) (-s) (-g:[groupName]) (-p:[pageNumber])",
      tab = {"warps", "playername"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true,
      customAlias = "warps")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String targetName = null;
    String warpGroup = null;
    boolean flag = false;
    Integer integer = null;
    boolean silent = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-own")) {
        silent = true;
      } else if (arg.startsWith("-g:")) {
        warpGroup = arg.substring("-g:".length());
      } else if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else {
        if (arg.startsWith("-p:")) {
          try {
            integer = Integer.parseInt(arg.substring("-p:".length()));
            continue;
          } catch (Exception exception) {
          }
        }

        if (string == null) {
          CmiWarp cmiWarp = cmi.getWarpManager().getWarp(arg);
          if (cmiWarp != null) {
            string = arg;
            continue;
          }
        }

        if (targetName == null) {
          Player player = CMIUser.getOnlinePlayer(commandSender, arg, true);
          if (player != null && player.getName().equalsIgnoreCase(arg)) {
            targetName = arg;
            continue;
          }
        }

        if (string == null) {
          string = arg;
        } else {
          targetName = arg;
        }
      }
    }

    Player target = cmi.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }

    CmiWarp warp = cmi.getWarpManager().getWarp(target, string);
    if (!(commandSender instanceof Player)) {
      warp = cmi.getWarpManager().getWarp(string);
    }

    if (commandSender instanceof Player
        && warp == null
        && !commandSender.getName().equals(target.getName())) {
      warp = cmi.getWarpManager().getWarp((Player) commandSender, string);
    }

    if (warp == null && string != null) {
      CMILC.info(this, commandSender, "noWarpByName");
      return null;
    }

    if (warp != null) {
      if (cmi.getUtilManager().haveBlackListedItems(target, TeleportManager.TpAction.warp)) {
        return null;
      }

      if (warp.getLoc() != null && warp.getLoc().getWorld() != null) {
        CMIPlayerWarpEvent cmiPlayerWarpEvent = new CMIPlayerWarpEvent(target, warp, commandSender);
        Bukkit.getPluginManager().callEvent(cmiPlayerWarpEvent);
        if (cmiPlayerWarpEvent.isCancelled()) {
          return null;
        }

        warp = cmiPlayerWarpEvent.getWarp();
        if (warp == null) {
          return null;
        }

        boolean silentCopy = flag;
        String shownName = warp.getShownName();
        CMI.getInstance()
            .getTeleportHandler()
            .teleportPlayer(target, warp.getLocIncRand(), CMITeleportType.Warp)
            .thenAccept(
                cmiTeleportFeedback -> {
                  if (!cmiTeleportFeedback.isSuccess()) {
                    cmiTeleportFeedback.showFeedbackMessage(commandSender, target);
                  } else if (!silentCopy) {
                    CMISoundPreset.TeleportWarp.play(target, cmiTeleportFeedback.getLocation());
                    CMILC.info(this, target, "teleported", "[warp]", shownName);
                  }
                });
        return true;
      } else {
        LC.info_worldDontExist.sendMessage(commandSender, new Object[0]);
        return null;
      }
    } else {
      if (!PermissionsManager.CMIPerm.command_warp_showlist.hasPermission(commandSender)) {
        return null;
      }

      new ArrayList();
      if (integer == null) {
        integer = 1;
      }

      List list;
      if (commandSender instanceof Player && cmi.getWarpManager().isWarpGUI()) {
        list = cmi.getWarpManager().getWarps(target, integer, warpGroup, false, silent);
      } else {
        list = cmi.getWarpManager().getWarps(target, null, warpGroup, false, silent);
      }

      TreeMap<String, CmiWarp> treeMap = new TreeMap();

      for (CmiWarp warpItem : ((Iterable<CmiWarp>) list)) {
        treeMap.put(warpItem.getName(), warpItem);
      }

      list.clear();
      list.addAll(treeMap.values());
      if (list.isEmpty()) {
        CMILC.info(this, commandSender, "noWarps");
        return null;
      }

      if (cmi.getWarpManager().isWarpGUI()) {
        cmi.getWarpManager().openComplexGUI(target, integer, warpGroup);
        return null;
      }

      PageInfo pageInfo = new PageInfo(10, list.size(), integer);

      for (CmiWarp warpItem : ((Iterable<CmiWarp>) list)) {
        if (!pageInfo.isContinue()) {
          if (pageInfo.isBreak()) {
            break;
          }

          try {
            RawMessage rawMessage = new RawMessage();
            String creatorSuffix = "";
            if (cmi.getWarpManager().isWarpShowCreator() && warpItem.getCreator() != null) {
              creatorSuffix =
                  " " + CMILC.getIM("warp", "creator", "[creator]", warpItem.getCreatorName());
            }

            rawMessage
                .addText(
                    CMILC.warp_list.getLocale(
                            "[pos]",
                            pageInfo.getPositionForOutput(),
                            "[warpName]",
                            warpItem.getShownName(),
                            "[worldName]",
                            warpItem.getLoc().getWorld().getName(),
                            "[x]",
                            warpItem.getLoc().getBlockX(),
                            "[y]",
                            warpItem.getLoc().getBlockY(),
                            "[z]",
                            warpItem.getLoc().getBlockZ())
                        + creatorSuffix)
                .addHover(
                    CMILC.getIM(this, "click", "[warp]", warpItem.getShownName()) + creatorSuffix)
                .addCommand("cmi warp " + warpItem.getName() + " " + target.getName());
            rawMessage.show(commandSender);
          } catch (NullPointerException nullPointerException) {
            cmi.consoleMessage(
                "Failed to show warp information ("
                    + warpItem.getName()
                    + ") remove it or fix possible issues throw warps file");
          }
        }
      }

      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
      return true;
    }
  }
}
