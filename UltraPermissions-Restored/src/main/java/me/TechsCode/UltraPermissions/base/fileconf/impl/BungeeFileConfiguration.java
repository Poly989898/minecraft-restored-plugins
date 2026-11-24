package me.TechsCode.UltraPermissions.base.fileconf.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.fileconf.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeFileConfiguration implements Configuration {
   private final File file;
   private net.md_5.bungee.config.Configuration configuration;

   public BungeeFileConfiguration(File file) {
      this.file = file;
      if (!file.exists()) {
         try {
            file.getParentFile().mkdirs();
            file.createNewFile();
         } catch (IOException exception) {
            exception.printStackTrace();
         }
      }

      try {
         this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
      } catch (IOException exception) {
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
      HashSet set = new HashSet();
      if (flag) {
         this.recursiveKeys(set, "", this.configuration);
      } else {
         set.addAll(this.configuration.getKeys());
      }

      return set;
   }

   private void recursiveKeys(Set<String> texts, String text, net.md_5.bungee.config.Configuration configuration) {
      texts.addAll(configuration.getKeys().stream().map(childKey -> text + childKey).collect(Collectors.toList()));

      for (String key : configuration.getKeys()) {
         try {
            net.md_5.bungee.config.Configuration childSection = configuration.getSection(key);
            if (childSection != null) {
               this.recursiveKeys(texts, text + key + ".", childSection);
            }
         } catch (ClassCastException exception) {
         }
      }
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
      try {
         ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
}
