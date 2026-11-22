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
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class unmute implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notMuted", "{gcp}Player is not muted");
    configReader.get("unmuted", "{gcp}Player can talk now");
    configReader.get("unmutedTarget", "{gcp}You can talk now");
  }

  @Override
  @CAnnotation(
      info = "Unmute player",
      args = "[playerName] (-s)",
      tab = {"mutedplayername"},
      explanation = {},
      regVar = {1, 2},
      consoleVar = {1, 2},
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
    object = cMI.getUser(commandSender, (String) targetName, this, true, false);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isMuted() && !((CMIUser) object).isShadowMuted()) {
      if (!bl) {
        CMILC.info((Object) this, commandSender, "notMuted", new Object[0]);
      }
      return true;
    }
    if (!bl) {
      CMILC.info((Object) this, commandSender, "unmuted", new Object[0]);
    }
    if (!bl && ((CMIUser) object).isOnline() && !((CMIUser) object).isShadowMuted()) {
      CMILC.info((Object) this, (CMIUser) object, "unmutedTarget", new Object[0]);
    }
    ((CMIUser) object).setMutedUntil(0L);
    ((CMIUser) object).setShadowMutedUntil(0L);
    return true;
  }
}
