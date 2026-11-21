package it.ytnoos.lpx.config;

import it.ytnoos.lpx.ConfigVersionUpdates;
import it.ytnoos.lpx.ConfigDefaultUpdates;
import it.ytnoos.lpx.ConfigDefaultUpdate;
import it.ytnoos.lpx.api.LPXPlugin;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

public class ConfigUpdater {
   static final String CONFIG_VERSION_KEY = "config-version";
   private static final String FLOOD_B_LIMITS_PATH = "checks.flood.b.options.limits";
   private static final String NETTY_B_PATH = "checks.netty.b";
   private static final Map<String, String> MOVED_KEYS = Map.of(
      "options.max-item-depth", "checks.netty.b.options.max-item-depth", "options.max-items-per-packet", "checks.netty.b.options.max-items-per-packet"
   );

   public ConfigUpdater() {
   }

   public static void update(LPXPlugin lpxPlugin) {
      File file = lpxPlugin.getConfigFile();
      if (file != null && file.exists()) {
         YamlConfiguration yamlConfiguration;
         try {
            label133: {
               try (InputStream inputStream = lpxPlugin.getResource("config.yml")) {
                  if (inputStream != null) {
                     yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                     break label133;
                  }
               }

               return;
            }
         } catch (IOException ioException) {
            lpxPlugin.getLogger().warning("Failed to read default config for auto-update");
            return;
         }

         YamlConfiguration yamlConfiguration2 = YamlConfiguration.loadConfiguration(file);
         LinkedHashMap linkedHashMap = new LinkedHashMap();

         for (Entry entry : MOVED_KEYS.entrySet()) {
            if (yamlConfiguration2.isSet((String)entry.getKey())) {
               linkedHashMap.put((String)entry.getKey(), yamlConfiguration2.isSet((String)entry.getValue()) ? null : yamlConfiguration2.getString((String)entry.getKey()));
            }
         }

         boolean flag = yamlConfiguration2.isList("checks.flood.b.options.limits");
         boolean flag2 = yamlConfiguration2.isSet("checks.netty.b.options.resolved");
         int value = yamlConfiguration2.getInt("config-version", 0);
         Set set = findMissingKeys(yamlConfiguration, yamlConfiguration2);
         if (flag2) {
            set.removeIf(object -> ((String)object).startsWith("checks.netty.b."));
         }

         if (!set.isEmpty() || !linkedHashMap.isEmpty() || flag || flag2 || value < 2) {
            List list = readBundledConfigLines(lpxPlugin);
            if (list != null) {
               ArrayList arrayList;
               try {
                  arrayList = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
               } catch (IOException ioException2) {
                  lpxPlugin.getLogger().warning("Failed to read config file for auto-update");
                  return;
               }

               if (flag) {
                  replaceSection(arrayList, list, "checks.flood.b.options.limits");
               }

               if (flag2) {
                  replaceSection(arrayList, list, "checks.netty.b");
               }

               addMissingAndMoveKeys(arrayList, list, set, linkedHashMap, lpxPlugin.getLogger());
               List list2 = applyDefaultValueUpdates(arrayList, value, lpxPlugin.getLogger());

               try {
                  Files.write(file.toPath(), arrayList, StandardCharsets.UTF_8);
               } catch (IOException ioException3) {
                  lpxPlugin.getLogger().warning("Failed to write updated config file");
                  return;
               }

               lpxPlugin.reload();
               if (flag) {
                  lpxPlugin.getLogger().info("Migrated FloodB config to new format with default values.");
               }

               if (flag2) {
                  lpxPlugin.getLogger().info("Replaced the removed NettyB check config with the new NettyB check.");
               }

               if (!set.isEmpty()) {
                  lpxPlugin.getLogger().info("Config updated: added missing keys " + set);
               }

               if (!linkedHashMap.isEmpty()) {
                  lpxPlugin.getLogger().info("Config updated: moved keys " + linkedHashMap.keySet());
               }

               if (!list2.isEmpty()) {
                  lpxPlugin.getLogger().info("Config updated: new default values for " + list2);
               }
            }
         }
      }
   }

   static List applyDefaultValueUpdates(List list, int value, Logger logger) {
      ArrayList arrayList = new ArrayList();

      for (ConfigVersionUpdates f : ConfigDefaultUpdates.VERSIONS) {
         if (f.version() > value) {
            for (ConfigDefaultUpdate qu : f.updates()) {
               int value2 = findKeyLine(list, qu.path());
               if (value2 != -1) {
                  String string = readScalarValue((String)list.get(value2));
                  if (qu.oldDefaults().contains(string)) {
                     setScalarValue(list, qu.path(), qu.newDefault());
                     arrayList.add(qu.path());
                     String string2 = qu.path();
                     String string3 = qu.newDefault();
                     String string4 = string;
                     String string5 = string2;
                     logger.info("Updating default value of " + string5 + ": " + string4 + " -> " + string3);
                  }
               }
            }
         }
      }

      setScalarValue(list, "config-version", String.valueOf(2));
      return arrayList;
   }

   private static String readScalarValue(String string) {
      String string2 = string.substring(string.indexOf(58) + 1);
      int value = string2.indexOf(" #");
      if (value != -1) {
         string2 = string2.substring(0, value);
      }

      string2 = string2.trim();
      if (string2.length() >= 2 && (string2.startsWith("\"") && string2.endsWith("\"") || string2.startsWith("'") && string2.endsWith("'"))) {
         string2 = string2.substring(1, string2.length() - 1);
      }

      return string2;
   }

   static void addMissingAndMoveKeys(List list, List list2, Set set, Map map, Logger logger) {
      for (Object var23_o : set) { String string = (String) var23_o;
         if (insertMissingKey(list, list2, string)) {
            logger.info("Auto updating config: " + string);
         }
      }

      for (Object var25_o : map.entrySet()) { Entry entry = (Entry) var25_o;
         if (entry.getValue() != null) {
            setScalarValue(list, MOVED_KEYS.get(entry.getKey()), (String)entry.getValue());
         }

         removeKeyBlock(list, (String)entry.getKey());
      }
   }

   static void replaceSection(List list, List list2, String string) {
      int value = findKeyLine(list, string);
      if (value != -1) {
         List list3 = extractKeyBlock(list2, string);
         if (!list3.isEmpty()) {
            list3.remove(0);
            int value2 = ((String)list.get(value)).length() - ((String)list.get(value)).stripLeading().length();
            int value3 = value;

            while (
               value3 > 0
                  && ((String)list.get(value3 - 1)).stripLeading().startsWith("#")
                  && ((String)list.get(value3 - 1)).length() - ((String)list.get(value3 - 1)).stripLeading().length() == value2
            ) {
               value3--;
            }

            int value4;
            for (value4 = value + 1; value4 < list.size(); value4++) {
               String string2 = (String)list.get(value4);
               if (!string2.trim().isEmpty() && string2.length() - string2.stripLeading().length() <= value2 && !string2.stripLeading().startsWith("- ")) {
                  break;
               }
            }

            while (value4 > value + 1 && ((String)list.get(value4 - 1)).trim().isEmpty()) {
               value4--;
            }

            list.subList(value3, value4).clear();
            list.addAll(value3, list3);
         }
      }
   }

   private static int findKeyLine(List list, String string) {
      String[] strings = string.split("\\.");
      int value = (strings.length - 1) * 2;
      String string2 = " ".repeat(value) + strings[strings.length - 1] + ":";

      for (int value2 = 0; value2 < list.size(); value2++) {
         if (lineIsKey((String)list.get(value2), string2) && parentKeysMatch(list, value2, strings, value)) {
            return value2;
         }
      }

      return -1;
   }

   private static void setScalarValue(List list, String string, String string2) {
      int value = findKeyLine(list, string);
      if (value != -1) {
         String string3 = (String)list.get(value);
         list.set(value, string3.substring(0, string3.indexOf(58) + 1) + " " + string2);
      }
   }

   private static void removeKeyBlock(List list, String string) {
      int value = findKeyLine(list, string);
      if (value != -1) {
         int value2 = value;

         while (value2 > 0 && ((String)list.get(value2 - 1)).stripLeading().startsWith("#")) {
            value2--;
         }

         int value3 = value + 1;
         boolean flag = value2 > 0 && ((String)list.get(value2 - 1)).trim().isEmpty();
         boolean flag2 = value2 > 0 && ((String)list.get(value2 - 1)).trim().endsWith(":");
         if ((flag || flag2) && value3 < list.size() && ((String)list.get(value3)).trim().isEmpty()) {
            value3++;
         }

         list.subList(value2, value3).clear();
      }
   }

   private static Set findMissingKeys(ConfigurationSection configurationSection, ConfigurationSection configurationSection2) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      collectMissingKeys(configurationSection, configurationSection2, linkedHashSet);
      return linkedHashSet;
   }

   private static void collectMissingKeys(ConfigurationSection configurationSection, ConfigurationSection configurationSection2, Set set) {
      for (String string : configurationSection.getKeys(false)) {
         String string2 = configurationSection.getCurrentPath().isEmpty() ? string : configurationSection.getCurrentPath() + "." + string;
         if (!configurationSection2.contains(string)) {
            set.add(string2);
         } else if (configurationSection.isConfigurationSection(string) && configurationSection2.isConfigurationSection(string)) {
            collectMissingKeys(configurationSection.getConfigurationSection(string), configurationSection2.getConfigurationSection(string), set);
         }
      }
   }

   private static List readBundledConfigLines(LPXPlugin lpxPlugin) {
      try (InputStream inputStream = lpxPlugin.getResource("config.yml")) {
         if (inputStream == null) {
            return null;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
         return bufferedReader.lines().toList();
      } catch (IOException ioException) {
         return null;
      }
   }

   private static List extractKeyBlock(List list, String string) {
      String[] strings = string.split("\\.");
      String string2 = strings[strings.length - 1];
      int value = (strings.length - 1) * 2;
      String string3 = " ".repeat(value) + string2 + ":";
      int value2 = -1;

      for (int value3 = 0; value3 < list.size(); value3++) {
         String string4 = (String)list.get(value3);
         if (lineIsKey(string4, string3) && parentKeysMatch(list, value3, strings, value)) {
            value2 = value3;
            break;
         }
      }

      if (value2 == -1) {
         return List.of();
      }

      int value4;
      for (value4 = value2; value4 > 0; value4--) {
         String string5 = (String)list.get(value4 - 1);
         String string6 = string5.stripLeading();
         if (!string6.startsWith("#") && !string6.isEmpty()) {
            break;
         }

         int value5 = string5.length() - string5.stripLeading().length();
         if (!string6.isEmpty() && value5 < value) {
            break;
         }
      }

      while (value4 < value2 && ((String)list.get(value4)).trim().isEmpty()) {
         value4++;
      }

      int value6 = value2 + 1;

      while (value6 < list.size()) {
         String string7 = (String)list.get(value6);
         if (string7.trim().isEmpty()) {
            value6++;
         } else {
            int value7 = string7.length() - string7.stripLeading().length();
            if (value7 <= value) {
               break;
            }

            value6++;
         }
      }

      while (value6 > value2 + 1 && ((String)list.get(value6 - 1)).trim().isEmpty()) {
         value6--;
      }

      ArrayList arrayList = new ArrayList();
      arrayList.add("");
      arrayList.addAll(list.subList(value4, value6));
      return arrayList;
   }

   private static boolean lineIsKey(String string, String string2) {
      return string.equals(string2) || string.startsWith(string2 + " ") || string.startsWith(string2 + "\n");
   }

   private static boolean parentKeysMatch(List list, int value, String[] strings, int value2) {
      String[] strings2 = strings;
      if (strings2.length <= 1) {
         return true;
      }

      for (int value3 = strings2.length - 2; value3 >= 0; value3--) {
         int value4 = value3 * 2;
         String string = " ".repeat(value4) + strings2[value3] + ":";
         boolean flag = false;

         for (int value5 = value - 1; value5 >= 0; value5--) {
            String string2 = (String)list.get(value5);
            if (!string2.trim().isEmpty() && !string2.stripLeading().startsWith("#")) {
               int value6 = string2.length() - string2.stripLeading().length();
               if (value6 <= value4) {
                  if (lineIsKey(string2, string)) {
                     flag = true;
                  }
                  break;
               }
            }
         }

         if (!flag) {
            return false;
         }
      }

      return true;
   }

   private static boolean insertMissingKey(List list, List list2, String string) {
      List list3 = extractKeyBlock(list2, string);
      if (list3.isEmpty()) {
         return false;
      }

      list3.remove(0);
      String[] strings = string.split("\\.");
      int value = (strings.length - 1) * 2;
      String string2 = strings.length == 1 ? null : string.substring(0, string.lastIndexOf(46));
      int value2 = findKeyLine(list2, string);

      while (value2 > 0 && isCommentAtIndent((String)list2.get(value2 - 1), value)) {
         value2--;
      }

      boolean flag = value2 > 0 && ((String)list2.get(value2 - 1)).trim().isEmpty();
      int value3 = findInsertIndex(list, list2, string, value, string2);
      if (value3 != -1) {
         boolean flag2 = value3 > 0 && ((String)list.get(value3 - 1)).trim().isEmpty();
         ArrayList arrayList = new ArrayList();
         if (flag && !flag2) {
            arrayList.add("");
         }

         arrayList.addAll(list3);
         if (hasBlankLineBefore(list2, string, value)) {
            arrayList.add("");
         }

         if (!flag && flag2) {
            list.remove(value3 - 1);
            value3--;
            if (!hasBlankLineBefore(list2, string, value)) {
               arrayList.add("");
            }
         }

         list.addAll(value3, arrayList);
         return true;
      } else {
         int value4 = string2 == null ? list.size() : findBlockEnd(list, string2);
         if (value4 == -1) {
            return false;
         }

         while (value4 > 0 && ((String)list.get(value4 - 1)).trim().isEmpty()) {
            value4--;
         }

         ArrayList arrayList2 = new ArrayList();
         if (flag) {
            arrayList2.add("");
         }

         arrayList2.addAll(list3);
         list.addAll(value4, arrayList2);
         return true;
      }
   }

   private static boolean isCommentAtIndent(String string, int value) {
      return string.stripLeading().startsWith("#") && string.length() - string.stripLeading().length() >= value;
   }

   private static int findInsertIndex(List list, List list2, String string, int value, @Nullable String string2) {
      int value2 = findKeyLine(list2, string);

      for (int value3 = value2 + 1; value3 < list2.size(); value3++) {
         String string3 = (String)list2.get(value3);
         if (!string3.trim().isEmpty() && !string3.stripLeading().startsWith("#")) {
            int value4 = string3.length() - string3.stripLeading().length();
            if (value4 < value) {
               return -1;
            }

            if (value4 <= value && !string3.stripLeading().startsWith("-")) {
               String string4 = string3.trim().substring(0, string3.trim().indexOf(58));
               int value5 = findKeyLine(list, string2 == null ? string4 : string2 + "." + string4);
               if (value5 != -1) {
                  while (value5 > 0 && isCommentAtIndent((String)list.get(value5 - 1), value)) {
                     value5--;
                  }

                  return value5;
               }
            }
         }
      }

      return -1;
   }

   private static boolean hasBlankLineBefore(List list, String string, int value) {
      int value2;
      for (value2 = findKeyLine(list, string) + 1; value2 < list.size(); value2++) {
         String string2 = (String)list.get(value2);
         if (!string2.trim().isEmpty() && string2.length() - string2.stripLeading().length() <= value) {
            break;
         }
      }

      while (value2 > 0 && isCommentAtIndent((String)list.get(value2 - 1), value)) {
         value2--;
      }

      return value2 > 0 && ((String)list.get(value2 - 1)).trim().isEmpty();
   }

   private static int findBlockEnd(List list, String string) {
      int value = findKeyLine(list, string);
      if (value == -1) {
         return -1;
      }

      int value2 = ((String)list.get(value)).length() - ((String)list.get(value)).stripLeading().length();

      int value3;
      for (value3 = value + 1; value3 < list.size(); value3++) {
         String string2 = (String)list.get(value3);
         if (!string2.trim().isEmpty() && string2.length() - string2.stripLeading().length() <= value2) {
            break;
         }
      }

      return value3;
   }
}
