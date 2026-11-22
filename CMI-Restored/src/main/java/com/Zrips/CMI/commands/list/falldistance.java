/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
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
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class falldistance implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set fall distance to {gcs}[amount]{gcp} for {gcs}[playerDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set players falldistance",
      args = "(playerName) (number) (-s)",
      tab = {"playername", "-s"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    boolean bl = false;
    String targetName = null;
    Float f = null;
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
        default:
          {
            if (targetName == null && cMI.getPlayerManager().getUser(string) != null) {
              targetName = string;
              break;
            }
            try {
              f = Float.valueOf(Float.parseFloat(string));
              break;
            } catch (Throwable throwable) {
              targetName = string;
            }
          }
      }
      ++argIndex;
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }
    if (f == null) {
      return false;
    }
    target.setFallDistance(f.floatValue());
    Snd snd = new Snd().setSender(commandSender).setTarget((Player) target);
    if (!bl) {
      CMILC.info((Object) this, commandSender, "feedback", "[amount]", f, snd);
    }
    return true;
  }
}
