package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/** A GUI layout loaded from a config file: a name, a size and a set of items bound to slots. */
public abstract class EditableInventory {
   private final String id;
   private String name = "";
   private int slots = 27;
   private final Map<String, InventoryItem<?>> items = new LinkedHashMap<>();

   public EditableInventory(String id) {
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public int getSlots() {
      return this.slots;
   }

   public Map<String, InventoryItem<?>> getItems() {
      return this.items;
   }

   public abstract InventoryItem<?> resolveItem(String function, ConfigurationSection config);

   public void reload(JavaPlugin plugin, ConfigurationSection config) {
      this.name = config.getString("name", this.id);
      int size = config.getInt("slots", 27);
      if (size < 9 || size > 54 || size % 9 != 0) {
         throw new IllegalArgumentException("slots must be a multiple of 9 between 9 and 54, got " + size);
      }

      this.slots = size;
      Map<String, InventoryItem<?>> loaded = new LinkedHashMap<>();
      ConfigurationSection section = config.getConfigurationSection("items");
      if (section != null) {
         for (String key : section.getKeys(false)) {
            ConfigurationSection itemConfig = section.getConfigurationSection(key);
            if (itemConfig == null) {
               continue;
            }

            String function = itemConfig.getString("function", key);
            InventoryItem<?> item = this.resolveItem(function, itemConfig);
            if (item == null) {
               throw new IllegalArgumentException("Could not find item function '" + function + "'");
            }

            loaded.put(function, item);
         }
      }

      this.items.clear();
      this.items.putAll(loaded);
   }

   public InventoryItem<?> getByFunction(String function) {
      return this.items.get(function);
   }
}
