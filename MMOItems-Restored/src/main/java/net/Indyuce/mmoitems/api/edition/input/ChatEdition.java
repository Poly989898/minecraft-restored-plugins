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
   public ChatEdition(Edition var1) {
      super(var1);
      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
   }

   @Override
   public void close() {
      HandlerList.unregisterAll(this);
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void a(AsyncPlayerChatEvent var1) {
      if (this.getPlayer() != null && var1.getPlayer().equals(this.getPlayer())) {
         var1.setCancelled(true);
         Bukkit.getScheduler().runTask(MMOItems.plugin, () -> this.registerInput(var1.getMessage()));
      }
   }

   @EventHandler
   public void b(InventoryOpenEvent var1) {
      if (var1.getPlayer().equals(this.getPlayer())) {
         this.close();
      }
   }
}
