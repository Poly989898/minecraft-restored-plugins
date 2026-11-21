package io.lumine.mythic.bukkit.utils.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class ConfigMerger {
   private ConfigMerger() {
   }

   public static boolean mergeDefaults(Plugin plugin, String resourcePath, File targetFile) {
      if (!targetFile.exists()) {
         return false;
      } else {
         InputStream resource = plugin.getResource(resourcePath);
         if (resource == null) {
            return false;
         } else {
            try {
               YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8));
               YamlConfiguration existing = YamlConfiguration.loadConfiguration(targetFile);
               Set<String> existingKeys = existing.getKeys(true);
               boolean changed = false;

               for (String key : defaults.getKeys(true)) {
                  if (!defaults.isConfigurationSection(key) && !existingKeys.contains(key) && !hasLeafAncestor(existing, key)) {
                     existing.set(key, defaults.get(key));
                     changed = true;
                  }
               }

               if (!changed) {
                  return false;
               } else {
                  for (String keyx : defaults.getKeys(true)) {
                     if (!existingKeys.contains(keyx) && existing.contains(keyx)) {
                        List<String> block = defaults.getComments(keyx);
                        if (!block.isEmpty()) {
                           existing.setComments(keyx, block);
                        }

                        List<String> inline = defaults.getInlineComments(keyx);
                        if (!inline.isEmpty()) {
                           existing.setInlineComments(keyx, inline);
                        }
                     }
                  }

                  atomicSave(existing, targetFile);
                  return true;
               }
            } catch (IOException e) {
               plugin.getLogger().warning("Failed to merge defaults from '" + resourcePath + "' into " + targetFile.getName() + ": " + e.getMessage());
               return false;
            }
         }
      }
   }

   private static boolean hasLeafAncestor(YamlConfiguration config, String key) {
      for (int dot = key.indexOf(46); dot > 0; dot = key.indexOf(46, dot + 1)) {
         String ancestor = key.substring(0, dot);
         if (config.contains(ancestor) && !config.isConfigurationSection(ancestor)) {
            return true;
         }
      }

      return false;
   }

   private static void atomicSave(YamlConfiguration config, File targetFile) throws IOException {
      Path targetPath = targetFile.toPath();
      Path tempPath = targetPath.resolveSibling(targetFile.getName() + ".tmp");

      try {
         config.save(tempPath.toFile());

         try {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException e) {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
         }
      } finally {
         Files.deleteIfExists(tempPath);
      }
   }
}
