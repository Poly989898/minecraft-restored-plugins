package io.lumine.mythic.lib.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationUpdater {
   private final Path configurationFile;
   private final String defaultConfigurationFile;
   private final ClassLoader loader;

   public ConfigurationUpdater(Path configurationFile, String defaultConfigurationFile, ClassLoader loader) {
      this.configurationFile = configurationFile;
      this.defaultConfigurationFile = defaultConfigurationFile;
      this.loader = loader;
   }

   public void update() {
      YamlConfiguration configuration = YamlConfiguration.loadConfiguration(this.configurationFile.toFile());

      try {
         InputStream inputStream = this.loader.getResourceAsStream(this.defaultConfigurationFile);

         try {
            if (inputStream == null) {
               throw new RuntimeException("Could not find default configuration file: " + this.defaultConfigurationFile);
            }

            boolean changed = false;
            Reader reader = new InputStreamReader(inputStream);

            try {
               YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(reader);

               for (String key : defaultConfiguration.getKeys(true)) {
                  if (!configuration.contains(key)) {
                     configuration.set(key, defaultConfiguration.get(key));
                     changed = true;
                  }
               }
            } catch (Throwable var10) {
               try {
                  reader.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            reader.close();
            if (changed) {
               configuration.save(this.configurationFile.toFile());
            }
         } catch (Throwable var11) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
