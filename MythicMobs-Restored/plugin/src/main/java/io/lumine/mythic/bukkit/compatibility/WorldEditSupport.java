package io.lumine.mythic.bukkit.compatibility;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class WorldEditSupport {
   private Plugin instance = null;
   private int pluginVersion = 0;
   private WorldEditSupport.WorldGuardAdapter adapter;

   public WorldEditSupport() {
      this.instance = Bukkit.getPluginManager().getPlugin("WorldGuard");

      try (Scanner s = new Scanner(this.instance.getResource("plugin.yml")).useDelimiter("\\A")) {
         while (s.hasNext()) {
            String s1 = s.nextLine();
            if (s1.startsWith("version")) {
               this.pluginVersion = Integer.parseInt(s1.substring(10, 11));
               break;
            }
         }
      }

      if (this.pluginVersion < 7) {
         try {
            this.adapter = new WorldEditSupport.WorldGuardSix(this.instance);
         } catch (Exception e) {
            throw new IllegalArgumentException("Invalid WorldGuard version");
         }
      } else {
         this.adapter = new WorldEditSupport.WorldGuardSeven(this.instance);
      }
   }

   public boolean isLocationInRegions(AbstractLocation loc, String region) {
      return this.adapter.isLocationInRegions(loc, region);
   }

   public boolean LocationAllowsMobSpawning(Location l) {
      return true;
   }

   protected interface WorldGuardAdapter {
      boolean isLocationInRegions(AbstractLocation abstractLocation, String str);
   }

   protected class WorldGuardSeven implements WorldEditSupport.WorldGuardAdapter {
      private WorldGuardPlatform worldguard = WorldGuard.getInstance().getPlatform();

      public WorldGuardSeven(Plugin worldguard) {
      }

      @Override
      public boolean isLocationInRegions(AbstractLocation loc, String region) {
         Location l = BukkitAdapter.adapt(loc);
         RegionManager regionManager = this.worldguard.getRegionContainer().get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l.getWorld()));
         List<String> set = regionManager.getApplicableRegionsIDs(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(l));
         String[] regions = region.split(",");

         for (String str : set) {
            for (String r : regions) {
               if (r.equals(str)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected class WorldGuardSix implements WorldEditSupport.WorldGuardAdapter {
      int version = 6;
      Class<?> class_WorldGuardPlugin;
      Class<?> class_WorldGuard;
      Class<?> class_FlagRegistry;
      Class<?> class_RegionContainer;
      Class<?> class_RegionManager;
      Class<?> class_ApplicableRegionSet;
      Class<?> class_EntityType;
      Class<?> class_Vector;
      Class<?> class_BukkitUtils;
      Method class_WorldGuardPlugin_getFlagRegistry;
      Method class_WorldGuard_getRegionContainer;
      Method class_RegionContainer_getRegionManager;
      Method class_RegionManager_getApplicableRegions;
      Method class_RegionManager_getApplicableRegionsIDs;
      Method class_RegionManager_getRegion;
      Method class_ApplicationRegionSet_getRegions;
      Method class_EntityType_getName;
      Method toVector;
      Object worldguard;
      Object flag_registry;

      public WorldGuardSix(Plugin worldguard) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
         this.load_classes();
         this.worldguard = worldguard;
         this.flag_registry = this.class_WorldGuardPlugin.getMethod("getFlagRegistry").invoke(worldguard);
      }

      @Override
      public boolean isLocationInRegions(AbstractLocation loc, String region) {
         Location l = BukkitAdapter.adapt(loc);
         Object region_manager = null;
         Object applicable_region_set = null;

         try {
            region_manager = this.class_RegionContainer_getRegionManager.invoke(this.class_WorldGuard_getRegionContainer.invoke(this.worldguard), l.getWorld());
            if (region_manager != null) {
               Object vector = this.toVector.invoke(null, l);
               applicable_region_set = this.class_RegionManager_getApplicableRegionsIDs.invoke(region_manager, vector);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (applicable_region_set == null) {
            return false;
         } else {
            List<String> set = (List<String>)applicable_region_set;
            String[] regions = region.split(",");

            for (String str : set) {
               for (String r : regions) {
                  if (r.equals(str)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      private void load_classes() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
         ClassLoader class_loader = this.getClass().getClassLoader();
         this.class_FlagRegistry = class_loader.loadClass("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
         this.class_RegionManager = class_loader.loadClass("com.sk89q.worldguard.protection.managers.RegionManager");
         this.class_ApplicableRegionSet = class_loader.loadClass("com.sk89q.worldguard.protection.ApplicableRegionSet");
         this.class_WorldGuardPlugin = class_loader.loadClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
         this.class_WorldGuard = this.class_WorldGuardPlugin;
         this.class_RegionContainer = class_loader.loadClass("com.sk89q.worldguard.bukkit.RegionContainer");
         this.class_RegionContainer_getRegionManager = this.class_RegionContainer.getMethod("get", World.class);
         this.class_EntityType = class_loader.loadClass("org.bukkit.entity.EntityType");
         this.class_BukkitUtils = class_loader.loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
         this.toVector = this.class_BukkitUtils.getMethod("toVector", Location.class);
         this.class_Vector = class_loader.loadClass("com.sk89q.worldedit.Vector");
         this.class_WorldGuardPlugin_getFlagRegistry = this.class_WorldGuardPlugin.getMethod("getFlagRegistry");
         this.class_WorldGuard_getRegionContainer = this.class_WorldGuard.getMethod("getRegionContainer");
         this.class_RegionManager_getApplicableRegions = this.class_RegionManager.getMethod("getApplicableRegions", this.class_Vector);
         this.class_RegionManager_getApplicableRegionsIDs = this.class_RegionManager.getMethod("getApplicableRegionsIDs", this.class_Vector);
         this.class_RegionManager_getRegion = this.class_RegionManager.getMethod("getRegion", String.class);
         this.class_ApplicationRegionSet_getRegions = this.class_ApplicableRegionSet.getMethod("getRegions");
         this.class_EntityType_getName = this.class_EntityType.getMethod("getName");
      }

      public Object getFlagRegistry() {
         return this.flag_registry;
      }

      public Object getApplicableRegions(World world, Vector vector) {
         Object region_manager = null;
         Object applicable_region_set = null;
         Object regions = null;

         try {
            region_manager = this.class_RegionContainer_getRegionManager
               .invoke(this.class_WorldGuard_getRegionContainer.invoke(this.worldguard), this.version > 6 ? new BukkitWorld(world) : world);
            if (region_manager != null) {
               applicable_region_set = this.class_RegionManager_getApplicableRegions.invoke(region_manager, vector);
               if (applicable_region_set != null) {
                  regions = this.class_ApplicationRegionSet_getRegions.invoke(applicable_region_set);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return regions;
      }

      public Object getApplicableRegionIDs(World world, Vector vector) {
         Object region_manager = null;
         Object applicable_region_set = null;
         Object regions = null;

         try {
            region_manager = this.class_RegionContainer_getRegionManager
               .invoke(this.class_WorldGuard_getRegionContainer.invoke(this.worldguard), this.version > 6 ? new BukkitWorld(world) : world);
            if (region_manager != null) {
               applicable_region_set = this.class_RegionManager_getApplicableRegions.invoke(region_manager, vector);
               if (applicable_region_set != null) {
                  regions = this.class_ApplicationRegionSet_getRegions.invoke(applicable_region_set);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return regions;
      }

      public Object getRegion(World world, String region_name) {
         Object region_manager = null;
         Object region = null;

         try {
            region_manager = this.class_RegionContainer_getRegionManager
               .invoke(this.class_WorldGuard_getRegionContainer.invoke(this.worldguard), this.version > 6 ? new BukkitWorld(world) : world);
            if (region_manager != null) {
               region = this.class_RegionManager_getRegion.invoke(region_manager, region_name);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return region;
      }

      public String class_EntityType_getName(Object entity_type) {
         String s1 = "";

         try {
            s1 = (String)this.class_EntityType_getName.invoke(entity_type);
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (s1.startsWith("minecraft:")) {
            s1 = s1.substring(10);
         }

         return s1;
      }
   }
}
