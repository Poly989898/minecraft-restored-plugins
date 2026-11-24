package me.TechsCode.UltraPermissions.base.legacy.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class Armorstands {
   private static final Method[] methods = ((Supplier<Method[]>)() -> {
         try {
            Method declaredMethod = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
            return new Method[]{
               declaredMethod, declaredMethod.getReturnType().getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
            };
         } catch (Exception exception) {
            return null;
         }
      })
      .get();

   public Armorstands() {
   }

   public static void move(ArmorStand armorStand, Location location) {
      try {
         methods[1].invoke(methods[0].invoke(armorStand), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      } catch (IllegalAccessException | InvocationTargetException exception) {
         exception.printStackTrace();
      }
   }
}
