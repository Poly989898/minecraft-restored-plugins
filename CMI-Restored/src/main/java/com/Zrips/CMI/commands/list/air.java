/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class air implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have set {gcs}[amount]{gcp} air for {gcs}[playerName]{gcp}.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your air was set to {gcs}[amount]{gcp} by {gcs}[senderDisplayName]{gcp}.");
    configReader.get("current", "{gcp}Current {gcs}[playerDisplayName] {gcp}air: {gcs}[amount]");
  }

  @Override
  @CAnnotation(
      priority = 110,
      info = "Set players air",
      args = "[playerName] [amount] (-s)",
      tab = {"playername"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    boolean bl = false;
    int n = -1;
    String targetName = null;
    String[] commandArgs = stringArray;
    int argCount = stringArray.length;
    int argIndex = 0;
    while (argIndex < argCount) {
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
            Object object;
            if (targetName == null && (object = CMIUser.getUser(string)) != null) {
              targetName = ((CMIUser) object).getName();
              break;
            }
            try {
              n = Integer.parseInt(string);
              break;
            } catch (NumberFormatException numberFormatException) {
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
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    if (n == -1) {
      CMILC.info(
          (Object) this, commandSender, "current", snd, "[amount]", target.getRemainingAir());
      return null;
    }
    int minAmount = 0;
    if (n < minAmount) {
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", minAmount});
      return null;
    }
    int maxAmount = Integer.MAX_VALUE;
    if (n > maxAmount) {
      LC.info_NoMoreThan.sendMessage((Object) commandSender, new Object[] {"[amount]", maxAmount});
      return null;
    }
    if (!target.isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    CMILC.info((Object) this, commandSender, "feedback", snd, "[amount]", n);
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info((Object) this, target, "targetFeedback", snd, "[amount]", n);
    }
    target.setMaximumAir(n);
    target.setRemainingAir(n);
    return true;
  }
}
