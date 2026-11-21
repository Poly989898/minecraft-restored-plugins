package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlugin extends JavaPlugin implements Platform {
   private final PluginLifecycle pluginLifecycle = new PluginLifecycle(this);
   private SchedulerHolder schedulerHolder = null;

   @Override
   public void cancelTasks() {
      try {
         if (FeatureDetection.obtainFlag2()) {
            this.schedulerHolder.perform();
         } else {
            this.obtainBukkitScheduler().cancelTasks(this);
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ConfigFile getConfigFile() {
      return this.pluginLifecycle.configFile;
   }

   @Override
   public PluginModule[] getAllModules() {
      return this.pluginLifecycle.getModules();
   }

   @Override
   public void shutdown() {
      this.pluginLifecycle.shutdown();
   }

   public @Nullable List onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String text, @NotNull String[] texts) {
      LiteCommandManager commandManager = this.getCommandManager();
      if (commandManager == null) {
         return null;
      }

      LiteCommand liteCommand = commandManager.computeLiteCommand(command.getName());
      return commandManager.computeList(liteCommand, text, this.wrapSender(commandSender), texts);
   }

   @Override
   public boolean registerModule(PluginModule pluginModule) {
      boolean flag = pluginModule.obtainFlag();
      if (flag) {
         if (pluginModule instanceof BukkitLiteCommandManager) {
            this.setCommandManager((BukkitLiteCommandManager)pluginModule);
         }

         if (pluginModule instanceof BukkitPlayerModule) {
            this.setPlayerModule((BukkitPlayerModule)pluginModule);
         }
      }

      return flag;
   }

   @Override
   public ClassUtil getClassUtil() {
      return this.pluginLifecycle.classUtil;
   }

   @Override
   public LiteCommandManager getCommandManager() {
      return this.pluginLifecycle.liteCommandManager;
   }

   @Override
   public PlayerModule getPlayerModule() {
      return this.pluginLifecycle.playerModule;
   }

   @Override
   public void setCommandManager(LiteCommandManager liteCommandManager) {
      this.pluginLifecycle.liteCommandManager = liteCommandManager;
   }

   @Override
   public LiteSender getConsole() {
      return this.pluginLifecycle.liteSender;
   }

   @Override
   public PluginModule getModule(Class class2) {
      return this.pluginLifecycle.getModuleExact(class2);
   }

   @Override
   public PluginModule getModuleOrNull(Class class2) {
      return this.pluginLifecycle.findModule(class2);
   }

   @Override
   public PluginModule[] getActiveModules() {
      return this.pluginLifecycle.moduleExact;
   }

   @Override
   public int obtainIntValue() {
      return this.pluginLifecycle.getOnlineCount();
   }

   @Override
   public LiteSender[] getOnlineSenders() {
      return this.pluginLifecycle.getOnlineSenders();
   }

   @Override
   public Map getSenderCache() {
      return this.pluginLifecycle.map;
   }

   public BukkitScheduler obtainBukkitScheduler() {
      return this.getServer().getScheduler();
   }

   @Override
   public ThreadPoolExecutor getExecutor() {
      return this.computeThreadPoolExecutor(false);
   }

   public ThreadPoolExecutor computeThreadPoolExecutor(boolean flag) {
      ThreadPoolExecutor threadPoolExecutor = this.pluginLifecycle.executor;
      if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown() || !flag && !this.pluginLifecycle.enabled.get() && !this.pluginLifecycle.loading.get()) {
         return threadPoolExecutor;
      }

      ThreadFactoryBuilder threadFactoryBuilder = this.pluginLifecycle.obtainThreadFactoryBuilder();
      return this.pluginLifecycle.computeThreadPoolExecutor(threadFactoryBuilder);
   }

   @Override
   public long getStartTime() {
      return this.pluginLifecycle.getElapsedMillis();
   }

   @Override
   public final byte getPlatformId() {
      return 0;
   }

   @Override
   public PlatformBridge createBridge() {
      return new BukkitBridge(this);
   }

   @Override
   public String getVersion() {
      return this.getDescription().getVersion();
   }

   @Override
   public LiteSender getConsoleSender() {
      return this.wrapSender(this.getServer().getConsoleSender());
   }

   @Override
   public ConfigFile createConfig() {
      BukkitConfigFile bukkitConfigFile = new BukkitConfigFile(this);
      bukkitConfigFile.load(new File(this.getDataFolder(), "config.yml"));
      return bukkitConfigFile;
   }

   @Override
   public void startup() {
      this.pluginLifecycle.perform();
      this.pluginLifecycle.onEnable();
   }

   @Override
   public boolean obtainFlag2() {
      return this.pluginLifecycle.started;
   }

   @Override
   public boolean obtainFlag3() {
      return this.pluginLifecycle.disabling.get();
   }

   @Override
   public boolean isOnlineMode() {
      return this.getServer().getOnlineMode();
   }

   @Override
   public boolean isPrimaryThread() {
      return FeatureDetection.obtainFlag2() ? false : this.getServer().isPrimaryThread();
   }

   @Override
   public boolean obtainFlag4() {
      return false;
   }

   @Override
   public ConfigFile loadConfigFile(File file) {
      try {
         return new BukkitConfigFile(this).load(file);
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public boolean onCommand(CommandSender commandSender, Command command, String text, String[] texts) {
      LiteCommandManager commandManager = this.getCommandManager();
      if (commandManager != null && commandManager instanceof BukkitLiteCommandManager) {
         return ((BukkitLiteCommandManager)commandManager).onCommand(commandSender, command, text, texts);
      }

      this.getLogger().severe("No command manager found!");
      return true;
   }

   public void onDisable() {
      this.shutdown();
   }

   public void onEnable() {
      this.startup();
   }

   public void onLoad() {
      this.computeThreadPoolExecutor(true);
      if (FeatureDetection.obtainFlag2()) {
         this.schedulerHolder = new SchedulerHolder();
      }

      BukkitStartupTask bukkitStartupTask = new BukkitStartupTask(this);
      if (!this.obtainFlag8()) {
         this.execute(bukkitStartupTask);
      } else {
         bukkitStartupTask.run();
      }
   }

   public boolean obtainFlag8() {
      return !ClassUtil.classExists("org.bukkit.entity.Llama");
   }

   @Override
   public void registerListeners(@Nullable PlatformListener[] platformListeners) {
      if (platformListeners != null) {
         for (PlatformListener platformListener : platformListeners) {
            if (platformListener instanceof BukkitListener) {
               this.getServer().getPluginManager().registerEvents((BukkitListener)platformListener, this);
            }
         }
      }
   }

   @Override
   public boolean checkText(String text, Supplier supplier) {
      return this.pluginLifecycle.registerListenersIfPresent(text, supplier);
   }

   @Override
   public void runSync(Runnable runnable) {
      if (FeatureDetection.obtainFlag2()) {
         runnable.run();
      } else {
         this.obtainBukkitScheduler().runTask(this, runnable);
      }
   }

   @Override
   public void runSyncLater(Runnable runnable, long value) {
      if (FeatureDetection.obtainFlag2()) {
         this.runAsyncLater(runnable, value);
      } else {
         this.obtainBukkitScheduler().runTaskLater(this, runnable, value);
      }
   }

   @Override
   public void runAsync(Runnable runnable) {
      if (FeatureDetection.obtainFlag2()) {
         this.execute(runnable);
      } else {
         this.obtainBukkitScheduler().runTaskAsynchronously(this, runnable);
      }
   }

   @Override
   public void runAsyncLater(Runnable runnable, long value) {
      if (FeatureDetection.obtainFlag2()) {
         this.schedulerHolder.computeScheduledExecutorService(this.pluginLifecycle).schedule(runnable, value * 50L, TimeUnit.MILLISECONDS);
      } else {
         this.obtainBukkitScheduler().runTaskLaterAsynchronously(this, runnable, value);
      }
   }

   @Override
   public void execute(Runnable runnable) {
      this.getExecutor().execute(runnable);
   }

   @Override
   public void runSyncTimer(Runnable runnable, long value, long value2) {
      if (FeatureDetection.obtainFlag2()) {
         this.runAsyncTimer(runnable, value, value2);
      } else {
         this.obtainBukkitScheduler().runTaskTimer(this, runnable, value, value2);
      }
   }

   @Override
   public void runAsyncTimer(Runnable runnable, long value, long value2) {
      if (FeatureDetection.obtainFlag2()) {
         this.schedulerHolder.computeScheduledExecutorService(this.pluginLifecycle).scheduleAtFixedRate(runnable, value * 50L, value2 * 50L, TimeUnit.MILLISECONDS);
      } else {
         this.obtainBukkitScheduler().runTaskTimerAsynchronously(this, runnable, value, value2);
      }
   }

   @Override
   public void saveResource(String text) {
      ConfigFile.saveResource(this.getDataFolder(), text, null, false);
   }

   @Override
   public void setBootstrap(Bootstrap bootstrap) {
      this.pluginLifecycle.bootstrap = bootstrap;
   }

   @Override
   public void setPlayerModule(PlayerModule playerModule) {
      if (!(playerModule instanceof BukkitPlayerModule)) {
         throw new IllegalArgumentException();
      }

      this.pluginLifecycle.playerModule = playerModule;
   }

   @Override
   public void unregisterAllListeners() {
      HandlerList.unregisterAll(this);
   }

   @Override
   public void unregisterListeners(@Nullable PlatformListener[] platformListeners) {
      if (platformListeners != null) {
         for (PlatformListener platformListener : platformListeners) {
            if (platformListener instanceof BukkitListener) {
               HandlerList.unregisterAll((BukkitListener)platformListener);
            }
         }
      }
   }

   @Override
   public LiteSender removeSender(Object obj) {
      if (!(obj instanceof CommandSender)) {
         if (obj instanceof LiteSender) {
            return (LiteSender)this.pluginLifecycle.map.remove(((LiteSender)obj).getName());
         } else {
            throw new IllegalArgumentException(obj == null ? "null" : obj.getClass().getName());
         }
      } else {
         CommandSender commandSender = (CommandSender)obj;
         return (LiteSender)this.pluginLifecycle.map.remove(commandSender.getName());
      }
   }

   @Override
   public LiteSender wrapSender(Object obj) {
      if (!(obj instanceof CommandSender)) {
         if (obj instanceof LiteSender) {
            return (LiteSender)obj;
         } else {
            throw new IllegalArgumentException(obj == null ? "null" : obj.getClass().getName());
         }
      } else {
         CommandSender commandSender = (CommandSender)obj;
         Map map = this.pluginLifecycle.map;
         LiteSender liteSender = (LiteSender)map.get(commandSender.getName());
         if (liteSender == null || liteSender.getHandle() != commandSender) {
            liteSender = new LiteLiteSenderImpl(this, commandSender);
            this.pluginLifecycle.perform2();
            map.put(commandSender.getName(), liteSender);
         }

         return liteSender;
      }
   }

   public PluginLifecycle getPluginLifecycle() {
      return this.pluginLifecycle;
   }
}
