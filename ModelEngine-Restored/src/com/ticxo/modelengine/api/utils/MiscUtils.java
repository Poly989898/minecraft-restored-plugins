package com.ticxo.modelengine.api.utils;

import it.unimi.dsi.fastutil.Pair;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {
   public static final DecimalFormat FORMATTER = new DecimalFormat() {
      {
         this.setMaximumFractionDigits(1);
         this.setMinimumFractionDigits(1);
      }
   };

   @SafeVarargs
   @NotNull
   public static <T> T or(T... values) {
      for (T v : values) {
         if (v != null) {
            return v;
         }
      }

      throw new RuntimeException("All values are null");
   }

   @SafeVarargs
   @NotNull
   public static <T> T orDef(@NotNull T def, T... values) {
      for (T v : values) {
         if (v != null) {
            return v;
         }
      }

      return def;
   }

   public static boolean isJava21OrHigher() {
      return isJavaVersionOrHigher(21);
   }

   public static boolean isJava25OrHigher() {
      return isJavaVersionOrHigher(25);
   }

   public static boolean isJavaVersionOrHigher(int v) {
      try {
         int version = Runtime.version().feature();
         return version >= v;
      } catch (Exception e) {
         int version = Integer.parseInt(System.getProperty("java.version"));
         return version >= v;
      }
   }

   public static UUID generateUUIDFromString(String input) {
      return UUID.nameUUIDFromBytes(input.getBytes(StandardCharsets.UTF_8));
   }

   public static Pair<String, String> getAnimationRef(String animation) {
      if (animation.startsWith("ref[") && animation.endsWith("]")) {
         animation = animation.substring(4, animation.length() - 1);
         String[] split = animation.split("\\.", 2);
         if (split.length >= 2) {
            return Pair.of(split[0], split[1]);
         }
      }

      return Pair.of(null, animation);
   }

   public static String createAnimationRef(String model, String animation) {
      return model == null ? animation : "ref[" + model + "." + animation + "]";
   }

   public static <T> T handle(T val, @Nullable Throwable throwable) {
      if (throwable != null) {
         throwable.printStackTrace();
      }

      return null;
   }

   public static void pfx(Location location, Color color, float size) {
      location.getWorld().spawnParticle(Particle.DUST, location, 1, new DustOptions(color, size));
   }
}
