package me.TechsCode.UltraPermissions.base.dialog;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar.ActionBar;
import me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar.Title;
import me.TechsCode.UltraPermissions.base.scheduler.RecurringTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public abstract class Dialog implements Listener, Runnable {
   private final Player p;
   private final ActionBar actionBar;
   private String lastMainTitle;
   private String lastSubTitle;
   private final RecurringTask visualTask;

   public Dialog(Player player, SpigotTechPlugin plugin) {
      this.p = player;
      this.actionBar = new ActionBar(plugin);
      plugin.getScheduler().run(player::closeInventory);
      this.visualTask = plugin.getScheduler().runTaskTimer(this, 1L, 1L);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin.getBootstrap());
   }

   public abstract void onClose(Player player);

   public abstract boolean onInput(String text);

   public abstract String getMainTitle();

   public abstract String getSubTitle();

   public abstract String getActionBar();

   @Override
   public void run() {
      String lastMainTitle = this.getMainTitle();
      String lastSubTitle = this.getSubTitle();
      String actionBar = this.getActionBar();
      if (this.lastMainTitle == null || this.lastSubTitle == null || !this.lastMainTitle.equals(lastMainTitle) || !this.lastSubTitle.equals(lastSubTitle)) {
         Title.sendTitle(this.p, 30, 6000, 0, lastMainTitle, lastSubTitle);
         this.lastMainTitle = lastMainTitle;
         this.lastSubTitle = lastSubTitle;
      }

      if (actionBar != null) {
         this.actionBar.sendActionBar(this.p, actionBar);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onChat(AsyncPlayerChatEvent event) {
      if (event.getPlayer().equals(this.p)) {
         this.onInput(event.getMessage());
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void close(PlayerQuitEvent event) {
      if (event.getPlayer().equals(this.p)) {
         this.close(false);
      }
   }

   @EventHandler
   public void close(InventoryOpenEvent event) {
      if (event.getPlayer().equals(this.p)) {
         this.close(false);
      }
   }

   public void close(boolean flag) {
      HandlerList.unregisterAll(this);
      this.visualTask.stop();
      if (!flag) {
         this.onClose(this.p);
      }

      Title.clearTitle(this.p);
      this.actionBar.sendActionBar(this.p, "");
   }
}
