/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class notarget implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("enabled", "{gcp}Monsters will no longer attack you!");
    configReader.get("disabled", "{gcp}Monsters will now attack you!");
  }

  @Override
  @CAnnotation(
      info = "Toggle no-mob target mode",
      args = "(playerName) (true/false) (-s)",
      tab = {"playername", "true%%false", "-s%%"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    boolean bl = false;
    Object targetName = null;
    Boolean notargetState = null;
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
            try {
              if (((String) object).equals("true")) {
                notargetState = true;
                break;
              }
              if (((String) object).equals("false")) {
                notargetState = false;
                break;
              }
            } catch (NumberFormatException numberFormatException) {
              // empty catch block
            }
            targetName = object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, (String) targetName, this);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    int currentState =
        ((CMIUser) object).getOptionState(PlayerOption.notarget).booleanValue() ? 1 : 0;
    if (notargetState == null) {
      notargetState = currentState == 0;
    }
    ((CMIUser) object).setOptionState(PlayerOption.notarget, notargetState);
    if (!bl) {
      if (notargetState.booleanValue()) {
        CMILC.info((Object) this, commandSender, "enabled", new Object[0]);
        if (!((CMIUser) object).getName().equals(commandSender.getName())) {
          CMILC.info((Object) this, (CMIUser) object, "enabled", new Object[0]);
        }
      } else {
        CMILC.info((Object) this, commandSender, "disabled", new Object[0]);
        if (!((CMIUser) object).getName().equals(commandSender.getName())) {
          CMILC.info((Object) this, (CMIUser) object, "disabled", new Object[0]);
        }
      }
    }
    return true;
  }
}
