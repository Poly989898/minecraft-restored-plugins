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
   public RandomStatData<AdvancedEnchantMap> whenInitialized(Object var1) {
      throw new RuntimeException("Not supported");
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull AdvancedEnchantMap var2) {
      var2.enchants.forEach((var2x, var3) -> {
         if (var2x != null && var3 != null && !var2x.isEmpty()) {
            var1.getLore().insert(0, AEAPI.getEnchantLore(var2x, var3));
            var1.getMeta().getPersistentDataContainer().set(new NamespacedKey(this.ae, "ae_enchantment-" + var2x), PersistentDataType.INTEGER, var3);
         }
      });
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull AdvancedEnchantMap var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomStatData<AdvancedEnchantMap>> var2) {
   }

   @NotNull
   public AdvancedEnchantMap getClearStatData() {
      return new AdvancedEnchantMap();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ItemMeta var2 = var1.getNBT().getItem().getItemMeta();
      if (var2 != null) {
         AdvancedEnchantMap var3 = new AdvancedEnchantMap();

         for (String var5 : var1.getNBT().getTags()) {
            if (var5 != null && var5.startsWith("ae_enchantment;")) {
               String var6 = var5.substring("ae_enchantment;".length());
               int var7 = var1.getNBT().getInteger(var5);
               var3.enchants.put(var6, var7);
            }
         }

         for (NamespacedKey var10 : var2.getPersistentDataContainer().getKeys()) {
            if (var10.getNamespace().equals(this.aeNamespace)) {
               String var11 = var10.getKey();
               if (var11.startsWith("ae_enchantment-")) {
                  Integer var12 = (Integer)var2.getPersistentDataContainer().get(var10, PersistentDataType.INTEGER);
                  if (var12 != null && var12 != 0) {
                     String var8 = var11.substring("ae_enchantment-".length());
                     var3.enchants.put(var8, var12);
                  }
               }
            }
         }

         if (!var3.enchants.isEmpty()) {
            var1.setData(this, var3);
         }
      }
   }

   @Nullable
   public AdvancedEnchantMap getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }
}
