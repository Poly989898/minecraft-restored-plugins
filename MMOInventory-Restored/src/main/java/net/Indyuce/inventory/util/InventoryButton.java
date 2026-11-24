package net.Indyuce.inventory.util;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.AdventureUtils;
import net.Indyuce.inventory.MMOInventory;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryButton {
   private final ItemStack item;
   public static final NamespacedKey NAMESPACED_KEY = new NamespacedKey(MMOInventory.plugin, "Button");

   public InventoryButton(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      Validate.isTrue(config.contains("material"), "Could not find item material");
      this.item = new ItemStack(Material.valueOf(config.getString("material")));
      ItemMeta meta = this.item.getItemMeta();
      if (config.contains("name")) {
         AdventureUtils.setDisplayName(meta, config.getString("name"));
      }

      if (config.contains("lore")) {
         AdventureUtils.setLore(meta, config.getStringList("lore"));
      }

      if (config.contains("skull-texture") && meta instanceof SkullMeta) {
         UtilityMethods.setTextureValue((SkullMeta)meta, config.getString("skull-texture"));
      }

      if (config.contains("custom-model-data")) {
         meta.setCustomModelData(config.getInt("custom-model-data"));
      }

      meta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.BYTE, (byte)1);
      this.item.setItemMeta(meta);
   }

   public ItemStack getItem() {
      return this.item;
   }
}
