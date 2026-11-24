package net.Indyuce.mmoitems.api.edition.input;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.Edition;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEdition extends PlayerInputHandler implements Listener {
   public ChatEdition(Edition edition) {
      super(edition);
      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
   }

   @Override
   public void close() {
      HandlerList.unregisterAll(this);
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void a(AsyncPlayerChatEvent event) {
      if (this.getPlayer() != null && event.getPlayer().equals(this.getPlayer())) {
         event.setCancelled(true);
         Bukkit.getScheduler().runTask(MMOItems.plugin, () -> this.registerInput(event.getMessage()));
      }
   }

   @EventHandler
   public void b(InventoryOpenEvent event) {
      if (event.getPlayer().equals(this.getPlayer())) {
         this.close();
      }
   }
}
