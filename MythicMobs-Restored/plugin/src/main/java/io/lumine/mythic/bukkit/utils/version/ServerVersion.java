package io.lumine.mythic.bukkit.utils.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class ServerVersion {
   private static final MinecraftVersion MINECRAFT_VERSION = getServerVersion();
   private static final NMSVersion NMS = NMSVersion.forMinecraftVersion(MINECRAFT_VERSION).orElse(null);
   private static final boolean IS_PAPER;
   private static final boolean IS_FOLIA;
   private static final boolean IS_LEAF;

   public static MinecraftVersion get() {
      return MINECRAFT_VERSION;
   }

   public static NMSVersion getNMS() {
      return NMS;
   }

   public static boolean isPaper() {
      return IS_PAPER;
   }

   public static boolean isFolia() {
      return IS_FOLIA;
   }

   public static boolean isLeaf() {
      return IS_LEAF;
   }

   public static boolean isAfter(MinecraftVersion other) {
      return MINECRAFT_VERSION.compareTo(other) > 0;
   }

   public static boolean isAfterOrEq(MinecraftVersion other) {
      return MINECRAFT_VERSION.compareTo(other) >= 0;
   }

   public static boolean isBefore(MinecraftVersion other) {
      return MINECRAFT_VERSION.compareTo(other) < 0;
   }

   public static boolean isBeforeOrEq(MinecraftVersion other) {
      return MINECRAFT_VERSION.compareTo(other) <= 0;
   }

   public static boolean isBetween(MinecraftVersion o1, MinecraftVersion o2) {
      return isAfterOrEq(o1) && isBeforeOrEq(o2) || isBeforeOrEq(o1) && isAfterOrEq(o2);
   }

   public static boolean equals(MinecraftVersion other) {
      return MINECRAFT_VERSION.getMajor() == other.getMajor()
         && MINECRAFT_VERSION.getMinor() == other.getMinor()
         && MINECRAFT_VERSION.getBuild() == other.getBuild();
   }

   public static boolean eq(MinecraftVersion other) {
      return MINECRAFT_VERSION.getMajor() == other.getMajor()
         && MINECRAFT_VERSION.getMinor() == other.getMinor()
         && MINECRAFT_VERSION.getBuild() == other.getBuild();
   }

   private static MinecraftVersion getServerVersion() {
      String versionString = Bukkit.getVersion();
      Matcher version = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)").matcher(versionString);
      if (version.matches() && version.group(1) != null) {
         return MinecraftVersion.parse(version.group(1));
      } else {
         throw new IllegalStateException("Cannot parse version String '" + versionString + "'");
      }
   }

   static {
      boolean paper = false;

      try {
         paper = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
      } catch (ClassNotFoundException e) {
         paper = false;
      }

      IS_PAPER = paper;
      boolean leaf = false;

      try {
         leaf = Class.forName("gg.airplane.AirplaneConfig") != null;
      } catch (ClassNotFoundException ex) {
         leaf = false;
      }

      IS_LEAF = leaf;
      boolean folia = false;

      try {
         folia = Class.forName("io.papermc.paper.threadedregions.RegionizedServer") != null;
      } catch (ClassNotFoundException ex2) {
         folia = false;
      }

      IS_FOLIA = folia;
   }
}
