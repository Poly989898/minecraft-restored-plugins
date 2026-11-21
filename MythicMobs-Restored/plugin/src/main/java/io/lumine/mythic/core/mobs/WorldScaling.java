package io.lumine.mythic.core.mobs;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public class WorldScaling {
   private boolean enabled;
   private String world;
   private boolean scaleVanillaMobs;
   @Getter
   private double scaleBase;
   private double scaleFactor_perBlocksFromSpawn;
   @Getter
   private double scaleFactorBelowZero;
   public static ConcurrentHashMap<String, WorldScaling> worldSettings = new ConcurrentHashMap<>();

   public WorldScaling(String world, MythicConfig mc) {
      this.world = world;
      this.enabled = mc.getBoolean("Enabled", true);
      this.scaleVanillaMobs = mc.getBoolean("ScaleVanillaMobs", true);
      this.scaleBase = mc.getDouble("Base", 0.0);
      this.scaleFactor_perBlocksFromSpawn = mc.getDouble("PerBlocksFromSpawn", -1.0);
      this.scaleFactorBelowZero = mc.getDouble("BelowZero", -1.0);
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "**** WorldScaling for world Enabled: " + this.enabled);
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "**** WorldScaling for world perBlocksFromSpawn: " + this.scaleFactor_perBlocksFromSpawn);
   }

   public WorldScaling(String world) {
      this.world = world;
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getWorldName() {
      return this.world;
   }

   public boolean getScaleVanillaMobs() {
      return this.scaleVanillaMobs;
   }

   public double getScaleFactor_BlocksFromSpawn() {
      return this.scaleFactor_perBlocksFromSpawn;
   }

   public static void initialize(String world, File configFile) {
      MythicConfigImpl mc = new MythicConfigImpl("Configuration.MobLeveling.WorldScaling." + world, configFile);
      WorldScaling ws = new WorldScaling(world, mc);
      worldSettings.put(world, ws);
   }

   public static WorldScaling get(String world) {
      if (worldSettings.containsKey(world)) {
         return worldSettings.get(world);
      } else if (worldSettings.containsKey("Default")) {
         return worldSettings.get("Default");
      } else {
         WorldScaling ws = new WorldScaling(world);
         worldSettings.put(world, ws);
         return ws;
      }
   }

   public static void reset() {
      worldSettings.clear();
   }

   public static double getLevelBonus(AbstractLocation l) {
      WorldScaling ws = get(l.getWorld().getName());
      if (!ws.isEnabled()) {
         return 0.0;
      } else {
         double lvl = ws.getScaleBase();
         if (ws.getScaleFactor_BlocksFromSpawn() > 0.0) {
            double d = l.distance2D(l.getWorld().getSpawnLocation());
            lvl += d / ws.getScaleFactor_BlocksFromSpawn();
         }

         if (ws.getScaleFactorBelowZero() > 0.0 && l.getY() < 0.0) {
            lvl += ws.getScaleFactorBelowZero();
         }

         return lvl;
      }
   }
}
