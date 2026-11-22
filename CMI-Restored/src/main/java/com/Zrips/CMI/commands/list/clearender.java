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

public class clearender implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have cleared {gcs}[playerDisplayName]{gcp} ender chest.");
    configReader.get(
        "targetfeedback", "{gcp}Your ender chest was cleared by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      priority = 125,
      info = "Clear players ender chest",
      args = "[playerName] (-s)",
      tab = {"playerName"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    boolean bl = false;
    String targetName = null;
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
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    target.getEnderChest().clear();
    cMI.save(target);
    CMILC.info((Object) this, commandSender, "feedback", snd);
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info((Object) this, (CommandSender) target, "targetfeedback", snd);
    }
    return true;
  }
}
