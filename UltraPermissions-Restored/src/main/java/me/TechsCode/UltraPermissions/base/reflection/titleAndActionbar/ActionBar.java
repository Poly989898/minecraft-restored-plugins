package me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.visual.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {
   private static SpigotTechPlugin plugin;
   private static String nmsver;
   private static boolean useOldMethods;

   public ActionBar(SpigotTechPlugin spigotTechPlugin) {
      plugin = spigotTechPlugin;
      if (nmsver == null) {
         nmsver = Bukkit.getServer().getClass().getPackage().getName();
         nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
         if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.startsWith("v1_7_")) {
            useOldMethods = true;
         }
      }
   }

   public void sendActionBar(Player player, String text) {
      this.sendActionBarNoColorReplacement(player, Text.color(text));
   }

   public void sendActionBarNoColorReplacement(Player player, String text) {
      try {
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
      } catch (NoSuchMethodError exception) {
         try {
            Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
         } catch (ClassNotFoundException classException) {
            return;
         }

         try {
            Class type = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object obj = type.cast(player);
            Class packetClass = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class basePacketClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Object packet;
            if (useOldMethods) {
               Class serializerClass = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
               Class componentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
               Method declaredMethod = serializerClass.getDeclaredMethod("a", String.class);
               Object component = componentClass.cast(declaredMethod.invoke(serializerClass, "{\"text\": \"" + text + "\"}"));
               packet = packetClass.getConstructor(componentClass, byte.class).newInstance(component, (byte)2);
            } else {
               Class componentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
               Class componentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");

               try {
                  Class messageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
                  Object[] enumConstants = messageTypeClass.getEnumConstants();
                  Object gameInfoType = null;

                  for (Object enumConstant : enumConstants) {
                     if (enumConstant.toString().equals("GAME_INFO")) {
                        gameInfoType = enumConstant;
                     }
                  }

                  Object instance = componentTextClass.getConstructor(String.class).newInstance(text);
                  packet = packetClass.getConstructor(componentClass, messageTypeClass).newInstance(instance, gameInfoType);
               } catch (ClassNotFoundException legacyException) {
                  Object instance = componentTextClass.getConstructor(String.class).newInstance(text);
                  packet = packetClass.getConstructor(componentClass, byte.class).newInstance(instance, (byte)2);
               }
            }

            Method declaredMethod = type.getDeclaredMethod("getHandle");
            Object handle = declaredMethod.invoke(obj);
            Field declaredField = handle.getClass().getDeclaredField("playerConnection");
            Object connection = declaredField.get(handle);
            Method sendPacketMethod = connection.getClass().getDeclaredMethod("sendPacket", basePacketClass);
            sendPacketMethod.invoke(connection, packet);
         } catch (Exception packetException) {
            exception.printStackTrace();
         }
      }
   }

   public void sendActionBar(final Player player, final String text, int count) {
      this.sendActionBar(player, text);
      if (count >= 0) {
         (new BukkitRunnable() {
            public void run() {
               ActionBar.this.sendActionBar(player, "");
            }
         }).runTaskLater((Plugin)plugin.getBootstrap(), count + 1);
      }

      while (count > 40) {
         count -= 40;
         (new BukkitRunnable() {
            public void run() {
               ActionBar.this.sendActionBar(player, text);
            }
         }).runTaskLater((Plugin)plugin.getBootstrap(), count);
      }
   }

   public void sendActionBarToAllPlayers(String text) {
      this.sendActionBarToAllPlayers(text, -1);
   }

   public void sendActionBarToAllPlayers(String text, int count) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
         this.sendActionBar(onlinePlayer, text, count);
      }
   }
}
