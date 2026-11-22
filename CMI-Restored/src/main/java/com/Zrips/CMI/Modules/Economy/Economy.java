package com.Zrips.CMI.Modules.Economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

public class Economy {
  public static CMI plugin;
  private static final String ecoName = "CMIEconomy";

  public Economy(CMI cmi) {
    plugin = cmi;
  }

  public boolean isEnabled() {
    return plugin != null;
  }

  public static String getName() {
    return "CMIEconomy";
  }

  private static String TrA(double number) {
    String string = "###,###.###";
    if (plugin != null) {
      number = Math.floor(number * 100.0 + 0.5) / 100.0;
      String formattedBalance = "" + number;
      EconomyManager.WorldGroup worldGroup = plugin.getEconomyManager().getDefaultGroup();
      if (worldGroup.isUseShortNumbers()) {
        formattedBalance =
            CMIEconomyAcount.format(number, worldGroup.getShortNumbersSuffixes(), null);
        return worldGroup
            .getPlacing()
            .replace("[money]", formattedBalance)
            .replace("[symbol]", worldGroup.getCurrencySymbol());
      }

      try {
        DecimalFormat decimalFormat = CMI.getInstance().getEconomyManager().getMoneyFormat();
        formattedBalance = decimalFormat.format(number);
      } catch (Exception exception) {
      }

      return CMI.getInstance()
          .getEconomyManager()
          .getDefaultGroup()
          .getPlacing()
          .replace("[money]", formattedBalance)
          .replace("[symbol]", plugin.getEconomyManager().getWorldGroup(null).getCurrencySymbol());
    } else {
      DecimalFormat formatter = new DecimalFormat(string);
      return formatter.format(number);
    }
  }

  public static String format(double number) {
    return TrA(number);
  }

  public static String currencyNameSingular() {
    return "";
  }

  public static String currencyNamePlural() {
    return "";
  }

  public static double getBalance(String string) {
    if (plugin == null) {
      return 0.0;
    } else if (plugin.getEconomyManager().isTownyAccount(string)) {
      CMIUser cmiUser = plugin.getPlayerManager().getUser(string, false, true, true, true);
      return getAccountBalance(cmiUser);
    } else {
      CMIUser accountUser = plugin.getPlayerManager().getUser(string, false, true, false, true);
      return getAccountBalance(accountUser);
    }
  }

  public static double getBalance(OfflinePlayer offlinePlayer) {
    if (plugin == null) {
      return 0.0;
    }

    CMIUser cmiUser = plugin.getPlayerManager().getUser(offlinePlayer);
    return getAccountBalance(cmiUser);
  }

  private static double getAccountBalance(CMIUser cmiUser) {
    return cmiUser == null ? 0.0 : cmiUser.getBalance();
  }

  public static EconomyResponse withdrawPlayer(String string, double number) {
    if (plugin == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Plugin is not loaded");
    } else if (plugin.getEconomyManager().isTownyAccount(string)) {
      CMIUser cmiUser = plugin.getPlayerManager().getUser(string, false, true, true, true);
      return withdraw(cmiUser, number);
    } else {
      CMIUser accountUser = plugin.getPlayerManager().getUser(string, false, true, false, true);
      return withdraw(accountUser, number);
    }
  }

  public static EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double number) {
    if (plugin == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Plugin is not loaded");
    }

    CMIUser cmiUser = plugin.getPlayerManager().getUser(offlinePlayer);
    return withdraw(cmiUser, number);
  }

  private static EconomyResponse withdraw(CMIUser cmiUser, double number) {
    if (cmiUser == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Account doesn't exist");
    } else if (number < 0.0) {
      return new EconomyResponse(
          0.0, cmiUser.getBalance(), ResponseType.FAILURE, "Cannot withdraw negative funds");
    } else if (cmiUser.hasMoney(number)) {
      cmiUser.withdraw(number);
      return new EconomyResponse(number, cmiUser.getBalance(), ResponseType.SUCCESS, "");
    } else {
      return new EconomyResponse(
          0.0, cmiUser.getBalance(), ResponseType.FAILURE, "Insufficient funds");
    }
  }

  public static EconomyResponse depositPlayer(String string, double number) {
    if (plugin == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Plugin is not loaded");
    } else if (plugin.getEconomyManager().isTownyAccount(string)) {
      CMIUser cmiUser = plugin.getPlayerManager().getUser(string, false, true, true, true);
      return deposit(cmiUser, number);
    } else {
      CMIUser accountUser = plugin.getPlayerManager().getUser(string, false, true, false, true);
      return deposit(accountUser, number);
    }
  }

  public static EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double number) {
    if (plugin == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Plugin is not loaded");
    }

    CMIUser cmiUser = plugin.getPlayerManager().getUser(offlinePlayer);
    return deposit(cmiUser, number);
  }

  private static EconomyResponse deposit(CMIUser cmiUser, double number) {
    if (cmiUser == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Account doesn't exist");
    }

    if (number < 0.0) {
      return new EconomyResponse(
          0.0, cmiUser.getBalance(), ResponseType.FAILURE, "Cannot deposit negative funds");
    }

    Double balance = cmiUser.getBalance();
    cmiUser.deposit(number);
    return new EconomyResponse(number, cmiUser.getBalance(), ResponseType.SUCCESS, "");
  }

  public static boolean has(String string, double number) {
    return getBalance(string) >= number;
  }

  public static boolean has(OfflinePlayer offlinePlayer, double number) {
    return getBalance(offlinePlayer) >= number;
  }

  public static EconomyResponse createBank(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse createBank(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse deleteBank(String string) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse bankHas(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse bankWithdraw(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse bankDeposit(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse isBankOwner(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse isBankOwner(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse isBankMember(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse isBankMember(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static EconomyResponse bankBalance(String string) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public static List<String> getBanks() {
    return new ArrayList<>();
  }

  public static boolean hasBankSupport() {
    return false;
  }

  public static boolean hasAccount(String string) {
    return plugin.getPlayerManager().getUser(string) != null;
  }

  public static boolean hasAccount(OfflinePlayer offlinePlayer) {
    return plugin.getPlayerManager().getUser(offlinePlayer) != null;
  }

  @Deprecated
  public static boolean createPlayerAccount(String string) {
    if (plugin == null) {
      return false;
    }

    if (hasAccount(string)) {
      return true;
    }

    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(string, true, false, true, true);
    return cmiUser != null;
  }

  public static boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
    return plugin == null ? false : plugin.getPlayerManager().getUser(offlinePlayer) != null;
  }

  public static int fractionalDigits() {
    return -1;
  }

  public static boolean hasAccount(String string, String worldName) {
    return hasAccount(string);
  }

  public static boolean hasAccount(OfflinePlayer offlinePlayer, String string) {
    return hasAccount(offlinePlayer);
  }

  public static double getBalance(String string, String worldName) {
    return getBalance(string);
  }

  public static double getBalance(OfflinePlayer offlinePlayer, String string) {
    return getBalance(offlinePlayer);
  }

  public static boolean has(String string, String worldName, double number) {
    return has(string, number);
  }

  public static boolean has(OfflinePlayer offlinePlayer, String string, double number) {
    return has(offlinePlayer, number);
  }

  public static EconomyResponse withdrawPlayer(String string, String worldName, double number) {
    return withdrawPlayer(string, number);
  }

  public static EconomyResponse withdrawPlayer(
      OfflinePlayer offlinePlayer, String string, double number) {
    return withdrawPlayer(offlinePlayer, number);
  }

  public static EconomyResponse depositPlayer(String string, String worldName, double number) {
    return depositPlayer(string, number);
  }

  public static EconomyResponse depositPlayer(
      OfflinePlayer offlinePlayer, String string, double number) {
    return depositPlayer(offlinePlayer, number);
  }

  public static boolean createPlayerAccount(String string, String worldName) {
    return createPlayerAccount(string);
  }

  public static boolean createPlayerAccount(OfflinePlayer offlinePlayer, String string) {
    return createPlayerAccount(offlinePlayer);
  }
}
