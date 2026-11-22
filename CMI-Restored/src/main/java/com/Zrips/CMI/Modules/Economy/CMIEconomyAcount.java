package com.Zrips.CMI.Modules.Economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class CMIEconomyAcount {
  private Map<EconomyManager.WorldGroup, Double> balances = null;
  private CMIUser user = null;
  private double lastKnownBalance = 0.0;

  public CMIEconomyAcount(CMIUser cmiUser) {
    this.user = cmiUser;
    this.balances = Collections.synchronizedMap(new HashMap<>());

    for (EconomyManager.WorldGroup worldGroup :
        CMI.getInstance().getEconomyManager().getWorldGroups()) {
      this.balances.put(worldGroup, null);
    }
  }

  public EconomyManager.WorldGroup getCurrentWorldGroup() {
    World world = this.user.getWorld();
    return world == null
        ? CMI.getInstance().getEconomyManager().getWorldGroup("CMIGlobal")
        : CMI.getInstance().getEconomyManager().getWorldGroup(world.getName());
  }

  public String getCurrentWorldGroupName() {
    World world = this.user.getWorld();
    return world == null ? "CMIGlobal" : world.getName();
  }

  public Double setBalance(double number) {
    return this.setBalance(
        CMI.getInstance().getEconomyManager().getDefaultGroup(), number, true, false);
  }

  public Double setBalance(EconomyManager.WorldGroup worldGroup, double number) {
    return this.setBalance(worldGroup, number, true, false);
  }

  public Double setBalance(EconomyManager.WorldGroup worldGroup, double number, boolean flag) {
    return this.setBalance(worldGroup, number, flag, false);
  }

  public Double setBalance(
      EconomyManager.WorldGroup worldGroup, double number, boolean flag, boolean isReload) {
    if (CMI.getInstance().isFullyLoaded()) {
      fireEvent(
          this.user,
          this.balances.get(worldGroup) == null
              ? worldGroup.getStartingAmount()
              : this.balances.get(worldGroup),
          number,
          isReload ? "setBalanceReload" : "setBalance",
          null);
    }

    Double previousBalance = this.balances.put(worldGroup, number);
    if (flag) {
      this.user.saveIfOffline();
    }

    return previousBalance;
  }

  private static void fireEvent(
      CMIUser cmiUser, Double oldBalance, Double newBalance, String string, CMIUser initiator) {
    if (CMI.getInstance().isFullyLoaded()) {
      CMIScheduler.runTaskAsynchronously(
          CMI.getInstance(),
          () -> {
            Bukkit.getServer()
                .getPluginManager()
                .callEvent(
                    new CMIUserBalanceChangeEvent(
                        cmiUser, oldBalance, newBalance, string, initiator));
            CMI.getInstance()
                .getEconomyManager()
                .moneyLog(
                    cmiUser,
                    initiator,
                    string != null && string.equalsIgnoreCase("setBalance")
                        ? oldBalance
                        : newBalance - oldBalance,
                    EconomyManager.CMIMoneyLogType.Unknown,
                    string);
          });
    }
  }

  public Double getBalance() {
    return this.getBalance(null);
  }

  private Double getVaultBalance() {
    if (!CMI.getInstance().getEconomyManager().isVaultEnabled()) {
      return 0.0;
    }

    if (CMI.getInstance()
        .getEconomyManager()
        .getVaultManager()
        .getVaultEconomy()
        .getName()
        .equalsIgnoreCase("CMIEconomy")) {
      CMI.getInstance()
          .consoleMessage(
              "&cYou have disabled CMI economy durring server run, this causes issues. Please"
                  + " perform full server restart!");
      return 0.0;
    }

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.user.getUniqueId());
    if (offlinePlayer == null) {
      return 0.0;
    }

    try {
      if (CMI.isShuttingDown()) {
        return this.lastKnownBalance;
      }

      if (!CMI.getInstance()
          .getEconomyManager()
          .getVaultManager()
          .getVaultEconomy()
          .hasAccount(offlinePlayer)) {
        return 0.0;
      }

      this.lastKnownBalance =
          CMI.getInstance()
              .getEconomyManager()
              .getVaultManager()
              .getVaultEconomy()
              .getBalance(offlinePlayer);
      return this.lastKnownBalance;
    } catch (RuntimeException runtimeException) {
      return 0.0;
    }
  }

  public Double getBalance(String string) {
    if (!CMI.getInstance().getEconomyManager().isEnabled()) {
      return this.getVaultBalance();
    }

    EconomyManager.WorldGroup worldGroup = CMI.getInstance().getEconomyManager().getDefaultGroup();
    Double balance = this.balances.get(worldGroup);
    if (balance == null) {
      if (this.user.isFakeAccount()) {
        this.balances.put(worldGroup, 0.0);
        return 0.0;
      } else {
        this.balances.put(worldGroup, worldGroup.getStartingAmount());
        return worldGroup.getStartingAmount();
      }
    } else {
      return balance;
    }
  }

  public String getFormatedBalance() {
    return this.getFormatedBalance(null);
  }

  public String getFormatedBalance(boolean flag) {
    return this.getFormatedBalance(null, flag);
  }

  public String getFormatedBalance(String string) {
    return this.getFormatedBalance(string, false);
  }

  public String getFormatedBalance(String string, boolean flag) {
    if (!CMI.getInstance().getEconomyManager().isEnabled()) {
      if (CMI.getInstance().getEconomyManager().isVaultEnabled()) {
        if (CMI.getInstance()
            .getEconomyManager()
            .getVaultManager()
            .getVaultEconomy()
            .getName()
            .equalsIgnoreCase("CMIEconomy")) {
          CMI.getInstance()
              .consoleMessage(
                  "&cYou have disabled CMI economy durring server run, this causes issues. Please"
                      + " perform full server restart!");
          return String.valueOf(0.0);
        }

        if (this.user.getPlayer() == null) {
          return CMI.getInstance()
              .getEconomyManager()
              .getVaultManager()
              .getVaultEconomy()
              .format(0.0);
        }

        try {
          double number =
              CMI.getInstance()
                  .getEconomyManager()
                  .getVaultManager()
                  .getVaultEconomy()
                  .getBalance(this.user.getPlayer());
          return CMI.getInstance()
              .getEconomyManager()
              .getVaultManager()
              .getVaultEconomy()
              .format(number);
        } catch (Exception exception) {
          return String.valueOf(0.0);
        }
      } else {
        return String.valueOf(0.0);
      }
    } else {
      EconomyManager.WorldGroup worldGroup =
          CMI.getInstance().getEconomyManager().getDefaultGroup();
      Double balance = this.balances.get(worldGroup);
      if (balance == null) {
        this.balances.put(worldGroup, worldGroup.getStartingAmount());
        balance = worldGroup.getStartingAmount();
      }

      balance = Math.floor(balance * 10000.0) / 10000.0;
      String formatted = String.valueOf(balance);
      if (!worldGroup.isUseShortNumbers() && !flag) {
        try {
          DecimalFormat decimalFormat =
              CMI.getInstance().getEconomyManager().getMoneyFormat(string);
          formatted = decimalFormat.format(balance);
        } catch (Exception exception2) {
        }

        return worldGroup
            .getPlacing()
            .replace("[money]", formatted)
            .replace("[symbol]", worldGroup.getCurrencySymbol());
      } else {
        formatted = format(balance, worldGroup.getShortNumbersSuffixes(), string);
        return worldGroup
            .getPlacing()
            .replace("[money]", formatted)
            .replace("[symbol]", worldGroup.getCurrencySymbol());
      }
    }
  }

  public static String format(double number, HashMap<Double, String> hashMap, String string) {
    int value = 0;
    double originalNumber = number;
    BigDecimal bigDecimal = null;
    if (Double.isFinite(number)) {
      bigDecimal = BigDecimal.valueOf(number);
    } else {
      bigDecimal = BigDecimal.valueOf(0.0);
    }

    BigDecimal divisor;
    for (divisor = BigDecimal.valueOf(10.0);
        bigDecimal.doubleValue() >= 10.0;
        bigDecimal = bigDecimal.divide(divisor)) {
      value++;
    }

    BigDecimal power = BigDecimal.valueOf(1.0);

    while (value > 0) {
      power = power.multiply(divisor);
      value--;
    }

    while (power.doubleValue() > 1.0) {
      String suffix = (String) hashMap.get(power.doubleValue());
      if (suffix != null) {
        DecimalFormat decimalFormat = CMI.getInstance().getEconomyManager().getShortMoneyFormat();
        return decimalFormat.format(bigDecimal.doubleValue()) + suffix;
      }

      bigDecimal = bigDecimal.multiply(divisor);
      power = power.divide(divisor);
    }

    if (hashMap.get(1.0) == null) {
      DecimalFormat plainFormat = CMI.getInstance().getEconomyManager().getMoneyFormat(string);
      return plainFormat.format(bigDecimal.doubleValue());
    } else {
      DecimalFormat plainFormat = CMI.getInstance().getEconomyManager().getMoneyFormat(string);
      return plainFormat.format(originalNumber) + (String) hashMap.get(1.0);
    }
  }

  public Double deposit(double number) {
    return this.deposit(null, number);
  }

  public Double deposit(String string, double number) {
    return this.deposit(string, number, null);
  }

  public Double deposit(String string, double number, CMIUser cmiUser) {
    if (!CMI.getInstance().getEconomyManager().isEnabled()) {
      if (CMI.getInstance().getEconomyManager().isVaultEnabled()) {
        if (this.user.getPlayer() == null) {
          return 0.0;
        }

        EconomyResponse value =
            CMI.getInstance()
                .getEconomyManager()
                .getVaultManager()
                .getVaultEconomy()
                .depositPlayer(this.user.getPlayer(), number);
        return value.balance;
      } else {
        return 0.0;
      }
    } else {
      EconomyManager.WorldGroup worldGroup =
          CMI.getInstance().getEconomyManager().getDefaultGroup();
      Double balance = this.balances.get(worldGroup);
      if (balance == null) {
        balance = worldGroup.getStartingAmount();
      }

      if (worldGroup.getMaximumAmount() < balance + number) {
        this.balances.put(worldGroup, worldGroup.getMaximumAmount());
      } else {
        this.balances.put(worldGroup, balance + number);
      }

      fireEvent(this.user, balance, this.getBalance(), "Deposit", cmiUser);
      this.user.addForDelayedSave();
      return this.getBalance();
    }
  }

  public Double withdraw(double number) {
    return this.withdraw(null, number);
  }

  public Double withdraw(String string, double number) {
    return this.withdraw(string, number, null);
  }

  public Double withdraw(String string, double number, CMIUser cmiUser) {
    if (!CMI.getInstance().getEconomyManager().isEnabled()) {
      if (CMI.getInstance().getEconomyManager().isVaultEnabled()) {
        if (this.user.getPlayer() == null) {
          return 0.0;
        }

        EconomyResponse value =
            CMI.getInstance()
                .getEconomyManager()
                .getVaultManager()
                .getVaultEconomy()
                .withdrawPlayer(this.user.getPlayer(), number);
        return value.balance;
      } else {
        return 0.0;
      }
    } else {
      EconomyManager.WorldGroup worldGroup =
          CMI.getInstance().getEconomyManager().getDefaultGroup();
      Double balance = this.balances.get(worldGroup);
      if (balance == null) {
        balance = worldGroup.getStartingAmount();
      }

      if (worldGroup.getMinimalAmount() > balance - number) {
        this.balances.put(worldGroup, worldGroup.getMinimalAmount());
      } else {
        this.balances.put(worldGroup, balance - number);
      }

      fireEvent(this.user, balance, this.getBalance(), "Withdraw", cmiUser);
      this.user.addForDelayedSave();
      return this.getBalance();
    }
  }

  public boolean has(double number) {
    return this.has(null, number);
  }

  public boolean has(String string, double number) {
    if (!CMI.getInstance().getEconomyManager().isEnabled()) {
      if (CMI.getInstance().getEconomyManager().isVaultEnabled()) {
        return this.user.getPlayer() == null
            ? false
            : CMI.getInstance()
                .getEconomyManager()
                .getVaultManager()
                .getVaultEconomy()
                .has(this.user.getPlayer(), number);
      } else {
        return false;
      }
    } else {
      EconomyManager.WorldGroup worldGroup =
          CMI.getInstance().getEconomyManager().getDefaultGroup();
      Double balance = this.balances.get(worldGroup);
      if (balance == null) {
        balance = this.balances.put(worldGroup, worldGroup.getStartingAmount());
      }

      return balance != null && balance >= number;
    }
  }

  public Map<EconomyManager.WorldGroup, Double> getBalances() {
    return this.balances;
  }

  public HashMap<String, Double> getWorldGroupBalancesRounded() {
    HashMap<String, Double> hashMap = new HashMap();

    for (var entry : this.balances.entrySet()) {
      if (entry.getValue() != null) {
        hashMap.put(
            ((EconomyManager.WorldGroup) entry.getKey()).getName(), (Double) entry.getValue());
      }
    }

    return hashMap;
  }

  public HashMap<String, Double> getWorldGroupBalances() {
    HashMap<String, Double> hashMap = new HashMap();

    for (var entry : this.balances.entrySet()) {
      hashMap.put(
          ((EconomyManager.WorldGroup) entry.getKey()).getName(), (Double) entry.getValue());
    }

    return hashMap;
  }

  @Deprecated
  public int updateBalTopPosition() {
    return this.getBalTopPosition();
  }

  @Deprecated
  public int getBalTopPosition() {
    return CMI.getInstance().getEconomyManager().getLeaderboard().getRank(this.user.getUniqueId());
  }

  @Deprecated
  public void setBalTopPosition(int value) {}
}
