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

public class flyc implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set fly charge mode to {gcs}[boolean]{gcp} for"
            + " {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetFeedback",
        "{gcp}Your fly charge mode set to {gcs}[boolean]{gcp} by {gcs}[senderDisplayName]{gcp}.");
    configReader.get("targetFeedbackSelf", "{gcp}Your fly charge mode set to {gcs}[boolean]");
    configReader.get("noCharge", "{gcp}Flight charge is depleted");
  }

  @Override
  @CAnnotation(
      info = "Toggle flight charge mode",
      args = "(playerName) (true/false) (-s)",
      tab = {"playername", "true%%false"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      modules = {"flightcharge"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    boolean bl = false;
    Object targetName = null;
    Boolean allowFlight = null;
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
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this);
    if (object == null) {
      return null;
    }
    cMI.getFlightChargeManager().autorecharge((CMIUser) object);
    if (((CMIUser) object).getFlightCharge().getCharge() <= 0.0) {
      CMILC.info((Object) this, (CMIUser) object, "noCharge", new Object[0]);
      return null;
    }
    Player player = ((CMIUser) object).getPlayer();
    if (allowFlight == null) {
      allowFlight = !player.getAllowFlight();
    }
    player.setAllowFlight(allowFlight.booleanValue());
    ((CMIUser) object).getFlightCharge().setEnabled(allowFlight);
    ((CMIUser) object).addForDelayedSave();
    cMI.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (player.getName().equalsIgnoreCase(commandSender.getName())) {
      CMILC.info((Object) this, commandSender, "targetFeedbackSelf", snd, "[boolean]", allowFlight);
      return true;
    }
    CMILC.info((Object) this, commandSender, "feedback", snd, "[boolean]", allowFlight);
    if (!bl) {
      CMILC.info(
          (Object) this, (CommandSender) player, "targetFeedback", "[boolean]", allowFlight, snd);
    }
    return true;
  }
}
