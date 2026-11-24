package me.TechsCode.UltraPermissions.base.fileconf;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.fileconf.impl.BungeeFileConfiguration;
import me.TechsCode.UltraPermissions.base.fileconf.impl.SpigotFileConfiguration;

public class FileConfiguration implements Configuration {
   private Configuration configuration;

   public FileConfiguration(TechPlugin<?> techPlugin, File file) {
      HashMap<Class<?>, Class<? extends Configuration>> map = new HashMap<>();
      map.put(SpigotTechPlugin.class, SpigotFileConfiguration.class);
      map.put(BungeeTechPlugin.class, BungeeFileConfiguration.class);
      Class<? extends Configuration> type = map.entrySet().stream().filter(entry -> entry.getKey().isInstance(techPlugin)).map(Entry::getValue).findFirst().orElse(null);
      if (type == null) {
         throw new IllegalStateException("Could not find File Configuration Implementation");
      }

      try {
         this.configuration = (Configuration)type.getDeclaredConstructor(File.class).newInstance(file);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public Object get(String text) {
      return this.configuration.get(text);
   }

   @Override
   public int getInt(String text) {
      return this.configuration.getInt(text);
   }

   @Override
   public String getString(String text) {
      return this.configuration.getString(text);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.configuration.getBoolean(text);
   }

   @Override
   public Set<String> getKeys(boolean flag) {
      return this.configuration.getKeys(flag);
   }

   @Override
   public boolean contains(String text) {
      return this.configuration.contains(text);
   }

   @Override
   public void set(String text, Object obj) {
      this.configuration.set(text, obj);
   }

   @Override
   public void save() {
      this.configuration.save();
   }
}
