/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.RawMessages.RawMessage
 *  net.Zrips.CMILib.RawMessages.RawMessageCommand
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Economy.CMIEconomyTax;
import com.Zrips.CMI.Modules.Economy.CMIEconomyTaxType;
import com.Zrips.CMI.Modules.Economy.EconomyManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pay implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @Override
  @CAnnotation(
      priority = 48,
      info = "Perform money transaction",
      args = "[playerName] [amount] (-s)",
      tab = {"playername"},
      regVar = {0, 2, 3},
      consoleVar = {666},
      alias = true,
      others = false)
  public Boolean perform(final CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    Object value;
    if (stringArray.length == 0) {
      cMI.performCommand(commandSender, "cmi balance");
      return null;
    }
    boolean bl = false;
    Object valueSecond = null;
    Double d = null;
    boolean blValue = false;
    String[] stringArrayValue = stringArray;
    int n = stringArray.length;
    int count = 0;
    while (count < n) {
      value = stringArrayValue[count];
      switch (((String) value).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            blValue = true;
            break;
          }
        default:
          {
            if (Bukkit.getPlayerExact((String) value) != null) {
              valueSecond = value;
              break;
            }
            if (valueSecond == null && cMI.getPlayerManager().getUser((String) value) != null) {
              valueSecond = cMI.getPlayerManager().getUser((String) value).getName();
              break;
            }
            if (d == null
                && (d = CMI.getInstance().getEconomyManager().translateMoney((String) value))
                    != null) break;
            if (((String) value).equalsIgnoreCase("-confirmed")) {
              bl = true;
              break;
            }
            valueSecond = value;
          }
      }
      ++count;
    }
    value = cMI.getUser(commandSender, (String) valueSecond, this, false);
    if (value == null) {
      return null;
    }
    if (d == null || d < 0.0) {
      return false;
    }
    double amount = d;
    double amountSecond = d;
    Snd snd = new Snd().setSender(commandSender).setTarget((CMIUser) value);
    CMIUser cMIUser = null;
    double amountThird = 0.0;
    if (commandSender instanceof Player) {
      cMIUser = cMI.getPlayerManager().getUser((Player) commandSender);
      object = cMIUser.getEconomyAccount().getCurrentWorldGroup().getTax();
      if (!((CMIEconomyTax) object).getType().equals((Object) CMIEconomyTaxType.None)) {
        amountThird = ((CMIEconomyTax) object).getTaxAmount(amount);
        if (((CMIEconomyTax) object).isDeductOnTop()) {
          amount += amountThird;
        } else if ((amountSecond -= amountThird) <= 0.0) {
          return false;
        }
        if (amountSecond > amount) {
          return false;
        }
      }
      if (!cMIUser.getEconomyAccount().getCurrentWorldGroup().isFractions()) {
        amount = Math.floor(amount);
      }
      if (!cMIUser.hasMoney(amount)) {
        LC.econ_notEnoughMoney.sendMessage(
            (Object) commandSender, new Object[] {snd, "[amount]", amount});
        return null;
      }
      if (!((CMIUser) value)
              .getEconomyAccount()
              .getCurrentWorldGroup()
              .equals(cMIUser.getEconomyAccount().getCurrentWorldGroup())
          && !PermissionsManager.CMIPerm.command_money_betweenworldgroups.hasPermission(
              commandSender)) {
        CMILC.info("money", commandSender, "worldGroups", snd);
        return null;
      }
      if (value.equals(cMIUser)) {
        CMILC.info("money", commandSender, "self", snd);
        return null;
      }
      if (!((CMIUser) value).getOptionState(PlayerOption.acceptingMoney).booleanValue()) {
        CMILC.info("money", commandSender, "notAccepting", snd);
        return null;
      }
      if (((CMIUser) value).isIgnoring(cMIUser.getUniqueId())
          && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
        CMILC.info("ignore", commandSender, "ignoringYou", snd);
        return null;
      }
      if (cMIUser.getEconomyAccount().getCurrentWorldGroup().getMinimalPay() > amount) {
        LC.info_NoLessThan.sendMessage(
            (Object) commandSender,
            new Object[] {
              "[amount]", cMIUser.getEconomyAccount().getCurrentWorldGroup().getMinimalPay()
            });
        return null;
      }
      if (((CMIUser) value).getEconomyAccount().getCurrentWorldGroup().getMaximumAmount()
          < ((CMIUser) value).getBalance() + amountSecond) {
        CMILC.info("money", commandSender, "overLimit", snd);
        return null;
      }
      if (!cMI.getEconomyManager().isOfflinePayments() && !((CMIUser) value).isOnline()) {
        LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
        return null;
      }
      if (cMI.getEconomyManager().isConfirmation() && !bl) {
        RawMessage rawMessage = new RawMessage();
        String string = String.valueOf(d);
        final CMIUser payer = (CMIUser) value;
        RawMessageCommand rawMessageCommand =
            new RawMessageCommand() {

              public void run(CommandSender commandSender) {
                cMI.getCommandManager()
                    .performCMICommand(
                        commandSender,
                        pay.class,
                        CommandsHandler.getLabel(),
                        pay.class.getSimpleName(),
                        payer.getName(),
                        string,
                        "-confirmed");
              }
            };
        rawMessage
            .addText(
                CMILC.getIM("money", "confirm", "[money]", cMI.getEconomyManager().format(d), snd))
            .addHover(LC.info_Click.getLocale(new Object[0]))
            .addCommand(rawMessageCommand.getCommand());
        rawMessage.show(commandSender);
        return null;
      }
      cMI.getEconomyManager()
          .moneyLog((CMIUser) value, cMIUser, d, EconomyManager.CMIMoneyLogType.Transfer, null);
      cMIUser.withdraw(amount, (CMIUser) value);
      cMIUser.addForDelayedSave();
    }
    ((CMIUser) value).deposit(amountSecond, cMIUser);
    ((CMIUser) value).addForDelayedSave();
    if (!blValue) {
      CharSequence charSequence;
      object = new StringBuilder();
      if (amountThird > 0.0) {
        charSequence =
            CMILC.getIM(
                money.class, "taxes", "[taxes]", cMI.getEconomyManager().format(amountThird));
        ((StringBuilder) object).append(" ");
        ((StringBuilder) object).append((String) charSequence);
      }
      charSequence = new StringBuilder();
      ((StringBuilder) charSequence)
          .append(
              CMILC.getIM(
                  money.class,
                  "paid",
                  snd,
                  "[amount]",
                  cMI.getEconomyManager().format(amountSecond)));
      ((StringBuilder) charSequence).append((CharSequence) object);
      CMILC.sendMessage(commandSender, ((StringBuilder) charSequence).toString());
      if (((CMIUser) value).isOnline()) {
        charSequence = new StringBuilder();
        ((StringBuilder) charSequence)
            .append(
                CMILC.getIM(
                    money.class,
                    "gotMoney",
                    snd,
                    "[amount]",
                    cMI.getEconomyManager().format(amountSecond),
                    "[money]",
                    ((CMIUser) value).getFormatedBalance()));
        ((StringBuilder) charSequence).append((CharSequence) object);
        CMILC.sendMessage(value, ((StringBuilder) charSequence).toString());
      }
    }
    return true;
  }
}
