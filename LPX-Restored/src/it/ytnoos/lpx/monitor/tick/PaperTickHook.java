package it.ytnoos.lpx.monitor.tick;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PaperTickHook extends AbstractTickHook implements TickHook, Listener {
   private final Plugin plugin;

   public PaperTickHook(Plugin plugin2) {
      this.plugin = plugin2;
   }

   @EventHandler
   public void onServerTickEvent(ServerTickStartEvent serverTickStartEvent) {
      this.runCallbacks();
   }

   @Override
   public void start() {
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
   }

   @Override
   public void close() {
      HandlerList.unregisterAll(this);
   }
}
