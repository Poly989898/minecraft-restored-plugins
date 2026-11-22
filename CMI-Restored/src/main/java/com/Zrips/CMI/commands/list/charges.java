/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Time.CMITimeManager
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class charges implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("charges", "{gcp}* Charges: {gcs}[current]{gcp}/{gcs}[max] [bypass]");
    configReader.get("cd", "{gcp}* CD: {gcs}[cdtime] {gcp}Next in: {gcs}[time]");
    configReader.get("bypass", "{gcs}(Bypass)");
    configReader.get("allUsed", "{gcp}All charges used! Next in [time]");
    configReader.get("noGroup", "{gcp}Don't have assigned charge group");
    configReader.get("cantBreak", "{gcp}You can't break until you will have assigned charge group");
    configReader.get("minusCd", "{gcp} Minus {gcs}[sec]{gcp}sec on spawner charge cooldown");
    configReader.get(
        "add", "{gcp}Added {gcs}[amount] {gcp}spawner charges for {gcs}[playerDisplayName]");
    configReader.get(
        "set", "{gcp}Set {gcs}[amount] {gcp}spawner charges for {gcs}[playerDisplayName]");
    configReader.get("clear", "{gcp}Cleared all spawner charges for {gcs}[playerDisplayName]");
    configReader.get("reset", "{gcp}Reset spawner charge group for {gcs}[playerDisplayName]");
    configReader.get(
        "take", "{gcp}Took {gcs}[amount] {gcp}spawner charges from {gcs}[playerDisplayName]");
  }

  @Override
  @CAnnotation(
      info = "Shows left spawner charges",
      args = "[playerName] [add/set/take/clear/reset] (-f)",
      tab = {"playername", "add%%set%%take%%clear%%reset"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      alias = true,
      modules = {"spawnercharge"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    String targetName = null;
    Action action = Action.Info;
    int n = 0;
    boolean bl = false;
    String[] commandArgs = stringArray;
    int argCount = stringArray.length;
    int argIndex = 0;
    while (argIndex < argCount) {
      string = commandArgs[argIndex];
      switch (string.toLowerCase()) {
        case "-f":
          {
            bl = true;
            break;
          }
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        case "set":
          {
            action = Action.Set;
            break;
          }
        case "take":
          {
            action = Action.Take;
            break;
          }
        case "reset":
          {
            action = Action.Reset;
            break;
          }
        default:
          {
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
    CMIUser cMIUser = cMI.getPlayerManager().getUser(target);
    int bypassFlag =
        cMIUser.hasPermission(PermissionsManager.CMIPerm.spawners_charge_bypass) ? 1 : 0;
    if (action == Action.Info) {
      if (!cMIUser.getPCharge().have()) {
        CMILC.info((Object) this, commandSender, "noGroup", new Object[0]);
        return null;
      }
      LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
      CMILC.info(
          (Object) this,
          commandSender,
          "charges",
          "[current]",
          cMIUser.getPCharge().getCharges(),
          "[max]",
          cMIUser.getPCharge().getMaxCharges(),
          "[bypass]",
          bypassFlag != 0 ? CMILC.getIM(this, "bypass", new Object[0]) : "");
      CMILC.info(
          (Object) this,
          commandSender,
          "cd",
          "[cdtime]",
          CMITimeManager.to24hourShort(
              (Long) Long.valueOf(cMIUser.getPCharge().getCooldown() * 1000)),
          "[time]",
          cMIUser.getPCharge().getLeftTime());
      LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
      return true;
    }
    if (!PermissionsManager.CMIPerm.command_charges_edit.hasPermission(
        commandSender, true, new String[0])) {
      return null;
    }
    switch (action) {
      case Add:
        {
          if (n <= 0) break;
          cMIUser.getPCharge().addCharge(n, bl);
          CMILC.info((Object) this, commandSender, "add", "[amount]", n, cMIUser);
          break;
        }
      case Clear:
        {
          cMIUser.getPCharge().setCharges(0);
          CMILC.info((Object) this, commandSender, "clear", cMIUser);
          break;
        }
      case Reset:
        {
          cMIUser.getPCharge().reset();
          CMILC.info((Object) this, commandSender, "reset", cMIUser);
          break;
        }
      case Set:
        {
          cMIUser.getPCharge().setCharges(n);
          CMILC.info((Object) this, commandSender, "set", "[amount]", n, cMIUser);
          break;
        }
      case Take:
        {
          cMIUser.getPCharge().takeCharge(n, bl);
          CMILC.info((Object) this, commandSender, "take", "[amount]", n, cMIUser);
          break;
        }
    }
    CMILC.info(
        (Object) this,
        commandSender,
        "charges",
        "[current]",
        cMIUser.getPCharge().getCharges(),
        "[max]",
        cMIUser.getPCharge().getMaxCharges(),
        "[bypass]",
        bypassFlag != 0 ? CMILC.getIM(this, "bypass", new Object[0]) : "");
    return true;
  }

  private static enum Action {
    Set,
    Add,
    Clear,
    Take,
    Info,
    Reset;
  }
}
