package io.lumine.mythic.bukkit.utils.text;

import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public final class Text {
   private static final Pattern URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
   public static final char SECTION_CHAR = '\u00a7';
   public static final char AMPERSAND_CHAR = '&';
   public static final int CENTER_PX = 154;
   public static final int CHAT_WIDTH = 300;
   public static final int INVENTORY_TITLE_WIDTH = 156;
   public static final int LORE_WIDTH = 170;
   public static final int BOOK_WIDTH = 108;
   private static final MiniMessage miniMessage = MiniMessage.builder().strict(false).tags(StandardTags.defaults()).build();
   private static final LegacyComponentSerializer unusualSectionRGB = LegacyComponentSerializer.builder()
      .character('\u00a7')
      .hexCharacter('#')
      .hexColors()
      .useUnusualXRepeatedCharacterHexFormat()
      .build();
   private static final int PARSE_CACHE_MAX = 2048;
   private static final ConcurrentHashMap<String, Component> PARSE_CACHE = new ConcurrentHashMap<>();

   @NotNull
   public static Component parse(String text) {
      Component cached = PARSE_CACHE.get(text);
      if (cached != null) {
         return cached;
      } else {
         String processed = text;
         int i = 0;

         for (int n = text.length(); i < n; i++) {
            char c = processed.charAt(i);
            if (c == '&' || c == 167) {
               processed = replaceLegacy(processed);
               break;
            }
         }

         Component component = miniMessage.deserialize(processed);
         if (component.decorations().get(TextDecoration.ITALIC) == State.NOT_SET) {
            component = component.decoration(TextDecoration.ITALIC, State.FALSE);
         }

         Component component2 = component.colorIfAbsent(NamedTextColor.WHITE);
         if (PARSE_CACHE.size() >= 2048) {
            PARSE_CACHE.clear();
         }

         PARSE_CACHE.put(text, component2);
         return component2;
      }
   }

   public static void clearParseCache() {
      PARSE_CACHE.clear();
   }

   @NotNull
   public static String replaceLegacy(@NotNull String text) {
      int len = text.length();
      StringBuilder sb = null;
      int last = 0;

      for (int i = 0; i < len - 1; i++) {
         char c = text.charAt(i);
         if (c == '&' || c == 167) {
            String replacement = legacyReplacement(text.charAt(i + 1));
            if (replacement != null) {
               if (sb == null) {
                  sb = new StringBuilder(len + 16);
               }

               sb.append(text, last, i).append(replacement);
               last = ++i + 1;
            }
         }
      }

      return sb == null ? text : sb.append(text, last, len).toString();
   }

   private static String legacyReplacement(char c) {
      return switch (c) {
         case '0' -> "<black>";
         case '1' -> "<dark_blue>";
         case '2' -> "<dark_green>";
         case '3' -> "<dark_aqua>";
         case '4' -> "<dark_red>";
         case '5' -> "<dark_purple>";
         case '6' -> "<gold>";
         case '7' -> "<gray>";
         case '8' -> "<dark_gray>";
         case '9' -> "<blue>";
         default -> null;
         case 'A', 'a' -> "<green>";
         case 'B', 'b' -> "<aqua>";
         case 'C', 'c' -> "<red>";
         case 'D', 'd' -> "<light_purple>";
         case 'E', 'e' -> "<yellow>";
         case 'F', 'f' -> "<white>";
         case 'K', 'k' -> "<obf>";
         case 'L', 'l' -> "<b>";
         case 'M', 'm' -> "<st>";
         case 'N', 'n' -> "<u>";
         case 'O', 'o' -> "<i>";
         case 'R', 'r' -> "<reset>";
      };
   }

   public static String joinNewline(String... strings) {
      return joinNewline(Arrays.stream(strings));
   }

   public static String joinNewline(Stream<String> strings) {
      return strings.collect(Collectors.joining("\n"));
   }

   public static Component fromLegacy(String input, char character) {
      return LegacyComponentSerializer.legacy(character).deserialize(input);
   }

   public static Component fromLegacy(String input) {
      return LegacyComponentSerializer.legacySection().deserialize(input);
   }

   public static String toLegacy(Component component, char character) {
      return LegacyComponentSerializer.legacy(character).serialize(component);
   }

   public static String toLegacy(Component component) {
      return LegacyComponentSerializer.legacySection().serialize(component);
   }

   public static Component fromBungee(BaseComponent[] component) {
      return ServerVersion.isAfter(MinecraftVersions.v1_15)
         ? BungeeComponentSerializer.get().deserialize(component)
         : BungeeComponentSerializer.legacy().deserialize(component);
   }

   public static BaseComponent[] toBungee(Component component) {
      return ServerVersion.isAfter(MinecraftVersions.v1_15)
         ? BungeeComponentSerializer.get().serialize(component)
         : BungeeComponentSerializer.legacy().serialize(component);
   }

   public static Component fromJson(String input) {
      return GsonComponentSerializer.gson().deserialize(input);
   }

   public static String toJson(Component component) {
      return (String)GsonComponentSerializer.gson().serialize(component);
   }

   public static String toReadable(Component component) {
      return (String)miniMessage.serialize(component);
   }

   public static void sendMessage(CommandSender sender, Component message) {
      if (sender instanceof ConsoleCommandSender) {
         sender.sendMessage(toLegacy(message));
      } else {
         Audience.audience(new Audience[]{sender}).sendMessage(message);
      }
   }

   public static void sendMessage(CommandSender sender, String message) {
      sendMessage(sender, parse(message));
   }

   public static void sendMessage(CommandSender sender, String... message) {
      Component out = Component.empty();

      for (String m : message) {
         out.append(parse(m));
      }

      sendMessage(sender, out);
   }

   public static void sendMessage(CommandSender sender, Component... message) {
      sendMessage(sender, Component.textOfChildren(message));
   }

   public static void sendMessage(Iterable<CommandSender> senders, Component... message) {
      sendMessage(senders, Component.textOfChildren(message));
   }

   public static void sendMessage(Iterable<CommandSender> senders, Component message) {
      senders.forEach(sender -> sendMessage(sender, message));
   }

   public static void sendActionBar(CommandSender sender, String message) {
      sendActionBar(sender, parse(message));
   }

   public static void sendActionBar(CommandSender sender, String... message) {
      Component out = Component.empty();

      for (String m : message) {
         out.append(parse(m));
      }

      sendActionBar(sender, out);
   }

   public static void sendActionBar(CommandSender sender, Component... message) {
      sendActionBar(sender, Component.textOfChildren(message));
   }

   public static void sendActionBar(CommandSender sender, Component message) {
      Audience.audience(new Audience[]{sender}).sendActionBar(message);
   }

   public static void sendActionBar(Iterable<CommandSender> senders, Component... message) {
      sendActionBar(senders, Component.textOfChildren(message));
   }

   public static void sendActionBar(Iterable<CommandSender> senders, Component message) {
      senders.forEach(sender -> Audience.audience(new Audience[]{sender}).sendActionBar(message));
   }

   public static String colorizeLegacy(String s) {
      return toLegacy(parse(s));
   }

   @Deprecated
   public static String colorize(String s) {
      return s == null ? null : translateAlternateColorCodes('&', '\u00a7', s);
   }

   @Deprecated
   public static String decolorize(String s) {
      return s == null ? null : translateAlternateColorCodes('\u00a7', '&', s);
   }

   public static String translateAlternateColorCodes(char from, char to, String textToTranslate) {
      char[] b = textToTranslate.toCharArray();

      for (int i = 0; i < b.length - 1; i++) {
         if (b[i] == from && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
            b[i] = to;
            b[i + 1] = Character.toLowerCase(b[i + 1]);
         }
      }

      return new String(b);
   }

   public static TextComponent extractUrl(TextComponent component) {
      return component;
   }

   public static String center(String message) {
      return getSpacesToCenter(message) + message;
   }

   public static String getSpacesToCenter(String message) {
      message = colorizeLegacy(message);
      int messagePxSize = 0;
      boolean previousCode = false;
      boolean isBold = false;

      for (char c : message.toCharArray()) {
         if (c == 167) {
            previousCode = true;
         } else if (previousCode) {
            previousCode = false;
            isBold = c == 'l' || c == 'L';
         } else {
            Text.DefaultFontInfo dFI = Text.DefaultFontInfo.getDefaultFontInfo(c);
            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
            messagePxSize++;
         }
      }

      int halvedMessageSize = messagePxSize / 2;
      int toCompensate = 154 - halvedMessageSize;
      int spaceLength = Text.DefaultFontInfo.SPACE.getLength() + 1;
      int compensated = 0;

      StringBuilder sb;
      for (sb = new StringBuilder(); compensated < toCompensate; compensated += spaceLength) {
         sb.append(" ");
      }

      return sb.toString();
   }

   public static String getSpacesToEdge(String message, int pixels) {
      message = colorizeLegacy(message);
      int messagePxSize = 0;
      boolean previousCode = false;
      boolean isBold = false;

      for (char c : message.toCharArray()) {
         if (c == 167) {
            previousCode = true;
         } else if (previousCode) {
            previousCode = false;
            isBold = c == 'l' || c == 'L';
         } else {
            Text.DefaultFontInfo dFI = Text.DefaultFontInfo.getDefaultFontInfo(c);
            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
            messagePxSize++;
         }
      }

      int toCompensate = pixels - messagePxSize;
      int spaceLength = Text.DefaultFontInfo.SPACE.getLength() + 1;
      int compensated = 0;

      StringBuilder sb;
      for (sb = new StringBuilder(); compensated < toCompensate; compensated += spaceLength) {
         sb.append(" ");
      }

      return sb.toString();
   }

   private Text() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   public static enum DefaultFontInfo {
      A('A', 5),
      a('a', 5),
      B('B', 5),
      b('b', 5),
      C('C', 5),
      c('c', 5),
      D('D', 5),
      d('d', 5),
      E('E', 5),
      e('e', 5),
      F('F', 5),
      f('f', 4),
      G('G', 5),
      g('g', 5),
      H('H', 5),
      h('h', 5),
      I('I', 3),
      i('i', 1),
      J('J', 5),
      j('j', 5),
      K('K', 5),
      k('k', 4),
      L('L', 5),
      l('l', 1),
      M('M', 5),
      m('m', 5),
      N('N', 5),
      n('n', 5),
      O('O', 5),
      o('o', 5),
      P('P', 5),
      p('p', 5),
      Q('Q', 5),
      q('q', 5),
      R('R', 5),
      r('r', 5),
      S('S', 5),
      s('s', 5),
      T('T', 5),
      t('t', 4),
      U('U', 5),
      u('u', 5),
      V('V', 5),
      v('v', 5),
      W('W', 5),
      w('w', 5),
      X('X', 5),
      x('x', 5),
      Y('Y', 5),
      y('y', 5),
      Z('Z', 5),
      z('z', 5),
      NUM_1('1', 5),
      NUM_2('2', 5),
      NUM_3('3', 5),
      NUM_4('4', 5),
      NUM_5('5', 5),
      NUM_6('6', 5),
      NUM_7('7', 5),
      NUM_8('8', 5),
      NUM_9('9', 5),
      NUM_0('0', 5),
      EXCLAMATION_POINT('!', 1),
      AT_SYMBOL('@', 6),
      NUM_SIGN('#', 5),
      DOLLAR_SIGN('$', 5),
      PERCENT('%', 5),
      UP_ARROW('^', 5),
      AMPERSAND('&', 5),
      ASTERISK('*', 5),
      LEFT_PARENTHESIS('(', 4),
      RIGHT_PERENTHESIS(')', 4),
      MINUS('-', 5),
      UNDERSCORE('_', 5),
      PLUS_SIGN('+', 5),
      EQUALS_SIGN('=', 5),
      LEFT_CURL_BRACE('{', 4),
      RIGHT_CURL_BRACE('}', 4),
      LEFT_BRACKET('[', 3),
      RIGHT_BRACKET(']', 3),
      COLON(':', 1),
      SEMI_COLON(';', 1),
      DOUBLE_QUOTE('"', 3),
      SINGLE_QUOTE('\'', 1),
      LEFT_ARROW('<', 4),
      RIGHT_ARROW('>', 4),
      QUESTION_MARK('?', 5),
      SLASH('/', 5),
      BACK_SLASH('\\', 5),
      LINE('|', 1),
      TILDE('~', 5),
      TICK('`', 2),
      PERIOD('.', 1),
      COMMA(',', 1),
      SPACE(' ', 3),
      DEFAULT('a', 4);

      private final char character;
      private final int length;

      private DefaultFontInfo(char character, int length) {
         this.character = character;
         this.length = length;
      }

      public static Text.DefaultFontInfo getDefaultFontInfo(char c) {
         for (Text.DefaultFontInfo dFI : values()) {
            if (dFI.getCharacter() == c) {
               return dFI;
            }
         }

         return DEFAULT;
      }

      public char getCharacter() {
         return this.character;
      }

      public int getLength() {
         return this.length;
      }

      public int getBoldLength() {
         return this == SPACE ? this.getLength() : this.length + 1;
      }
   }
}
