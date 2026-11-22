package com.Zrips.CMI.Modules.Economy;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class VaultManager {
  private CMI plugin;
  private String version = "";
  private boolean supportsCmi = false;
  private net.milkbowl.vault.economy.Economy economy = null;

  public VaultManager(CMI cmi) {
    this.plugin = cmi;
  }

  public VaultManager.EconomySetupResponse setupVault() {
    if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
      return VaultManager.EconomySetupResponse.NoVault;
    }

    var registeredServiceProvider =
        this.plugin
            .getServer()
            .getServicesManager()
            .getRegistration(net.milkbowl.vault.economy.Economy.class);
    if (registeredServiceProvider == null) {
      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(),
          ChatColor.RED
              + "[CMI] Vault was found but economy engine is missing. Use one from list provided in"
              + " https://dev.bukkit.org/bukkit-plugins/vault/");
      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(),
          ChatColor.RED + "[CMI] Alternatively you can enable CMI economy in config.yml file");
      return VaultManager.EconomySetupResponse.NoVaultEconomy;
    }

    Plugin vaultPlugin = this.plugin.getServer().getPluginManager().getPlugin("Vault");
    this.version = vaultPlugin.getDescription().getVersion();
    if (vaultPlugin.getDescription().getDescription().contains("CMIEconomy")) {
      this.supportsCmi = true;
    }

    this.economy = (net.milkbowl.vault.economy.Economy) registeredServiceProvider.getProvider();
    if (this.economy.getName().equalsIgnoreCase("CMIEconomy")) {
      return VaultManager.EconomySetupResponse.CMI;
    } else {
      return this.economy != null
          ? VaultManager.EconomySetupResponse.Vault
          : VaultManager.EconomySetupResponse.NoVaultEconomy;
    }
  }

  public net.milkbowl.vault.economy.Economy getVaultEconomy() {
    return this.economy;
  }

  public boolean isVaultEnabled() {
    return this.economy != null;
  }

  public String format(Double amount) {
    if (amount == null) {
      return "0";
    } else {
      return this.economy == null ? String.valueOf(amount) : this.economy.format(amount);
    }
  }

  public String getVersion() {
    return this.version;
  }

  public boolean isSupportsCmi() {
    return this.supportsCmi;
  }

  public enum EconomySetupResponse {
    Vault,
    NoVault,
    NoVaultEconomy,
    CMI;
  }
}
