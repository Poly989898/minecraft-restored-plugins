package net.Indyuce.mmoitems.api.crafting;

import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class StationItemOptions {
   private final ItemStack fill;
   private final ItemStack noRecipe;
   private final ItemStack noQueueItem;

   public StationItemOptions(ConfigurationSection var1) {
      this.fill = new ConfigItem(var1.getConfigurationSection("fill")).getItem();
      this.noRecipe = new ConfigItem(var1.getConfigurationSection("no-recipe")).getItem();
      this.noQueueItem = new ConfigItem(var1.getConfigurationSection("no-queue-item")).getItem();
   }

   public ItemStack getFill() {
      return this.fill;
   }

   public ItemStack getNoRecipe() {
      return this.noRecipe;
   }

   public ItemStack getNoQueueItem() {
      return this.noQueueItem;
   }

   public boolean hasFill() {
      return this.fill.getType() != Material.AIR;
   }

   public boolean hasNoRecipe() {
      return this.noRecipe.getType() != Material.AIR;
   }

   public boolean hasNoQueueItem() {
      return this.noQueueItem.getType() != Material.AIR;
   }
}
