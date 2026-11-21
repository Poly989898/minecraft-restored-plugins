package io.lumine.mythic.bukkit.utils.config.properties.types;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LineItemListProp extends PropertyType<List<ItemStack>> {
   private List<ItemStack> defaultValue;
   private StringListProp LIST;

   public LineItemListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.LIST = new StringListProp(plugin, file, node);
      this.defaultValue = Lists.newArrayList(new ItemStack[]{new ItemStack(Material.STONE)});
   }

   public LineItemListProp(LuminePlugin plugin, Object file, String node, List<ItemStack> defaultValue) {
      super(plugin, file, node);
      this.LIST = new StringListProp(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected List<ItemStack> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         List<ItemStack> items = Lists.newArrayList();

         for (String line : this.LIST.get(section)) {
            items.add(ItemFactory.of(line).build());
         }

         return items;
      }
   }

   public void set(String holder, List<ItemStack> value) {
      if (this.config != null) {
         ;
      }
   }

   public List<ItemStack> getDefaultValue() {
      return this.defaultValue;
   }
}
