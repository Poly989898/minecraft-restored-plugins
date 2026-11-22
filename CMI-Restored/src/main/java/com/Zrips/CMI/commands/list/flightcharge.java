/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.GUI.CMIGui
 *  net.Zrips.CMILib.GUI.CMIGuiButton
 *  net.Zrips.CMILib.GUI.GUIManager$GUIClickType
 *  net.Zrips.CMILib.GUI.GUIManager$InvType
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class flightcharge implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("charge", "{gcp}Current charge: {gcs}[charge]{gcp}/{gcs}[max]");
    configReader.get("changed", "{gcp}Your flight charge level changed to {gcs}[charge]");
    configReader.get("newCharge", "{gcp}New charge level: {gcs}[charge]");
    configReader.get(
        "bossbar",
        "{gcu}>> {gcp}Flight Charge {gcs}[charge][changeAmount]{gcp}/{gcs}[max] {gcu}<<");
    configReader.get("guiTitle", "{gcp}Flight recharge");
    configReader.get("buttonExp", "{gcp}Recharge with exp");
    configReader.get("buttonMoney", "{gcp}Recharge with money");
    configReader.get("max", "{gcp}Already fully charged");
    configReader.get(
        "amounts",
        Arrays.asList(
            "{gcp}Left click +1. Cost: {gcs}[amount1]",
            "{gcp}Shift left +10. Cost: {gcs}[amount10]",
            "{gcp}Right click +100. Cost: {gcs}[amount100]",
            "{gcp}Shift right +1000. Cost: {gcs}[amount1000]",
            "{gcp}Middle Mouse +10000. Cost: {gcs}[amount10000]",
            ""));
    configReader.get("changeAmount", "{gcp}({gcs}[amount]{gcp})");
    configReader.get(
        "cost", "{gcp}Charged {gcs}[amount] {gcp}for {gcs}[charge] {gcp}flight charges");
  }

  @Override
  @CAnnotation(
      info = "Manage and check flight charges",
      args = "(add/take/set/show/expcharge/moneycharge/recharge) (playerName) (amount) (-s)",
      tab = {"add%%take%%set%%show%%expcharge%%moneycharge%%recharge", "playername"},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {2, 3, 4},
      customAlias = {"!+recharge:recharge", "!fcharge"},
      alias = true,
      modules = {"flightcharge"},
      others = true)
  public Boolean perform(final CMI cMI, final CommandSender commandSender, String[] cMIGui) {
    boolean bl = false;
    Integer n = null;
    String string = null;
    Action action = Action.show;
    String[] args = cMIGui;
    int argCount = args.length;
    int argIndex = 0;
    while (argIndex < argCount) {
      String arg = args[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (string == null && cMI.getPlayerManager().getUser(arg) != null) {
              string = arg;
              break;
            }
            try {
              n = Integer.parseInt(arg);
              break;
            } catch (NumberFormatException numberFormatException) {
              if (Action.getByName(arg) != null) {
                action = Action.getByName(arg);
                break;
              }
              string = arg;
            }
          }
      }
      ++argIndex;
    }
    final boolean silent = bl;
    final CMIUser cMIUser = cMI.getUser(commandSender, string, this);
    if (cMIUser == null) {
      return null;
    }
    if (n == null) {
      n = 0;
    }
    if (action == null) {
      action = Action.show;
    }
    switch (action) {
      case add:
      case take:
      case set:
        {
          if (PermissionsManager.CMIPerm.command_flightcharge_admin.hasPermission(
              commandSender, true, new String[0])) break;
          return null;
        }
      default:
        {
          break;
        }
      case recharge:
        {
          if (!(commandSender instanceof Player)
              || cMI.getFlightChargeManager().getMoneyRechargeCost() <= 0.0
                  && cMI.getFlightChargeManager().getExpRechargeCost() <= 0.0) {
            return false;
          }
          Player player = (Player) commandSender;
          CMIGui flightGui = new CMIGui(player);
          flightGui.setTitle(CMILC.getIM(this, "guiTitle", new Object[0]));
          flightGui.addLock(GUIManager.InvType.Gui);
          flightGui.addLock(GUIManager.InvType.Main);
          CMIGuiButton chargeButton =
              new CMIGuiButton(3, new ItemStack(Material.DIAMOND)) {

                public void click(GUIManager.GUIClickType gUIClickType) {
                  switch (gUIClickType) {
                    case Left:
                      {
                        flightcharge.moneyRecharge(commandSender, cMIUser, 1, silent);
                        break;
                      }
                    case LeftShift:
                      {
                        flightcharge.moneyRecharge(commandSender, cMIUser, 10, silent);
                        break;
                      }
                    case MiddleMouse:
                    case Q:
                      {
                        flightcharge.moneyRecharge(commandSender, cMIUser, 10000, silent);
                        break;
                      }
                    case Right:
                      {
                        flightcharge.moneyRecharge(commandSender, cMIUser, 100, silent);
                        break;
                      }
                    case RightShift:
                      {
                        flightcharge.moneyRecharge(commandSender, cMIUser, 1000, silent);
                        break;
                      }
                  }
                  flightcharge.inform(commandSender, cMIUser, silent);
                  this.updateLooks();
                  this.update();
                }

                public void updateLooks() {
                  this.setName(
                      CMILC.getIM(
                          flightcharge.class.getSimpleName(), "buttonMoney", new Object[0]));
                  this.clearLore();
                  this.addLore(
                      flightcharge.updateLore(
                          CMILC.getIML(
                              flightcharge.class.getSimpleName(), "amounts", new Object[0]),
                          (int)
                              ((double) cMIUser.getFlightCharge().getMax()
                                  - cMIUser.getFlightCharge().getCharge()),
                          CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost(),
                          true));
                  this.addLore(
                      CMILC.getIM(
                          flightcharge.class.getSimpleName(),
                          "charge",
                          "[charge]",
                          (int) cMIUser.getFlightCharge().getCharge(),
                          "[max]",
                          cMI.getFlightChargeManager().getMaxChargeLevel()));
                }
              };
          if (cMI.getFlightChargeManager().getMoneyRechargeCost() > 0.0) {
            flightGui.addButton((CMIGuiButton) chargeButton);
          }
          chargeButton =
              new CMIGuiButton(5, CMIMaterial.EXPERIENCE_BOTTLE.newItemStack()) {

                public void click(GUIManager.GUIClickType gUIClickType) {
                  switch (gUIClickType) {
                    case Left:
                      {
                        flightcharge.expRecharge(commandSender, cMIUser, 1, silent);
                        break;
                      }
                    case LeftShift:
                      {
                        flightcharge.expRecharge(commandSender, cMIUser, 10, silent);
                        break;
                      }
                    case MiddleMouse:
                    case Q:
                      {
                        flightcharge.expRecharge(commandSender, cMIUser, 10000, silent);
                        break;
                      }
                    case Right:
                      {
                        flightcharge.expRecharge(commandSender, cMIUser, 100, silent);
                        break;
                      }
                    case RightShift:
                      {
                        flightcharge.expRecharge(commandSender, cMIUser, 1000, silent);
                        break;
                      }
                  }
                  flightcharge.inform(commandSender, cMIUser, silent);
                  this.updateLooks();
                  this.update();
                }

                public void updateLooks() {
                  this.setName(
                      CMILC.getIM(flightcharge.class.getSimpleName(), "buttonExp", new Object[0]));
                  this.clearLore();
                  this.addLore(
                      flightcharge.updateLore(
                          CMILC.getIML(
                              flightcharge.class.getSimpleName(), "amounts", new Object[0]),
                          (int)
                              ((double) cMIUser.getFlightCharge().getMax()
                                  - cMIUser.getFlightCharge().getCharge()),
                          CMI.getInstance().getFlightChargeManager().getExpRechargeCost(),
                          false));
                  this.addLore(
                      CMILC.getIM(
                          flightcharge.class.getSimpleName(),
                          "charge",
                          "[charge]",
                          (int) cMIUser.getFlightCharge().getCharge(),
                          "[max]",
                          cMI.getFlightChargeManager().getMaxChargeLevel()));
                }
              };
          if (cMI.getFlightChargeManager().getExpRechargeCost() > 0.0) {
            flightGui.addButton((CMIGuiButton) chargeButton);
          }
          flightGui.fillEmptyButtons();
          flightGui.open();
          return true;
        }
    }
    switch (action) {
      case expcharge:
        {
          if (flightcharge.expRecharge(commandSender, cMIUser, n, silent)) break;
          return null;
        }
      case moneycharge:
        {
          if (flightcharge.moneyRecharge(commandSender, cMIUser, n, silent)) break;
          return null;
        }
      case add:
        {
          cMIUser.getFlightCharge().addCharge(n.doubleValue());
          cMIUser.saveIfOffline();
          break;
        }
      case set:
        {
          cMIUser.getFlightCharge().setCharge(n.doubleValue());
          cMIUser.saveIfOffline();
          break;
        }
      case show:
        {
          CMILC.info(
              (Object) this,
              commandSender,
              "charge",
              "[charge]",
              (int) cMIUser.getFlightCharge().getCharge(),
              "[max]",
              cMI.getFlightChargeManager().getMaxChargeLevel());
          return true;
        }
      case take:
        {
          cMIUser.getFlightCharge().takeCharge(n.doubleValue());
          cMIUser.saveIfOffline();
          break;
        }
    }
    flightcharge.inform(commandSender, cMIUser, silent);
    return true;
  }

  private static List<String> updateLore(List<String> lore, int n, double d, boolean bl) {
    int index = 0;
    while (index < lore.size()) {
      String string = lore.get(index);
      string = string.replace("[amount1]", flightcharge.format(1.0, n, d, bl));
      string = string.replace("[amount10]", flightcharge.format(10.0, n, d, bl));
      string = string.replace("[amount100]", flightcharge.format(100.0, n, d, bl));
      string = string.replace("[amount1000]", flightcharge.format(1000.0, n, d, bl));
      string = string.replace("[amount10000]", flightcharge.format(10000.0, n, d, bl));
      lore.set(index, string);
      ++index;
    }
    return lore;
  }

  private static String format(double d, double cap, double rechargeCost, boolean bl) {
    if (bl) {
      return cap >= d
          ? CMI.getInstance().getEconomyManager().format(d * rechargeCost)
          : CMI.getInstance().getEconomyManager().format(cap * rechargeCost);
    }
    return cap >= d
        ? String.valueOf((double) ((int) (d * rechargeCost * 100.0)) / 100.0)
        : String.valueOf((double) ((int) (cap * rechargeCost * 100.0)) / 100.0);
  }

  private static void inform(CommandSender commandSender, CMIUser cMIUser, boolean bl) {
    if (!bl) {
      if (cMIUser.isOnline() || cMIUser.getName().equals(commandSender.getName())) {
        CMILC.info(
            (Object) flightcharge.class.getSimpleName(),
            cMIUser,
            "changed",
            "[charge]",
            (int) cMIUser.getFlightCharge().getCharge());
        CMI.getInstance().getFlightChargeManager().updateBossBar(cMIUser);
      }
      if (!cMIUser.getName().equals(commandSender.getName())) {
        CMILC.info(
            flightcharge.class.getSimpleName(),
            commandSender,
            "newCharge",
            "[charge]",
            (int) cMIUser.getFlightCharge().getCharge());
      }
    }
    if (cMIUser.isOnline()
        && cMIUser.getFlightCharge().getCharge() > 0.0
        && CMI.getInstance().getFlightChargeManager().isFlightChargeEnabledByDefault()) {
      cMIUser.getFlightCharge().setEnabled(true);
      cMIUser.getPlayer().setAllowFlight(true);
    }
  }

  public static boolean expRecharge(CommandSender commandSender, CMIUser cMIUser, int n) {
    return flightcharge.expRecharge(commandSender, cMIUser, n, false);
  }

  public static boolean expRecharge(
      CommandSender commandSender, CMIUser cMIUser, int n, boolean bl) {
    Double d;
    if (CMI.getInstance().getFlightChargeManager().getExpRechargeCost() <= 0.0 || n <= 0) {
      return false;
    }
    double expCost = CMI.getInstance().getFlightChargeManager().getExpRechargeCost();
    if (cMIUser.getTotalExp() == 0L || (double) cMIUser.getTotalExp() < expCost) {
      if (!bl && !cMIUser.getFlightCharge().isInformedMissingExp()) {
        CMILC.Information_NotEnoughExp.sendMessage(
            commandSender == null ? cMIUser : commandSender, "[Exp]", expCost, "[exp]", expCost);
        cMIUser.getFlightCharge().setInformedMissingExp(true);
      }
      return false;
    }
    double totalExpCost =
        (double) n * CMI.getInstance().getFlightChargeManager().getExpRechargeCost();
    if ((double) cMIUser.getTotalExp() < totalExpCost) {
      n =
          (int)
              ((double) cMIUser.getTotalExp()
                  / CMI.getInstance().getFlightChargeManager().getExpRechargeCost());
      totalExpCost = (double) n * CMI.getInstance().getFlightChargeManager().getExpRechargeCost();
    }
    if ((d =
            Double.valueOf(
                (double) cMIUser.getFlightCharge().getMax()
                    - cMIUser.getFlightCharge().getCharge()))
        < 1.0) {
      if (!bl) {
        if (commandSender == null) {
          CMILC.info((Object) flightcharge.class.getSimpleName(), cMIUser, "max", new Object[0]);
        } else {
          CMILC.info(flightcharge.class.getSimpleName(), commandSender, "max", new Object[0]);
        }
      }
      return false;
    }
    if (d < (double) n) {
      n = d.intValue();
      totalExpCost = (double) n * CMI.getInstance().getFlightChargeManager().getExpRechargeCost();
    }
    if ((int) totalExpCost == 0) {
      double expRatio = 1.0 / CMI.getInstance().getFlightChargeManager().getExpRechargeCost();
      n = (int) expRatio;
      totalExpCost = 1.0;
    }
    cMIUser.takeExp((int) totalExpCost);
    cMIUser.getFlightCharge().addCharge(n);
    cMIUser.getFlightCharge().setInformedMissingExp(false);
    if (cMIUser.getFlightCharge().getCharge() > (double) cMIUser.getFlightCharge().getMax()) {
      cMIUser.getFlightCharge().setCharge(cMIUser.getFlightCharge().getMax());
    }
    if (!bl && commandSender == null) {
      CMILC.info(
          (Object) flightcharge.class.getSimpleName(),
          cMIUser,
          "cost",
          "[amount]",
          (int) totalExpCost,
          "[charge]",
          n);
    }
    cMIUser.saveIfOffline();
    return true;
  }

  public static boolean moneyRecharge(
      CommandSender commandSender, CMIUser cMIUser, int n, boolean bl) {
    Double d;
    if (CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost() <= 0.0 || n <= 0) {
      return false;
    }
    if (cMIUser.getBalance() <= 0.0
        || cMIUser.getBalance()
            < CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost()) {
      if (!bl && !cMIUser.getFlightCharge().isInformedMissingMoney()) {
        if (commandSender == null) {
          LC.econ_noMoney.sendMessage((Object) cMIUser.getPlayer(), new Object[0]);
        } else {
          LC.econ_noMoney.sendMessage((Object) commandSender, new Object[0]);
        }
        cMIUser.getFlightCharge().setInformedMissingMoney(true);
      }
      return false;
    }
    double moneyCost =
        (double) n * CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost();
    if (cMIUser.getBalance() < moneyCost) {
      n =
          (int)
              (cMIUser.getBalance()
                  / CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost());
      moneyCost = (double) n * CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost();
    }
    if ((d =
            Double.valueOf(
                (double) cMIUser.getFlightCharge().getMax()
                    - cMIUser.getFlightCharge().getCharge()))
        < 1.0) {
      if (!bl) {
        if (commandSender == null) {
          CMILC.info((Object) flightcharge.class.getSimpleName(), cMIUser, "max", new Object[0]);
        } else {
          CMILC.info(flightcharge.class.getSimpleName(), commandSender, "max", new Object[0]);
        }
      }
      return false;
    }
    if (d < (double) n) {
      n = d.intValue();
      moneyCost = (double) n * CMI.getInstance().getFlightChargeManager().getMoneyRechargeCost();
    }
    cMIUser.withdraw(moneyCost);
    cMIUser.getFlightCharge().addCharge(n);
    cMIUser.getFlightCharge().setInformedMissingMoney(false);
    if (cMIUser.getFlightCharge().getCharge() > (double) cMIUser.getFlightCharge().getMax()) {
      cMIUser.getFlightCharge().setCharge(cMIUser.getFlightCharge().getMax());
    }
    if (!bl && commandSender == null) {
      CMILC.info(
          (Object) flightcharge.class.getSimpleName(),
          cMIUser,
          "cost",
          "[amount]",
          CMI.getInstance().getEconomyManager().format(moneyCost),
          "[charge]",
          n);
    }
    cMIUser.saveIfOffline();
    return true;
  }

  private static enum Action {
    add,
    take,
    set,
    show,
    expcharge,
    moneycharge,
    recharge;

    public static Action getByName(String string) {
      Action[] actionArray = Action.values();
      int n = actionArray.length;
      int index = 0;
      while (index < n) {
        Action action = actionArray[index];
        if (string.equalsIgnoreCase(action.name())) {
          return action;
        }
        ++index;
      }
      return null;
    }
  }
}
