package net.Indyuce.mmoitems.comp.eco;

import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {
   private final Economy economy;
   private final Permission permissions;

   public VaultSupport() {
      RegisteredServiceProvider var1 = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
      this.economy = var1 != null ? (Economy)var1.getProvider() : null;
      RegisteredServiceProvider var2 = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
      this.permissions = var2 != null ? (Permission)var2.getProvider() : null;
      if (this.economy == null) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load Economy Support (Vault)");
      } else {
         MMOItems.plugin
            .getCrafting()
            .registerCondition("money", MoneyCondition::new, new ConditionalDisplay("&a✔ Requires $#money#", "&c✖ Requires $#money#"));
      }

      if (this.permissions == null) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load Permissions Support (Vault)");
      }
   }

   public Permission getPermissions() {
      return this.permissions;
   }

   public Economy getEconomy() {
      return this.economy;
   }
}
