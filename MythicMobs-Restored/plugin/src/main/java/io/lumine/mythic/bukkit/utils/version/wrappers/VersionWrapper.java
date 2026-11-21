package io.lumine.mythic.bukkit.utils.version.wrappers;

import io.lumine.mythic.bukkit.utils.logging.Log;
import org.bukkit.Bukkit;

public abstract class VersionWrapper {
   private static VersionWrapper wrapper = null;
   private static boolean supported = true;

   public static final VersionWrapper get() {
      if (supported && wrapper == null) {
         String packageName = Bukkit.getServer().getClass().getPackage().getName();
         String version = packageName.substring(packageName.lastIndexOf(46) + 1);
         if (version.equals("v1_19_R1") && !"1.19-R0.1-SNAPSHOT".equals(Bukkit.getBukkitVersion())) {
            version = "v1_19_R1_2";
         }

         try {
            Class<?> clazz = Class.forName("io.lumine.mythic.bukkit.utils.version.wrappers.VersionWrapper_" + version);
            if (VersionWrapper.class.isAssignableFrom(clazz)) {
               wrapper = (VersionWrapper)clazz.getConstructor().newInstance();
            } else {
               supported = false;
            }
         } catch (ClassNotFoundException e) {
            Log.error("No compatible Bukkit version found");
            Log.error("Server NMS Version Detected - {0}", version);
            supported = false;
         } catch (Exception ex) {
            supported = false;
            throw new RuntimeException("Unknown exception loading version handler. NMS has been disabled.", ex);
         }

         return wrapper;
      } else {
         return wrapper;
      }
   }

   public static boolean isSupported() {
      return get() != null;
   }

   public abstract VersionItemHandler getItemHandler();
}
