package net.Indyuce.mmoitems.comp.enchants.advanced_enchants;

import java.util.HashMap;
import java.util.Map;
import net.Indyuce.mmoitems.stat.data.type.StatData;

public class AdvancedEnchantMap implements StatData {
   public final Map<String, Integer> enchants = new HashMap<>();

   @Override
   public boolean isEmpty() {
      return this.enchants.isEmpty();
   }
}
