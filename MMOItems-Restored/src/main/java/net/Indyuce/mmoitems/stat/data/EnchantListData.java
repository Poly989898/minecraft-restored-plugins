package net.Indyuce.mmoitems.stat.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantListData implements StatData, Mergeable<EnchantListData> {
   private final Map<Enchantment, Integer> enchants = new HashMap<>();

   @NotNull
   public Set<Enchantment> getEnchants() {
      return this.enchants.keySet();
   }

   public int getLevel(@NotNull Enchantment enchantment) {
      Integer value = this.enchants.get(enchantment);
      return value == null ? 0 : value;
   }

   public void addEnchant(Enchantment enchantment, int count) {
      if (count <= 0) {
         this.enchants.remove(enchantment);
      } else {
         this.enchants.put(enchantment, count);
      }
   }

   public void clear() {
      this.enchants.clear();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof EnchantListData)) {
         return false;
      }

      if (((EnchantListData)obj).enchants.size() != this.enchants.size()) {
         return false;
      }

      for (Enchantment enchant : this.getEnchants()) {
         if (this.getLevel(enchant) != ((EnchantListData)obj).getLevel(enchant)) {
            return false;
         }
      }

      return true;
   }

   public void mergeWith(@NotNull EnchantListData enchantListData) {
      for (Enchantment enchant : enchantListData.getEnchants()) {
         this.addEnchant(enchant, Math.max(this.enchants.getOrDefault(enchant, 0), enchantListData.getLevel(enchant)));
      }
   }

   @NotNull
   public EnchantListData clone() {
      EnchantListData enchantListData = new EnchantListData();

      for (Enchantment key : this.enchants.keySet()) {
         enchantListData.addEnchant(key, this.enchants.getOrDefault(key, 0));
      }

      return enchantListData;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder("EnchantList{");
      AtomicBoolean atomicBoolean = new AtomicBoolean();
      this.enchants.forEach((var2x, value) -> {
         if (atomicBoolean.get()) {
            builder.append(",");
         }

         builder.append(var2x).append("=").append(value);
         atomicBoolean.set(true);
      });
      builder.append("}");
      return builder.toString();
   }

   @Override
   public boolean isEmpty() {
      for (int count : this.enchants.values()) {
         if (count > 0) {
            return false;
         }
      }

      return true;
   }

   public void identifyTrueOriginalEnchantments(@NotNull MMOItem mmoItem) {
      if (mmoItem.hasData(ItemStats.DISABLE_ENCHANTING) && mmoItem.hasData(ItemStats.DISABLE_REPAIRING)) {
         this.clear();
      } else {
         EnchantListData data = (EnchantListData)mmoItem.computeData(ItemStats.ENCHANTS);
         if (data.getEnchants().size() == 0) {
            mmoItem.mergeData(ItemStats.ENCHANTS, this, null);
         } else {
            EnchantListData enchantListData = new EnchantListData();
            mmoItem.hasData(ItemStats.MATERIAL);
            MaterialData materialData = (MaterialData)mmoItem.getData(ItemStats.MATERIAL);
            Material material = materialData.getMaterial();

            for (Enchantment enchant : this.getEnchants()) {
               int level = this.getLevel(enchant);
               int otherLevel = data.getLevel(enchant);
               if (otherLevel <= enchant.getMaxLevel() && enchant.getItemTarget().includes(material) && otherLevel < level) {
                  enchantListData.addEnchant(enchant, level);
               }
            }

            if (!enchantListData.isEmpty()) {
               mmoItem.mergeData(ItemStats.ENCHANTS, enchantListData, null);
            }
         }
      }
   }
}
