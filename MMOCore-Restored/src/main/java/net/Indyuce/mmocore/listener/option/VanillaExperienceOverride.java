package net.Indyuce.mmocore.listener.option;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class VanillaExperienceOverride implements Listener {
   @EventHandler(priority = EventPriority.HIGHEST)
   public void cancelChange(PlayerExpChangeEvent event) {
      event.setAmount(0);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void cancelDrop(PlayerDeathEvent event) {
      event.setDroppedExp(0);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void cancelChange(PlayerRespawnEvent event) {
      Bukkit.getScheduler().runTask(MMOCore.plugin, () -> PlayerData.get(event.getPlayer()).refreshVanillaExp());
   }

   @EventHandler
   public void cancelChange(EnchantItemEvent event) {
      Player player = event.getEnchanter();
      Bukkit.getScheduler().runTask(MMOCore.plugin, () -> player.setLevel(PlayerData.get(player).getLevel()));
   }
}
