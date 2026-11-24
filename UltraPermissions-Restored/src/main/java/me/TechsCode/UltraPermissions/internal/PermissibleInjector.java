package me.TechsCode.UltraPermissions.internal;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

public class PermissibleInjector {
   private static final String CRAFTBUKKIT_PREFIX = "org.bukkit.craftbukkit";
   private static final String VERSION;
   protected static final String className;
   protected static final String fieldName;

   public PermissibleInjector() {
   }

   public static String getCBClassName(String text) {
      return VERSION == null ? null : "org.bukkit.craftbukkit" + VERSION + text;
   }

   public static void inject(Player player, PermissibleBase permissibleBase) throws IllegalAccessException {
      Field field = null;

      try {
         field = getPermissibleField(player);
      } catch (Exception exception) {
      }

      if (field != null) {
         field.set(player, permissibleBase);
      }
   }

   private static Field getPermissibleField(Player player) throws Exception {
      Class type;
      try {
         type = Class.forName(className);
      } catch (ClassNotFoundException exception) {
         throw new Exception("Invalid Server Jar");
      }

      if (!type.isAssignableFrom(player.getClass())) {
         System.out.println("Failed to inject permissions for " + player.getName());
         return null;
      } else {
         Field declaredField = type.getDeclaredField(fieldName);
         declaredField.setAccessible(true);
         return declaredField;
      }
   }

   static {
      Class clazz = Bukkit.getServer().getClass();
      if (!clazz.getSimpleName().equals("CraftServer")) {
         VERSION = null;
      } else if (clazz.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
         VERSION = ".";
      } else {
         String name = clazz.getName();
         name = name.substring("org.bukkit.craftbukkit".length());
         name = name.substring(0, name.length() - "CraftServer".length());
         VERSION = name;
      }

      className = getCBClassName("entity.CraftHumanEntity");
      fieldName = "perm";
   }
}
