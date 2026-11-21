package io.lumine.mythic.core.skills.cinematic;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.packs.PackExecutor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class CameraPathLoader {
   private final MythicBukkit plugin;
   private final CameraPathRegistry registry;

   public CameraPathLoader(MythicBukkit plugin, CameraPathRegistry registry) {
      this.plugin = plugin;
      this.registry = registry;
   }

   public void load() {
      this.registry.clear();
      int count = 0;

      for (PackExecutor.ConfigFile cf : this.plugin.getPackManager().getConfigFiles(ConfigFileType.CAMERA_PATH)) {
         count += this.loadFile(cf.file());
      }

      MythicLogger.log("Loaded {0} camera path(s).", count);
   }

   private int loadFile(File file) {
      int loaded = 0;

      try {
         YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

         for (String key : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(key);
            if (section != null) {
               try {
                  CameraPath path = this.parsePath(section);
                  path.setSourceFile(file);
                  this.registry.register(key, path);
                  loaded++;
               } catch (Exception e) {
                  MythicLogger.error("Failed to parse camera path '{0}' in {1}: {2}", key, file.getName(), e.getMessage());
               }
            }
         }
      } catch (Exception ex) {
         MythicLogger.error("Failed to load camera path file {0}: {1}", file.getName(), ex.getMessage());
      }

      return loaded;
   }

   private CameraPath parsePath(ConfigurationSection section) {
      int duration = section.getInt("duration", 100);
      boolean loop = section.getBoolean("loop", false);
      Easing easing = Easing.fromString(section.getString("easing"), Easing.EASE_IN_OUT_CUBIC);
      List<String> rawPoints = section.getStringList("points");
      if (rawPoints.isEmpty()) {
         throw new IllegalArgumentException("path has no 'points' list");
      } else {
         List<CameraPoint> parsed = new ArrayList<>(rawPoints.size());

         for (String raw : rawPoints) {
            parsed.add(CameraPoint.fromString(raw));
         }

         return new CameraPath(parsed, duration, loop, easing);
      }
   }
}
