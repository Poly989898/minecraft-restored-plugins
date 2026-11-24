package net.Indyuce.mmocore.api.eco;

import io.lumine.mythic.lib.api.util.SmartGive;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.util.item.CurrencyItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Withdraw implements Listener {
   private static final Set<UUID> withdrawing = new HashSet<>();
   private final Player player;

   public Withdraw(Player player) {
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }

   public void open() {
      if (!this.isWithdrawing()) {
         withdrawing.add(this.player.getUniqueId());
         ConfigMessage.fromKey("withdrawing").send(this.player);
         Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
         Bukkit.getScheduler().runTaskLater(MMOCore.plugin, this::close, 400L);
      }
   }

   public void close() {
      HandlerList.unregisterAll(this);
      withdrawing.remove(this.player.getUniqueId());
   }

   public boolean isWithdrawing() {
      return withdrawing.contains(this.player.getUniqueId());
   }

   @EventHandler
   public void a(PlayerMoveEvent event) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         if (event.getPlayer().equals(this.player)) {
            ConfigMessage.fromKey("withdraw-cancel").send(this.player);
            this.close();
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void b(AsyncPlayerChatEvent event) {
      if (event.getPlayer().equals(this.player)) {
         event.setCancelled(true);

         int worth;
         try {
            worth = Integer.parseInt(event.getMessage());
         } catch (Exception e) {
            ConfigMessage.fromKey("wrong-number").addPlaceholders("arg", event.getMessage()).send(this.player);
            return;
         }

         int left = (int)(MMOCore.plugin.economy.getEconomy().getBalance(this.player) - worth);
         if (left < 0) {
            ConfigMessage.fromKey("not-enough-money").addPlaceholders("left", -left).send(this.player);
         } else {
            this.close();
            Bukkit.getScheduler().runTask(MMOCore.plugin, () -> {
               MMOCore.plugin.economy.getEconomy().withdrawPlayer(this.player, worth);
               this.withdrawAlgorithm(worth);
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
               ConfigMessage.fromKey("withdrew").addPlaceholders("worth", worth).send(this.player);
            });
         }
      }
   }

   @Deprecated
   public void withdrawAlgorythm(int worth) {
      this.withdrawAlgorithm(worth);
   }

   public void withdrawAlgorithm(int worth) {
      int note = worth / 10 * 10;
      int coins = worth - note;
      SmartGive smart = new SmartGive(this.player);
      if (note > 0) {
         smart.give(new ItemStack[]{new CurrencyItemBuilder("NOTE", note).build()});
      }

      ItemStack coinsItem = new CurrencyItemBuilder("GOLD_COIN", 1).build();
      coinsItem.setAmount(coins);
      smart.give(new ItemStack[]{coinsItem});
   }

   @EventHandler
   public void c(PlayerQuitEvent event) {
      if (event.getPlayer().equals(this.player)) {
         this.close();
      }
   }
}
