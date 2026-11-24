package net.Indyuce.mmocore.util.item;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractItemBuilder {
   private final ConfigItem configItem;
   private final Map<String, String> placeholders = new HashMap<>();

   public AbstractItemBuilder(@NotNull ConfigItem configItem) {
      this.configItem = Objects.requireNonNull(configItem, "Config item cannot be null");
   }

   public AbstractItemBuilder(String key) {
      this(MMOCore.plugin.configItems.get(key));
   }

   public ConfigItem getItem() {
      return this.configItem;
   }

   public AbstractItemBuilder addPlaceholders(String... placeholders) {
      for (int j = 0; j < placeholders.length - 1; j += 2) {
         this.placeholders.put(placeholders[j], placeholders[j + 1]);
      }

      return this;
   }

   public ItemStack build() {
      ItemStack item = new ItemStack(this.configItem.getMaterial());
      ItemMeta meta = item.getItemMeta();
      if (item.getType() == Material.PLAYER_HEAD && this.configItem.getTexture() != null) {
         UtilityMethods.setTextureValue((SkullMeta)meta, this.configItem.getTexture());
      }

      meta.addItemFlags(ItemFlag.values());
      meta.setDisplayName(this.applyPlaceholders(this.configItem.getName()));
      if (this.configItem.isUnbreakable()) {
         meta.setUnbreakable(true);
      }

      if (meta instanceof Damageable) {
         ((Damageable)meta).setDamage(this.configItem.getDamage());
      }

      meta.setCustomModelData(this.configItem.getModelData());
      List<String> lore = new ArrayList<>();
      this.configItem.getLore().forEach(line -> lore.add(this.applyPlaceholders(line)));
      meta.setLore(lore);
      this.whenBuildingMeta(item, meta);
      item.setItemMeta(meta);
      NBTItem nbtItem = NBTItem.get(item);
      nbtItem.addTag(new ItemTag[]{new ItemTag("MMOCoreItemId", this.configItem.getId())});
      this.whenBuildingNBT(nbtItem);
      return nbtItem.toItem();
   }

   public abstract void whenBuildingMeta(ItemStack item, ItemMeta itemMeta);

   public abstract void whenBuildingNBT(NBTItem nbtItem);

   public String applyPlaceholders(String string) {
      for (String placeholder : this.placeholders.keySet()) {
         if (string.contains("{" + placeholder + "}")) {
            string = string.replace("{" + placeholder + "}", this.placeholders.get(placeholder) + "");
         }
      }

      return MythicLib.plugin.parseColors(string);
   }
}
