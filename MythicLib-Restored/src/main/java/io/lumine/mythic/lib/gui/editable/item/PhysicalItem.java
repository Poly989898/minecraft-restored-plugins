package io.lumine.mythic.lib.gui.editable.item;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.gui.util.IconOptions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** A GUI item that is displayed as an actual item, with a name, a lore and placeholders. */
public abstract class PhysicalItem<T extends GeneratedInventory> extends InventoryItem<T> {
   private final String id;
   private final IconOptions iconOptions;
   private final String name;
   private final List<String> lore;

   public PhysicalItem(ConfigurationSection config) {
      this(null, config);
   }

   public PhysicalItem(InventoryItem<T> parent, ConfigurationSection config) {
      super(parent, config);
      this.id = config.getName();
      this.iconOptions = IconOptions.from(config);
      this.name = config.getString("name");
      this.lore = config.getStringList("lore");
   }

   public String getId() {
      return this.id;
   }

   /** Edits the meta of the item before the name and the lore are applied. */
   public void preprocessMeta(T inv, int n, ItemMeta meta) {
   }

   public void preprocessLore(T inv, int n, List<String> lore) {
   }

   public String preprocessName(T inv, int n, String name) {
      return name;
   }

   @Override
   public ItemStack getDisplayedItem(T inv, int n) {
      return this.getDisplayedItem(inv, ItemOptions.index(n));
   }

   public ItemStack getDisplayedItem(T inv, ItemOptions options) {
      int n = options.index();
      Placeholders placeholders = this.getPlaceholders(inv, n);
      OfflinePlayer effective = this.getEffectivePlayer(inv, n);
      IconOptions icon = options.icon().combine(this.iconOptions);
      ItemStack item = new ItemStack(icon.getMaterialElse(Material.BARRIER));
      ItemMeta meta = item.getItemMeta();
      this.preprocessMeta(inv, n, meta);
      if (this.name != null) {
         meta.setDisplayName(placeholders.apply(effective, this.preprocessName(inv, n, this.name)));
      }

      icon.applyToItemMeta(meta);
      if (!this.lore.isEmpty()) {
         List<String> lore = new ArrayList<>(this.lore);
         this.preprocessLore(inv, n, lore);
         List<String> parsed = new ArrayList<>();
         for (String line : lore) {
            for (String part : placeholders.apply(effective, line).split("\\n")) {
               parsed.add(org.bukkit.ChatColor.GRAY + part);
            }
         }
         meta.setLore(parsed);
      }

      item.setItemMeta(meta);
      return item;
   }

   public abstract Placeholders getPlaceholders(T inv, int n);
}
