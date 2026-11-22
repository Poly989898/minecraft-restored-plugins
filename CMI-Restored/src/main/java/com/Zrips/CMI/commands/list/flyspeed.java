/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMINumber
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
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class flyspeed implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcp}[amount]{gcp} fly speed for {gcp}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your flyspeed was set to {gcp}[amount]{gcp} by {gcp}[senderDisplayName]{gcp}.");
    configReader.get("max", "{gcp}Max allowed fly speed for you is {gcs}[max]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set players fly speed from 0 to 10",
      args = "(playerName) [amount] (-s)",
      tab = {"playername"},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      customAlias = {"fspeed"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    String string;
    boolean bl = false;
    Double d = -1.0;
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
            if (targetName == null && (object = CMIUser.getUser(string)) != null) {
              targetName = ((CMIUser) object).getName();
              break;
            }
            try {
              d = Double.parseDouble(string);
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
    int minAmount = 0;
    if (d < (double) minAmount) {
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", minAmount});
      return null;
    }
    int maxAmount = 10;
    if (d > (double) maxAmount) {
      LC.info_NoMoreThan.sendMessage((Object) commandSender, new Object[] {"[amount]", maxAmount});
      return null;
    }
    double allowedMax =
        CMINumber.clamp((double) flyspeed.maxSpeed(commandSender), (double) 0.0, (int) maxAmount);
    if (d > allowedMax) {
      CMILC.info((Object) this, commandSender, "max", "[max]", allowedMax);
      return null;
    }
    target.setFlySpeed((float) (d / 10.0));
    cMI.save(target);
    object = new Snd().setSender(commandSender).setTarget(target);
    d = (double) (target.getFlySpeed() * 10.0f);
    d = (double) Math.round((int) (d * 100.0)) / 100.0;
    if (!bl) {
      CMILC.info((Object) this, commandSender, "feedback", object, "[amount]", d);
      if (!target.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info((Object) this, target, "targetfeedback", object, "[amount]", d);
      }
    }
    return true;
  }

  private static double maxSpeed(CommandSender commandSender) {
    if (!(commandSender instanceof Player)
        || PermissionsManager.CMIPerm.command_flyspeed_$1.hasPermission(commandSender, "*")) {
      return 10.0;
    }
    return PermissionsManager.CMIPerm.command_flyspeed_$1
        .getPermissionInfo((Player) commandSender, "")
        .getMaxValue(1.0);
  }
}
