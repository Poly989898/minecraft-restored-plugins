/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.command.CommandSender
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

public class unjail implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notInJail", "{gcw}Target player is not in jail");
    configReader.get("unjailed", "{gcp}You have released {gcs}[playerDisplayName] {gcp}from jail");
    configReader.get("unjailedTarget", "{gcp}You have been released from jail!");
  }

  @Override
  @CAnnotation(
      info = "Release player from jail",
      args = "[playerName] (-s)",
      tab = {"playername", "-s"},
      explanation = {},
      regVar = {1, 2},
      consoleVar = {1, 2},
      modules = {"jail"},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    Object targetName = null;
    boolean bl = false;
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
            if (targetName != null) break;
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this, false);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isJailed()) {
      CMILC.info((Object) this, commandSender, "notInJail", new Object[0]);
      return null;
    }
    ((CMIUser) object).unjail();
    ((CMIUser) object).addForDelayedSave();
    if (!bl) {
      Snd snd = new Snd();
      snd.setSender(commandSender);
      snd.setTarget((CMIUser) object);
      if (!commandSender.getName().equalsIgnoreCase(((CMIUser) object).getName())) {
        CMILC.info((Object) this, commandSender, "unjailed", snd);
      }
      CMILC.info((Object) this, (CMIUser) object, "unjailedTarget", snd);
    }
    return true;
  }
}
