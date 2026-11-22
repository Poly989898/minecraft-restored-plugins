/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMIArray
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Bukkit
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class money implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("newBalance", "{gcs}[playerDisplayName] {gcp}new balance: {gcs}[money]");
    configReader.get("paid", "{gcp}You have sent {gcs}[amount] {gcp}to {gcs}[playerDisplayName]");
    configReader.get(
        "gotMoney",
        "{gcp}You got {gcs}[amount] {gcp}from {gcs}[senderDisplayName]{gcp}. Balance:"
            + " {gcs}[money]");
    configReader.get("gotMoneyGive", "{gcp}You received {gcs}[amount]{gcp}. Balance: {gcs}[money]");
    configReader.get(
        "lostMoney",
        "{gcs}[amount]{gcp} has been removed from your balance. Remaining: {gcs}[money]");
    configReader.get("worldGroups", "{gcw}You can't send money between different world groups");
    configReader.get("self", "{gcw}You can't send money to yourself!");
    configReader.get(
        "confirm",
        "{gcp}Click to confirm the {gcs}[money] {gcp}transfer to {gcs}[playerDisplayName]"
            + " {gcu}&l[\u2714]");
    configReader.get("notAccepting", "{gcp}This user is not accepting payments from other players");
    configReader.get(
        "giveAll", "{gcp}Give out {gcs}[totalAmount] {gcp}for {gcs}[playerCount] {gcp}players");
    configReader.get(
        "takeAll", "{gcp}Took {gcs}[totalAmount] {gcp}from {gcs}[playerCount] {gcp}players");
    configReader.get("taxes", "{gcp}Taxes: {gcs}[taxes]");
    configReader.get(
        "overLimit",
        "{gcs}[playerDisplayName] {gcp}can't receive your money due limitations in how many he can"
            + " hold at once");
  }

  /*
   * Enabled aggressive block sorting
   * Enabled unnecessary exception pruning
   * Enabled aggressive exception aggregation
   */
  @Override
  @CAnnotation(
      priority = 48,
      info = "Manage money balance",
      args =
          "[pay/give/take/set] [playerName/all/alloffline/allonline]"
              + " [amount][%rand/1-1000%][1%[min-max][[playerName]]] (-s)",
      tab = {"pay%%give%%take%%set", "playername%%-all%%-alloffline%%-allonline", "-s%%"},
      explanation = {},
      regVar = {0, 1, 3, 4},
      consoleVar = {1, 3, 4},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    Object argument;
    if (stringArray.length == 0) {
      return cMI.getCommandManager()
          .performCMICommand(
              commandSender, balance.class, CMIArray.removeFirst((String[]) stringArray));
    }
    if (stringArray.length == 1 && cMI.getPlayerManager().getUser(stringArray[0]) != null) {
      return cMI.getCommandManager()
          .performCMICommand(commandSender, balance.class, stringArray[0]);
    }
    Object targetName = null;
    Actions action = null;
    Double d = null;
    boolean bl = false;
    boolean isPercent = false;
    Double minClamp = null;
    Double maxClamp = null;
    String string = null;
    TargetPlayers targetPlayers = null;
    Pattern pattern = Pattern.compile("%rand\\/(\\d+|\\d+.?\\d+?)-(\\d+|\\d+.?\\d+?)%");
    Pattern percentPattern =
        Pattern.compile(
            "(^(\\d+(\\.\\d+)?)(%)((\\[(\\d+(\\.\\d+)?)\\-(\\d+(\\.\\d+)?)\\])?(\\[([a-zA-z\\d]+)\\]+)?)$)");
    String[] argsCopy = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      block83:
      {
        argument = argsCopy[argIndex];
        switch (((String) argument).toLowerCase()) {
          case "-s":
            {
              if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
                bl = true;
              }
              break block83;
            }
          case "all":
          case "-all":
            {
              if (targetPlayers != null) break;
              targetPlayers = TargetPlayers.all;
              break block83;
            }
          case "alloffline":
          case "-alloffline":
            {
              if (targetPlayers != null) break;
              targetPlayers = TargetPlayers.alloffline;
              break block83;
            }
          case "-allonline":
          case "allonline":
            {
              if (targetPlayers != null) break;
              targetPlayers = TargetPlayers.allonline;
              break block83;
            }
        }
        if (action != null || (action = Actions.getByName((String) argument)) == null) {
          if (Bukkit.getPlayerExact((String) argument) != null) {
            targetName = argument;
          } else if (targetName == null
              && cMI.getPlayerManager().getUser((String) argument) != null) {
            targetName = cMI.getPlayerManager().getUser((String) argument).getName();
          } else {
            String upperBoundText;
            Matcher randMatcher = pattern.matcher((CharSequence) argument);
            while (randMatcher.find()) {
              if (!randMatcher.group(0).contains("-")) continue;
              Random random = new Random(System.nanoTime());
              try {
                int randMax;
                int randMin;
                object = randMatcher.group(1);
                upperBoundText = randMatcher.group(2);
                double randScale = 1.0;
                if (((String) object).contains(".")) {
                  randScale = 0xA ^ ((String) object).split("\\.")[1].length();
                }
                if ((randMin = (int) (Double.parseDouble((String) object) * randScale))
                    >= (randMax = (int) (Double.parseDouble(upperBoundText) * randScale) + 1))
                  continue;
                double randomValue =
                    (double) (random.nextInt(randMax - randMin) + randMin) / randScale;
                d = randomValue;
                break block83;
              } catch (Exception exception) {
                // empty catch block
              }
            }
            Matcher percentMatcher = percentPattern.matcher((CharSequence) argument);
            while (percentMatcher.find()) {
              try {
                d = Double.parseDouble(percentMatcher.group(2));
                d = d < 0.0 ? 0.0 : d;
                isPercent = true;
                object = percentMatcher.group(7);
                upperBoundText = percentMatcher.group(9);
                string = percentMatcher.group(11);
                if (object != null) {
                  minClamp = Double.parseDouble((String) object);
                }
                if (upperBoundText != null) {
                  maxClamp = Double.parseDouble(upperBoundText);
                }
                break block83;
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            }
            if (d != null
                || (d = CMI.getInstance().getEconomyManager().translateMoney((String) argument))
                    == null) {
              targetName = argument;
            }
          }
        }
      }
      ++argIndex;
    }
    if (targetPlayers == null) {
      targetPlayers = TargetPlayers.single;
    }
    if (action == null) return false;
    if (d == null) {
      return false;
    }
    if (action.isAdminCmd()
        && !PermissionsManager.CMIPerm.command_money_admin.hasPermission(
            commandSender, true, new String[0])) {
      return null;
    }
    Set<CMIUser> targetUsers = new HashSet<>();
    Iterator<CMIUser> userIterator = null;
    if (!targetPlayers.equals((Object) TargetPlayers.single)) {
      if (commandSender instanceof Player) {
        LC.info_FromConsole.sendMessage((Object) commandSender, new Object[0]);
        return null;
      }
      switch (targetPlayers) {
        case all:
          {
            for (Map.Entry<UUID, CMIUser> entry : cMI.getPlayerManager().getAllUsers().entrySet()) {
              if (entry.getValue().isFakeAccount().booleanValue()) continue;
              targetUsers.add(entry.getValue());
            }
            break;
          }
        case alloffline:
          {
            for (Map.Entry<UUID, CMIUser> entry : cMI.getPlayerManager().getAllUsers().entrySet()) {
              if (entry.getValue().isFakeAccount().booleanValue() || entry.getValue().isOnline())
                continue;
              targetUsers.add(entry.getValue());
            }
            break;
          }
        case allonline:
          {
            for (Player player : Bukkit.getOnlinePlayers()) {
              targetUsers.add(cMI.getPlayerManager().getUser(player));
            }
            break;
          }
      }
      Double grandTotal = 0.0;
      block22:
      switch (action) {
        case give:
        case add:
          {
            double targetBalance = 0.0;
            if (isPercent && string != null) {
              CMIUser percentSourceUser = cMI.getPlayerManager().getUser(string);
              targetBalance =
                  percentSourceUser != null ? percentSourceUser.getBalance() : targetBalance;
            }
            userIterator = targetUsers.iterator();
            while (true) {
              if (!userIterator.hasNext()) {
                CMILC.info(
                    (Object) this,
                    commandSender,
                    "giveAll",
                    "[totalAmount]",
                    cMI.getEconomyManager().format(grandTotal),
                    "[playerCount]",
                    targetUsers.size());
                break block22;
              }
              CMIUser targetUser = (CMIUser) userIterator.next();
              object = new Snd().setSender(commandSender).setTarget(targetUser);
              double depositAmount = d;
              if (isPercent) {
                depositAmount = string != null ? targetBalance : targetUser.getBalance();
                depositAmount = depositAmount * d / 100.0;
                if (minClamp != null && depositAmount < minClamp) {
                  depositAmount = minClamp;
                }
                if (maxClamp != null && depositAmount > maxClamp) {
                  depositAmount = maxClamp;
                }
              }
              targetUser.deposit(depositAmount);
              grandTotal = grandTotal + depositAmount;
              if (!targetUser.isOnline() || bl) continue;
              CMILC.info(
                  (Object) this,
                  targetUser,
                  "gotMoneyGive",
                  object,
                  "[amount]",
                  cMI.getEconomyManager().format(depositAmount),
                  "[money]",
                  targetUser.getFormatedBalance());
            }
          }
        case set:
          {
            userIterator = targetUsers.iterator();
            while (true) {
              if (!userIterator.hasNext()) {
                CMILC.info(
                    (Object) this,
                    commandSender,
                    "giveAll",
                    "[totalAmount]",
                    cMI.getEconomyManager().format(grandTotal),
                    "[playerCount]",
                    targetUsers.size());
                break block22;
              }
              CMIUser targetUser = (CMIUser) userIterator.next();
              object = new Snd().setSender(commandSender).setTarget(targetUser);
              grandTotal = grandTotal + -(targetUser.getBalance() - d);
              targetUser.getEconomyAccount().setBalance(d);
              targetUser.addForDelayedSave();
              if (!targetUser.isOnline() || bl) continue;
              CMILC.info(
                  (Object) this,
                  targetUser,
                  "newBalance",
                  object,
                  "[money]",
                  targetUser.getFormatedBalance());
            }
          }
        case take:
          {
            double sourceBalance = 0.0;
            if (isPercent && string != null) {
              CMIUser percentSourceUser = cMI.getPlayerManager().getUser(string);
              sourceBalance =
                  percentSourceUser != null ? percentSourceUser.getBalance() : sourceBalance;
            }
            userIterator = targetUsers.iterator();
            while (true) {
              if (!userIterator.hasNext()) {
                CMILC.info(
                    (Object) this,
                    commandSender,
                    "takeAll",
                    "[totalAmount]",
                    cMI.getEconomyManager().format(grandTotal),
                    "[playerCount]",
                    targetUsers.size());
                break block22;
              }
              CMIUser targetUser = (CMIUser) userIterator.next();
              object = new Snd().setSender(commandSender).setTarget(targetUser);
              double withdrawAmount = d;
              if (isPercent) {
                withdrawAmount = string != null ? sourceBalance : targetUser.getBalance();
                withdrawAmount = withdrawAmount * d / 100.0;
                if (minClamp != null && withdrawAmount < minClamp) {
                  withdrawAmount = minClamp;
                }
                if (maxClamp != null && withdrawAmount > maxClamp) {
                  withdrawAmount = maxClamp;
                }
              }
              grandTotal = grandTotal + withdrawAmount;
              double balanceBefore = targetUser.getEconomyAccount().getBalance();
              targetUser.getEconomyAccount().withdraw(withdrawAmount);
              if (targetUser.getEconomyAccount().getBalance() == balanceBefore
                  || !targetUser.isOnline()
                  || bl) continue;
              CMILC.info(
                  (Object) this,
                  targetUser,
                  "lostMoney",
                  object,
                  "[amount]",
                  cMI.getEconomyManager().format(withdrawAmount),
                  "[money]",
                  targetUser.getFormatedBalance());
            }
          }
        default:
          {
            return false;
          }
      }
      cMI.getDbManager().startRapidSave();
      cMI.getDbManager().start();
      return true;
    }
    CMIUser cMIUser = cMI.getUser(commandSender, (String) targetName, this, false);
    if (cMIUser == null) {
      return null;
    }
    targetUsers.add(cMIUser);
    if (d < 0.0) {
      return false;
    }
    Snd snd = new Snd().setSender(commandSender).setTarget(cMIUser);
    switch (action) {
      case give:
      case add:
        {
          double depositAmountSingle = d;
          if (isPercent) {
            CMIUser percentSourceUser;
            depositAmountSingle =
                string != null
                    ? ((percentSourceUser = cMI.getPlayerManager().getUser(string)) != null
                        ? percentSourceUser.getBalance()
                        : depositAmountSingle)
                    : cMIUser.getBalance();
            depositAmountSingle = depositAmountSingle * d / 100.0;
            if (minClamp != null && depositAmountSingle < minClamp) {
              depositAmountSingle = minClamp;
            }
            if (maxClamp != null && depositAmountSingle > maxClamp) {
              depositAmountSingle = maxClamp;
            }
          }
          cMIUser.deposit(depositAmountSingle);
          cMIUser.addForDelayedSave();
          if (!bl) {
            CMILC.info(
                (Object) this,
                commandSender,
                "newBalance",
                snd,
                "[money]",
                cMIUser.getFormatedBalance());
          }
          if (!cMIUser.isOnline()) return true;
          if (bl) return true;
          CMILC.info(
              (Object) this,
              cMIUser,
              "gotMoneyGive",
              snd,
              "[amount]",
              cMI.getEconomyManager().format(depositAmountSingle),
              "[money]",
              cMIUser.getFormatedBalance());
          return true;
        }
      case pay:
      case send:
        {
          String[] payArgs = Arrays.copyOfRange(stringArray, 1, stringArray.length);
          cMI.getCommandManager()
              .getCommands()
              .get(pay.class.getSimpleName())
              .getCmdClass()
              .perform(cMI, commandSender, payArgs);
          return true;
        }
      case set:
        {
          cMIUser.getEconomyAccount().setBalance(d);
          cMIUser.addForDelayedSave();
          if (!bl) {
            CMILC.info(
                (Object) this,
                commandSender,
                "newBalance",
                snd,
                "[money]",
                cMIUser.getFormatedBalance());
          }
          if (!cMIUser.isOnline()) return true;
          if (bl) return true;
          if (cMIUser.getName().equalsIgnoreCase(commandSender.getName())) return true;
          CMILC.info(
              (Object) this, cMIUser, "newBalance", snd, "[money]", cMIUser.getFormatedBalance());
          return true;
        }
      case take:
        {
          double withdrawAmountSingle = d;
          if (isPercent) {
            withdrawAmountSingle =
                string != null
                    ? ((object = cMI.getPlayerManager().getUser(string)) != null
                        ? ((CMIUser) object).getBalance()
                        : withdrawAmountSingle)
                    : cMIUser.getBalance();
            withdrawAmountSingle = withdrawAmountSingle * d / 100.0;
            if (minClamp != null && withdrawAmountSingle < minClamp) {
              withdrawAmountSingle = minClamp;
            }
            if (maxClamp != null && withdrawAmountSingle > maxClamp) {
              withdrawAmountSingle = maxClamp;
            }
          }
          double balanceBeforeSingle = cMIUser.getEconomyAccount().getBalance();
          cMIUser.getEconomyAccount().withdraw(withdrawAmountSingle);
          cMIUser.addForDelayedSave();
          if (!bl && !cMIUser.getName().equalsIgnoreCase(commandSender.getName())) {
            CMILC.info(
                (Object) this,
                commandSender,
                "newBalance",
                snd,
                "[money]",
                cMIUser.getFormatedBalance());
          }
          if (cMIUser.getEconomyAccount().getBalance() == balanceBeforeSingle) return true;
          if (!cMIUser.isOnline()) return true;
          if (bl) return true;
          CMILC.info(
              (Object) this,
              cMIUser,
              "lostMoney",
              snd,
              "[amount]",
              cMI.getEconomyManager().format(withdrawAmountSingle),
              "[money]",
              cMIUser.getFormatedBalance());
          return true;
        }
    }
    return true;
  }

  private static enum Actions {
    pay(false),
    send(false),
    give(true),
    add(true),
    take(true),
    set(true);

    private boolean adminCmd;

    private Actions(boolean bl) {
      this.adminCmd = bl;
    }

    public static Actions getByName(String string) {
      Actions[] actionsArray = Actions.values();
      int n = actionsArray.length;
      int argIndex = 0;
      while (argIndex < n) {
        Actions action = actionsArray[argIndex];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
        ++argIndex;
      }
      return null;
    }

    public boolean isAdminCmd() {
      return this.adminCmd;
    }
  }

  private static enum TargetPlayers {
    single,
    all,
    allonline,
    alloffline;

    public static TargetPlayers getByName(String string) {
      TargetPlayers[] targetPlayersArray = TargetPlayers.values();
      int n = targetPlayersArray.length;
      int argIndex = 0;
      while (argIndex < n) {
        TargetPlayers targetPlayers = targetPlayersArray[argIndex];
        if (targetPlayers.name().equalsIgnoreCase(string)) {
          return targetPlayers;
        }
        ++argIndex;
      }
      return null;
    }
  }
}
