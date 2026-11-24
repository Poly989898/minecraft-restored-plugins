package io.lumine.mythic.lib.data;

import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveRunnable extends BukkitRunnable {
   private final SynchronizedDataManager<?, ?> manager;
   private final boolean log;
   private static final long MINIMUM_INTERVAL = 60L;

   public AutoSaveRunnable(SynchronizedDataManager<?, ?> manager) {
      this.manager = manager;
      ConfigurationSection config = manager.getOwningPlugin().getConfig().getConfigurationSection("auto-save");
      this.log = config.getBoolean("log", false);
      long timer = Math.max(60L, config.getLong("interval", 1800L)) * 20L;
      this.runTaskTimer(manager.getOwningPlugin(), timer, timer);
   }

   public void run() {
      if (this.log) {
         this.manager.getOwningPlugin().getLogger().log(Level.INFO, "Autosaving player data, might take a while...");
      }

      this.manager.autosave();
   }
}
