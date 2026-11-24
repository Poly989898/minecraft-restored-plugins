package me.TechsCode.UltraPermissions.base;

import java.io.File;
import java.io.InputStream;
import me.TechsCode.UltraPermissions.base.addons.AddonManager;
import me.TechsCode.UltraPermissions.base.fileconf.FileConfiguration;
import me.TechsCode.UltraPermissions.base.loader.reloader.BungeePluginReloader;
import me.TechsCode.UltraPermissions.base.loader.reloader.PluginReloader;
import me.TechsCode.UltraPermissions.base.messaging.BungeeMessagingService;
import me.TechsCode.UltraPermissions.base.scheduler.BungeeScheduler;
import me.TechsCode.UltraPermissions.base.scheduler.Scheduler;
import me.TechsCode.UltraPermissions.base.storage.syncing.BungeeSyncingAgent;
import me.TechsCode.UltraPermissions.base.storage.syncing.SyncingAgent;
import me.TechsCode.UltraPermissions.base.update.networkUpdate.BungeeUpdateAgent;
import org.bstats.bungeecord.Metrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class BungeeTechPlugin extends TechPlugin<Plugin> {
   private BungeeScheduler scheduler;
   private BungeeMessagingService messagingService;
   private SyncingAgent syncingAgent;
   private Metrics metrics;

   public BungeeTechPlugin(Plugin plugin) {
      super(plugin);
   }

   @Override
   protected void onInitialization() {
      this.scheduler = new BungeeScheduler(this);
      this.messagingService = new BungeeMessagingService(this);
      this.syncingAgent = new BungeeSyncingAgent(this);
      new BungeeUpdateAgent(this);
   }

   @Override
   protected void onPlatformEnable() {
   }

   @Override
   protected void onPlatformDisable() {
   }

   @Override
   public String getName() {
      return this.getBootstrap().getDescription().getName();
   }

   @Override
   public String getVersion() {
      return this.getBootstrap().getDescription().getVersion();
   }

   @Override
   public int getBuildNumber() {
      File file = this.getBootstrap().getDescription().getFile();
      FileConfiguration fileConfiguration = new FileConfiguration(this, file);
      return fileConfiguration.getInt("build");
   }

   @Override
   public File getPluginFolder() {
      return this.getBootstrap().getDataFolder();
   }

   @Override
   public File getServerFolder() {
      return ProxyServer.getInstance().getPluginsFolder().getParentFile();
   }

   @Override
   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public BungeeMessagingService getMessagingService() {
      return this.messagingService;
   }

   @Override
   public SyncingAgent getSyncingAgent() {
      return this.syncingAgent;
   }

   @Override
   public PluginReloader<?> getPluginReloader() {
      return new BungeePluginReloader(this);
   }

   @Override
   public AddonManager getAddonManager() {
      return null;
   }

   @Override
   protected void sendConsole(String text) {
      this.getBootstrap().getLogger().info(text);
   }

   @Override
   public InputStream getResource(String text) {
      return this.getBootstrap().getResourceAsStream(text);
   }

   @Override
   protected void disablePlugin() {
   }

   protected void enableMetrics(int value) {
      this.metrics = new Metrics(this.getBootstrap(), value);
   }

   public Metrics getMetrics() {
      return this.metrics;
   }
}
