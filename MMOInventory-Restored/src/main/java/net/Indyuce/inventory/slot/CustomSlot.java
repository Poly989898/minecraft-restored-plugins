package net.Indyuce.inventory.slot;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.inventory.util.LineConfig;
import net.Indyuce.inventory.util.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CustomSlot {
   private final String id;
   private final SlotType type;
   private final int slot;
   private final ItemStack item;
   private final List<SlotRestriction> restrictions = new ArrayList<>();

   @Deprecated
   public CustomSlot(String id, String name, SlotType type, int slot, ItemStack item) {
      this(id, type, slot, item);
   }

   public CustomSlot(@NotNull String id, @NotNull SlotType type, int slot, @NotNull ItemStack item) {
      this.id = id;
      this.type = type;
      this.item = item;
      this.slot = type == SlotType.FILL ? -1 : slot;
   }

   public CustomSlot(@NotNull ConfigurationSection config) {
      Validate.notNull(config, "Could not read slot config");
      this.id = config.getName().toLowerCase().replace("_", "-").replace(" ", "-");
      Validate.notNull(config.getString("type"), "Could not read slot type");
      this.type = SlotType.valueOf(config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.slot = this.type == SlotType.FILL ? -1 : config.getInt("slot");
      Validate.isTrue(this.type == SlotType.FILL || this.slot >= 0, "Non filler slots must have a positive slot index");
      int model = config.getInt("durability", config.getInt("custom-model-data"));
      this.item = new ItemStack(Material.valueOf(UtilityMethods.enumName(Objects.requireNonNull(config.getString("material"), "Could not read slot material"))));
      ItemMeta meta = this.item.getItemMeta();
      meta.setCustomModelData(model);
      AdventureUtils.setDisplayName(meta, Objects.requireNonNull(config.getString("name"), "Could not read slot name"));
      meta.addItemFlags(ItemFlag.values());
      List<String> lore = new ArrayList<>();

      for (String line : Objects.requireNonNull(config.getStringList("lore"), "Could not read slot lore")) {
         lore.add(ChatColor.GRAY + line);
      }

      AdventureUtils.setLore(meta, lore);
      meta.setUnbreakable(true);
      meta.getPersistentDataContainer().set(Utils.GUI_ITEM_ID, PersistentDataType.STRING, this.getId());
      if (config.contains("texture-value") && meta instanceof SkullMeta) {
         UtilityMethods.setTextureValue((SkullMeta)meta, config.getString("texture-value"));
      }

      this.item.setItemMeta(meta);
      if (config.contains("restrictions")) {
         for (String key : config.getStringList("restrictions")) {
            this.restrictions.add(MMOInventory.plugin.getSlotManager().readRestriction(new LineConfig(key)));
         }
      }
   }

   public String getId() {
      return this.id;
   }

   public int getIndex() {
      return this.slot;
   }

   public SlotType getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public ItemStack displayItem(Player player) {
      if (!this.item.hasItemMeta()) {
         return this.item;
      }

      ItemMeta meta = this.item.getItemMeta();
      if (!meta.hasLore()) {
         return this.item;
      }

      List<String> lore = meta.getLore();

      for (int j = 0; j < lore.size(); j++) {
         lore.set(j, MMOInventory.plugin.getPlaceholderParser().parse(player, lore.get(j)));
      }

      meta.setLore(lore);
      ItemStack clone = this.item.clone();
      clone.setItemMeta(meta);
      return clone;
   }

   public boolean checkSlotRestrictions(InventoryHandler player, ItemStack item) {
      for (SlotRestriction restriction : this.restrictions) {
         if (!restriction.isVerified(player, this, item)) {
            return false;
         }
      }

      return true;
   }

   public boolean canHost(InventoryHandler player, ItemStack item) {
      return !this.getType().isCustom() && !this.getType().getVanillaSlotHandler().canEquip(item) ? false : this.checkSlotRestrictions(player, item);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CustomSlot that = (CustomSlot)o;
         return this.slot == that.slot;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.slot);
   }
}
