/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMILocation
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class smite implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("gotSmited", "{gcp}You got smited!");
    configReader.get("targetSmited", "{gcp}Smiting target!");
  }

  @Override
  @CAnnotation(
      info = "Strike ground or player with lightning",
      args = "(playerName/location) (-safe) (-s)",
      tab = {"playername", "-safe", "-s"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      customAlias = {"lightning", "thor"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Block block;
    String string;
    boolean bl = false;
    boolean safeStrike = false;
    String targetName = null;
    CMILocation cMILocation = null;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      string = commandArgs[argIndex];
      switch (string.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "-safe":
          {
            safeStrike = true;
            break;
          }
        default:
          {
            if (cMILocation == null
                && (cMILocation = CMILocation.fromString((String) string)) != null) break;
            targetName = string;
          }
      }
      ++argIndex;
    }
    if (!(commandSender instanceof Player) && targetName == null && cMILocation == null) {
      return false;
    }
    Player target = cMI.getTarget(commandSender, targetName, this, true, false);
    if (targetName != null && target != null) {
      cMILocation = new CMILocation(target.getLocation());
    }
    int smitedAtLocation = 0;
    if (targetName == null && cMILocation != null) {
      smitedAtLocation = 1;
    }
    if (targetName == null
        && cMILocation == null
        && (block = cMI.getUtilManager().getTargetBlock((Player) commandSender, 200)) != null) {
      cMILocation = new CMILocation(block.getLocation());
      smitedAtLocation = 1;
    }
    if (cMILocation == null) {
      return null;
    }
    if (safeStrike) {
      cMILocation.getWorld().strikeLightningEffect((Location) cMILocation);
    } else {
      cMILocation.getWorld().strikeLightning((Location) cMILocation);
    }
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    if (!bl) {
      if (smitedAtLocation != 0) {
        CMILC.info((Object) this, commandSender, "targetSmited", snd);
      } else {
        CMILC.info((Object) this, commandSender, "gotSmited", snd);
      }
    }
    return true;
  }
}
