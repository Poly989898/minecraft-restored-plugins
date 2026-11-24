package io.lumine.mythic.lib.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;

/** Reads a value from a configuration section under any of several accepted key spellings. */
public class YamlUtils {

   public static Object get(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.get(key);
         }
      }
      return null;
   }

   public static String getString(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.getString(key);
         }
      }
      return null;
   }

   public static Integer getInteger(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.getInt(key);
         }
      }
      return null;
   }

   public static int getInt(ConfigurationSection config, String... keys) {
      Integer value = getInteger(config, keys);
      return value == null ? 0 : value;
   }

   public static Boolean getBooleanObj(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.getBoolean(key);
         }
      }
      return null;
   }

   public static boolean getBoolean(ConfigurationSection config, String... keys) {
      Boolean value = getBooleanObj(config, keys);
      return value != null && value;
   }

   public static boolean getBool(ConfigurationSection config, String... keys) {
      return getBoolean(config, keys);
   }

   public static Float getFloatObj(ConfigurationSection config, String... keys) {
      Double value = getDoubleObj(config, keys);
      return value == null ? null : value.floatValue();
   }

   public static float getFloat(ConfigurationSection config, String... keys) {
      Float value = getFloatObj(config, keys);
      return value == null ? 0.0F : value;
   }

   public static Double getDoubleObj(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.getDouble(key);
         }
      }
      return null;
   }

   public static double getDouble(ConfigurationSection config, String... keys) {
      Double value = getDoubleObj(config, keys);
      return value == null ? 0.0 : value;
   }

   public static List<String> getStringList(ConfigurationSection config, String... keys) {
      for (String key : keys) {
         if (config.contains(key)) {
            return config.getStringList(key);
         }
      }
      return new ArrayList<>();
   }

   public static <T> boolean containsOneKey(ConfigurationSection config, Iterable<T> keys) {
      for (T key : keys) {
         if (config.contains(String.valueOf(key))) {
            return true;
         }
      }
      return false;
   }

   public static <T extends Enum<?>> boolean containsOneKey(ConfigurationSection config, T[] values, Function<T, String> keyMapper) {
      for (T value : values) {
         if (config.contains(keyMapper.apply(value))) {
            return true;
         }
      }
      return false;
   }
}
