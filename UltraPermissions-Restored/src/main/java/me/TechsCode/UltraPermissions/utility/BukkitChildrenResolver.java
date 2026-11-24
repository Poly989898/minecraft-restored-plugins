package me.TechsCode.UltraPermissions.utility;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

public class BukkitChildrenResolver {
   private static BukkitChildrenResolver i;
   private Set<Permission> permissions = Bukkit.getPluginManager().getPermissions();

   public static BukkitChildrenResolver getInstance() {
      if (i == null) {
         i = new BukkitChildrenResolver();
      }

      return i;
   }

   public BukkitChildrenResolver() {
   }

   public HashSet<String> retrieveChildPermissions(String text) {
      Permission permission = Bukkit.getServer().getPluginManager().getPermission(text);
      if (permission == null) {
         return new HashSet<>();
      }

      HashSet set = new HashSet();
      permission.getChildren().forEach((childName, flag) -> {
         if (flag != null && flag) {
            set.add(childName);
            set.addAll(this.retrieveChildPermissions(childName));
         }
      });
      return set;
   }
}
