package net.Indyuce.mmoitems.stat.data.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public class RandomEnchantListData implements RandomStatData<EnchantListData> {
   private final Map<Enchantment, NumericStatFormula> enchants = new HashMap<>();

   public RandomEnchantListData(ConfigurationSection var1) {
      Validate.notNull(var1, "Config cannot be null");

      for (String var3 : var1.getKeys(false)) {
         Enchantment var4 = Enchants.getEnchant(var3);
         Validate.notNull(var4, "Could not find enchant with key '" + var3 + "'");
         this.addEnchant(var4, new NumericStatFormula(var1.get(var3)));
      }
   }

   public Set<Enchantment> getEnchants() {
      return this.enchants.keySet();
   }

   public NumericStatFormula getLevel(Enchantment var1) {
      return this.enchants.get(var1);
   }

   public void addEnchant(Enchantment var1, NumericStatFormula var2) {
      this.enchants.put(var1, var2);
   }

   public EnchantListData randomize(MMOItemBuilder var1) {
      EnchantListData var2 = new EnchantListData();
      this.enchants.forEach((var2x, var3) -> var2.addEnchant(var2x, (int)Math.max(var3.calculate(var1.getLevel()), var2x.getStartLevel())));
      return var2;
   }
}
