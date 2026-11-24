package me.TechsCode.UltraPermissions.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.TechsCode.UltraPermissions.base.legacy.task.TaskManager;
import me.TechsCode.UltraPermissions.base.loader.reloader.PluginReloader;
import me.TechsCode.UltraPermissions.base.loader.reloader.SpigotPluginReloader;
import me.TechsCode.UltraPermissions.base.messaging.SpigotMessagingService;
import me.TechsCode.UltraPermissions.base.scheduler.Scheduler;
import me.TechsCode.UltraPermissions.base.scheduler.SpigotScheduler;
import me.TechsCode.UltraPermissions.base.source.Maven;
import me.TechsCode.UltraPermissions.base.storage.syncing.SpigotSyncingAgent;
import me.TechsCode.UltraPermissions.base.storage.syncing.SyncingAgent;
import me.TechsCode.UltraPermissions.base.update.networkUpdate.SpigotUpdateAgent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpigotTechPlugin extends TechPlugin<JavaPlugin> {
   private SpigotScheduler scheduler;
   private SpigotMessagingService messagingService;
   private SyncingAgent syncingAgent;
   private SpigotUpdateAgent updateAgent;
   private Metrics metrics;

   public SpigotTechPlugin(JavaPlugin javaPlugin) {
      super(javaPlugin);
   }

   @Override
   protected void onInitialization() {
      this.scheduler = new SpigotScheduler(this);
      this.messagingService = new SpigotMessagingService(this);
      this.syncingAgent = new SpigotSyncingAgent(this);
      this.updateAgent = new SpigotUpdateAgent(this);
   }

   @Override
   protected void onPlatformEnable() {
      Maven.initialize(this);
      Maven.addRepository("https://repo.everit.biz/artifactory/public-release");
      new TaskManager(this);
   }

   @Override
   protected void onPlatformDisable() {
      this.messagingService.onDisable();
   }

   @Override
   public String getName() {
      return this.getBootstrap().getName();
   }

   @Override
   public String getVersion() {
      return this.getBootstrap().getDescription().getVersion();
   }

   @Override
   public int getBuildNumber() {
      InputStream resource = this.getBootstrap().getResource("plugin.yml");
      return resource == null
         ? 0
         : new BufferedReader(new InputStreamReader(resource))
            .lines()
            .filter(text -> text.startsWith("build: "))
            .map(text -> text.replace("build: ", ""))
            .map(Integer::parseInt)
            .findFirst()
            .orElse(0);
   }

   @Override
   public File getPluginFolder() {
      return this.getBootstrap().getDataFolder();
   }

   @Override
   public File getServerFolder() {
      return new File(".");
   }

   @Override
   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public SpigotMessagingService getMessagingService() {
      return this.messagingService;
   }

   @Override
   public SyncingAgent getSyncingAgent() {
      return this.syncingAgent;
   }

   @Override
   public PluginReloader<?> getPluginReloader() {
      return new SpigotPluginReloader(this);
   }

   @Override
   protected void sendConsole(String text) {
      Bukkit.getConsoleSender().sendMessage(text);
   }

   public SpigotUpdateAgent getUpdateAgent() {
      return this.updateAgent;
   }

   @Override
   public InputStream getResource(String text) {
      return this.getBootstrap().getResource(text);
   }

   @Override
   protected void disablePlugin() {
      this.getBootstrap().getPluginLoader().disablePlugin((Plugin)this.getBootstrap());
   }

   protected void enableMetrics(int count) {
      this.metrics = new Metrics((Plugin)this.getBootstrap(), count);
   }

   public Metrics getMetrics() {
      return this.metrics;
   }

   public String getOwnServerName() {
      return this.getMySQLManager().getRegistry().getServerName();
   }
}
