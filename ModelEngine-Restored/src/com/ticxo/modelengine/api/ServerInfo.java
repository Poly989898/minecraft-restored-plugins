package com.ticxo.modelengine.api;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ServerInfo {
   public static final boolean IS_PAPER = classExists("com.destroystokyo.paper.VersionHistoryManager$VersionData");
   public static final boolean IS_FOLIA = classExists("io.papermc.paper.threadedregions.RegionizedServer");
   public static final String NMS_VERSION = Bukkit.getServer().getMinecraftVersion();
   public static final int VERSION_NUMBER = Integer.parseInt(NMS_VERSION.split("\\.")[1]);
   public static boolean HAS_VIAVERSION;
   public static boolean HAS_CITIZENS;
   public static volatile int CURRENT_TICK;
   private static final Set<UUID> ONLINE_PLAYERS = new HashSet<>();
   private static volatile ImmutableSet<UUID> ONLINE_PLAYERS_VIEW = ImmutableSet.of();

   public static void setOnline(UUID uuid, boolean flag) {
      if (flag) {
         ONLINE_PLAYERS.add(uuid);
      } else {
         ONLINE_PLAYERS.remove(uuid);
      }

      ONLINE_PLAYERS_VIEW = ImmutableSet.copyOf(ONLINE_PLAYERS);
   }

   public static ImmutableSet<UUID> getOnlinePlayers() {
      return ONLINE_PLAYERS_VIEW;
   }

   public static boolean scheduleOnRegion(Entity entity, Runnable task) {
      if (!IS_FOLIA) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         }

         Bukkit.getScheduler().runTask(ModelEngineAPI.getAPI(), task);
         return true;
      } else {
         boolean onRegionThread;
         try {
            onRegionThread = Bukkit.isOwnedByCurrentRegion(entity);
         } catch (Exception e) {
            onRegionThread = false;
         }

         if (onRegionThread) {
            return false;
         }

         Location loc = new Location(null, 0.0, 0.0, 0.0);
         ModelEngineAPI.getEntityHandler().getLocation(entity, loc);
         if (loc.getWorld() != null) {
            Bukkit.getRegionScheduler().execute(ModelEngineAPI.getAPI(), loc, task);
         }

         return true;
      }
   }

   public static void scheduleOnRegionDelayed(Entity entity, Runnable task, long delayTicks) {
      entity.getScheduler().execute(ModelEngineAPI.getAPI(), task, null, delayTicks);
   }

   private static boolean classExists(String path) {
      try {
         Class.forName(path);
         return true;
      } catch (ClassNotFoundException ignore) {
         return false;
      }
   }
}
