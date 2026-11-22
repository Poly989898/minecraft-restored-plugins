/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMIArray
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class speed implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  /*
   * Recovered potentially malformed switches.  Disable with '--allowmalformedswitch false'
   * Enabled aggressive block sorting
   * Enabled unnecessary exception pruning
   * Enabled aggressive exception aggregation
   */
  @Override
  @CAnnotation(
      priority = 140,
      info = "Set players walk or fly speed",
      args = "(playerName) [amount] (-s)",
      tab = {"playername"},
      explanation = {},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    String targetName = null;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      string = commandArgs[argIndex];
      switch (string.toLowerCase()) {
        default:
          {
            if (targetName == null
                && CMIUser.getOnlinePlayer(commandSender, string, true) != null) {
              targetName = string;
              break;
            }
            try {
              Double.parseDouble(string);
            } catch (NumberFormatException numberFormatException) {
              targetName = string;
            }
            break;
          }
        case "-s":
      }
      ++argIndex;
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }
    if (target.isFlying()) {
      cMI.getCommandManager()
          .onCommand(
              commandSender,
              null,
              null,
              CMIArray.addFirst((String[]) stringArray, (String) flyspeed.class.getSimpleName()));
      return true;
    }
    cMI.getCommandManager()
        .onCommand(
            commandSender,
            null,
            null,
            CMIArray.addFirst((String[]) stringArray, (String) walkspeed.class.getSimpleName()));
    return true;
  }
}
