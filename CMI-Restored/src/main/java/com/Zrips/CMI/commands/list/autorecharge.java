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
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class autorecharge implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("moneyState", "{gcp}Money recharge: [state]");
    configReader.get("expState", "{gcp}Exp recharge: [state]");
    configReader.get("updated", "{gcp}State changed to [state]");
  }

  @Override
  @CAnnotation(
      priority = 110,
      info = "Toggle auto flight recharge",
      args = "(playerName) [exp/money/off/on] (-s)",
      tab = {"playername%%exp%%money%%off%%on", "exp%%money%%off%%on"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    boolean bl = false;
    Object targetName = null;
    boolean moneyFlag = false;
    boolean expFlag = false;
    Boolean autoRechargeState = null;
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
            if (((String) object).equalsIgnoreCase("exp")) {
              expFlag = true;
              break;
            }
            if (((String) object).equalsIgnoreCase("money")) {
              moneyFlag = true;
              break;
            }
            if (((String) object).equalsIgnoreCase("off")) {
              autoRechargeState = false;
              break;
            }
            if (((String) object).equalsIgnoreCase("on")) {
              autoRechargeState = true;
              break;
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
    Snd snd = new Snd().setSender(commandSender).setTarget((CMIUser) object);
    if (autoRechargeState == null && !moneyFlag && !expFlag) {
      LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
      CMILC.info(
          (Object) this,
          commandSender,
          "moneyState",
          snd,
          "[state]",
          ((CMIUser) object).getFlightCharge().isMoneyAutoRecharge());
      CMILC.info(
          (Object) this,
          commandSender,
          "expState",
          snd,
          "[state]",
          ((CMIUser) object).getFlightCharge().isExpAutoRecharge());
      return true;
    }
    if (autoRechargeState != null && !moneyFlag && !expFlag) {
      ((CMIUser) object).getFlightCharge().setAutoRecharge(autoRechargeState);
      if (!bl) {
        CMILC.info(
            (Object) this,
            commandSender,
            "updated",
            snd,
            "[state]",
            ((CMIUser) object).getFlightCharge().isAutoRecharge());
      }
      return true;
    }
    if (moneyFlag) {
      ((CMIUser) object)
          .getFlightCharge()
          .setAutoMoneyRecharge(
              autoRechargeState == null
                  ? !((CMIUser) object).getFlightCharge().isMoneyAutoRecharge()
                  : autoRechargeState);
    }
    if (expFlag) {
      ((CMIUser) object)
          .getFlightCharge()
          .setAutoExpRecharge(
              autoRechargeState == null
                  ? !((CMIUser) object).getFlightCharge().isExpAutoRecharge()
                  : autoRechargeState);
    }
    ((CMIUser) object)
        .getFlightCharge()
        .setAutoRecharge(
            ((CMIUser) object).getFlightCharge().isMoneyAutoRecharge()
                || ((CMIUser) object).getFlightCharge().isExpAutoRecharge());
    if (!bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "updated",
          snd,
          "[state]",
          expFlag
              ? ((CMIUser) object).getFlightCharge().isExpAutoRecharge()
              : ((CMIUser) object).getFlightCharge().isMoneyAutoRecharge());
    }
    return true;
  }
}
