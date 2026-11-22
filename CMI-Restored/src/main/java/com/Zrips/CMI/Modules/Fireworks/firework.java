/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMILocation
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.Modules.Fireworks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class firework implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @Override
  @CAnnotation(
      info = "&eShoot firework",
      args = "",
      tab = {""},
      regVar = {-66},
      consoleVar = {2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    String string = null;
    boolean bl = false;
    Location location = null;
    Object targetName = null;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = commandArgs[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (((String) object).startsWith("-p:")) {
              string = ((String) object).substring("-p:".length());
              break;
            }
            if (location == null && (location = CMILocation.fromString((String) object)) != null)
              break;
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, string, this);
    if (location == null && object != null) {
      location = ((CMIUser) object).getPlayer().getEyeLocation();
    }
    if (location == null) {
      return false;
    }
    return true;
  }
}
