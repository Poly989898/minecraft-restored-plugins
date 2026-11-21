package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.collections.WeightedCollection;
import io.lumine.mythic.bukkit.utils.collections.WeightedEntry;
import io.lumine.mythic.bukkit.utils.config.LineConfig;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class WeightedLineItemListProp extends PropertyType<WeightedCollection<WeightedEntry<ItemStack>>> {
   private WeightedCollection<WeightedEntry<ItemStack>> defaultValue = new WeightedCollection<>();
   private StringListProp LIST;

   public WeightedLineItemListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.LIST = new StringListProp(plugin, file, node);
      WeightedEntry<ItemStack> defaultValuea = new WeightedEntry<>(new ItemStack(Material.STONE), 1.0);
      this.defaultValue.add(defaultValuea);
   }

   public WeightedLineItemListProp(LuminePlugin plugin, Object file, String node, WeightedCollection<WeightedEntry<ItemStack>> defaultValue) {
      super(plugin, file, node);
      this.LIST = new StringListProp(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected WeightedCollection<WeightedEntry<ItemStack>> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         WeightedCollection<WeightedEntry<ItemStack>> items = new WeightedCollection<>();
         Iterator iterator = this.LIST.get(section).iterator();

         while (true) {
            LineConfig lc;
            double weight;
            while (true) {
               if (!iterator.hasNext()) {
                  return items;
               }

               String line = (String)iterator.next();
               lc = LineConfig.of(line);
               String[] lineSplit = lc.getLine().split(" ");
               if (lineSplit.length <= 1) {
                  weight = 1.0;
                  break;
               }

               try {
                  weight = Double.parseDouble(lineSplit[1]);
                  break;
               } catch (Error | Exception e) {
                  Log.info("Invalid entry weight {0} on line {1}", lineSplit[1], line);
               }
            }

            ItemStack item = ItemFactory.of(lc).build();
            items.add(new WeightedEntry<>(item, weight));
         }
      }
   }

   public void set(String holder, WeightedCollection<WeightedEntry<ItemStack>> value) {
      if (this.config != null) {
         ;
      }
   }

   public WeightedCollection<WeightedEntry<ItemStack>> getDefaultValue() {
      return this.defaultValue;
   }
}
