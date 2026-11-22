/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMITreeType
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.TreeType
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockPlaceEvent
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMITreeType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

public class tree implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcs}[tree] {gcp}spawned.");
    configReader.get("cantSpawn", "{gcw}Cant spawn ({gcs}[tree]{gcw}) on this block.");
  }

  @Override
  @CAnnotation(
      info = "Spawn tree where you are looking",
      args = "(TreeType) (-p:[playerName])",
      tab = {"treeType"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] location) {
    String string;
    TreeType treeType = null;
    CMITreeType cMITreeType = null;
    Object unusedObject = null;
    String playerName = null;
    int n = location.length;
    int index = 0;
    while (index < n) {
      block28:
      {
        block27:
        {
          string = location[index];
          if (treeType != null) break block27;
          cMITreeType = CMITreeType.getType((String) string);
          if (cMITreeType != null) {
            treeType = cMITreeType.getType();
          }
          if (treeType != null) break block28;
        }
        if (string.startsWith("-p:")) {
          playerName = string.substring("-p:".length());
        }
      }
      ++index;
    }
    if (treeType == null) {
      treeType = TreeType.BIRCH;
    }
    Player target = cMI.getTarget(commandSender, playerName, this);
    if (target == null) {
      return null;
    }
    Block block = cMI.getUtilManager().getTargetBlock(target, 50);
    BlockPlaceEvent blockPlaceEvent = CMIEvent.placeBlockEvent(block, target);
    Bukkit.getServer().getPluginManager().callEvent((Event) blockPlaceEvent);
    if (blockPlaceEvent.isCancelled()) {
      LC.info_protectedArea.sendMessage((Object) target, new Object[0]);
      return null;
    }
    Location treeLocation = block.getLocation().clone();
    switch (treeLocation.getBlock().getType().toString()) {
      case "YELLOW_FLOWER":
      case "SNOW":
      case "RED_ROSE":
      case "DOUBLE_PLANT":
      case "LONG_GRASS":
        {
          treeLocation.getBlock().setType(Material.AIR);
          break;
        }
      default:
        {
          treeLocation = treeLocation.add(0.0, 1.0, 0.0);
        }
    }
    boolean bl = target.getWorld().generateTree(treeLocation, treeType);
    if (bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "feedback",
          "[tree]",
          cMITreeType == null ? treeType.name().toLowerCase() : cMITreeType.getName());
    } else {
      CMILC.info(
          (Object) this,
          commandSender,
          "cantSpawn",
          "[tree]",
          cMITreeType == null ? treeType.name().toLowerCase() : cMITreeType.getName());
    }
    return true;
  }
}
