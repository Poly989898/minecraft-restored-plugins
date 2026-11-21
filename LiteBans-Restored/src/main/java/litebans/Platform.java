package litebans;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import java.util.logging.Logger;

public interface Platform extends Named {
   PluginModule[] pluginModuleArray = new PluginModule[0];

   void cancelTasks();

   ConfigFile getConfigFile();

   PluginModule[] getAllModules();

   void shutdown();

   boolean registerModule(PluginModule pluginModule);

   ClassUtil getClassUtil();

   LiteCommandManager getCommandManager();

   PlayerModule getPlayerModule();

   void setCommandManager(LiteCommandManager liteCommandManager);

   LiteSender getConsole();

   LiteSender getConsoleSender();

   ConfigFile createConfig();

   File getDataFolder();

   Logger getLogger();

   PluginModule getModule(Class class2);

   PluginModule getModuleOrNull(Class class2);

   PluginModule[] getActiveModules();

   int obtainIntValue();

   LiteSender[] getOnlineSenders();

   Map getSenderCache();

   ThreadPoolExecutor getExecutor();

   long getStartTime();

   byte getPlatformId();

   PlatformBridge createBridge();

   String getVersion();

   void startup();

   boolean isEnabled();

   boolean obtainFlag2();

   boolean obtainFlag3();

   boolean isOnlineMode();

   boolean isPrimaryThread();

   boolean obtainFlag4();

   ConfigFile loadConfigFile(File file);

   void registerListeners(PlatformListener[] platformListeners);

   boolean checkText(String text, Supplier supplier);

   void runSync(Runnable runnable);

   void runSyncLater(Runnable runnable, long value);

   void runAsync(Runnable runnable);

   void runAsyncLater(Runnable runnable, long value);

   void execute(Runnable runnable);

   void runSyncTimer(Runnable runnable, long value, long value2);

   void runAsyncTimer(Runnable runnable, long value, long value2);

   void saveResource(String text);

   void setBootstrap(Bootstrap bootstrap);

   void setPlayerModule(PlayerModule playerModule);

   void unregisterAllListeners();

   void unregisterListeners(PlatformListener[] platformListeners);

   LiteSender removeSender(Object obj);

   LiteSender wrapSender(Object obj);
}
