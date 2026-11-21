package io.lumine.mythic.bukkit.utils.plugin;

import com.google.common.base.Preconditions;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.command.CommandMapUtil;
import io.lumine.mythic.bukkit.utils.config.ConfigurationLoader;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.maven.LibraryLoader;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableModule;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class LuminePlugin extends JavaPlugin implements Terminable, TerminableConsumer {
   private LuminePlugin loaderPlugin;
   private CompositeTerminable terminableRegistry = CompositeTerminable.create();
   private Map<String, Object> commands = new ConcurrentHashMap<>();
   private Map<String, ConfigurationLoader<LuminePlugin>> propertyFiles = new ConcurrentHashMap<>();
   private Set<PropertyType> properties = new HashSet<>();

   protected void load() {
   }

   protected void enable() {
   }

   protected void disable() {
   }

   protected void postStartup() {
   }

   public final void onLoad() {
      this.loaderPlugin = LoaderUtils.getPlugin();
      this.terminableRegistry = CompositeTerminable.create();
      LibraryLoader.loadAll(this.getClass());
      this.load();
   }

   public final void onEnable() {
      Schedulers.builder()
         .async()
         .after(10L, TimeUnit.SECONDS)
         .every(30L, TimeUnit.SECONDS)
         .run(this.terminableRegistry::cleanup)
         .bindWith(this.terminableRegistry);
      this.enable();
      Schedulers.sync().runLater(() -> this.postStartup(), 1L);
   }

   public final void onDisable() {
      try {
         this.disable();
      } catch (Error | Exception e) {
         e.printStackTrace();
      }

      this.close();
   }

   @Override
   public void close() {
      this.terminableRegistry.closeAndReportException();
   }

   public void reloadConfiguration() {
      this.propertyFiles.clear();
      this.properties.forEach(prop -> prop.reload());
   }

   @Nonnull
   @Override
   public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
      return this.terminableRegistry.bind(terminable);
   }

   @Nonnull
   @Override
   public <T extends TerminableModule> T bindModule(@Nonnull T module) {
      return this.terminableRegistry.bindModule(module);
   }

   @Nonnull
   public <T extends CommandExecutor & TabCompleter> void registerCommand(String cmd, T handler) {
      PluginCommand command = this.getCommand(cmd);
      if (command != null) {
         command.setExecutor(handler);
         command.setTabCompleter(handler);
      } else {
         CommandMapUtil.registerCommand(this, handler, cmd);
      }

      this.commands.put(cmd, handler);
   }

   @Nonnull
   public <T extends CommandExecutor> T registerCommand(
      @Nonnull T command, String permission, String permissionMessage, String description, @Nonnull String... aliases
   ) {
      return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
   }

   @Nonnull
   protected <T extends CommandExecutor & TabCompleter> void unregisterCommand(T handler) {
      CommandMapUtil.unregisterCommand(handler);
   }

   @Nonnull
   protected void unregisterCommand(String cmd) {
      Object handler = this.commands.get(cmd);
      if (handler != null && handler instanceof CommandExecutor && handler instanceof TabCompleter) {
         CommandMapUtil.unregisterCommand((CommandExecutor & TabCompleter)handler);
      }
   }

   @Nonnull
   protected void unregisterCommands() {
      for (Object handler : this.commands.values()) {
         if (handler != null && handler instanceof CommandExecutor && handler instanceof TabCompleter) {
            CommandMapUtil.unregisterCommand((CommandExecutor & TabCompleter)handler);
         }
      }

      this.commands.clear();
   }

   @Nonnull
   public <T extends Listener> void registerListener(@Nonnull T listener) {
      Preconditions.checkNotNull(listener, "listener");
      this.getServer().getPluginManager().registerEvents(listener, this);
   }

   @Nullable
   public <T> T getService(@Nonnull Class<T> service) {
      return this.getServer().getServicesManager().load(service);
   }

   @Nonnull
   public <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance, @Nonnull ServicePriority priority) {
      this.getServer().getServicesManager().register(clazz, instance, this, priority);
      return instance;
   }

   @Nonnull
   public <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance) {
      Preconditions.checkNotNull(clazz, "clazz");
      Preconditions.checkNotNull(instance, "instance");
      return this.provideService(clazz, instance, ServicePriority.Normal);
   }

   @Nullable
   public <T> T getPlugin(@Nonnull String name, @Nonnull Class<T> pluginClass) {
      Preconditions.checkNotNull(name, "name");
      Preconditions.checkNotNull(pluginClass, "pluginClass");
      return (T)this.getServer().getPluginManager().getPlugin(name);
   }

   public boolean isPluginPresent(@Nonnull String name) {
      return this.getServer().getPluginManager().getPlugin(name) != null;
   }

   public void registerProperty(PropertyType property) {
      this.properties.add(property);
   }

   public ConfigurationSection getPropertyConfig(String file) {
      return this.getPropertyConfig(file, false);
   }

   public ConfigurationSection getPropertyConfig(String file, boolean isFileAbsolute) {
      ConfigurationLoader<LuminePlugin> loader;
      if (this.propertyFiles.containsKey(file)) {
         loader = this.propertyFiles.get(file);
      } else {
         loader = new ConfigurationLoader<>(this, file.contains(".") ? file : file + ".yml", isFileAbsolute);
         this.propertyFiles.put(file, loader);
      }

      return loader.getCustomConfig();
   }

   public File getPropertyFile(String file) {
      ConfigurationLoader<LuminePlugin> loader;
      if (this.propertyFiles.containsKey(file)) {
         loader = this.propertyFiles.get(file);
      } else {
         loader = new ConfigurationLoader<>(this, file + ".yml");
         this.propertyFiles.put(file, loader);
      }

      return loader.getFile();
   }

   public ConfigurationSection getPropertyFileInternal(String file) {
      return new ConfigurationLoader<>(this, this.getClass().getResourceAsStream("/" + file)).getCustomConfig();
   }

   public ConfigurationSection getPropertyFileInternal(InputStream file) {
      return new ConfigurationLoader<>(this, file).getCustomConfig();
   }

   public File getJarFile() {
      return this.getFile();
   }

   public void saveResource(String resourcePath, File outputFile, boolean replace) {
      if (resourcePath != null && !resourcePath.equals("")) {
         resourcePath = resourcePath.replace('\\', '/');
         InputStream in = this.getResource(resourcePath);
         if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' could not be found.");
         } else {
            File outFile = outputFile;
            File outDir = outputFile.getParentFile();
            if (!outDir.exists()) {
               outDir.mkdirs();
            }

            try {
               if (outFile.exists() && !replace) {
                  Log.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
               } else {
                  OutputStream out = new FileOutputStream(outFile);
                  byte[] buf = new byte[1024];

                  int len;
                  while ((len = in.read(buf)) > 0) {
                     out.write(buf, 0, len);
                  }

                  out.close();
                  in.close();
               }
            } catch (Throwable e) {
               Log.severe("Could not save " + outputFile.getName() + " to " + outputFile);
               e.printStackTrace();
            }
         }
      } else {
         throw new IllegalArgumentException("ResourcePath cannot be null or empty");
      }
   }

   public void saveDirectoryResource(String resourceDirPath, File outputDir, boolean replace) {
      if (resourceDirPath != null && !resourceDirPath.equals("")) {
         resourceDirPath = resourceDirPath.replace('\\', '/');
         if (!resourceDirPath.endsWith("/")) {
            resourceDirPath = resourceDirPath + "/";
         }

         URL dirURL = this.getClass().getClassLoader().getResource(resourceDirPath);
         if (dirURL == null) {
            throw new IllegalArgumentException("The embedded directory '" + resourceDirPath + "' could not be found.");
         } else if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
               Enumeration<JarEntry> entries = jar.entries();

               while (entries.hasMoreElements()) {
                  JarEntry entry = entries.nextElement();
                  String name = entry.getName();
                  if (name.startsWith(resourceDirPath)) {
                     String entryPathInsideDir = name.substring(resourceDirPath.length());
                     File outputFile = new File(outputDir, entryPathInsideDir);
                     if (entry.isDirectory()) {
                        outputFile.mkdirs();
                     } else {
                        try (InputStream entryInputStream = jar.getInputStream(entry)) {
                           this.saveResourceFromStream(entryInputStream, outputFile, replace);
                        }
                     }
                  }
               }
            } catch (Throwable e) {
               Log.severe("Could not save directory " + resourceDirPath + " to " + outputDir);
               e.printStackTrace();
            }
         } else {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
         }
      } else {
         throw new IllegalArgumentException("ResourceDirPath cannot be null or empty");
      }
   }

   private void saveResourceFromStream(InputStream in, File outputFile, boolean replace) {
      File outDir = outputFile.getParentFile();
      if (!outDir.exists()) {
         outDir.mkdirs();
      }

      try {
         if (outputFile.exists() && !replace) {
            Log.warn("Could not save " + outputFile.getName() + " to " + outputFile + " because " + outputFile.getName() + " already exists.");
         } else {
            try (OutputStream out = new FileOutputStream(outputFile)) {
               byte[] buf = new byte[1024];

               int len;
               while ((len = in.read(buf)) > 0) {
                  out.write(buf, 0, len);
               }
            }
         }
      } catch (Throwable e) {
         Log.severe("Could not save " + outputFile.getName() + " to " + outputFile);
         e.printStackTrace();
      }
   }
}
