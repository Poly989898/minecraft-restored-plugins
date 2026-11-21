package litebans;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class BukkitKeyReader implements ConfigKeyReader {
   private final BukkitConfigFile bukkitConfigFile;
   private final ConfigurationSection configurationSection2;

   public BukkitKeyReader(BukkitConfigFile bukkitConfigFile, ConfigurationSection configurationSection) {
      this.bukkitConfigFile = bukkitConfigFile;
      this.configurationSection2 = configurationSection;
   }

   @Override
   public ConfigKeyReader getSection(String text) {
      ConfigurationSection configurationSection = this.configurationSection2.getConfigurationSection(text);
      return configurationSection == null ? null : new BukkitKeyReader(this.bukkitConfigFile, configurationSection);
   }

   @Override
   public ConfigFile set(String text, Object obj) {
      this.configurationSection2.set(text, obj);
      return this.bukkitConfigFile;
   }

   @Override
   public Collection getKeys(boolean flag) {
      return this.configurationSection2 == null ? Collections.emptyList() : this.configurationSection2.getKeys(flag);
   }

   @Override
   public Object get(String text) {
      return this.configurationSection2.get(text);
   }

   @Override
   public Object get(String text, Object obj) {
      return this.configurationSection2.get(text, obj);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.configurationSection2.getBoolean(text);
   }

   @Override
   public boolean getBoolean(String text, boolean flag) {
      return this.configurationSection2.getBoolean(text, flag);
   }

   @Override
   public double getDouble(String text) {
      return this.configurationSection2.getDouble(text);
   }

   @Override
   public int getInt(String text, int value) {
      return this.configurationSection2.getInt(text, value);
   }

   @Override
   public long getLong(String text, long value) {
      return this.configurationSection2.getLong(text, value);
   }

   @Override
   public String getString(String text) {
      return this.configurationSection2.getString(text);
   }

   @Override
   public String getString(String text, String text2) {
      return this.configurationSection2.getString(text, text2);
   }

   @Override
   public List getStringList(String text) {
      return this.configurationSection2.getStringList(text);
   }

   @Override
   public boolean isSet(String text) {
      return this.configurationSection2.isSet(text);
   }

   @Override
   public boolean contains(String text) {
      return this.configurationSection2.contains(text);
   }
}
