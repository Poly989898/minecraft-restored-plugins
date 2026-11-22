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
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class fly implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set fly to {gcs}[boolean]{gcp} for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your fly mode set to {gcs}[boolean]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set players fly true or false",
      args = "[playerName] (true/false) (-s)",
      tab = {"playername", "true%%false"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    boolean bl = false;
    String targetName = null;
    Boolean allowFlight = null;
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
        case "t":
        case "true":
          {
            allowFlight = true;
            break;
          }
        case "f":
        case "false":
          {
            allowFlight = false;
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
    if (allowFlight == null) {
      allowFlight = !target.getAllowFlight();
    }
    target.setFallDistance(0.0f);
    target.setAllowFlight(allowFlight.booleanValue());
    CMIUser cMIUser = cMI.getPlayerManager().getUser(target);
    if (cMIUser != null) {
      cMIUser.setTfly(0L);
      if (allowFlight.booleanValue()) {
        cMIUser.getFlightCharge().setEnabled(false);
      }
      if (!cMIUser.isOnline()) {
        cMIUser.setHadAllowFlight(allowFlight);
        cMIUser.setWasFlying(allowFlight);
        cMIUser.setFlying(allowFlight);
      }
    }
    cMI.save(target);
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    if (!bl) {
      CMILC.info((Object) this, commandSender, "feedback", snd, "[boolean]", allowFlight);
    }
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info((Object) this, target, "targetFeedback", "[boolean]", allowFlight, snd);
    }
    return true;
  }
}
