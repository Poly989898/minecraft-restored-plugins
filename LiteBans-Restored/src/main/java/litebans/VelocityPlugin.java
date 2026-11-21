package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;

@Plugin(
id = "litebans",
name = "LiteBans",
version = "2.19.0",
url = "https://www.spigotmc.org/resources/litebans.3715/",
description = "Lightweight banning plugin with full UUID support.",
authors = "Ruan"
)
public class VelocityPlugin implements Platform {
   private final PluginLifecycle pluginLifecycle = new PluginLifecycle(this);
   public final ProxyServer proxyServer;
   private final PlatformLogger platformLogger;
   private final Path path;
   private final List console = Collections.synchronizedList(new ArrayList());

   @Inject
   public VelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path path) {
      this.proxyServer = proxyServer;
      this.platformLogger = new PlatformLogger(logger, this.getName());
      this.path = path;
   }

   @Subscribe
   public void handleProxyInitializeEvent(ProxyInitializeEvent proxyInitializeEvent) {
      this.startup();
   }

   @Subscribe
   public void handleProxyShutdownEvent(ProxyShutdownEvent proxyShutdownEvent) {
      this.shutdown();
   }

   @Override
   public void cancelTasks() {
      this.perform();
      synchronized (this.console) {
         this.console.forEach(obj -> ((com.velocitypowered.api.scheduler.ScheduledTask) obj).cancel());
         this.console.clear();
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

   @Override
   public boolean registerModule(PluginModule pluginModule) {
      boolean flag = pluginModule.obtainFlag();
      if (flag) {
         if (pluginModule instanceof VelocityPlayerModule) {
            this.setPlayerModule((PlayerModule)pluginModule);
         }

         if (pluginModule instanceof VelocityLiteCommandManager) {
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
   public LiteSender getConsoleSender() {
      return this.wrapSender(this.proxyServer.getConsoleCommandSource());
   }

   @Override
   public ConfigFile createConfig() {
      ConfigFile configFile = this.loadConfigFile(new File(this.getDataFolder(), "config.yml"));
      this.pluginLifecycle.configFile = configFile;
      return configFile;
   }

   @Override
   public File getDataFolder() {
      return this.path.toFile();
   }

   @Override
   public java.util.logging.Logger getLogger() {
      return this.platformLogger;
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
      return "LiteBans";
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
      ThreadPoolExecutor threadPoolExecutor = this.pluginLifecycle.executor;
      if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown() || !this.pluginLifecycle.enabled.get() && !this.pluginLifecycle.loading.get()) {
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
   public byte getPlatformId() {
      return 2;
   }

   @Override
   public PlatformBridge createBridge() {
      return new VelocityBridge(this);
   }

   @Override
   public String getVersion() {
      return "2.19.0";
   }

   @Override
   public void startup() {
      this.pluginLifecycle.perform();
      this.pluginLifecycle.onEnable();
   }

   @Override
   public boolean isEnabled() {
      return this.pluginLifecycle.started;
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
      return this.proxyServer.getConfiguration().isOnlineMode();
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
   public ConfigFile loadConfigFile(File file) {
      return this.pluginLifecycle.bootstrap.loadConfigFile(this, file);
   }

   @Override
   public void registerListeners(PlatformListener[] platformListeners) {
      for (PlatformListener platformListener : platformListeners) {
         this.proxyServer.getEventManager().register(this, platformListener);
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

      this.perform();
      this.console.add(this.proxyServer.getScheduler().buildTask(this, runnable).delay(value * 50L, TimeUnit.MILLISECONDS).schedule());
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
      this.perform();
      this.console.add(this.proxyServer.getScheduler().buildTask(this, runnable).delay(value * 50L, TimeUnit.MILLISECONDS).repeat(value2 * 50L, TimeUnit.MILLISECONDS).schedule());
   }

   public void perform() {
      synchronized (this.console) {
         this.console.removeIf(obj -> ((ScheduledTask) obj).status() != TaskStatus.SCHEDULED);
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
      this.proxyServer.getEventManager().unregisterListeners(this);
   }

   @Override
   public void unregisterListeners(PlatformListener[] platformListeners) {
      for (PlatformListener platformListener : platformListeners) {
         this.proxyServer.getEventManager().unregisterListener(this, platformListener);
      }
   }

   @Override
   public LiteSender removeSender(Object obj) {
      if (!(obj instanceof CommandSource)) {
         if (obj instanceof LiteSender) {
            return (LiteSender)this.pluginLifecycle.map.remove(((LiteSender)obj).getName());
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         String text = "[Console]";
         if (obj instanceof Player) {
            text = ((Player)obj).getUsername();
         }

         return (LiteSender)this.pluginLifecycle.map.remove(text);
      }
   }

   @Override
   public LiteSender wrapSender(Object obj) {
      if (!(obj instanceof CommandSource)) {
         if (obj instanceof LiteSender) {
            return (LiteSender)obj;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         CommandSource commandSource = (CommandSource)obj;
         Map map = this.pluginLifecycle.map;
         String text = "[Console]";
         if (commandSource instanceof Player) {
            text = ((Player)commandSource).getUsername();
         }

         LiteSender liteSender = (LiteSender)map.get(text);
         if (liteSender == null || liteSender.getHandle() != commandSource) {
            liteSender = new VelocityConsoleSender(this, commandSource);
            this.pluginLifecycle.perform2();
            map.put(text, liteSender);
         }

         return liteSender;
      }
   }

   public PluginLifecycle getPluginLifecycle() {
      return this.pluginLifecycle;
   }

}
