package io.lumine.mythic.api.config;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderTime;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.serialize.Percent;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface MythicConfig {
   static MythicConfig of(File file) {
      return MythicProvider.get().getConfiguration().createConfig(file);
   }

   File getFile();

   void setKey(String str);

   String getKey();

   boolean isSet(String str);

   String determineWhichKeyToUse(String str, String... strs);

   void set(String str, Object obj);

   void setSave(String str, Object obj);

   void load();

   void save();

   MythicConfig getNestedConfig(String str);

   Map<String, MythicConfig> getNestedConfigs(String str);

   boolean isMapList(String str);

   boolean isDouble(String str);

   boolean isLong(String str);

   boolean isString(String str);

   String getString(String str);

   String getString(String[] strs);

   String getString(String str, String str2);

   String getString(String[] strs, String str, String... strs2);

   String getNamespacedString(String str);

   String getNamespacedString(String[] strs);

   String getNamespacedString(String str, String str2);

   String getNamespacedString(String[] strs, String str, String... strs2);

   <T extends Enum> T getEnum(String str, Class<T> clazz, T t);

   <T extends Enum<T>> T getEnum(String[] strs, Class<T> clazz, T t);

   Chroma getColor(String str);

   Chroma getColor(String[] strs);

   Chroma getColor(String str, Chroma chroma);

   Chroma getColor(String[] strs, Chroma chroma);

   PlaceholderString getPlaceholderString(String str);

   PlaceholderString getPlaceholderString(String str, String str2);

   PlaceholderString getPlaceholderString(String[] strs);

   PlaceholderString getPlaceholderString(String[] strs, String str);

   String getColorString(String str);

   String getColorString(String str, String str2);

   boolean getBoolean(String str);

   boolean getBoolean(String str, boolean flag);

   boolean getBoolean(String[] strs, boolean flag);

   Boolean getBooleanNullable(String str);

   Boolean getBooleanNullable(String[] strs);

   PlaceholderBoolean getPlaceholderBoolean(String str);

   PlaceholderBoolean getPlaceholderBoolean(String[] strs);

   PlaceholderBoolean getPlaceholderBoolean(String str, Boolean flag);

   PlaceholderBoolean getPlaceholderBoolean(String[] strs, Boolean flag);

   int getInteger(String str);

   int getInteger(String str, int value);

   int getInteger(String[] strs, int value);

   long getLong(String str);

   long getLong(String str, long value);

   long getLong(String[] strs, long value);

   @Deprecated
   int getInt(String str);

   @Deprecated
   int getInt(String str, int value);

   PlaceholderAngle getPlaceholderAngle(String str, AngleUnit angleUnit);

   PlaceholderAngle getPlaceholderAngle(String str, AngleUnit angleUnit, String str2);

   PlaceholderAngle getPlaceholderAngle(String[] strs, AngleUnit angleUnit, String str);

   PlaceholderAngle getPlaceholderAngle(String[] strs, AngleUnit angleUnit, String str, String... strs2);

   double getDouble(String str);

   double getDouble(String str, double value);

   double getDouble(String[] strs, double value);

   float getFloat(String str);

   float getFloat(String str, float value);

   float getFloat(String[] strs, float value);

   Percent getPercent(String str);

   Percent getPercent(String str, Percent percent);

   Percent getPercent(String[] strs);

   Percent getPercent(String[] strs, Percent percent);

   List<String> getStringList(String str);

   List<String> getStringList(String str, List<String> strs);

   List<String> getStringList(String[] strs);

   List<String> getStringList(String[] strs, List<String> strs2);

   List<String> getColorStringList(String str);

   List<String> getColorStringList(String[] strs);

   List<PlaceholderString> getPlaceholderStringList(String str);

   List<PlaceholderString> getPlaceholderStringList(String[] strs);

   List<PlaceholderString> getPlaceholderStringList(String str, List<String> strs);

   List<PlaceholderString> getPlaceholderStringList(String[] strs, List<String> strs2);

   List<PlaceholderString> getPlaceholderStringListAlt(String str, List<PlaceholderString> placeholderStrings);

   List<PlaceholderString> getPlaceholderStringListAlt(String[] strs, List<PlaceholderString> placeholderStrings);

   PlaceholderColorImpl getPlaceholderColor(String str, String str2);

   PlaceholderVector getPlaceholderVector(String str, String str2);

   PlaceholderVector getPlaceholderVector(String[] strs, String str);

   PlaceholderVector getPlaceholderVector(String[] strs, String str, String... strs2);

   PlaceholderTime getPlaceholderTime(String str, String str2);

   PlaceholderTime getPlaceholderTime(String[] strs, String str);

   PlaceholderTime getPlaceholderTime(String[] strs, String str, String... strs2);

   PlaceholderFloat getPlaceholderFloat(String str, String str2);

   PlaceholderFloat getPlaceholderFloat(String[] strs, String str, String... strs2);

   PlaceholderFloat getPlaceholderFloat(String str, float value);

   PlaceholderFloat getPlaceholderFloat(String[] strs, float value, String... strs2);

   List<Map<?, ?>> getMapList(String str);

   List<Map<?, ?>> getMapList(String[] strs);

   List<MythicConfig> getConfigurationList(String str);

   List<MythicConfig> getConfigurationList(String[] strs);

   List<?> getList(String str);

   List<?> getList(String[] strs);

   List<Byte> getByteList(String str);

   List<Byte> getByteList(String[] strs);

   ItemStack getItemStack(String str, String str2);

   ItemStack getItemStack(String[] strs, String str);

   boolean isConfigurationSection(String str);

   Set<String> getKeys();

   Set<String> getKeys(String str);

   boolean contains(String str);

   boolean isBoolean(String str);

   boolean isInt(String str);

   boolean isList(String str);

   PlaceholderInt getPlaceholderInt(String str, String str2);

   PlaceholderInt getPlaceholderInt(String[] strs, String str);

   PlaceholderInt getPlaceholderInt(String[] strs, String str, String... strs2);

   PlaceholderLong getPlaceholderLong(String str, String str2);

   PlaceholderLong getPlaceholderLong(String[] strs, String str);

   PlaceholderDouble getPlaceholderDouble(String str, String str2);

   PlaceholderDouble getPlaceholderDouble(String[] strs, String str, String... strs2);

   PlaceholderDuration getPlaceholderDuration(String str, DurationUnit durationUnit, String str2);

   PlaceholderDuration getPlaceholderDuration(String[] strs, DurationUnit durationUnit, String str);

   PlaceholderDuration getPlaceholderDuration(String[] strs, DurationUnit durationUnit, String str, String... strs2);

   void merge(MythicConfig mythicConfig, List<String> strs);

   ConfigurationSection getRootConfiguration();

   void unset(String str);

   void unsetSave(String str);

   void deleteNodeAndSave();

   String getFileName();

   static String normalizeNamespacedKey(String value) {
      if (value != null && !value.isEmpty()) {
         value = value.toLowerCase(Locale.ROOT);
         if (!value.contains(":")) {
            value = "minecraft:" + value;
         }

         return value;
      } else {
         return value;
      }
   }
}
