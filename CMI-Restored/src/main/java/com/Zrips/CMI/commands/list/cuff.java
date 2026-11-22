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

public class cuff implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noChat", "{gcw}All your actions are suspended. You can't chat.");
    configReader.get(
        "noCommand",
        "{gcw}You cant use commands while suspended. {gcp}Allowed: {gcs}/msg, /r, /tell");
    configReader.get("cuffed", "{gcs}[playerDisplayName] {gcp}cuffed");
    configReader.get("unCuffed", "{gcs}[playerDisplayName] {gcp}uncuffed");
    configReader.get("free", "{gcp}You are free now");
    configReader.get("gotCuffed", "{gcp}You have been cuffed");
    configReader.get("cuffedInfo", "{gcp}You are cuffed");
    configReader.get("cantCuff", "&cYou can't cuff this player!");
  }

  @Override
  @CAnnotation(
      info = "Suspends players actions",
      args = "[playername] (true/false) (-s)",
      multiTab = {"[playername] true,false -s"},
      regVar = {1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      customAlias = {"!+false:uncuff", "freeze"},
      modules = {"cuffed"},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    boolean bl = true;
    Object targetName = null;
    boolean silent = false;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = commandArgs[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            silent = true;
            break;
          }
        default:
          {
            if (((String) object).equalsIgnoreCase("true")) {
              bl = true;
              break;
            }
            if (((String) object).equalsIgnoreCase("false")) {
              bl = false;
              break;
            }
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this, false);
    if (object == null) {
      return null;
    }
    if (((CMIUser) object).getName().equals(commandSender.getName())) {
      CMILC.info((Object) this, commandSender, "cantCuff", new Object[0]);
      return null;
    }
    if (((CMIUser) object).hasPermission(PermissionsManager.CMIPerm.command_cuff_bypass)) {
      if (((CMIUser) object).isCuffed()) {
        ((CMIUser) object).setCuffed(false);
      }
      CMILC.info((Object) this, commandSender, "cantCuff", new Object[0]);
      return null;
    }
    Snd snd = new Snd().setSender(commandSender).setTarget((CMIUser) object);
    ((CMIUser) object).setCuffed(bl);
    if (!silent) {
      if (bl) {
        CMILC.info((Object) this, commandSender, "cuffed", snd);
        if (((CMIUser) object).isOnline()) {
          CMILC.info((Object) this, (CMIUser) object, "gotCuffed", new Object[0]);
        }
      } else {
        CMILC.info((Object) this, commandSender, "unCuffed", snd);
        if (((CMIUser) object).isOnline()) {
          CMILC.info((Object) this, (CMIUser) object, "free", new Object[0]);
        }
      }
    }
    ((CMIUser) object).saveIfOffline();
    return true;
  }
}
