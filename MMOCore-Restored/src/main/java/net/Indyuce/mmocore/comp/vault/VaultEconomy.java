package net.Indyuce.mmocore.comp.vault;

import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class VaultEconomy {
   @Nullable
   private final Economy economy;

   public VaultEconomy() {
      Economy eco;
      try {
         eco = (Economy)Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
         MMOCore.log(Level.INFO, "Hooked onto Vault");
      } catch (Exception exception) {
         MMOCore.plugin.getLogger().log(Level.WARNING, "Vault was found but MMOCore was unable to successfully find/load an economy plugin.");
         eco = null;
      }

      this.economy = eco;
   }

   @Deprecated
   public boolean isValid() {
      return this.economy != null;
   }

   public Economy getEconomy() {
      return this.economy;
   }
}
