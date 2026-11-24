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

   public int getLevel(@NotNull Enchantment var1) {
      Integer var2 = this.enchants.get(var1);
      return var2 == null ? 0 : var2;
   }

   public void addEnchant(Enchantment var1, int var2) {
      if (var2 <= 0) {
         this.enchants.remove(var1);
      } else {
         this.enchants.put(var1, var2);
      }
   }

   public void clear() {
      this.enchants.clear();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof EnchantListData)) {
         return false;
      }

      if (((EnchantListData)var1).enchants.size() != this.enchants.size()) {
         return false;
      }

      for (Enchantment var3 : this.getEnchants()) {
         if (this.getLevel(var3) != ((EnchantListData)var1).getLevel(var3)) {
            return false;
         }
      }

      return true;
   }

   public void mergeWith(@NotNull EnchantListData var1) {
      for (Enchantment var3 : var1.getEnchants()) {
         this.addEnchant(var3, Math.max(this.enchants.getOrDefault(var3, 0), var1.getLevel(var3)));
      }
   }

   @NotNull
   public EnchantListData clone() {
      EnchantListData var1 = new EnchantListData();

      for (Enchantment var3 : this.enchants.keySet()) {
         var1.addEnchant(var3, this.enchants.getOrDefault(var3, 0));
      }

      return var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("EnchantList{");
      AtomicBoolean var2 = new AtomicBoolean();
      this.enchants.forEach((var2x, var3) -> {
         if (var2.get()) {
            var1.append(",");
         }

         var1.append(var2x).append("=").append(var3);
         var2.set(true);
      });
      var1.append("}");
      return var1.toString();
   }

   @Override
   public boolean isEmpty() {
      for (int var2 : this.enchants.values()) {
         if (var2 > 0) {
            return false;
         }
      }

      return true;
   }

   public void identifyTrueOriginalEnchantments(@NotNull MMOItem var1) {
      if (var1.hasData(ItemStats.DISABLE_ENCHANTING) && var1.hasData(ItemStats.DISABLE_REPAIRING)) {
         this.clear();
      } else {
         EnchantListData var2 = (EnchantListData)var1.computeData(ItemStats.ENCHANTS);
         if (var2.getEnchants().size() == 0) {
            var1.mergeData(ItemStats.ENCHANTS, this, null);
         } else {
            EnchantListData var3 = new EnchantListData();
            var1.hasData(ItemStats.MATERIAL);
            MaterialData var4 = (MaterialData)var1.getData(ItemStats.MATERIAL);
            Material var5 = var4.getMaterial();

            for (Enchantment var7 : this.getEnchants()) {
               int var8 = this.getLevel(var7);
               int var9 = var2.getLevel(var7);
               if (var9 <= var7.getMaxLevel() && var7.getItemTarget().includes(var5) && var9 < var8) {
                  var3.addEnchant(var7, var8);
               }
            }

            if (!var3.isEmpty()) {
               var1.mergeData(ItemStats.ENCHANTS, var3, null);
            }
         }
      }
   }
}
