package me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Title {
   public Title() {
   }

   public static void sendPacket(Player player, Object obj) {
      try {
         Object handle = player.getClass().getMethod("getHandle").invoke(player);
         Object connection = handle.getClass().getField("playerConnection").get(handle);
         connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, obj);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static Class<?> getNMSClass(String text) {
      String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

      try {
         return Class.forName("net.minecraft.server." + version + "." + text);
      } catch (ClassNotFoundException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static void sendTitle(Player player, Integer value, Integer stay, Integer fadeOut, String text) {
      sendTitle(player, value, stay, fadeOut, text, null);
   }

   public static void sendSubtitle(Player player, Integer value, Integer stay, Integer fadeOut, String text) {
      sendTitle(player, value, stay, fadeOut, null, text);
   }

   public static void sendFullTitle(Player player, Integer value, Integer stay, Integer fadeOut, String text, String subtitle) {
      sendTitle(player, value, stay, fadeOut, text, subtitle);
   }

   public static void sendTitle(Player player, Integer value, Integer stay, Integer fadeOut, String text, String subtitle) {
      try {
         Method declaredMethod = player.getClass().getDeclaredMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
         if (declaredMethod != null) {
            declaredMethod.invoke(player, text, subtitle, value, stay, fadeOut);
            return;
         }
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
      }

      try {
         if (text != null) {
            String colored = ChatColor.translateAlternateColorCodes('&', text);
            text = colored.replaceAll("%player%", player.getDisplayName());
            Object obj = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
            Object component = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
            Constructor constructor = getNMSClass("PacketPlayOutTitle")
               .getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object instance = constructor.newInstance(obj, component, value, stay, fadeOut);
            sendPacket(player, instance);
            obj = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            component = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
            constructor = getNMSClass("PacketPlayOutTitle")
               .getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
            instance = constructor.newInstance(obj, component);
            sendPacket(player, instance);
         }

         if (subtitle != null) {
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            Object obj = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
            Object component = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
            Constructor constructor = getNMSClass("PacketPlayOutTitle")
               .getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object instance = constructor.newInstance(obj, component, value, stay, fadeOut);
            sendPacket(player, instance);
            obj = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            component = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
            constructor = getNMSClass("PacketPlayOutTitle")
               .getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            instance = constructor.newInstance(obj, component, value, stay, fadeOut);
            sendPacket(player, instance);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static void clearTitle(Player player) {
      sendTitle(player, 0, 1, 0, "", "");
   }

   public static void sendTabTitle(Player player, String text, String subtitle) {
      if (text == null) {
         text = "";
      }

      text = ChatColor.translateAlternateColorCodes('&', text);
      if (subtitle == null) {
         subtitle = "";
      }

      subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
      text = text.replaceAll("%player%", player.getDisplayName());
      subtitle = subtitle.replaceAll("%player%", player.getDisplayName());

      try {
         Object obj = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
         Object component = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
         Constructor constructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor();
         Object instance = constructor.newInstance();
         Field declaredField = instance.getClass().getDeclaredField("a");
         declaredField.setAccessible(true);
         declaredField.set(instance, obj);
         Field footerField = instance.getClass().getDeclaredField("b");
         footerField.setAccessible(true);
         footerField.set(instance, component);
         sendPacket(player, instance);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
