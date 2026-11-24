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

   public void reload(boolean var1) {
      if (var1) {
         this.map.clear();
      }

      for (Field var5 : Type.class.getFields()) {
         try {
            if (Modifier.isStatic(var5.getModifiers()) && Modifier.isFinal(var5.getModifiers()) && var5.get(null) instanceof Type) {
               this.register((Type)var5.get(null));
            }
         } catch (Exception var8) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Couldn't register type called '" + var5.getName() + "': " + var8.getMessage());
         }
      }

      ConfigManager.DefaultFile.ITEM_TYPES.checkFile();
      FileConfiguration var10 = new ConfigFile("item-types").getConfig();

      for (String var13 : var10.getKeys(false)) {
         if (!this.map.containsKey(var13)) {
            try {
               this.register(new Type(this, var10.getConfigurationSection(var13)));
            } catch (IllegalArgumentException var7) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not register type '" + var13 + "': " + var7.getMessage());
            }
         }
      }

      Iterator var12 = this.map.values().iterator();

      while (var12.hasNext()) {
         Type var14 = (Type)var12.next();

         try {
            ConfigurationSection var15 = var10.getConfigurationSection(var14.getId());
            Validate.notNull(var15, "Could not find config section");
            var14.load(var15);
            if (var1) {
               var14.getPostLoadAction().performAction();
            }
         } catch (RuntimeException var9) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not register type '" + var14.getId() + "': " + var9.getMessage());
            var12.remove();
            continue;
         }

         var14.getAvailableStats().clear();
         MMOItems.plugin.getStats().getAll().stream().filter(var1x -> var1x.isCompatible(var14)).forEach(var1x -> var14.getAvailableStats().add(var1x));
      }
   }

   public void postload() {
      for (Type var2 : this.map.values()) {
         try {
            var2.getPostLoadAction().performAction();
         } catch (RuntimeException var4) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "An error occured while post-loading type '" + var2.getId() + "': " + var4.getMessage());
         }
      }
   }

   public void register(Type var1) {
      this.map.put(var1.getId(), var1);
   }

   public void registerAll(Type... var1) {
      for (Type var5 : var1) {
         this.register(var5);
      }
   }

   @Nullable
   public Type get(@Nullable String var1) {
      return var1 == null ? null : this.map.get(var1);
   }

   @NotNull
   public Type getOrThrow(@Nullable String var1) {
      Validate.isTrue(this.map.containsKey(var1), "Could not find item type with ID '" + var1 + "'");
      return this.map.get(var1);
   }

   public boolean has(String var1) {
      return this.map.containsKey(var1);
   }

   public Collection<Type> getAll() {
      return this.map.values();
   }

   public ArrayList<String> getAllTypeNames() {
      ArrayList var1 = new ArrayList();

      for (Type var3 : this.getAll()) {
         var1.add(var3.getId());
      }

      return var1;
   }
}
