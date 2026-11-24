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

   public RandomEnchantListData(ConfigurationSection section) {
      Validate.notNull(section, "Config cannot be null");

      for (String key : section.getKeys(false)) {
         Enchantment enchant = Enchants.getEnchant(key);
         Validate.notNull(enchant, "Could not find enchant with key '" + key + "'");
         this.addEnchant(enchant, new NumericStatFormula(section.get(key)));
      }
   }

   public Set<Enchantment> getEnchants() {
      return this.enchants.keySet();
   }

   public NumericStatFormula getLevel(Enchantment enchantment) {
      return this.enchants.get(enchantment);
   }

   public void addEnchant(Enchantment enchantment, NumericStatFormula numericStatFormula) {
      this.enchants.put(enchantment, numericStatFormula);
   }

   public EnchantListData randomize(MMOItemBuilder mmoItemBuilder) {
      EnchantListData enchantListData = new EnchantListData();
      this.enchants.forEach((var2x, numericStatFormula) -> enchantListData.addEnchant(var2x, (int)Math.max(numericStatFormula.calculate(mmoItemBuilder.getLevel()), var2x.getStartLevel())));
      return enchantListData;
   }
}
