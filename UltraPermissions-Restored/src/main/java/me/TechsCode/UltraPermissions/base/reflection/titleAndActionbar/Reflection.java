package me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import org.bukkit.Bukkit;

public class Reflection {
   public Reflection() {
   }

   public static Class<?> getNMSClass(String text) {
      return MinecraftVersion.getServersVersion().isBelow(MinecraftVersion.V1_20_R4)
         ? getClass("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + "." + text)
         : getClass(Bukkit.getServer().getClass().getPackage().getName() + "." + text);
   }

   public static Class<?> getCraftClass(String text) {
      return MinecraftVersion.getServersVersion().isBelow(MinecraftVersion.V1_20_R4)
         ? getClass("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + "." + text)
         : getClass(Bukkit.getServer().getClass().getPackage().getName() + "." + text);
   }

   public static Class<?> getClass(String text) {
      try {
         return Class.forName(text);
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static Object getValue(Object obj, String text) {
      try {
         Field declaredField = obj.getClass().getDeclaredField(text);
         if (!declaredField.isAccessible()) {
            declaredField.setAccessible(true);
         }

         return declaredField.get(obj);
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static Method getMethod(Object obj, String text, Class<?>... types) {
      try {
         Method method = obj.getClass().getMethod(text, types);
         if (!method.isAccessible()) {
            method.setAccessible(true);
         }

         return method;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static Field getField(Field field) {
      field.setAccessible(true);
      return field;
   }
}
