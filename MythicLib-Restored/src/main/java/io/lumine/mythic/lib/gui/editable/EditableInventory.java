package io.lumine.mythic.lib.gui.editable;

import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.util.lang3.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/** A GUI layout loaded from a configuration file: a name, a size and a list of items. */
public abstract class EditableInventory {
   private final String id;
   private String name;
   private int vanillaSlots;
   private final List<InventoryItem<?>> items = new ArrayList<>();

   public EditableInventory(String id) {
      this.id = Validate.notNull(id, "ID must not be null");
   }

   /** Loads the layout. An item that fails to load is skipped and reported in the console of the given plugin. */
   public void reload(JavaPlugin plugin, ConfigurationSection config) {
      this.name = config.getString("name");
      Validate.notNull(this.name, "Name must not be null");
      this.vanillaSlots = Math.min(Math.max(9, config.getInt("slots")), 54);
      Validate.isTrue(this.vanillaSlots % 9 == 0, "Slots must be a multiple of 9");
      this.items.clear();
      if (config.isConfigurationSection("items")) {
         for (String key : config.getConfigurationSection("items").getKeys(false)) {
            try {
               ConfigurationSection itemConfig = config.getConfigurationSection("items." + key);
               Validate.notNull(itemConfig, "Could not load config");
               this.items.add(this.resolveEntry(itemConfig));
            } catch (Exception exception) {
               plugin.getLogger().log(Level.WARNING, "Could not load item '" + key + "' from inventory '" + this.getId() + "': " + exception.getMessage());
            }
         }
      }
   }

   public String getId() {
      return this.id;
   }

   public List<InventoryItem<?>> getItems() {
      return this.items;
   }

   public String getName() {
      return this.name;
   }

   public int getVanillaSlots() {
      return this.vanillaSlots;
   }

   public InventoryItem<?> getByFunction(String function) {
      for (InventoryItem<?> item : this.items) {
         if (item.getFunction().equals(function)) {
            return item;
         }
      }
      return null;
   }

   private InventoryItem<?> resolveEntry(ConfigurationSection config) {
      String function = Objects.requireNonNull(config.getString("function", config.getName())).toLowerCase();
      InventoryItem<?> item = this.resolveItem(function, config);
      if (item == null) {
         item = new SimpleItem<>(config);
      }

      item.setFunction(function);
      return item;
   }

   /** Creates the item bound to a function name, or null so that a plain {@link SimpleItem} is used. */
   public abstract InventoryItem<?> resolveItem(String function, ConfigurationSection config);
}
