package com.nexomc.libs.gui.components.util;

import com.google.common.primitives.Ints;
import com.nexomc.libs.gui.components.exception.GuiException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class VersionHelper {
   private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
   private static final int V1_11 = 1110;
   private static final int V1_13 = 1130;
   private static final int V1_14 = 1140;
   private static final int V1_16_5 = 1165;
   private static final int V1_12_1 = 1121;
   private static final int V1_20_1 = 1201;
   private static final int V1_20_5 = 1205;
   private static final int CURRENT_VERSION = getCurrentVersion();
   public static final boolean IS_COMPONENT_LEGACY;
   public static final boolean IS_ITEM_LEGACY;
   public static final boolean IS_UNBREAKABLE_LEGACY;
   public static final boolean IS_PDC_VERSION;
   public static final boolean IS_SKULL_OWNER_LEGACY;
   public static final boolean IS_CUSTOM_MODEL_DATA;
   public static final boolean IS_PLAYER_PROFILE_API;
   public static final boolean IS_ITEM_NAME_COMPONENT;
   private static final boolean IS_PAPER;
   public static final boolean IS_FOLIA;

   private static boolean checkPaper() {
      try {
         Class.forName("com.destroystokyo.paper.PaperConfig");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static boolean checkFolia() {
      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static int getCurrentVersion() {
      Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
      StringBuilder stringBuilder = new StringBuilder();
      if (matcher.find()) {
         stringBuilder.append(matcher.group("version").replace(".", ""));
         String patch = matcher.group("patch");
         if (patch == null) {
            stringBuilder.append("0");
         } else {
            stringBuilder.append(patch.replace(".", ""));
         }
      }

      Integer version = Ints.tryParse(stringBuilder.toString());
      if (version == null) {
         throw new GuiException("Could not retrieve server version!");
      } else {
         return version;
      }
   }

   public static Class<?> craftClass(@NotNull String name) throws ClassNotFoundException {
      return Class.forName(CRAFTBUKKIT_PACKAGE + "." + name);
   }

   static {
      IS_COMPONENT_LEGACY = CURRENT_VERSION < 1165;
      IS_ITEM_LEGACY = CURRENT_VERSION < 1130;
      IS_UNBREAKABLE_LEGACY = CURRENT_VERSION < 1110;
      IS_PDC_VERSION = CURRENT_VERSION >= 1140;
      IS_SKULL_OWNER_LEGACY = CURRENT_VERSION < 1121;
      IS_CUSTOM_MODEL_DATA = CURRENT_VERSION >= 1140;
      IS_PLAYER_PROFILE_API = CURRENT_VERSION >= 1201;
      IS_ITEM_NAME_COMPONENT = CURRENT_VERSION >= 1205;
      IS_PAPER = checkPaper();
      IS_FOLIA = checkFolia();
   }
}
