/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMILocation
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Version.Schedulers.CMIScheduler
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class sit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("onSit", "!actionbar!{gcp}You are sitting now");
    configReader.get("onStandUp", "!actionbar!{gcp}You are no longer sitting");
    configReader.get("accupied", "!actionbar!{gcw}Someone is sitting here already!");
  }

  @Override
  @CAnnotation(
      info = "Sit in your position",
      args = "(playerName) (-persistent) (-s) (location) (on/off)",
      regVar = {0, 1, 2, 3, 4, 5},
      consoleVar = {1, 2, 3, 4, 5},
      multiTab = {"[playerName],!-persistent,-s !-persistent,!-s !-s"},
      alias = true,
      modules = {"sitanimation"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] playerMoveEvent) {
    String string;
    if (Version.isCurrentLower((Version) Version.v1_8_R1)) {
      LC.info_versionNotSupported.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    String targetName = null;
    boolean bl = false;
    boolean persistentSit = false;
    CMILocation cMILocation = null;
    Boolean sitState = null;
    String[] args = playerMoveEvent;
    int n = ((String[]) args).length;
    int argIndex = 0;
    while (argIndex < n) {
      string = args[argIndex];
      switch (string.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(
                commandSender, true, new String[0])) break;
            bl = true;
            break;
          }
        case "on":
          {
            sitState = true;
            break;
          }
        case "off":
          {
            sitState = false;
            break;
          }
        case "-persistent":
          {
            if (!PermissionsManager.CMIPerm.command_sit_persistent.hasPermission(
                commandSender, true, new String[0])) break;
            persistentSit = true;
            break;
          }
        default:
          {
            if (cMILocation == null
                && (cMILocation = CMILocation.fromString((String) string)) != null) {
              if (PermissionsManager.CMIPerm.command_sit_location.hasPermission(
                  commandSender, true, new String[0])) break;
              cMILocation = null;
              break;
            }
            targetName = string;
          }
      }
      ++argIndex;
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null || !target.isOnline()) {
      return null;
    }
    if (sitState != null && !sitState.booleanValue()) {
      cMI.getAnimationManager().removePlayerFromChair(target);
      return true;
    }
    Block block =
        cMI.getUtilManager().getTargetBlock(target, cMI.getAnimationManager().getChairRange());
    if (cMILocation != null) {
      block = cMILocation.getBlock();
    }
    if (cMI.getAnimationManager().isValidChairBlock(block)
        && PermissionsManager.CMIPerm.command_sit_stairs.hasPermission(commandSender)) {
      PlayerTeleportEvent playerTeleportEvent =
          new PlayerTeleportEvent(target, target.getLocation(), block.getLocation());
      Bukkit.getServer().getPluginManager().callEvent((Event) playerTeleportEvent);
      if (playerTeleportEvent.isCancelled()) {
        return false;
      }
      PlayerMoveEvent moveEvent =
          new PlayerMoveEvent(target, target.getLocation(), block.getLocation());
      Bukkit.getServer().getPluginManager().callEvent((Event) moveEvent);
      if (moveEvent.isCancelled()) {
        return false;
      }
      if (target.getLocation().getBlockY() < block.getLocation().getBlockY()) {
        return null;
      }
      if (cMI.getAnimationManager().isSomeOneSittingHere(block)) {
        CMILC.info((Object) this, commandSender, "accupied", new Object[0]);
        return null;
      }
      cMI.getAnimationManager().sit(target, block, persistentSit);
    } else {
      if (cMI.getAnimationManager().isSitting(target)) {
        if (sitState != null && sitState.booleanValue()) {
          return true;
        }
        cMI.getAnimationManager().removePlayerFromChair(target);
        return true;
      }
      if (cMILocation == null && !Util.isOnGround(target)
          || cMI.getAnimationManager().isRiding(target.getUniqueId())
          || cMILocation == null && target.isFlying()) {
        LC.info_notOnGround.sendMessage((Object) commandSender, new Object[0]);
        return null;
      }
      cMI.getAnimationManager()
          .sit(
              target,
              (Location) (cMILocation == null ? target.getLocation() : cMILocation),
              persistentSit);
    }
    sit sitCommand = this;
    if (!bl) {
      CMIScheduler.runTaskLater(
          (JavaPlugin) cMI,
          () -> CMILC.info((Object) sitCommand, commandSender, "onSit", new Object[0]),
          (long) 2L);
    }
    return true;
  }
}
