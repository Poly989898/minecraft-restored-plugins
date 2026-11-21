package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.utils.Schedulers;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {
   public boolean enabled = false;
   public Economy economy;

   public VaultSupport() {
      Schedulers.sync().runLater(this::setup, 20L);
   }

   private void setup() {
      RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
      if (economyProvider != null) {
         this.economy = economyProvider.getProvider();
         if (this.economy != null) {
            this.enabled = true;
         }
      }
   }

   public void giveMoney(Player p, double amount) {
      try {
         this.economy.depositPlayer(p, amount);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void giveMoney(AbstractPlayer player, double amount) {
      Player p = (Player)player.getBukkitEntity();

      try {
         this.economy.depositPlayer(p, amount);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void takeMoney(AbstractPlayer player, double amount) {
      Player p = (Player)player.getBukkitEntity();

      try {
         this.economy.withdrawPlayer(p, amount);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public boolean hasMoney(AbstractPlayer player, double amount) {
      Player p = (Player)player.getBukkitEntity();

      try {
         return this.economy.has(p, amount);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}
