package litebans;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class YamlKeyReader implements ConfigKeyReader {
   private final YamlConfigFile yamlConfigFile;
   private final ConfigSection configSection2;

   public YamlKeyReader(YamlConfigFile yamlConfigFile, ConfigSection configSection) {
      this.yamlConfigFile = yamlConfigFile;
      this.configSection2 = configSection;
   }

   @Override
   public Collection getKeys(boolean flag) {
      return this.configSection2 == null ? Collections.emptyList() : this.configSection2.getKeys();
   }

   @Override
   public ConfigKeyReader getSection(String text) {
      ConfigSection configSection;
      try {
         configSection = this.configSection2.getSection(text);
      } catch (ClassCastException e) {
         this.yamlConfigFile.handleText(text, e);
         return null;
      }

      return configSection == null ? null : new YamlKeyReader(this.yamlConfigFile, configSection);
   }

   @Override
   public ConfigFile set(String text, Object obj) {
      this.configSection2.set(text, obj);
      return this.yamlConfigFile;
   }

   @Override
   public Object get(String text, Object obj) {
      return this.configSection2.computeObj(text, obj);
   }

   @Override
   public Object get(String text) {
      return this.configSection2.computeObj(text);
   }

   @Override
   public int getInt(String text, int value) {
      return this.configSection2.getInt(text, value);
   }

   @Override
   public long getLong(String text, long value) {
      return this.configSection2.getLong(text, value);
   }

   @Override
   public double getDouble(String text) {
      return this.configSection2.getDouble(text);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.configSection2.getBoolean(text);
   }

   @Override
   public boolean getBoolean(String text, boolean flag) {
      return this.configSection2.getBoolean(text, flag);
   }

   @Override
   public String getString(String text) {
      return this.configSection2.getString(text);
   }

   @Override
   public String getString(String text, String text2) {
      return this.configSection2.getString(text, text2);
   }

   @Override
   public List getStringList(String text) {
      return this.configSection2.computeList(text);
   }

   @Override
   public boolean isSet(String text) {
      return this.contains(text);
   }

   @Override
   public boolean contains(String text) {
      return this.configSection2 != null && this.configSection2.computeObj(text) != null;
   }
}
