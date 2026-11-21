package com.ticxo.modelengine.api.utils.config;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.configuration.ConfigurationSection;

public interface Property {
   String getPath();

   Object getDef();

   default int getInt() {
      return ModelEngineAPI.getAPI().getConfigManager().getInt(this);
   }

   default int getIntOrIfNeg(Supplier<Integer> supplier) {
      int i = this.getInt();
      return i < 0 ? supplier.get() : i;
   }

   default double getDouble() {
      return ModelEngineAPI.getAPI().getConfigManager().getDouble(this);
   }

   default String getString() {
      return ModelEngineAPI.getAPI().getConfigManager().getString(this);
   }

   default boolean getBoolean() {
      return ModelEngineAPI.getAPI().getConfigManager().getBoolean(this);
   }

   default List<String> getStringList() {
      return ModelEngineAPI.getAPI().getConfigManager().getStringList(this);
   }

   default ConfigurationSection getSection() {
      return ModelEngineAPI.getAPI().getConfigManager().getConfigSection(this.getPath());
   }

   default ConfigurationSection getSection(String path) {
      return ModelEngineAPI.getAPI().getConfigManager().getConfigSection(this.getPath() + "." + path);
   }

   default BaseItemEnum getBaseItem() {
      return BaseItemEnum.get(this.getString().toUpperCase(Locale.ENGLISH));
   }

   default Set<BaseItemEnum> getBaseItems() {
      HashSet<BaseItemEnum> set = new HashSet<>();

      for (String s : this.getStringList()) {
         BaseItemEnum item = BaseItemEnum.get(s.toUpperCase(Locale.ENGLISH));
         if (item != null) {
            set.add(item);
         }
      }

      return set;
   }

   default CullType getCullType() {
      return CullType.get(this.getString().toUpperCase(Locale.ENGLISH));
   }
}
