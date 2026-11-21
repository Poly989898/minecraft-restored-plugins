package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class PluginLifecycle implements Bootstrap {
   private final Platform platform;
   public PluginModule[] moduleExact = Platform.pluginModuleArray;
   public LiteCommandManager liteCommandManager;
   public PlayerModule playerModule;
   public Map map = new ConcurrentHashMap();
   public ClassUtil classUtil;
   public LiteSender liteSender;
   public ThreadPoolExecutor executor;
   public ConfigFile configFile;
   public Bootstrap bootstrap = this;
   public AtomicBoolean enabled = new AtomicBoolean(false);
   public AtomicBoolean loading = new AtomicBoolean(false);
   public AtomicBoolean disabling = new AtomicBoolean(false);
   public long elapsedMillis;
   public boolean started;

   public PluginLifecycle(Platform platform) {
      this.platform = platform;
      this.classUtil = new ClassUtil(platform);
   }

   public PluginModule getModuleExact(Class class2) {
      for (PluginModule pluginModule : this.moduleExact) {
         if (pluginModule.getClass() == class2) {
            return pluginModule;
         }
      }

      return null;
   }

   public PluginModule findModule(Class class2) {
      for (PluginModule pluginModule : this.moduleExact) {
         if (class2.isAssignableFrom(pluginModule.getClass())) {
            return pluginModule;
         }
      }

      return null;
   }

   @Override
   public ConfigFile loadConfigFile(Platform platform, File file) {
      return new YamlConfigFile(platform).load(file);
   }

   public void perform() {
      this.loading.set(true);
      this.configFile = this.platform.createConfig();
      this.startModules(false);
   }

   public void onEnable() {
      try {
         if (this.enabled.get()) {
            this.platform.getLogger().warning("onEnable() called while plugin is already enabled! This might break something.");
            this.shutdown();
         }

         this.elapsedMillis = System.currentTimeMillis();
         this.enabled.set(true);
         if (this.moduleExact == Platform.pluginModuleArray) {
            this.perform();
         }

         this.liteSender = this.platform.getConsoleSender();
         this.startModules(true);
         this.platform.getLogger().info(this.getEnabledMessage());
         this.classUtil.handleFlag(false);
         this.started = true;
      } finally {
         this.loading.set(false);
      }
   }

   public @NotNull ThreadFactoryBuilder obtainThreadFactoryBuilder() {
      return new ThreadFactoryBuilder().setNameFormat(this.platform.getName() + "-%1$d").setDaemon(true);
   }

   public void startModules(boolean flag) {
      if (this.moduleExact == Platform.pluginModuleArray) {
         this.moduleExact = this.platform.getAllModules();
      }

      if (flag) {
         for (PluginModule pluginModule : this.platform.getActiveModules()) {
            pluginModule.enable();
         }
      } else {
         for (PluginModule pluginModule2 : this.platform.getActiveModules()) {
            pluginModule2.load();
         }
      }
   }

   public void shutdown() {
      if (this.loading.get()) {
         this.platform
            .getLogger()
            .severe(
               "onDisable() called while plugin hasn't finished loading yet! This will be ignored to prevent errors. It's likely that another plugin stopped your server asynchronously."
            );
      } else {
         this.disabling.set(true);
         this.enabled.set(false);
         this.started = false;

         for (PluginModule pluginModule : this.moduleExact) {
            pluginModule.disable();
         }

         this.platform.unregisterAllListeners();
         this.platform.cancelTasks();
         this.configFile = null;
         this.map.clear();
         ThreadPoolExecutor threadPoolExecutor = this.executor;
         if (threadPoolExecutor != null) {
            try {
               threadPoolExecutor.shutdown();
               if (!threadPoolExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
                  threadPoolExecutor.shutdownNow();
               }
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         this.platform.getLogger().info(this.getDisabledMessage());
         this.liteSender = null;
         this.executor = null;
         this.disabling.set(false);
      }
   }

   public LiteSender[] getOnlineSenders() {
      PlayerModule playerModule = this.playerModule;
      return playerModule != null ? playerModule.obtainLiteSenderArray() : LiteSender.liteSenderArray;
   }

   public int getOnlineCount() {
      PlayerModule playerModule = this.playerModule;
      return playerModule != null ? playerModule.obtainIntValue() : 0;
   }

   public PluginModule[] getModules() {
      return AbstractPlatform.computePluginModuleArray(this.platform);
   }

   public boolean registerListenersIfPresent(String text, Supplier supplier) {
      boolean classExists2 = ClassUtil.classExists(text);
      if (classExists2) {
         this.platform.registerListeners((PlatformListener[])supplier.get());
      }

      return classExists2;
   }

   public long getElapsedMillis() {
      return System.currentTimeMillis() - this.elapsedMillis;
   }

   public String getDisabledMessage() {
      return 'v' + this.platform.getVersion() + " disabled.";
   }

   public String getEnabledMessage() {
      return 'v' + this.platform.getVersion() + " enabled. Startup took " + this.platform.getStartTime() + " ms.";
   }

   public ThreadPoolExecutor computeThreadPoolExecutor(ThreadFactoryBuilder threadFactoryBuilder) {
      return this.executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactoryBuilder.build());
   }

   public void perform2() {
      int value = this.map.size();
      if (value > 4 && value % 32 == 0 && value > this.getOnlineCount() + 4) {
         this.map.clear();
      }
   }
}
