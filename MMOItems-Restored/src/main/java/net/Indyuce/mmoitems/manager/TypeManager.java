package net.Indyuce.mmoitems.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.Type;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeManager {
   private final Map<String, Type> map = new LinkedHashMap<>();

   public void reload(boolean flag) {
      if (flag) {
         this.map.clear();
      }

      for (Field field : Type.class.getFields()) {
         try {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.get(null) instanceof Type) {
               this.register((Type)field.get(null));
            }
         } catch (Exception exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Couldn't register type called '" + field.getName() + "': " + exception.getMessage());
         }
      }

      ConfigManager.DefaultFile.ITEM_TYPES.checkFile();
      FileConfiguration config = new ConfigFile("item-types").getConfig();

      for (String key : config.getKeys(false)) {
         if (!this.map.containsKey(key)) {
            try {
               this.register(new Type(this, config.getConfigurationSection(key)));
            } catch (IllegalArgumentException exception) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not register type '" + key + "': " + exception.getMessage());
            }
         }
      }

      Iterator iterator = this.map.values().iterator();

      while (iterator.hasNext()) {
         Type type = (Type)iterator.next();

         try {
            ConfigurationSection section = config.getConfigurationSection(type.getId());
            Validate.notNull(section, "Could not find config section");
            type.load(section);
            if (flag) {
               type.getPostLoadAction().performAction();
            }
         } catch (RuntimeException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not register type '" + type.getId() + "': " + exception.getMessage());
            iterator.remove();
            continue;
         }

         type.getAvailableStats().clear();
         MMOItems.plugin.getStats().getAll().stream().filter(stat -> stat.isCompatible(type)).forEach(stat -> type.getAvailableStats().add(stat));
      }
   }

   public void postload() {
      for (Type type : this.map.values()) {
         try {
            type.getPostLoadAction().performAction();
         } catch (RuntimeException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occured while post-loading type '" + type.getId() + "': " + exception.getMessage());
         }
      }
   }

   public void register(Type type) {
      this.map.put(type.getId(), type);
   }

   public void registerAll(Type... types) {
      for (Type type : types) {
         this.register(type);
      }
   }

   @Nullable
   public Type get(@Nullable String text) {
      return text == null ? null : this.map.get(text);
   }

   @NotNull
   public Type getOrThrow(@Nullable String text) {
      Validate.isTrue(this.map.containsKey(text), "Could not find item type with ID '" + text + "'");
      return this.map.get(text);
   }

   public boolean has(String text) {
      return this.map.containsKey(text);
   }

   public Collection<Type> getAll() {
      return this.map.values();
   }

   public ArrayList<String> getAllTypeNames() {
      ArrayList list = new ArrayList();

      for (Type type : this.getAll()) {
         list.add(type.getId());
      }

      return list;
   }
}
