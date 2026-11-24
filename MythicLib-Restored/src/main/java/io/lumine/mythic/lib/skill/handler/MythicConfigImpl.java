package io.lumine.mythic.lib.skill.handler;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.config.GenericConfig;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderBooleanImpl;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MythicConfigImpl implements GenericConfig, Cloneable, MythicConfig {
   private String configName;
   private File file;
   private ConfigurationSection fc;

   public MythicConfigImpl(String name, ConfigurationSection fc) {
      this.configName = name;
      this.file = null;
      this.fc = fc;
   }

   public void setKey(String key) {
      this.configName = key;
   }

   // Present in newer MythicMobs APIs (5.8+); not an override against the 5.7 API this code compiles with.
   public ConfigurationSection getRootConfiguration() {
      return this.fc;
   }

   public String getKey() {
      return this.configName;
   }

   public String getFileName() {
      if (this.file != null) {
         return this.file.getAbsolutePath();
      } else {
         return this.configName != null ? "Nested Configuration '" + this.configName + "'" : "API";
      }
   }

   public FileConfiguration getFileConfiguration() {
      throw new NotImplementedException();
   }

   public String getNode(String field) {
      if (this.configName != null && this.configName.length() != 0) {
         return field != null && !field.isEmpty() ? this.configName + "." : this.configName;
      } else {
         return "";
      }
   }

   public void deleteNodeAndSave() {
      this.fc.set(this.getNode((String)null), null);
      this.save();
   }

   public boolean isSet(String key) {
      ConfigurationSection fc = this.fc;
      String node = this.getNode(key);
      return fc.isSet(node + key);
   }

   public String determineWhichKeyToUse(String def, String... keys) {
      String[] texts = keys;
      int length = keys.length;

      for (int i = 0; i < length; i++) {
         String key = texts[i];
         if (this.isSet(key)) {
            return key;
         }
      }

      return def;
   }

   public void set(String key, Object value) {
      this.fc.set(this.getNode(key) + key, value);
   }

   public void setSave(String field, Object value) {
      this.fc.set(this.getNode(field) + field, value);
      this.save();
   }

   public void unset(String field) {
      this.fc.set(this.getNode(field) + field, null);
   }

   public void unsetSave(String key) {
      this.unset(key);
      this.save();
   }

   public void load() {
      this.fc = YamlConfiguration.loadConfiguration(this.file);
   }

   public void save() {
      throw new NotImplementedException();
   }

   public MythicConfig getNestedConfig(String key) {
      return new MythicConfigImpl(this.getNode(key) + key, this.fc);
   }

   public Map<String, MythicConfig> getNestedConfigs(String field) {
      Map<String, MythicConfig> map = new HashMap<>();
      if (!this.isSet(field)) {
         return map;
      }

      for (String k : this.getKeys(field)) {
         map.put(k, new MythicConfigImpl(this.getNode(field) + field + "." + k, this.fc));
      }

      return map;
   }

   public String getString(String field) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getString(key, this.fc.getString(key.toLowerCase()));
   }

   public String getString(String[] key) {
      return this.getString(key, (String)null);
   }

   public String getString(String field, String def) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getString(key, this.fc.getString(key.toLowerCase(), def));
   }

   public String getString(String[] keysToCheck, String finalDefaultValue, String... defaultValues) {
      String[] texts = keysToCheck;
      int length = keysToCheck.length;

      for (int i = 0; i < length; i++) {
         String value = texts[i];
         if (!this.isConfigurationSection(value)) {
            String get = this.getString(value, (String)null);
            if (get != null) {
               return get;
            }
         }
      }

      texts = defaultValues;
      length = defaultValues.length;

      for (int i = 0; i < length; i++) {
         String value = texts[i];
         if (value != null) {
            return value;
         }
      }

      return finalDefaultValue;
   }

   public Chroma getColor(String field) {
      return this.getColor(field, Chroma.of(255, 255, 255));
   }

   public Chroma getColor(String field, Chroma def) {
      String data = this.getString(field, (String)null);
      return data == null ? def : Chroma.of(data);
   }

   public PlaceholderString getPlaceholderString(String field) {
      String node = this.getNode(field);
      String key = node + field;
      String s = this.fc.getString(key);
      return s == null ? null : PlaceholderString.of(s);
   }

   public PlaceholderString getPlaceholderString(String field, String def) {
      String node = this.getNode(field);
      String key = node + field;
      String s = this.fc.getString(key, def);
      return s == null ? null : PlaceholderString.of(s);
   }

   public String getColorString(String field) {
      String node = this.getNode(field);
      String key = node + field;
      String s = this.fc.getString(key);
      if (s != null) {
         s = Text.colorizeLegacy(s);
      }

      return s;
   }

   public String getColorString(String field, String def) {
      String node = this.getNode(field);
      String key = node + field;
      String s = this.fc.getString(key, def);
      if (s != null) {
         s = Text.colorizeLegacy(s);
      }

      return s;
   }

   public boolean getBoolean(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      return fc.getBoolean(node2 + field);
   }

   public boolean getBoolean(String field, boolean def) {
      return this.fc.getBoolean(this.getNode(field) + field, def);
   }

   public PlaceholderBoolean getPlaceholderBoolean(String field) {
      ConfigurationSection fc = this.fc;
      String node = this.getNode(field);
      if (fc.isSet(node + field)) {
         fc = this.fc;
         node = this.getNode(field);
         return PlaceholderBooleanImpl.of(fc.getString(node + field));
      } else {
         return null;
      }
   }

   public PlaceholderBoolean getPlaceholderBoolean(String[] strings) {
      String[] texts = strings;
      int length = strings.length;

      for (int i = 0; i < length; i++) {
         String key = texts[i];
         ConfigurationSection fc = this.fc;
         String node = this.getNode(key);
         if (fc.isSet(node + key)) {
            fc = this.fc;
            node = this.getNode(key);
            return PlaceholderBooleanImpl.of(fc.getString(node + key));
         }
      }

      return null;
   }

   public PlaceholderBoolean getPlaceholderBoolean(String field, Boolean def) {
      ConfigurationSection fc = this.fc;
      String node = this.getNode(field);
      if (fc.isSet(node + field)) {
         fc = this.fc;
         node = this.getNode(field);
         return PlaceholderBooleanImpl.of(fc.getString(node + field));
      } else {
         return def == null ? null : PlaceholderBoolean.of(def.toString());
      }
   }

   public PlaceholderBoolean getPlaceholderBoolean(String[] field, Boolean def) {
      String[] texts = field;
      int length = field.length;

      for (int i = 0; i < length; i++) {
         String key = texts[i];
         ConfigurationSection fc = this.fc;
         String node = this.getNode(key);
         if (fc.isSet(node + key)) {
            fc = this.fc;
            node = this.getNode(key);
            return PlaceholderBooleanImpl.of(fc.getString(node + key));
         }
      }

      return def == null ? null : PlaceholderBoolean.of(def.toString());
   }

   public Boolean getBooleanNullable(String field) {
      ConfigurationSection fc = this.fc;
      String node = this.getNode(field);
      if (fc.isSet(node + field)) {
         fc = this.fc;
         node = this.getNode(field);
         return fc.getBoolean(node + field);
      } else {
         return null;
      }
   }

   public Boolean getBooleanNullable(String[] field) {
      String[] texts = field;
      int length = field.length;

      for (int i = 0; i < length; i++) {
         String key = texts[i];
         ConfigurationSection fc = this.fc;
         String node = this.getNode(key);
         if (fc.isSet(node + key)) {
            fc = this.fc;
            node = this.getNode(key);
            return fc.getBoolean(node + key);
         }
      }

      return null;
   }

   public int getInteger(String field) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getInt(key, this.fc.getInt(key.toLowerCase()));
   }

   public int getInteger(String field, int def) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getInt(key, this.fc.getInt(key.toLowerCase(), def));
   }

   public int getInteger(String[] keys, int def) {
      String[] texts = keys;
      int length = keys.length;

      for (int i = 0; i < length; i++) {
         String key = texts[i];
         String node = this.getNode(key);
         key = node + key;
         if (this.fc.isInt(key)) {
            return this.fc.getInt(key);
         }
      }

      return def;
   }

   @Deprecated
   public int getInt(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      return fc.getInt(node2 + field);
   }

   @Deprecated
   public int getInt(String field, int def) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getInt(this.getNode(field) + field, def);
   }

   public float getFloat(String field) {
      return (float)this.getDouble(field);
   }

   public float getFloat(String field, float def) {
      return (float)this.getDouble(field, def);
   }

   public double getDouble(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      return fc.getDouble(node2 + field);
   }

   public double getDouble(String field, double def) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getDouble(this.getNode(field) + field, def);
   }

   public List<String> getStringList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      return fc.getStringList(node2 + field);
   }

   public List<String> getStringList(String field, List<String> def) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      if (fc.isSet(node2 + field)) {
         fc = this.fc;
         node2 = this.getNode(field);
         return fc.getStringList(node2 + field);
      } else {
         return def;
      }
   }

   public List<String> getColorStringList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      List<String> list = fc.getStringList(node2 + field);
      List<String> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(ChatColor.translateAlternateColorCodes('&', str));
         }
      }

      return parsed;
   }

   public List<PlaceholderString> getPlaceholderStringList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      List<String> list = fc.getStringList(node2 + field);
      List<PlaceholderString> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }
      }

      return parsed;
   }

   public List<PlaceholderString> getPlaceholderStringList(String field, List<String> def) {
      String node = this.getNode(field);
      String key = node + field;
      List<String> list = this.getStringList(field, def);
      List<PlaceholderString> parsed = new ArrayList<>();
      if (list != null) {
         for (String str : list) {
            parsed.add(PlaceholderString.of(str));
         }
      }

      return parsed;
   }

   public List<PlaceholderString> getPlaceholderStringListAlt(String field, List<PlaceholderString> def) {
      String node = this.getNode(field);
      String key = node + field;
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

   public List<Map<?, ?>> getMapList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      ConfigurationSection fc = this.fc;
      String node2 = this.getNode(field);
      return fc.getMapList(node2 + field);
   }

   public List<?> getList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      if (this.fc.isSet(key)) {
         return this.fc.getList(key);
      } else {
         return this.fc.isSet(key.toLowerCase()) ? this.fc.getList(key.toLowerCase()) : null;
      }
   }

   public List<Byte> getByteList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      if (this.fc.isSet(key)) {
         return this.fc.getByteList(key);
      } else {
         return this.fc.isSet(key.toLowerCase()) ? this.fc.getByteList(key.toLowerCase()) : null;
      }
   }

   public ItemStack getItemStack(String field, String def) {
      String node = this.getNode(field);
      String key = node + field;
      if (this.fc.isSet(key)) {
         return this.fc.getItemStack(key);
      }

      if (this.fc.isSet(key.toLowerCase())) {
         return this.fc.getItemStack(key.toLowerCase());
      }

      try {
         return new ItemStack(Material.valueOf(def));
      } catch (Exception exception) {
         return null;
      }
   }

   public boolean isConfigurationSection(String field) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.isConfigurationSection(key);
   }

   public Set<String> getKeys() {
      return this.fc.getConfigurationSection(this.configName).getKeys(false);
   }

   public Set<String> getKeys(String field) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.getConfigurationSection(key).getKeys(false);
   }

   public boolean isList(String field) {
      String node = this.getNode(field);
      String key = node + field;
      return this.fc.isList(key);
   }

   public PlaceholderInt getPlaceholderInt(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderInt.of(s);
   }

   public PlaceholderInt getPlaceholderInt(String[] key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderInt.of(s);
   }

   public PlaceholderInt getPlaceholderInt(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderInt.of(s);
   }

   public PlaceholderDouble getPlaceholderDouble(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   public PlaceholderDouble getPlaceholderDouble(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   public PlaceholderColorImpl getPlaceholderColor(String key, String def) {
      PlaceholderString s = this.getPlaceholderString(key, def);
      return s == null ? null : new PlaceholderColorImpl(s);
   }

   public <T extends Enum> T getEnum(String field, Class<T> clazz, T def) {
      try {
         String in = this.getString(field);
         if (in == null) {
            return def;
         }

         Enum value = Enum.valueOf(clazz, in.toUpperCase());
         return (T)(value == null ? def : value);
      } catch (Error | Exception exception) {
         return def;
      }
   }

   public void merge(MythicConfig tmplConfig, List<String> keysToIgnore) {
      throw new NotImplementedException();
   }

   public File getFile() {
      return this.file;
   }
}
