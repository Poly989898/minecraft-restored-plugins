package com.Zrips.CMI.Modules.Economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class VaultHandler extends AbstractEconomy {
  public CMI cmi;
  Plugin plugin;
  private final String name = "CMIEconomy";

  public VaultHandler(CMI cmiInstance) {
    if (this.cmi == null) {
      this.cmi = cmiInstance;
      VaultHandler.EconomyServerListener economyServerListener =
          new VaultHandler.EconomyServerListener(this);
      Bukkit.getServer().getPluginManager().registerEvents(economyServerListener, cmiInstance);
    }
  }

  public VaultHandler(Plugin hostPlugin) {
    this.plugin = hostPlugin;
    Bukkit.getServer()
        .getPluginManager()
        .registerEvents(new VaultHandler.EconomyServerListener(this), hostPlugin);
    if (this.cmi == null) {
      Plugin cmiPlugin = hostPlugin.getServer().getPluginManager().getPlugin("CMI");
      if (cmiPlugin != null && cmiPlugin.isEnabled()) {
        this.cmi = (CMI) cmiPlugin;
        CMI.getInstance()
            .consoleMessage(
                String.format(
                    "[%s][Economy] %s hooked.",
                    hostPlugin.getDescription().getName(), "CMIEconomy"));
      }
    }
  }

  public void setCMI(CMI cmiInstance) {
    this.cmi = cmiInstance;
  }

  public boolean isEnabled() {
    return this.cmi != null;
  }

  public String getName() {
    return "CMIEconomy";
  }

  public String TrA(double number) {
    DecimalFormat decimalFormat = CMI.getInstance().getEconomyManager().getMoneyFormat();
    return decimalFormat.format(number);
  }

  public String format(double number) {
    return this.TrA(number);
  }

  public String currencyNameSingular() {
    return "";
  }

  public String currencyNamePlural() {
    return "";
  }

  public double getBalance(String string) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(string, false, true, false, true);
    return getAccountBalance(cmiUser);
  }

  public double getBalance(OfflinePlayer offlinePlayer) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(offlinePlayer);
    return getAccountBalance(cmiUser);
  }

  private static double getAccountBalance(CMIUser cmiUser) {
    return cmiUser == null ? 0.0 : cmiUser.getBalance();
  }

  public EconomyResponse withdrawPlayer(String string, double number) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(string, false, true, false, true);
    return withdraw(cmiUser, number);
  }

  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double number) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(offlinePlayer);
    return withdraw(cmiUser, number);
  }

  private static EconomyResponse withdraw(CMIUser cmiUser, double number) {
    if (cmiUser == null) {
      return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Account doesn't exist");
    } else if (number < 0.0) {
      return new EconomyResponse(
          0.0, cmiUser.getBalance(), ResponseType.FAILURE, "Cannot withdraw negative funds");
    } else if (cmiUser.hasMoney(number)) {
      Double balance = cmiUser.getBalance();
      cmiUser.withdraw(number);
      return new EconomyResponse(number, cmiUser.getBalance(), ResponseType.SUCCESS, "");
    } else {
      return new EconomyResponse(
          0.0, cmiUser.getBalance(), ResponseType.FAILURE, "Insufficient funds");
    }
  }

  public EconomyResponse depositPlayer(String string, double number) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(string, false, true, false, true);
    return deposit(cmiUser, number);
  }

  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double number) {
    CMIUser cmiUser = this.cmi.getPlayerManager().getUser(offlinePlayer);
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

  public boolean has(String string, double number) {
    return this.getBalance(string) >= number;
  }

  public boolean has(OfflinePlayer offlinePlayer, double number) {
    return this.getBalance(offlinePlayer) >= number;
  }

  public EconomyResponse createBank(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse createBank(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse deleteBank(String string) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse bankHas(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse bankWithdraw(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse bankDeposit(String string, double number) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse isBankOwner(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse isBankOwner(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse isBankMember(String string, String playerName) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse isBankMember(String string, OfflinePlayer offlinePlayer) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public EconomyResponse bankBalance(String string) {
    return new EconomyResponse(
        0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "CMI currently don't support bank's");
  }

  public List<String> getBanks() {
    return new ArrayList<>();
  }

  public boolean hasBankSupport() {
    return false;
  }

  public boolean hasAccount(String string) {
    return CMI.getInstance().getPlayerManager().getUser(string) != null;
  }

  public boolean hasAccount(OfflinePlayer offlinePlayer) {
    return CMI.getInstance().getPlayerManager().getUser(offlinePlayer) != null;
  }

  public boolean createPlayerAccount(String string) {
    return this.hasAccount(string)
        ? true
        : this.cmi.getPlayerManager().getUser(string, true, false, true, true) != null;
  }

  public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
    return this.hasAccount(offlinePlayer)
        ? true
        : this.cmi.getPlayerManager().getUser(offlinePlayer) != null;
  }

  public int fractionalDigits() {
    return -1;
  }

  public boolean hasAccount(String string, String worldName) {
    return this.hasAccount(string);
  }

  public boolean hasAccount(OfflinePlayer offlinePlayer, String string) {
    return this.hasAccount(offlinePlayer);
  }

  public double getBalance(String string, String worldName) {
    return this.getBalance(string);
  }

  public double getBalance(OfflinePlayer offlinePlayer, String string) {
    return this.getBalance(offlinePlayer);
  }

  public boolean has(String string, String worldName, double number) {
    return this.has(string, number);
  }

  public boolean has(OfflinePlayer offlinePlayer, String string, double number) {
    return this.has(offlinePlayer, number);
  }

  public EconomyResponse withdrawPlayer(String string, String worldName, double number) {
    return this.withdrawPlayer(string, number);
  }

  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String string, double number) {
    return this.withdrawPlayer(offlinePlayer, number);
  }

  public EconomyResponse depositPlayer(String string, String worldName, double number) {
    return this.depositPlayer(string, number);
  }

  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String string, double number) {
    return this.depositPlayer(offlinePlayer, number);
  }

  public boolean createPlayerAccount(String string, String worldName) {
    return this.createPlayerAccount(string);
  }

  public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String string) {
    return this.createPlayerAccount(offlinePlayer);
  }

  public class EconomyServerListener implements Listener {
    VaultHandler economy = null;

    public EconomyServerListener(VaultHandler vaultHandler) {
      this.economy = vaultHandler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent pluginEnableEvent) {
      if (this.economy.cmi == null) {
        Plugin enabledPlugin = pluginEnableEvent.getPlugin();
        if (enabledPlugin.getDescription().getName().equals("CMI")) {
          CMI enabledCmi = (CMI) enabledPlugin;
          if (enabledCmi.getEconomyManager().isEnabled()) {
            this.economy.cmi = enabledCmi;
            CMI.getInstance()
                .consoleMessage(
                    "&2"
                        + String.format(
                            "[%s][Economy] %s hooked.",
                            VaultHandler.this.plugin.getDescription().getName(), "CMIEconomy"));
          } else {
            VaultHandler.this
                .plugin
                .getServer()
                .getServicesManager()
                .unregister(net.milkbowl.vault.economy.Economy.class, this.economy);
            if (CMI.getInstance().getEconomyManager().getVaultManager() != null) {
              CMI.getInstance().getEconomyManager().getVaultManager().setupVault();
            }
          }
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent pluginDisableEvent) {
      if (this.economy.cmi != null
          && pluginDisableEvent.getPlugin().getDescription().getName().equals("CMI")) {
        try {
          VaultHandler.this
              .plugin
              .getServer()
              .getServicesManager()
              .unregister(net.milkbowl.vault.economy.Economy.class, this.economy);
        } catch (Exception exception) {
        }

        this.economy.cmi = null;

        try {
          CMI.getInstance()
              .consoleMessage(
                  "&2"
                      + String.format(
                          "[%s][Economy] %s unhooked.",
                          VaultHandler.this.plugin.getDescription().getName(), "CMIEconomy"));
        } catch (Exception exception2) {
        }
      }
    }
  }
}
