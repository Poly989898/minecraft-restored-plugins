package io.lumine.mythic.bukkit.utils.menu;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CosmeticIcon<T> {
   public static <T> Icon<T> create(Material material) {
      return IconBuilder.<T>create().material(material).name("" + ChatColor.GOLD + ChatColor.BOLD + " ").build();
   }

   public static <T> Map<Integer, Icon<T>> createMap(Material material, int... keys) {
      Map<Integer, Icon<T>> list = Maps.newHashMap();

      for (int i : keys) {
         list.put(i, create(material));
      }

      return list;
   }

   private CosmeticIcon() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
