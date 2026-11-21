package io.lumine.mythic.bukkit.utils.reflection;

import io.lumine.mythic.bukkit.utils.version.NMSVersion;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public final class ServerReflection {
   public static final String NMS = "net.minecraft.server";
   public static final String OBC = "org.bukkit.craftbukkit";
   private static final String SERVER_VERSION;
   private static final NMSVersion NMS_VERSION;

   @Nonnull
   public static String getServerVersion() {
      return SERVER_VERSION;
   }

   @Nonnull
   public static NMSVersion getNMSVersion() {
      return NMS_VERSION;
   }

   @Nonnull
   public static String nms(String className) {
      return NMS_VERSION.nms(className);
   }

   @Nonnull
   public static Class<?> nmsClass(String className) throws ClassNotFoundException {
      return NMS_VERSION.nmsClass(className);
   }

   @Nonnull
   public static String obc(String className) {
      return NMS_VERSION.obc(className);
   }

   @Nonnull
   public static Class<?> obcClass(String className) throws ClassNotFoundException {
      return NMS_VERSION.obcClass(className);
   }

   private ServerReflection() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   static {
      String serverVersion = "";
      Class<?> server = Bukkit.getServer().getClass();
      if (server.getSimpleName().equals("CraftServer") && !server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
         String obcPackage = server.getPackage().getName();
         if (obcPackage.startsWith("org.bukkit.craftbukkit.")) {
            serverVersion = obcPackage.substring("org.bukkit.craftbukkit.".length());
         }
      }

      SERVER_VERSION = serverVersion;
      if (SERVER_VERSION.isEmpty()) {
         NMS_VERSION = NMSVersion.NONE;
      } else {
         NMS_VERSION = NMSVersion.valueOf(serverVersion);
      }
   }
}
