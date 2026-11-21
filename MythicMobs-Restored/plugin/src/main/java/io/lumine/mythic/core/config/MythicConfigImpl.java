package io.lumine.mythic.core.config;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
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
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.serialize.Percent;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MythicConfigImpl implements GenericConfig, Cloneable, MythicConfig {
   private String configName;
   @Getter
   private File file;
   private ConfigurationSection fc;
   private final MythicConfigImpl.PathLookupCache lookupCache;

   public MythicConfigImpl(ConfigurationSection fc) {
      this(null, null, fc);
   }

   public MythicConfigImpl(String name, ConfigurationSection fc) {
      this(name, null, fc);
   }

   public MythicConfigImpl(MythicConfig config, String name) {
      this(
         name,
         config.getFile(),
         config.getRootConfiguration(),
         config instanceof MythicConfigImpl impl ? impl.lookupCache : new MythicConfigImpl.PathLookupCache()
      );
   }

   public MythicConfigImpl(File file) {
      this("", file);
   }

   public MythicConfigImpl(String name, File file) {
      this(name, file, null);
      this.fc = new YamlConfiguration();
      if (file.exists()) {
         try {
            PlaceholderExecutor executor = VolatileCodeHandler.getPlugin().getPlaceholderManager();
            if (executor != null && executor.getStaticPlaceholderRegistry().isCompiled()) {
               String content = Files.readString(file.toPath()) + "\n";
               ((FileConfiguration)this.fc).loadFromString(executor.getStaticPlaceholderRegistry().replaceText(content));
            } else {
               ((FileConfiguration)this.fc).load(file);
            }
         } catch (Error | Exception e) {
            Log.error("An error occurred while loading MythicConfig file " + file.getAbsolutePath() + ":");
            e.printStackTrace();
         }
      }

      if (!this.fc.isConfigurationSection(this.configName)) {
         this.fc.createSection(this.configName);
      }
   }

   public MythicConfigImpl(String name, File file, ConfigurationSection fc) {
      this(name, file, fc, new MythicConfigImpl.PathLookupCache());
   }

   private MythicConfigImpl(String name, File file, ConfigurationSection fc, MythicConfigImpl.PathLookupCache lookupCache) {
      this.configName = name;
      this.file = file;
      this.fc = fc;
      this.lookupCache = lookupCache;
   }

   @Override
   public void setKey(String key) {
      this.configName = key;
   }

   @Override
   public String getKey() {
      return this.configName;
   }

   @Override
   public String getFileName() {
      if (this.file != null) {
         return this.file.getAbsolutePath();
      } else {
         return this.configName != null ? "Nested Configuration '" + this.configName + "'" : "API";
      }
   }

   @Override
   public ConfigurationSection getRootConfiguration() {
      return this.fc;
   }

   public String getNode(String field) {
      if (this.configName == null || this.configName.length() == 0) {
         return "";
      } else {
         return field != null && !field.isEmpty() ? this.configName + "." : this.configName;
      }
   }

   private static String normalizeKey(String key) {
      return key.toLowerCase(Locale.ROOT);
   }

   private String qualifyPath(String field) {
      if (this.configName == null || this.configName.isEmpty()) {
         return field == null ? "" : field;
      } else {
         return field != null && !field.isEmpty() ? this.configName + "." + field : this.configName;
      }
   }

   private boolean pathExistsExact(String path) {
      return path != null && !path.isEmpty() ? this.fc.isSet(path) || this.fc.contains(path) || this.fc.isConfigurationSection(path) : true;
   }

   private MythicConfigImpl.SectionKeyIndex getSectionKeyIndex(String sectionPath) {
      String normalizedPath = sectionPath == null ? "" : sectionPath;
      MythicConfigImpl.SectionKeyIndex cachedIndex = this.lookupCache.getSectionKeyIndex(normalizedPath);
      if (cachedIndex != null) {
         return cachedIndex;
      } else {
         ConfigurationSection section;
         if (normalizedPath.isEmpty()) {
            section = this.fc;
         } else {
            section = this.fc.getConfigurationSection(normalizedPath);
         }

         MythicConfigImpl.SectionKeyIndex sectionKeyIndex = MythicConfigImpl.SectionKeyIndex.from(section);
         this.lookupCache.cacheSectionKeyIndex(normalizedPath, sectionKeyIndex);
         return sectionKeyIndex;
      }
   }

   private void invalidateLookupCache() {
      this.lookupCache.clear();
   }

   private void invalidateWrittenPath(String writePath) {
      this.lookupCache.clearResolvedAndMissing();
      if (writePath != null && !writePath.isEmpty()) {
         if (this.pathExistsExact(writePath)) {
            this.lookupCache.removeSectionKeyIndexSubtree(writePath);
         }

         String section = getParentPath(writePath);

         while (true) {
            this.lookupCache.removeSectionKeyIndex(section);
            if (section.isEmpty() || this.pathExistsExact(section)) {
               return;
            }

            section = getParentPath(section);
         }
      } else {
         this.lookupCache.removeSectionKeyIndexSubtree("");
      }
   }

   private String resolveQualifiedPath(String path) {
      if (path != null && !path.isEmpty()) {
         String cachedPath = this.lookupCache.getResolvedPath(path);
         if (cachedPath != null) {
            return cachedPath;
         } else if (this.lookupCache.isMissingPath(path)) {
            return null;
         } else if (this.pathExistsExact(path)) {
            this.lookupCache.cacheResolvedPath(path, path);
            return path;
         } else {
            ConfigurationSection currentSection = this.fc;
            StringBuilder resolvedPath = new StringBuilder();
            int segmentStart = 0;

            while (segmentStart < path.length()) {
               int nextSeparator = path.indexOf(46, segmentStart);
               String segment = nextSeparator < 0 ? path.substring(segmentStart) : path.substring(segmentStart, nextSeparator);
               String actualKey = this.getSectionKeyIndex(resolvedPath.toString()).find(segment);
               if (actualKey == null) {
                  this.lookupCache.cacheMissingPath(path);
                  return null;
               }

               if (resolvedPath.length() > 0) {
                  resolvedPath.append('.');
               }

               resolvedPath.append(actualKey);
               if (nextSeparator < 0) {
                  String result = resolvedPath.toString();
                  this.lookupCache.cacheResolvedPath(path, result);
                  return result;
               }

               currentSection = this.fc.getConfigurationSection(resolvedPath.toString());
               if (currentSection == null) {
                  this.lookupCache.cacheMissingPath(path);
                  return null;
               }

               segmentStart = nextSeparator + 1;
            }

            this.lookupCache.cacheMissingPath(path);
            return null;
         }
      } else {
         return "";
      }
   }

   private String resolvePath(String field) {
      return this.resolveQualifiedPath(this.qualifyPath(field));
   }

   private static String getParentPath(String path) {
      if (path != null && !path.isEmpty()) {
         int index = path.lastIndexOf(46);
         return index < 0 ? "" : path.substring(0, index);
      } else {
         return "";
      }
   }

   private static String getLeafPath(String path) {
      if (path != null && !path.isEmpty()) {
         int index = path.lastIndexOf(46);
         return index < 0 ? path : path.substring(index + 1);
      } else {
         return "";
      }
   }

   private static String buildChildPath(String parentPath, String childKey) {
      if (parentPath == null || parentPath.isEmpty()) {
         return childKey;
      } else {
         return childKey != null && !childKey.isEmpty() ? parentPath + "." + childKey : parentPath;
      }
   }

   private String getWritePath(String field) {
      String requestedPath = this.qualifyPath(field);
      if (requestedPath == null || requestedPath.isEmpty()) {
         return requestedPath == null ? "" : requestedPath;
      } else if (this.pathExistsExact(requestedPath)) {
         return requestedPath;
      } else {
         String resolvedPath = this.resolveQualifiedPath(requestedPath);
         if (resolvedPath != null) {
            return resolvedPath;
         } else {
            String parentPath = getParentPath(requestedPath);
            String parentResolvedPath = this.resolveQualifiedPath(parentPath);
            return parentResolvedPath != null ? buildChildPath(parentResolvedPath, getLeafPath(requestedPath)) : requestedPath;
         }
      }
   }

   private ConfigurationSection getResolvedSection(String field) {
      String path = this.resolvePath(field);
      if (path == null) {
         return null;
      } else {
         return path.isEmpty() ? this.fc : this.fc.getConfigurationSection(path);
      }
   }

   private String findFirstSetKey(String[] fields) {
      for (String field : fields) {
         String requestedPath = this.qualifyPath(field);
         if (this.pathExistsExact(requestedPath)) {
            return requestedPath;
         }

         String resolvedPath = this.resolveQualifiedPath(requestedPath);
         if (resolvedPath != null) {
            return resolvedPath;
         }
      }

      return null;
   }

   private String findFirstSetKeyIgnoreCase(String[] fields) {
      return this.findFirstSetKey(fields);
   }

   @Override
   public void deleteNodeAndSave() {
      this.fc.set(this.getWritePath(null), null);
      this.invalidateLookupCache();
      this.save();
   }

   @Override
   public boolean isSet(String key) {
      return this.resolvePath(key) != null;
   }

   @Override
   public String determineWhichKeyToUse(String def, String... keys) {
      for (String key : keys) {
         if (this.isSet(key)) {
            return key;
         }
      }

      return def;
   }

   @Override
   public void set(String key, Object value) {
      String path = this.getWritePath(key);
      this.invalidateWrittenPath(path);
      this.fc.set(path, value);
   }

   @Override
   public void setSave(String field, Object value) {
      String path = this.getWritePath(field);
      this.invalidateWrittenPath(path);
      this.fc.set(path, value);
      this.save();
   }

   @Override
   public void unset(String field) {
      String path = this.getWritePath(field);
      this.invalidateWrittenPath(path);
      this.fc.set(path, null);
   }

   @Override
   public void unsetSave(String key) {
      this.unset(key);
      this.save();
   }

   @Override
   public void load() {
      this.fc = YamlConfiguration.loadConfiguration(this.file);
      this.invalidateLookupCache();
   }

   @Override
   public void save() {
      if (this.file == null) {
         MythicLogger.errorGenericConfig("Save operation not supported on null file: " + this.getFileName());
      } else {
         if (this.fc instanceof FileConfiguration fileConfiguration) {
            try {
               fileConfiguration.save(this.file);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public MythicConfig getNestedConfig(String key) {
      return new MythicConfigImpl(this.getWritePath(key), this.file, this.fc, this.lookupCache);
   }

   @Override
   public Map<String, MythicConfig> getNestedConfigs(String field) {
      Map<String, MythicConfig> map = new HashMap<>();
      String sectionPath = this.resolvePath(field);
      if (sectionPath == null) {
         return map;
      } else {
         for (String k : this.getKeys(field)) {
            map.put(k, new MythicConfigImpl(buildChildPath(sectionPath, k), this.file, this.fc, this.lookupCache));
         }

         return map;
      }
   }

   @Override
   public boolean contains(String field) {
      return this.resolvePath(field) != null;
   }

   @Override
   public boolean isBoolean(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isBoolean(path);
   }

   @Override
   public boolean isInt(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isInt(path);
   }

   @Override
   public boolean isList(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isList(path);
   }

   @Override
   public boolean isMapList(String field) {
      String path = this.resolvePath(field);
      if (path != null && this.fc.isList(path)) {
         try {
            List<Map<?, ?>> mapList = this.fc.getMapList(path);
            return !mapList.isEmpty();
         } catch (Throwable e) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isDouble(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isDouble(path);
   }

   @Override
   public boolean isLong(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isLong(path);
   }

   @Override
   public boolean isString(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.isString(path);
   }

   @Override
   public String getString(String field) {
      String path = this.resolvePath(field);
      return path == null ? null : this.fc.getString(path);
   }

   @Override
   public String getString(String[] key) {
      return this.getString(key, null);
   }

   @Override
   public String getString(String field, String def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getString(path, def);
   }

   @Override
   public String getString(String[] keysToCheck, String finalDefaultValue, String... defaultValues) {
      for (String key : keysToCheck) {
         if (!this.isConfigurationSection(key)) {
            String get = this.getString(key, null);
            if (get != null) {
               return get;
            }
         }
      }

      for (String value : defaultValues) {
         if (value != null) {
            return value;
         }
      }

      return finalDefaultValue;
   }

   @Override
   public String getNamespacedString(String field) {
      return this.wrapWithNamespace(this.getString(field));
   }

   @Override
   public String getNamespacedString(String[] key) {
      return this.getNamespacedString(key, null);
   }

   @Override
   public String getNamespacedString(String field, String def) {
      return this.wrapWithNamespace(this.getString(field, def));
   }

   @Override
   public String getNamespacedString(String[] keysToCheck, String finalDefaultValue, String... defaultValues) {
      for (String key : keysToCheck) {
         if (!this.isConfigurationSection(key)) {
            String get = this.getString(key, null);
            if (get != null) {
               return this.wrapWithNamespace(get);
            }
         }
      }

      for (String value : defaultValues) {
         if (value != null) {
            return this.wrapWithNamespace(value);
         }
      }

      return this.wrapWithNamespace(finalDefaultValue);
   }

   private String wrapWithNamespace(String string) {
      if (string == null) {
         return null;
      } else {
         return !string.contains(":") ? "minecraft:" + string.toLowerCase() : string.toLowerCase();
      }
   }

   @Override
   public Chroma getColor(String field) {
      return this.getColor(field, Chroma.of(255, 255, 255));
   }

   @Override
   public Chroma getColor(String[] field) {
      return this.getColor(field, Chroma.of(255, 255, 255));
   }

   @Override
   public Chroma getColor(String field, Chroma def) {
      String data = this.getString(field, null);
      return data == null ? def : Chroma.of(data);
   }

   @Override
   public Chroma getColor(String[] field, Chroma def) {
      String data = this.getString(field, null);
      return data == null ? def : Chroma.of(data);
   }

   @Override
   public PlaceholderString getPlaceholderString(String field) {
      String s = this.getString(field);
      return s == null ? null : PlaceholderString.of(s);
   }

   @Override
   public PlaceholderString getPlaceholderString(String field, String def) {
      String s = this.getString(field, def);
      return s == null ? null : PlaceholderString.of(s);
   }

   @Override
   public PlaceholderString getPlaceholderString(String[] field) {
      String value = this.getString(field, null);
      return value == null ? null : PlaceholderString.of(value);
   }

   @Override
   public PlaceholderString getPlaceholderString(String[] field, String def) {
      String value = this.getString(field, def);
      return value == null ? null : PlaceholderString.of(value);
   }

   @Override
   public String getColorString(String field) {
      String s = this.getString(field);
      if (s != null) {
         s = Text.colorizeLegacy(s);
      }

      return s;
   }

   @Override
   public String getColorString(String field, String def) {
      String s = this.getString(field, def);
      if (s != null) {
         s = Text.colorizeLegacy(s);
      }

      return s;
   }

   @Override
   public boolean getBoolean(String field) {
      String path = this.resolvePath(field);
      return path != null && this.fc.getBoolean(path);
   }

   @Override
   public boolean getBoolean(String field, boolean def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getBoolean(path, def);
   }

   @Override
   public boolean getBoolean(String[] field, boolean def) {
      String key = this.findFirstSetKey(field);
      return key == null ? def : this.fc.getBoolean(key);
   }

   @Override
   public Boolean getBooleanNullable(String field) {
      String path = this.resolvePath(field);
      return path == null ? null : this.fc.getBoolean(path);
   }

   @Override
   public Boolean getBooleanNullable(String[] field) {
      String path = this.findFirstSetKey(field);
      return path == null ? null : this.fc.getBoolean(path);
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String field) {
      String value = this.getString(field, null);
      return value == null ? null : PlaceholderBoolean.of(value);
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String[] field) {
      String value = this.getString(field, null);
      return value == null ? null : PlaceholderBoolean.of(value);
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String field, Boolean def) {
      String value = this.getString(field, def == null ? null : def.toString());
      return value == null ? null : PlaceholderBoolean.of(value);
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String[] field, Boolean def) {
      String value = this.getString(field, def == null ? null : def.toString());
      return value == null ? null : PlaceholderBoolean.of(value);
   }

   @Override
   public int getInteger(String field) {
      String path = this.resolvePath(field);
      return path == null ? 0 : this.fc.getInt(path);
   }

   @Override
   public int getInteger(String field, int def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getInt(path, def);
   }

   @Override
   public int getInteger(String[] keys, int def) {
      String path = this.findFirstSetKey(keys);
      return path != null && this.fc.isInt(path) ? this.fc.getInt(path) : def;
   }

   @Override
   public long getLong(String field) {
      String path = this.resolvePath(field);
      return path == null ? 0L : this.fc.getLong(path);
   }

   @Override
   public long getLong(String field, long def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getLong(path, def);
   }

   @Override
   public long getLong(String[] field, long def) {
      String key = this.findFirstSetKeyIgnoreCase(field);
      return key == null ? def : this.fc.getLong(key);
   }

   @Deprecated
   @Override
   public int getInt(String field) {
      String path = this.resolvePath(field);
      return path == null ? 0 : this.fc.getInt(path);
   }

   @Deprecated
   @Override
   public int getInt(String field, int def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getInt(path, def);
   }

   @Override
   public float getFloat(String field) {
      return (float)this.getDouble(field);
   }

   @Override
   public float getFloat(String field, float def) {
      return (float)this.getDouble(field, def);
   }

   @Override
   public float getFloat(String[] field, float def) {
      return (float)this.getDouble(field, def);
   }

   @Override
   public Percent getPercent(String field) {
      String percent = this.getString(field);
      return percent == null ? Percent.of(1.0) : Percent.of(percent);
   }

   @Override
   public Percent getPercent(String field, Percent def) {
      String percent = this.getString(field);
      return percent == null ? def : Percent.of(percent);
   }

   @Override
   public Percent getPercent(String[] field) {
      String percent = this.getString(field, null);
      return percent == null ? Percent.of(1.0) : Percent.of(percent);
   }

   @Override
   public Percent getPercent(String[] field, Percent def) {
      String percent = this.getString(field, null);
      return percent == null ? def : Percent.of(percent);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String field, AngleUnit defaultUnit) {
      String percent = this.getString(field);
      return percent == null ? PlaceholderAngle.of("0", defaultUnit) : PlaceholderAngle.of(percent, defaultUnit);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String field, AngleUnit defaultUnit, String def) {
      String percent = this.getString(field);
      return percent == null ? PlaceholderAngle.of(def, defaultUnit) : PlaceholderAngle.of(percent, defaultUnit);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String[] field, AngleUnit defaultUnit, String def) {
      String percent = this.getString(field, def);
      return percent == null ? PlaceholderAngle.of(def, defaultUnit) : PlaceholderAngle.of(percent, defaultUnit);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String[] field, AngleUnit defaultUnit, String def, String... args) {
      String percent = this.getString(field, def, args);
      return percent == null ? PlaceholderAngle.of(def, defaultUnit) : PlaceholderAngle.of(percent, defaultUnit);
   }

   @Override
   public double getDouble(String field) {
      String path = this.resolvePath(field);
      return path == null ? 0.0 : this.fc.getDouble(path);
   }

   @Override
   public double getDouble(String field, double def) {
      String path = this.resolvePath(field);
      return path == null ? def : this.fc.getDouble(path, def);
   }

   @Override
   public double getDouble(String[] field, double def) {
      String key = this.findFirstSetKey(field);
      return key == null ? def : this.fc.getDouble(key);
   }

   @Override
   public List<String> getStringList(String field) {
      String path = this.resolvePath(field);
      return path == null ? new ArrayList<>() : new ArrayList<>(this.fc.getStringList(path));
   }

   @Override
   public List<String> getStringList(String field, List<String> def) {
      String path = this.resolvePath(field);
      return (List<String>)(path == null ? def : new ArrayList<>(this.fc.getStringList(path)));
   }

   @Override
   public List<String> getStringList(String[] field) {
      return this.getStringList(field, new ArrayList<>());
   }

   @Override
   public List<String> getStringList(String[] field, List<String> def) {
      String key = this.findFirstSetKey(field);
      return (List<String>)(key == null ? def : new ArrayList<>(this.fc.getStringList(key)));
   }

   @Override
   public List<String> getColorStringList(String field) {
      List<String> list = this.getStringList(field);
      List<String> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(ChatColor.translateAlternateColorCodes('&', str));
         }
      }

      return parsed;
   }

   @Override
   public List<String> getColorStringList(String[] field) {
      String key = this.findFirstSetKey(field);
      List<String> parsed = new ArrayList<>();
      if (key == null) {
         return parsed;
      } else {
         List<String> list = this.fc.getStringList(key);
         if (list != null) {
            for (String str : list) {
               parsed.add(ChatColor.translateAlternateColorCodes('&', str));
            }
         }

         return parsed;
      }
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringList(String field) {
      List<String> list = this.getStringList(field);
      List<PlaceholderString> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }
      }

      return parsed;
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringList(String[] field) {
      return this.getPlaceholderStringList(field, Collections.emptyList());
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringList(String field, List<String> def) {
      List<String> list = this.getStringList(field, def);
      List<PlaceholderString> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }
      }

      return parsed;
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringList(String[] field, List<String> def) {
      List<String> list = this.getStringList(field, def);
      List<PlaceholderString> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }
      }

      return parsed;
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringListAlt(String field, List<PlaceholderString> def) {
      List<String> list = this.getStringList(field);
      if (list != null && !list.isEmpty()) {
         List<PlaceholderString> parsed = new ArrayList<>();
         if (list != null) {
            for (String str : list) {
               parsed.add(PlaceholderString.of(str));
            }
         }

         return parsed;
      } else {
         return def;
      }
   }

   @Override
   public List<PlaceholderString> getPlaceholderStringListAlt(String[] field, List<PlaceholderString> def) {
      List<String> list = this.getStringList(field);
      if (list != null && !list.isEmpty()) {
         List<PlaceholderString> parsed = new ArrayList<>();

         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }

         return parsed;
      } else {
         return def;
      }
   }

   @Override
   public List<Map<?, ?>> getMapList(String field) {
      String path = this.resolvePath(field);
      return path == null ? Collections.emptyList() : this.fc.getMapList(path);
   }

   @Override
   public List<Map<?, ?>> getMapList(String[] field) {
      String key = this.findFirstSetKey(field);
      return key == null ? Collections.emptyList() : this.fc.getMapList(key);
   }

   @Override
   public List<MythicConfig> getConfigurationList(String field) {
      List<MythicConfig> values = Lists.newArrayList();
      String path = this.resolvePath(field);
      if (path == null) {
         return values;
      } else {
         try {
            for (Map<?, ?> configMap : this.fc.getMapList(path)) {
               MythicConfig configList = mapToConfig((Map<String, Object>)configMap);
               values.add(configList);
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }

         return values;
      }
   }

   @Override
   public List<MythicConfig> getConfigurationList(String[] field) {
      String key = this.findFirstSetKey(field);
      List<MythicConfig> values = Lists.newArrayList();
      if (key == null) {
         return values;
      } else {
         try {
            for (Map<?, ?> configMap : this.fc.getMapList(key)) {
               MythicConfig configList = mapToConfig((Map<String, Object>)configMap);
               values.add(configList);
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }

         return values;
      }
   }

   @Override
   public List<?> getList(String field) {
      String path = this.resolvePath(field);
      return path == null ? null : this.fc.getList(path);
   }

   @Override
   public List<?> getList(String[] field) {
      String key = this.findFirstSetKeyIgnoreCase(field);
      return key == null ? null : this.fc.getList(key);
   }

   @Override
   public List<Byte> getByteList(String field) {
      String path = this.resolvePath(field);
      return path == null ? null : this.fc.getByteList(path);
   }

   @Override
   public List<Byte> getByteList(String[] field) {
      String key = this.findFirstSetKeyIgnoreCase(field);
      return key == null ? null : this.fc.getByteList(key);
   }

   @Override
   public ItemStack getItemStack(String field, String def) {
      String path = this.resolvePath(field);
      if (path != null) {
         return this.fc.getItemStack(path);
      } else {
         try {
            return new ItemStack(Material.valueOf(def));
         } catch (Exception e) {
            return null;
         }
      }
   }

   @Override
   public ItemStack getItemStack(String[] field, String def) {
      String key = this.findFirstSetKeyIgnoreCase(field);
      if (key != null) {
         return this.fc.getItemStack(key);
      } else {
         try {
            return new ItemStack(Material.valueOf(def));
         } catch (Exception e) {
            return null;
         }
      }
   }

   @Override
   public boolean isConfigurationSection(String field) {
      return this.getResolvedSection(field) != null;
   }

   @Override
   public Set<String> getKeys() {
      ConfigurationSection section = this.getResolvedSection("");
      return section == null ? Collections.emptySet() : section.getKeys(false);
   }

   @Override
   public Set<String> getKeys(String field) {
      ConfigurationSection section = this.getResolvedSection(field);
      return section == null ? Collections.emptySet() : section.getKeys(false);
   }

   @Override
   public PlaceholderInt getPlaceholderInt(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInt(String[] key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInt(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String[] key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderFloat.of(s);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderFloat.of(s);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String key, float def) {
      return this.getPlaceholderFloat(key, String.valueOf(def));
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String[] key, float def, String... args) {
      return this.getPlaceholderFloat(key, String.valueOf(def), args);
   }

   @Override
   public PlaceholderColorImpl getPlaceholderColor(String key, String def) {
      PlaceholderString s = this.getPlaceholderString(key, def);
      return s == null ? null : new PlaceholderColorImpl(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String[] key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderTime getPlaceholderTime(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderTime.of(s);
   }

   @Override
   public PlaceholderTime getPlaceholderTime(String[] key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderTime.of(s);
   }

   @Override
   public PlaceholderTime getPlaceholderTime(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderTime.of(s);
   }

   @Override
   public PlaceholderDuration getPlaceholderDuration(String key, DurationUnit defaultUnit, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDuration.of(s, defaultUnit);
   }

   @Override
   public PlaceholderDuration getPlaceholderDuration(String[] key, DurationUnit defaultUnit, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDuration.of(s, defaultUnit);
   }

   @Override
   public PlaceholderDuration getPlaceholderDuration(String[] key, DurationUnit defaultUnit, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderDuration.of(s, defaultUnit);
   }

   @Override
   public <T extends Enum> T getEnum(String field, Class<T> clazz, T def) {
      try {
         String in = this.getString(field);
         if (in == null) {
            return def;
         } else {
            Enum value = Enum.valueOf(clazz, in.toUpperCase());
            return (T)(value == null ? def : value);
         }
      } catch (Error | Exception e) {
         return def;
      }
   }

   @Override
   public <T extends Enum<T>> T getEnum(String[] field, Class<T> clazz, T def) {
      try {
         String in = this.getString(field, null);
         if (in == null) {
            return def;
         } else {
            T value = Enum.valueOf(clazz, in.toUpperCase(Locale.ROOT));
            return value == null ? def : value;
         }
      } catch (Error | Exception e) {
         return def;
      }
   }

   @Override
   public void merge(MythicConfig tmplConfig, List<String> keysToIgnore) {
      ConfigurationSection tmplFile = tmplConfig.getRootConfiguration();
      MythicConfigImpl tmplImpl = tmplConfig instanceof MythicConfigImpl impl ? impl : null;

      for (String key : tmplConfig.getKeys("")) {
         if (!keysToIgnore.contains(key)) {
            String templatePath = tmplImpl != null ? tmplImpl.getWritePath(key) : buildChildPath(tmplConfig.getKey(), key);
            Object templateValue = tmplFile.get(templatePath);
            if (!this.getStringList(key).isEmpty()) {
               List<String> templateStringList = tmplConfig.getStringList(key);
               List<String> currentStringList = this.getStringList(key);
               templateStringList.addAll(currentStringList);
               this.set(key, templateStringList);
            } else if (templateValue instanceof ConfigurationSection templateSection) {
               if (!this.isSet(key) || this.isConfigurationSection(key)) {
                  this.mergeSection(key, templateSection);
               }
            } else if (!this.isSet(key)) {
               this.set(key, templateValue);
            }
         }
      }
   }

   private void mergeSection(String key, ConfigurationSection templateSection) {
      for (String node : templateSection.getKeys(false)) {
         String childKey = key + "." + node;
         Object templateValue = templateSection.get(node);
         if (templateValue instanceof ConfigurationSection childSection) {
            if (!this.isSet(childKey) || this.isConfigurationSection(childKey)) {
               this.mergeSection(childKey, childSection);
            }
         } else if (!this.isSet(childKey)) {
            this.set(childKey, templateValue);
         }
      }
   }

   public static MythicConfig mapToConfig(Map<String, Object> flatMap) {
      YamlConfiguration cfg = new YamlConfiguration();
      flatMap.forEach((key, value) -> copyValue(cfg, key, value));
      return new MythicConfigImpl(cfg);
   }

   private static void copyValue(ConfigurationSection section, String path, Object value) {
      if (value instanceof Map) {
         ((Map)value).forEach((subKey, subVal) -> copyValue(section, path + "." + subKey, subVal));
      } else {
         section.set(path, value);
      }
   }

   private static final class PathLookupCache {
      private final Map<String, String> resolvedPaths = new HashMap<>();
      private final Set<String> missingPaths = new HashSet<>();
      private final Map<String, MythicConfigImpl.SectionKeyIndex> sectionKeyIndexes = new HashMap<>();

      private String getResolvedPath(String path) {
         return this.resolvedPaths.get(path);
      }

      private boolean isMissingPath(String path) {
         return this.missingPaths.contains(path);
      }

      private void cacheResolvedPath(String path, String resolvedPath) {
         this.resolvedPaths.put(path, resolvedPath);
         this.missingPaths.remove(path);
      }

      private void cacheMissingPath(String path) {
         this.resolvedPaths.remove(path);
         this.missingPaths.add(path);
      }

      private MythicConfigImpl.SectionKeyIndex getSectionKeyIndex(String sectionPath) {
         return this.sectionKeyIndexes.get(sectionPath);
      }

      private void cacheSectionKeyIndex(String sectionPath, MythicConfigImpl.SectionKeyIndex sectionKeyIndex) {
         this.sectionKeyIndexes.put(sectionPath, sectionKeyIndex);
      }

      private void clear() {
         this.resolvedPaths.clear();
         this.missingPaths.clear();
         this.sectionKeyIndexes.clear();
      }

      private void clearResolvedAndMissing() {
         this.resolvedPaths.clear();
         this.missingPaths.clear();
      }

      private void removeSectionKeyIndex(String sectionPath) {
         this.sectionKeyIndexes.remove(sectionPath);
      }

      private void removeSectionKeyIndexSubtree(String sectionPath) {
         this.sectionKeyIndexes.remove(sectionPath);
         String prefix = sectionPath.isEmpty() ? null : sectionPath + ".";
         if (prefix == null) {
            this.sectionKeyIndexes.clear();
         } else {
            this.sectionKeyIndexes.keySet().removeIf(cached -> cached.startsWith(prefix));
         }
      }
   }

   private static final class SectionKeyIndex {
      private static final MythicConfigImpl.SectionKeyIndex EMPTY = new MythicConfigImpl.SectionKeyIndex(Collections.emptySet(), Collections.emptyMap());
      private final Set<String> exactKeys;
      private final Map<String, String> foldedKeys;

      private SectionKeyIndex(Set<String> exactKeys, Map<String, String> foldedKeys) {
         this.exactKeys = exactKeys;
         this.foldedKeys = foldedKeys;
      }

      private static MythicConfigImpl.SectionKeyIndex from(ConfigurationSection section) {
         if (section == null) {
            return EMPTY;
         } else {
            Set<String> keys = section.getKeys(false);
            if (keys.isEmpty()) {
               return EMPTY;
            } else {
               HashSet<String> exactKeys = new HashSet<>(keys.size());
               HashMap<String, String> foldedKeys = new HashMap<>(keys.size());

               for (String existingKey : keys) {
                  exactKeys.add(existingKey);
                  foldedKeys.putIfAbsent(MythicConfigImpl.normalizeKey(existingKey), existingKey);
               }

               return new MythicConfigImpl.SectionKeyIndex(exactKeys, foldedKeys);
            }
         }
      }

      private String find(String key) {
         return this.exactKeys.contains(key) ? key : this.foldedKeys.get(MythicConfigImpl.normalizeKey(key));
      }
   }
}
