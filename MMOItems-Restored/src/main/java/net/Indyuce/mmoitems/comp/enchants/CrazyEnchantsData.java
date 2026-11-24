package net.Indyuce.mmoitems.comp.enchants;

import java.util.Map;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import net.Indyuce.mmoitems.stat.data.type.StatData;

public class CrazyEnchantsData implements StatData {
   private final Map<CEnchantment, Integer> enchants;

   public CrazyEnchantsData(Map<CEnchantment, Integer> map) {
      this.enchants = map;
   }

   public Map<CEnchantment, Integer> getEnchants() {
      return this.enchants;
   }

   @Override
   public boolean isEmpty() {
      return this.enchants.isEmpty();
   }
}
