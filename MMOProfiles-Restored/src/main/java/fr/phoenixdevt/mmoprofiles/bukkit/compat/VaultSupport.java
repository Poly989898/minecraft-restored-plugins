package fr.phoenixdevt.mmoprofiles.bukkit.compat;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultSupport {
   @Nullable
   public final Economy economy;

   public VaultSupport() {
      Economy economy = null;

      try {
         economy = (Economy)Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
         MMOProfiles.plugin.getLogger().log(Level.INFO, "Hooked onto Vault economy");
      } catch (Exception exception) {
         MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not find an economy plugin supported by Vault.");
      }

      this.economy = economy;
   }

   public void applyVaultTransaction(@NotNull Player player, double transaction) {
      if (transaction > 0.0) {
         MMOProfiles.plugin.vaultSupport.economy.depositPlayer(player, transaction);
      } else if (transaction < 0.0) {
         MMOProfiles.plugin.vaultSupport.economy.withdrawPlayer(player, -transaction);
      }
   }
}
