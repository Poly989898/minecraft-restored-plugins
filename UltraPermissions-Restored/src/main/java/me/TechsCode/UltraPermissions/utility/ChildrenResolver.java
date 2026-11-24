package me.TechsCode.UltraPermissions.utility;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChildrenResolver {
   private static final HashMap<String, Set<String>> cache = new HashMap<>();
   private static final boolean isSpigot = new File("server.properties").exists() && new File("bukkit.yml").exists();

   public ChildrenResolver() {
   }

   public static Set<String> retrieveChildPermissions(String text) {
      if (cache.containsKey(text)) {
         return cache.get(text);
      }

      HashSet set = !isSpigot ? new HashSet() : BukkitChildrenResolver.getInstance().retrieveChildPermissions(text);
      set.add(text);
      cache.put(text, set);
      return set;
   }
}
