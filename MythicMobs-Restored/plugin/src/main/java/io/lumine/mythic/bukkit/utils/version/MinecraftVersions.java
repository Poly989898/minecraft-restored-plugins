package io.lumine.mythic.bukkit.utils.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class MinecraftVersions {
   public static final MinecraftVersion v26_1_1 = MinecraftVersion.parse("26.1.1");
   public static final MinecraftVersion v26_1 = MinecraftVersion.parse("26.1");
   public static final MinecraftVersion v1_21_11 = MinecraftVersion.parse("1.21.11");
   public static final MinecraftVersion v1_21_10 = MinecraftVersion.parse("1.21.10");
   public static final MinecraftVersion v1_21_9 = MinecraftVersion.parse("1.21.9");
   public static final MinecraftVersion v1_21_8 = MinecraftVersion.parse("1.21.8");
   public static final MinecraftVersion v1_21_7 = MinecraftVersion.parse("1.21.7");
   public static final MinecraftVersion v1_21_6 = MinecraftVersion.parse("1.21.6");
   public static final MinecraftVersion v1_21_5 = MinecraftVersion.parse("1.21.5");
   public static final MinecraftVersion v1_21_4 = MinecraftVersion.parse("1.21.4");
   public static final MinecraftVersion v1_21_3 = MinecraftVersion.parse("1.21.3");
   public static final MinecraftVersion v1_21_2 = MinecraftVersion.parse("1.21.2");
   public static final MinecraftVersion v1_21_1 = MinecraftVersion.parse("1.21.1");
   public static final MinecraftVersion v1_21 = MinecraftVersion.parse("1.21");
   public static final MinecraftVersion v1_20_6 = MinecraftVersion.parse("1.20.6");
   public static final MinecraftVersion v1_20_5 = MinecraftVersion.parse("1.20.5");
   public static final MinecraftVersion v1_20_4 = MinecraftVersion.parse("1.20.4");
   public static final MinecraftVersion v1_20_3 = MinecraftVersion.parse("1.20.3");
   public static final MinecraftVersion v1_20_2 = MinecraftVersion.parse("1.20.2");
   public static final MinecraftVersion v1_20_1 = MinecraftVersion.parse("1.20.1");
   public static final MinecraftVersion v1_20 = MinecraftVersion.parse("1.20");
   public static final MinecraftVersion v1_19_4 = MinecraftVersion.parse("1.19.4");
   public static final MinecraftVersion v1_19 = MinecraftVersion.parse("1.19");
   public static final MinecraftVersion v1_18_2 = MinecraftVersion.parse("1.18.2");
   public static final MinecraftVersion v1_18 = MinecraftVersion.parse("1.18");
   public static final MinecraftVersion v1_17 = MinecraftVersion.parse("1.17");
   public static final MinecraftVersion v1_16_4 = MinecraftVersion.parse("1.16.4");
   public static final MinecraftVersion v1_16_2 = MinecraftVersion.parse("1.16.2");
   public static final MinecraftVersion v1_16 = MinecraftVersion.parse("1.16");
   public static final MinecraftVersion v1_15 = MinecraftVersion.parse("1.15");
   public static final MinecraftVersion v1_14 = MinecraftVersion.parse("1.14");
   public static final MinecraftVersion v1_13 = MinecraftVersion.parse("1.13");
   public static final MinecraftVersion v1_12 = MinecraftVersion.parse("1.12");
   public static final MinecraftVersion v1_11 = MinecraftVersion.parse("1.11");
   public static final MinecraftVersion v1_10 = MinecraftVersion.parse("1.10");
   public static final MinecraftVersion v1_9 = MinecraftVersion.parse("1.9");
   public static final MinecraftVersion v1_8 = MinecraftVersion.parse("1.8");
   public static final MinecraftVersion v1_7_8 = MinecraftVersion.parse("1.7.8");
   public static final MinecraftVersion v1_7_2 = MinecraftVersion.parse("1.7.2");
   public static final MinecraftVersion v1_6_1 = MinecraftVersion.parse("1.6.1");
   public static final MinecraftVersion v1_5_0 = MinecraftVersion.parse("1.5.0");
   public static final MinecraftVersion v1_4_2 = MinecraftVersion.parse("1.4.2");
   private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)");
   static final MinecraftVersion RUNTIME_VERSION = parseServerVersion(Bukkit.getVersion());

   private static MinecraftVersion parseServerVersion(String serverVersion) {
      Matcher version = VERSION_PATTERN.matcher(serverVersion);
      if (version.matches() && version.group(1) != null) {
         return MinecraftVersion.parse(version.group(1));
      } else {
         throw new IllegalStateException("Cannot parse version String '" + serverVersion + "'");
      }
   }

   private MinecraftVersions() {
   }
}
