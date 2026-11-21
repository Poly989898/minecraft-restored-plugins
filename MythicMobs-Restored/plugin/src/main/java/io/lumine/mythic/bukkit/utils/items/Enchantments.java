package io.lumine.mythic.bukkit.utils.items;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Enchantments {
   private static Map<String, Enchantment> enchantments = Maps.newConcurrentMap();

   public static int getEnchantmentLevel(Player player, Enchantment enchant) {
      return 0;
   }
}
