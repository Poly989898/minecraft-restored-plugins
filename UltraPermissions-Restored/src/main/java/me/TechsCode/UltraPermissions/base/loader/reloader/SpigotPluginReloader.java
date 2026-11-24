package me.TechsCode.UltraPermissions.base.loader.reloader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class SpigotPluginReloader extends PluginReloader<SpigotTechPlugin> {
   public SpigotPluginReloader(SpigotTechPlugin spigotTechPlugin) {
      super(spigotTechPlugin);
   }

   @Override
   public void unload() {
      Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin.getBootstrap());

      try {
         Field declaredField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
         declaredField.setAccessible(true);
         Map<?, ? extends Collection<RegisteredListener>> map = (Map)declaredField.get(Bukkit.getPluginManager());
         map.values().forEach(value -> value.removeIf(registeredListener -> registeredListener.getPlugin() == this.plugin.getBootstrap()));
      } catch (Exception exception) {
      }

      try {
         Field declaredField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
         declaredField.setAccessible(true);
         SimpleCommandMap simpleCommandMap = (SimpleCommandMap)declaredField.get(Bukkit.getPluginManager());
         Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
         knownCommandsField.setAccessible(true);
         Map map = (Map)knownCommandsField.get(simpleCommandMap);
         Iterator iterator = map.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            if (entry.getValue() instanceof PluginCommand) {
               PluginCommand pluginCommand = (PluginCommand)entry.getValue();
               if (pluginCommand.getPlugin() == this.plugin.getBootstrap()) {
                  pluginCommand.unregister(simpleCommandMap);
                  iterator.remove();
               }
            }
         }
      } catch (NoSuchFieldException | IllegalAccessException exception) {
         exception.printStackTrace();
      }

      ClassLoader classLoader = this.plugin.getBootstrap().getClass().getClassLoader();
      if (classLoader instanceof URLClassLoader) {
         try {
            Field declaredField = classLoader.getClass().getDeclaredField("plugin");
            declaredField.setAccessible(true);
            declaredField.set(classLoader, null);
            Field pluginInitField = classLoader.getClass().getDeclaredField("pluginInit");
            pluginInitField.setAccessible(true);
            pluginInitField.set(classLoader, null);
         } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
         }

         try {
            ((URLClassLoader)classLoader).close();
         } catch (IOException exception) {
            exception.printStackTrace();
         }
      }

      System.gc();
   }

   @Override
   public void load() {
      try {
         File file = new File(URLDecoder.decode(this.plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
         Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
         plugin.onLoad();
         Bukkit.getPluginManager().enablePlugin(plugin);
      } catch (UnsupportedEncodingException | InvalidDescriptionException | InvalidPluginException exception) {
         exception.printStackTrace();
      }
   }
}
