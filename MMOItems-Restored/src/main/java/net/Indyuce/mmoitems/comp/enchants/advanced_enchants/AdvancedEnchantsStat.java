package net.Indyuce.mmoitems.comp.enchants.advanced_enchants;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.advancedplugins.ae.api.AEAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedEnchantsStat extends ItemStat<RandomStatData<AdvancedEnchantMap>, AdvancedEnchantMap> implements InternalStat {
   private final Plugin ae = Bukkit.getPluginManager().getPlugin("AdvancedEnchantments");
   private final String aeNamespace;
   private static final String AE_KEY_PREFIX = "ae_enchantment-";
   private static final String LEGACY_AE_KEY_PREFIX = "ae_enchantment;";

   public AdvancedEnchantsStat() {
      super(
         "ADVANCED_ENCHANTS",
         Material.EXPERIENCE_BOTTLE,
         "Advanced Enchants",
         new String[]{"The AEnchants of this item. Format:", "§e[internal_name] [level]"},
         new String[]{"!miscellaneous", "!block", "all"}
      );
      Validate.notNull(this.ae, "Could not find plugin AdvancedEnchants");
      this.aeNamespace = new NamespacedKey(this.ae, "any").getNamespace();
   }

   @Override
   public RandomStatData<AdvancedEnchantMap> whenInitialized(Object obj) {
      throw new RuntimeException("Not supported");
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull AdvancedEnchantMap advancedEnchantMap) {
      advancedEnchantMap.enchants.forEach((var2x, value) -> {
         if (var2x != null && value != null && !var2x.isEmpty()) {
            itemStackBuilder.getLore().insert(0, AEAPI.getEnchantLore(var2x, value));
            itemStackBuilder.getMeta().getPersistentDataContainer().set(new NamespacedKey(this.ae, "ae_enchantment-" + var2x), PersistentDataType.INTEGER, value);
         }
      });
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull AdvancedEnchantMap advancedEnchantMap) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomStatData<AdvancedEnchantMap>> optional) {
   }

   @NotNull
   public AdvancedEnchantMap getClearStatData() {
      return new AdvancedEnchantMap();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ItemMeta itemMeta = readMMOItem.getNBT().getItem().getItemMeta();
      if (itemMeta != null) {
         AdvancedEnchantMap advancedEnchantMap = new AdvancedEnchantMap();

         for (String tag : readMMOItem.getNBT().getTags()) {
            if (tag != null && tag.startsWith("ae_enchantment;")) {
               String text = tag.substring("ae_enchantment;".length());
               int count = readMMOItem.getNBT().getInteger(tag);
               advancedEnchantMap.enchants.put(text, count);
            }
         }

         for (NamespacedKey key : itemMeta.getPersistentDataContainer().getKeys()) {
            if (key.getNamespace().equals(this.aeNamespace)) {
               String enchantKey = key.getKey();
               if (enchantKey.startsWith("ae_enchantment-")) {
                  Integer value = (Integer)itemMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                  if (value != null && value != 0) {
                     String text = enchantKey.substring("ae_enchantment-".length());
                     advancedEnchantMap.enchants.put(text, value);
                  }
               }
            }
         }

         if (!advancedEnchantMap.enchants.isEmpty()) {
            readMMOItem.setData(this, advancedEnchantMap);
         }
      }
   }

   @Nullable
   public AdvancedEnchantMap getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }
}
