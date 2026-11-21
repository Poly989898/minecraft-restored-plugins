package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import org.jetbrains.annotations.Nullable;

public class BungeePlugin extends Plugin implements Platform {
   private final PluginLifecycle pluginLifecycle = new PluginLifecycle(this);

   @Override
   public void cancelTasks() {
      this.getProxy().getScheduler().cancel(this);
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

   @Override
   public boolean registerModule(PluginModule pluginModule) {
      boolean flag = pluginModule.obtainFlag();
      if (flag) {
         if (pluginModule instanceof BungeePlayerModule) {
            this.setPlayerModule((PlayerModule)pluginModule);
         }

         if (pluginModule instanceof BungeeLiteCommandManager) {
            this.setCommandManager((LiteCommandManager)pluginModule);
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
   public String getName() {
      return this.getDescription().getName();
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

   @Override
   public ThreadPoolExecutor getExecutor() {
      try {
         ThreadPoolExecutor threadPoolExecutor = this.pluginLifecycle.executor;
         if (threadPoolExecutor == null) {
            ThreadFactoryBuilder threadFactoryBuilder = this.pluginLifecycle.obtainThreadFactoryBuilder();

            try {
               threadFactoryBuilder.setThreadFactory(new GroupedThreadFactory(this, this.getName()));
            } catch (NoSuchMethodError e) {
               threadFactoryBuilder.setThreadFactory((ThreadFactory)GroupedThreadFactory.class.getConstructor(Plugin.class).newInstance(this));
            }

            return this.pluginLifecycle.computeThreadPoolExecutor(threadFactoryBuilder);
         } else {
            return threadPoolExecutor;
         }
      } catch (Throwable e2) {
         throw SneakyThrow.<RuntimeException>rethrow(e2);
      }
   }

   @Override
   public long getStartTime() {
      return this.pluginLifecycle.getElapsedMillis();
   }

   @Override
   public final byte getPlatformId() {
      return 1;
   }

   @Override
   public PlatformBridge createBridge() {
      return new BungeeBridge(this);
   }

   @Override
   public String getVersion() {
      return this.getDescription().getVersion();
   }

   @Override
   public LiteSender getConsoleSender() {
      return this.wrapSender(this.getProxy().getConsole());
   }

   @Override
   public ConfigFile createConfig() {
      ConfigFile configFile = this.loadConfigFile(new File(this.getDataFolder(), "config.yml"));
      this.pluginLifecycle.configFile = configFile;
      return configFile;
   }

   @Override
   public void startup() {
      this.pluginLifecycle.perform();
      this.pluginLifecycle.onEnable();
   }

   @Override
   public boolean obtainFlag3() {
      return this.pluginLifecycle.disabling.get();
   }

   @Override
   public boolean isOnlineMode() {
      return this.getProxy().getConfig().isOnlineMode();
   }

   @Override
   public boolean isPrimaryThread() {
      return false;
   }

   @Override
   public boolean obtainFlag4() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return this.pluginLifecycle.enabled.get();
   }

   @Override
   public boolean obtainFlag2() {
      return this.pluginLifecycle.started;
   }

   @Override
   public ConfigFile loadConfigFile(File file) {
      try {
         return this.pluginLifecycle.bootstrap.loadConfigFile(this, file);
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public void onDisable() {
      this.shutdown();
   }

   public void onEnable() {

      try {
         this.startup();
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerListeners(@Nullable PlatformListener[] platformListeners) {
      if (platformListeners != null) {
         for (PlatformListener platformListener : platformListeners) {
            if (platformListener instanceof BungeeListener) {
               this.getProxy().getPluginManager().registerListener(this, (BungeeListener)platformListener);
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
      this.runAsync(runnable);
   }

   @Override
   public void runSyncLater(Runnable runnable, long value) {
      this.runAsyncLater(runnable, value);
   }

   @Override
   public void runAsync(Runnable runnable) {
      this.getExecutor().execute(runnable);
   }

   @Override
   public void runAsyncLater(Runnable runnable, long value) {
      if (value < 0L) {
         throw new IllegalArgumentException("Invalid delay: " + value);
      }

      try {
         this.getProxy().getScheduler().schedule(this, runnable, value * 50L, TimeUnit.MILLISECONDS);
      } catch (RejectedExecutionException e) {
         this.handleException(e);
      }
   }

   @Override
   public void execute(Runnable runnable) {
      this.runAsync(runnable);
   }

   @Override
   public void runSyncTimer(Runnable runnable, long value, long value2) {
      this.runAsyncTimer(runnable, value, value2);
   }

   @Override
   public void runAsyncTimer(Runnable runnable, long value, long value2) {
      try {
         this.getProxy().getScheduler().schedule(this, runnable, value * 50L, value2 * 50L, TimeUnit.MILLISECONDS);
      } catch (RejectedExecutionException e) {
         this.handleException(e);
      }
   }

   private final void handleException(Exception exception) {
      try {
         if (this.pluginLifecycle.loading.get()) {
            this.getLogger().warning("Startup was cancelled!!");
         } else {
            throw exception;
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
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
      this.pluginLifecycle.playerModule = playerModule;
   }

   @Override
   public void unregisterAllListeners() {
      this.getProxy().getPluginManager().unregisterListeners(this);
   }

   @Override
   public void unregisterListeners(@Nullable PlatformListener[] platformListeners) {
      if (platformListeners != null) {
         for (PlatformListener platformListener : platformListeners) {
            if (platformListener instanceof BungeeListener) {
               this.getProxy().getPluginManager().unregisterListener((BungeeListener)platformListener);
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
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
         }
      } else {
         CommandSender commandSender = (CommandSender)obj;
         Map map = this.pluginLifecycle.map;
         LiteSender liteSender = (LiteSender)map.get(commandSender.getName());
         if (liteSender == null || liteSender.getHandle() != commandSender) {
            liteSender = new BungeeConsoleSender(this, commandSender);
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
