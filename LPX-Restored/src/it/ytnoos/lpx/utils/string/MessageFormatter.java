package it.ytnoos.lpx.utils.string;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageFormatter {
   private static final MiniMessage miniMessage = MiniMessage.miniMessage();
   private static final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
   private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacySection();
   private static final PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
   private static final Map<Character, String> LEGACY_COLORS = createLegacyMap();

   private MessageFormatter() {
   }

   public static Component deserialize(String string) {
      return miniMessage.deserialize(convertLegacyColors(string == null ? "" : string));
   }

   public static String escapeTags(String string) {
      return miniMessage.escapeTags(string == null ? "" : string);
   }

   public static BaseComponent[] toBungee(String string) {
      return ComponentSerializer.parse((String)gsonComponentSerializer.serialize(deserialize(string)));
   }

   public static String toPlain(String string) {
      return plainTextComponentSerializer.serialize(deserialize(string));
   }

   public static String toLegacy(String string) {
      return legacyComponentSerializer.serialize(deserialize(string));
   }

   public static void send(CommandSender commandSender, String string) {
      if (commandSender instanceof Player player) {
         send(player, string);
      } else {
         commandSender.sendMessage(toPlain(string));
      }
   }

   public static void send(Player player, String string) {
      player.spigot().sendMessage(toBungee(string));
   }

   static String convertLegacyColors(String string) {
      StringBuilder stringBuilder = new StringBuilder(string.length());

      for (int value = 0; value < string.length(); value++) {
         char ch = string.charAt(value);
         if ((ch == 167 || ch == '&') && value + 1 < string.length()) {
            char ch2 = Character.toLowerCase(string.charAt(value + 1));
            String string2 = LEGACY_COLORS.get(ch2);
            if (string2 != null) {
               stringBuilder.append(string2);
               value++;
               continue;
            }
         }

         stringBuilder.append(ch);
      }

      return stringBuilder.toString();
   }

   private static Map createLegacyMap() {
      HashMap hashMap = new HashMap();
      hashMap.put('0', "<black>");
      hashMap.put('1', "<dark_blue>");
      hashMap.put('2', "<dark_green>");
      hashMap.put('3', "<dark_aqua>");
      hashMap.put('4', "<dark_red>");
      hashMap.put('5', "<dark_purple>");
      hashMap.put('6', "<gold>");
      hashMap.put('7', "<gray>");
      hashMap.put('8', "<dark_gray>");
      hashMap.put('9', "<blue>");
      hashMap.put('a', "<green>");
      hashMap.put('b', "<aqua>");
      hashMap.put('c', "<red>");
      hashMap.put('d', "<light_purple>");
      hashMap.put('e', "<yellow>");
      hashMap.put('f', "<white>");
      hashMap.put('k', "<obfuscated>");
      hashMap.put('l', "<bold>");
      hashMap.put('m', "<strikethrough>");
      hashMap.put('n', "<underlined>");
      hashMap.put('o', "<italic>");
      hashMap.put('r', "<reset>");
      return hashMap;
   }

}
