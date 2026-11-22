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

public class workbench implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have opened workbench for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("targetfeedback", "{gcs}[senderDisplayName]{gcp} opened workbench for you.");
  }

  @Override
  @CAnnotation(
      info = "Open workbench",
      args = "(playerName) (-s)",
      tab = {"playername", "-s%%"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      customAlias = {"wb", "craft"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    String targetName = null;
    boolean bl = false;
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
            targetName = string;
          }
      }
      ++argIndex;
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }
    Snd snd = new Snd().setSender(commandSender).setTarget((Player) target);
    if (!bl) {
      CMILC.info((Object) this, commandSender, "feedback", snd);
    }
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info((Object) this, (CommandSender) target, "targetfeedback", snd);
    }
    target.openWorkbench(null, true);
    return true;
  }
}
