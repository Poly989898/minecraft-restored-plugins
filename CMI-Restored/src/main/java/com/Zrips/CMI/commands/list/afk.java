/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Colors.CMIChatColor
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class afk implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.addComment(
        "afkTitle",
        new String[] {
          "This only determines message in title message and not message shown in chat. For chat"
              + " message check config.yml file"
        });
    configReader.get("afkTitle", "{gcp}You are now AFK");
    configReader.get(
        "afkSubTitle",
        Arrays.asList(
            "{gcu}Don't eat to much cookies!",
            "{gcu}Nice day for fishing",
            "{gcu}Afk, again?",
            "{gcu}Where are you?"));
  }

  @Override
  @CAnnotation(
      info = "Toggle afk mode. Reason could be provided",
      args = "(-p:playerName) (reason) (-s)",
      tab = {"playername", "-s"},
      modules = {"afk"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    String string = null;
    String reason = "";
    boolean bl = false;
    String[] args = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = args[argIndex];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (((String) object).startsWith("-p:")) {
              string = ((String) object).substring("-p:".length());
              break;
            }
            if (!reason.isEmpty()) {
              reason = String.valueOf(reason) + " ";
            }
            reason = String.valueOf(reason) + (String) object;
          }
      }
      ++argIndex;
    }
    object = cMI.getUser(commandSender, string, this);
    if (object == null) {
      return null;
    }
    reason = CMIChatColor.stripColor((String) reason);
    reason = CTextManager.clean(reason);
    reason = cMI.getPlaceholderAPIManager().coolDownPlaceHolders(reason);
    if (!reason.isEmpty()) {
      ((CMIUser) object).setAfkReason(reason);
    }
    ((CMIUser) object).setAfk(!((CMIUser) object).isAfk(), CMIAfkEnterEvent.AfkType.manual, !bl);
    return true;
  }
}
