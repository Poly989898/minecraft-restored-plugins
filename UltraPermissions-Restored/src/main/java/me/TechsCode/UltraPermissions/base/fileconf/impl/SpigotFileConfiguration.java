package me.TechsCode.UltraPermissions.base.fileconf.impl;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import me.TechsCode.UltraPermissions.base.fileconf.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotFileConfiguration implements Configuration {
   private final File file;
   private final YamlConfiguration cfg;

   public SpigotFileConfiguration(File file) {
      this.file = file;
      this.cfg = YamlConfiguration.loadConfiguration(file);
   }

   @Override
   public Object get(String text) {
      return this.cfg.get(text);
   }

   @Override
   public int getInt(String text) {
      return this.cfg.getInt(text);
   }

   @Override
   public String getString(String text) {
      return this.cfg.getString(text);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.cfg.getBoolean(text);
   }

   @Override
   public Set<String> getKeys(boolean flag) {
      return this.cfg.getKeys(flag);
   }

   @Override
   public boolean contains(String text) {
      return this.cfg.contains(text);
   }

   @Override
   public void set(String text, Object obj) {
      this.cfg.set(text, obj);
   }

   @Override
   public void save() {
      try {
         this.cfg.save(this.file);
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
}
