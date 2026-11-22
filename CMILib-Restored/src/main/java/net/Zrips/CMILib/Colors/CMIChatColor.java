package net.Zrips.CMILib.Colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILibConfig;
import net.Zrips.CMILib.Container.CMICachedLinkedMap;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public class CMIChatColor {
  private static final Map<String, CMIChatColor> BY_CHAR = new HashMap<>();
  private static final Map<String, CMIChatColor> BY_NAME = new HashMap<>();
  private static final LinkedHashMap<String, CMIChatColor> CUSTOM_BY_NAME = new LinkedHashMap<>();
  private static final TreeMap<String, CMIChatColor> CUSTOM_BY_HEX =
      new TreeMap<String, CMIChatColor>() {
        public CMIChatColor put(String key, CMIChatColor value) {
          if (this.size() >= 3000 && !this.containsKey(key)) {
            this.pollFirstEntry();
          }

          return super.put(key, value);
        }
      };
  private static final TreeMap<String, CMIChatColor> CUSTOM_BY_RGB =
      new TreeMap<String, CMIChatColor>() {
        public CMIChatColor put(String key, CMIChatColor value) {
          if (this.size() >= 5000 && !this.containsKey(key)) {
            this.pollFirstEntry();
          }

          return super.put(key, value);
        }
      };
  private static CMICachedLinkedMap<String, String> baseCacheByText = new CMICachedLinkedMap<>(100);
  private static CMICachedLinkedMap<String, String> baseCMICacheByText =
      new CMICachedLinkedMap<>(100);
  public static final String colorReplacerPlaceholder = "＆";
  public static String globalColorPrefix;
  public static final String hexSymbol = "#";
  public static final String colorHexReplacerPlaceholder = "{＆#";
  public static final String colorFontPrefix = "{@";
  public static final String colorCodePrefix = "{#";
  public static final String colorCodeSuffix = "}";
  public static final String gradientStart = ">";
  public static final String gradientEnd = "<";
  public static final String gradientMiddle = "<>";
  public static final String hexColorRegex = "(\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})";
  public static final Pattern cleanOfficialColorRegexPattern;
  public static final Pattern cleanQuirkyHexColorRegexPattern;
  public static final Pattern hexColorRegexPattern;
  public static final Pattern hexColorRegexPatternLast;
  public static final Pattern hexDeColorNamePattern;
  public static final String ColorNameRegex = "(\\{#)([a-zA-Z_]{3,})(\\})";
  public static final Pattern hexColorNamePattern;
  public static final Pattern hexColorNamePatternLast;
  public static final String ColorFontRegex = "(\\{@)([a-zA-Z_]{2,})(\\})";
  public static final Pattern gradientPattern;
  public static final String hexColorDecolRegex = "(&x)(&[0-9A-Fa-f]){6}";
  public static final Pattern postGradientPattern;
  public static final Pattern post2GradientPattern;
  public static final Pattern fullPattern;
  public static final Pattern formatPattern;
  public static final CMIChatColor BLACK;
  public static final CMIChatColor DARK_BLUE;
  public static final CMIChatColor DARK_GREEN;
  public static final CMIChatColor DARK_AQUA;
  public static final CMIChatColor DARK_RED;
  public static final CMIChatColor DARK_PURPLE;
  public static final CMIChatColor GOLD;
  public static final CMIChatColor GRAY;
  public static final CMIChatColor DARK_GRAY;
  public static final CMIChatColor BLUE;
  public static final CMIChatColor GREEN;
  public static final CMIChatColor AQUA;
  public static final CMIChatColor RED;
  public static final CMIChatColor LIGHT_PURPLE;
  public static final CMIChatColor YELLOW;
  public static final CMIChatColor WHITE;
  public static final CMIChatColor OBFUSCATED;
  public static final CMIChatColor BOLD;
  public static final CMIChatColor STRIKETHROUGH;
  public static final CMIChatColor UNDERLINE;
  public static final CMIChatColor ITALIC;
  public static final CMIChatColor RESET;
  public static final CMIChatColor HEX;
  private char c = '\n';
  private boolean color = true;
  private boolean isReset = false;
  private Pattern pattern = null;
  private int redChannel = -1;
  private int greenChannel = -1;
  private int blueChannel = -1;
  private int alpha = 255;
  private String hexCode = null;
  private String name;

  public CMIChatColor(String hex) {
    this(null, hex);
  }

  private static String expandHex(String hex) {
    if (hex == null) {
      return null;
    }

    hex = hex.startsWith("#") ? hex.substring(1) : hex;
    switch (hex.length()) {
      case 3:
        return ""
            + hex.charAt(0)
            + hex.substring(0, 1)
            + hex.charAt(1)
            + hex.substring(1, 2)
            + hex.charAt(2)
            + hex.substring(2, 3);
      case 4:
        return ""
            + hex.charAt(0)
            + hex.substring(0, 1)
            + hex.charAt(1)
            + hex.substring(1, 2)
            + hex.charAt(2)
            + hex.substring(2, 3)
            + hex.charAt(3)
            + hex.substring(3, 4);
      default:
        return hex;
    }
  }

  public CMIChatColor(String name, String hex) {
    if (hex != null) {
      if (hex.startsWith("{#")) {
        hex = hex.substring("{#".length());
      }

      if (hex.endsWith("}")) {
        hex = hex.substring(0, hex.length() - "}".length());
      }

      if (hex.startsWith("#")) {
        hex = hex.substring(1);
      }

      hex = expandHex(hex);
      if (hex.length() == 6) {
        this.hexCode = hex;
      }

      String alphaString = null;
      if (hex.length() == 8) {
        this.hexCode = hex.substring(0, 6);
        alphaString = hex.substring(6, 8);
      }

      this.name = name;

      try {
        if (this.hexCode != null) {
          this.redChannel = Integer.valueOf(this.hexCode.substring(0, 2), 16);
          this.greenChannel = Integer.valueOf(this.hexCode.substring(2, 4), 16);
          this.blueChannel = Integer.parseInt(this.hexCode.substring(4, 6), 16);
        }

        if (alphaString != null) {
          this.alpha = Integer.valueOf(alphaString, 16);
        }
      } catch (Throwable e) {
        this.redChannel = -1;
        this.greenChannel = -1;
        this.blueChannel = -1;
        this.hexCode = null;
        this.alpha = 255;
      }
    }
  }

  public CMIChatColor(Color color) {
    this(
        color.getRed(),
        color.getGreen(),
        color.getBlue(),
        Version.isCurrentEqualOrHigher(Version.v1_20_R1) ? color.getAlpha() : 255);
  }

  public CMIChatColor(int red, int green, int blue) {
    this(red, green, blue, 255);
  }

  public CMIChatColor(int red, int green, int blue, int alpha) {
    this.redChannel = red;
    this.greenChannel = green;
    this.blueChannel = blue;
    this.alpha = alpha;
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1) && alpha < 255) {
      this.hexCode =
          String.format(
              "%02X%02X%02X%02X", alpha, this.redChannel, this.greenChannel, this.blueChannel);
    } else {
      this.hexCode =
          String.format("%02X%02X%02X", this.redChannel, this.greenChannel, this.blueChannel);
    }
  }

  public CMIChatColor(String name, char c, Boolean color) {
    this(name, c, color, false);
  }

  public CMIChatColor(String name, char c, Boolean color, Boolean reset) {
    this(name, c, color, reset, -1, -1, -1);
  }

  public CMIChatColor(
      String name, char c, Boolean color, Boolean reset, int red, int green, int blue) {
    this.name = name;
    this.c = c;
    this.color = color;
    this.isReset = reset;
    this.pattern = Pattern.compile("(?i)(&[" + c + "])");
    this.redChannel = red;
    this.greenChannel = green;
    this.blueChannel = blue;
  }

  public CMIChatColor(String name, char c, int red, int green, int blue) {
    this(
        name,
        c,
        red > -1 && green > -1 && blue > -1,
        name.equalsIgnoreCase("Reset"),
        red,
        green,
        blue);
  }

  private CMIChatColor(char c) {
    this(c, c == 'r' ? "Reset" : (c == 'x' ? "Hex" : "Temp"), -1, -1, -1);
  }

  private CMIChatColor(char c, String name) {
    this(c, name, -1, -1, -1);
  }

  private CMIChatColor(char c, String name, int red, int green, int blue) {
    this(
        name,
        c,
        red > -1 && green > -1 && blue > -1,
        name.equalsIgnoreCase("Reset"),
        red,
        green,
        blue);
    if (!Version.isCurrentLower(Version.v1_16_R1) || c != 'x') {
      BY_CHAR.put(String.valueOf(c).toLowerCase(), this);
      BY_NAME.put(this.getName().toLowerCase().replace("_", ""), this);
    }
  }

  public boolean isValid() {
    return this.c != '\n'
        || this.getHex() != null
        || this.name != null
        || this.blueChannel > -1 && this.greenChannel > -1 && this.redChannel > -1;
  }

  public static String processGradient(String text) {
    return GradientColor.processGradient(text);
  }

  public static List<String> translate(List<String> lines) {
    for (int i = 0; i < lines.size(); i++) {
      lines.set(i, translate(lines.get(i)));
    }

    return lines;
  }

  public static void clearCache() {
    GradientColor.clearCache();
    baseCacheByText.clear();
    baseCMICacheByText.clear();
  }

  public static String translate(String text) {
    return translate(text, false);
  }

  public static String updateGlobals(String msg) {
    if (!msg.contains(globalColorPrefix)) {
      return msg;
    }

    for (CMIGlobalColor one : CMIGlobalColor.values()) {
      msg = msg.replace(one.getColorId(), one.getColor());
      if (!msg.contains(globalColorPrefix)) {
        return msg;
      }
    }

    return msg;
  }

  public static String removeGlobals(String msg) {
    if (!msg.contains(globalColorPrefix)) {
      return msg;
    }

    for (CMIGlobalColor one : CMIGlobalColor.values()) {
      msg = msg.replace(one.getColorId(), "");
      if (!msg.contains(globalColorPrefix)) {
        return msg;
      }
    }

    return msg;
  }

  public static String translate(String text, boolean onlyCMIformat) {
    if (text == null) {
      return null;
    }

    if (onlyCMIformat) {
      String cached = baseCMICacheByText.get(text);
      if (cached != null) {
        return cached;
      }
    } else {
      String cached = baseCacheByText.get(text);
      if (cached != null) {
        return cached;
      }
    }

    String ori = text;
    text = updateGlobals(text);
    text = GradientColor.processGradient(text, true);
    if (text.contains("{#")) {
      Matcher match = hexColorRegexPattern.matcher(text);

      while (match.find()) {
        String string = match.group();
        if (Version.isCurrentLower(Version.v1_16_R1)) {
          String copy = string;
          copy = copy.substring(2, copy.length() - 1);
          if (copy.length() == 3) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < copy.length(); i++) {
              sb.append(copy.charAt(i)).append(copy.charAt(i));
            }

            copy = sb.toString();
          }

          copy = getClosestVanilla(copy);
          text = text.replace(string, copy);
        } else {
          StringBuilder magic = new StringBuilder("§x");

          for (char c : string.substring(2, string.length() - 1).toCharArray()) {
            magic.append('§').append(c);
            if (string.substring(2, string.length() - 1).length() == 3) {
              magic.append('§').append(c);
            }
          }

          text = text.replace(string, magic.toString());
        }
      }

      Matcher nameMatch = hexColorNamePattern.matcher(text);

      while (nameMatch.find()) {
        String string = nameMatch.group(2);
        CMIChatColor cn = getByCustomName(string.toLowerCase().replace("_", ""));
        if (cn != null) {
          String gex = cn.getHex();
          StringBuilder magic = new StringBuilder("§x");

          for (char c : gex.toCharArray()) {
            magic.append('§').append(c);
          }

          text = text.replace(nameMatch.group(), magic.toString());
        }
      }
    }

    if (!onlyCMIformat && CMILibConfig.QuirkyHex && text.contains("&#")) {
      Matcher match = cleanQuirkyHexColorRegexPattern.matcher(text);

      while (match.find()) {
        String string = match.group();
        if (Version.isCurrentLower(Version.v1_16_R1)) {
          String copy = string;
          copy = copy.substring(2, copy.length());
          if (copy.length() == 3) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < copy.length(); i++) {
              sb.append(copy.charAt(i) + "" + copy.charAt(i));
            }

            copy = sb.toString();
          }

          copy = getClosestVanilla(copy);
          text = text.replace(string, copy);
        } else {
          StringBuilder magic = new StringBuilder("§x");
          String shorten = string.substring(2, string.length());

          for (char c : shorten.toCharArray()) {
            magic.append('§').append(c);
            if (shorten.length() == 3) {
              magic.append('§').append(c);
            }
          }

          text = text.replace(string, magic.toString());
        }
      }
    }

    if (!onlyCMIformat && CMILibConfig.OfficialHex && text.contains("#")) {
      Matcher match = cleanOfficialColorRegexPattern.matcher(text);

      while (match.find()) {
        String string = match.group();
        if (Version.isCurrentLower(Version.v1_16_R1)) {
          String copy = string;
          copy = copy.substring(1, copy.length());
          if (copy.length() == 3) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < copy.length(); i++) {
              sb.append(copy.charAt(i) + "" + copy.charAt(i));
            }

            copy = sb.toString();
          }

          copy = getClosestVanilla(copy);
          text = text.replace(string, copy);
        } else {
          StringBuilder magic = new StringBuilder("§x");
          String shorten = string.substring(1, string.length());

          for (char c : shorten.toCharArray()) {
            magic.append('§').append(c);
            if (shorten.length() == 3) {
              magic.append('§').append(c);
            }
          }

          text = text.replace(string, magic.toString());
        }
      }
    }

    text = translateVanillaColorCodes(text);
    if (onlyCMIformat) {
      baseCMICacheByText.put(ori, text);
    } else {
      baseCacheByText.put(ori, text);
    }

    return text;
  }

  private static String translateVanillaColorCodes(String text) {
    if (text != null && !text.isEmpty()) {
      char[] chars = text.toCharArray();

      for (int i = 0; i < chars.length - 1; i++) {
        if (chars[i] == '&' && isColorCode(chars[i + 1])) {
          chars[i] = 167;
          chars[i + 1] = Character.toLowerCase(chars[i + 1]);
        }
      }

      return new String(chars);
    } else {
      return text;
    }
  }

  private static boolean isColorCode(char c) {
    return c >= '0' && c <= '9'
        || c >= 'a' && c <= 'f'
        || c >= 'k' && c <= 'o'
        || c == 'r'
        || c == 'x'
        || c >= 'A' && c <= 'F'
        || c >= 'K' && c <= 'O'
        || c == 'R'
        || c == 'X';
  }

  public static String convertNamedHex(String text) {
    if (text == null) {
      return null;
    }

    text = processGradient(text);
    if (text.contains("{#")) {
      Matcher nameMatch = hexColorNamePattern.matcher(text);

      while (nameMatch.find()) {
        String string = nameMatch.group(2);
        CMIChatColor cn = getByCustomName(string.toLowerCase().replace("_", ""));
        if (cn != null) {
          String gex = cn.getHex();
          StringBuilder magic = new StringBuilder("{#");

          for (char c : gex.toCharArray()) {
            magic.append('&').append(c);
          }

          magic.append("}");
          text = text.replace(nameMatch.group(), magic.toString());
        }
      }
    }

    return text;
  }

  public static String applyEqualGradient(String text, List<CMIChatColor> gradients) {
    if (gradients != null && !gradients.isEmpty()) {
      int size = text.length() / gradients.size();
      StringBuilder messageWithGradient = new StringBuilder();
      messageWithGradient.append(gradients.get(0).getFormatedHex(">"));

      for (int y = 0; y <= gradients.size() - 1; y++) {
        if (y > 0 && size > 0) {
          messageWithGradient.append(gradients.get(y).getFormatedHex("<>"));
        }

        for (int i = 0; i < size; i++) {
          messageWithGradient.append(text.charAt(0));
          text = text.substring(1);
        }
      }

      messageWithGradient.append(text + gradients.get(gradients.size() - 1).getFormatedHex("<"));
      return messageWithGradient.toString();
    } else {
      return text;
    }
  }

  @Deprecated
  public static String translateAlternateColorCodes(String text) {
    return translate(text);
  }

  public static String colorize(String text) {
    return text == null ? null : translate(text);
  }

  public static String simpleFlaten(String text) {
    return text.replace("&", "＆").replace("{#", "{＆#");
  }

  public static String flaten(String text) {
    return flaten(text, true);
  }

  public static String flaten(String text, boolean colorizeBefore) {
    return deColorize(text, colorizeBefore).replace("&", "＆").replace("{#", "{＆#");
  }

  public static String deColorize(String text) {
    return deColorize(text, true, true);
  }

  public static String deColorize(String text, boolean colorizeBeforeDe) {
    return deColorize(text, colorizeBeforeDe, true);
  }

  public static String deColorize(
      String text, boolean colorizeBeforeDe, boolean gradientSimplification) {
    return baseDeColorize(text, colorizeBeforeDe);
  }

  private static String baseDeColorize(String text, boolean colorizeBeforeDe) {
    if (text == null) {
      return null;
    }

    if (colorizeBeforeDe) {
      text = translate(text);
    }

    text = text.replace("§", "&");
    if (text.contains("&x")) {
      Matcher match = hexDeColorNamePattern.matcher(text);

      while (match.find()) {
        String reg = match.group(3).replace("&", "");
        CMIChatColor custom = CUSTOM_BY_HEX.get(reg.toLowerCase());
        if (custom != null) {
          text =
              text.replace(
                  match.group(), "{#" + custom.getName().toLowerCase().replace("_", "") + "}");
        } else {
          text = text.replace(match.group(), "{#" + reg + "}");
        }
      }
    }

    return text;
  }

  public static List<String> deColorize(List<String> lore) {
    for (int i = 0; i < lore.size(); i++) {
      lore.set(i, deColorize(lore.get(i)));
    }

    return lore;
  }

  public static String stripColor(String text) {
    if (text == null) {
      return null;
    }

    text = translate(text);
    return ChatColor.stripColor(text);
  }

  public static String stripHexColor(String message) {
    message = translate(message);
    Matcher match = hexColorRegexPattern.matcher(message);

    while (match.find()) {
      String string = match.group();
      message = message.replace(string, "");
    }

    if (message.contains("&x") || message.contains("§x")) {
      match = hexDeColorNamePattern.matcher(message);

      while (match.find()) {
        String string = match.group();
        message = message.replace(string, "");
      }
    }

    return message;
  }

  public static String getLastColors(String text) {
    if (text == null) {
      return null;
    }

    text = deColorize(text);
    Matcher match = hexColorRegexPatternLast.matcher(text);
    if (match.find()) {
      String colorByHex = match.group(0);
      if (text.endsWith(colorByHex)) {
        return colorByHex;
      }

      String[] split = text.split(escape(colorByHex), 2);
      if (split == null) {
        return colorByHex;
      }

      String last = getLastColors(split[1]);
      return last != null && !last.isEmpty() ? last : colorByHex;
    } else {
      match = hexColorNamePatternLast.matcher(text);
      if (!match.find()) {
        return ChatColor.getLastColors(translate(text));
      }

      String colorByName = match.group();
      if (text.endsWith(colorByName)) {
        return colorByName;
      }

      String[] split = text.split(escape(colorByName), 2);
      if (split == null) {
        return colorByName;
      }

      String last = getLastColors(split[1]);
      return last != null && !last.isEmpty() ? last : colorByName;
    }
  }

  public String getColorCode() {
    return this.hexCode != null ? "{#" + this.hexCode + "}" : "&" + this.c;
  }

  public String getBukkitColorCode() {
    return this.hexCode != null ? translate("{#" + this.hexCode + "}") : "§" + this.c;
  }

  @Override
  public String toString() {
    return this.getBukkitColorCode();
  }

  public char getChar() {
    return this.c;
  }

  public void setChar(char c) {
    this.c = c;
  }

  public boolean isColor() {
    return this.color;
  }

  public boolean isFormat() {
    return !this.color && !this.isReset;
  }

  public boolean isReset() {
    return this.isReset;
  }

  @Deprecated
  public ChatColor getColor() {
    return ChatColor.getByChar(this.getChar());
  }

  public static Set<CMIChatColor> getFormats(String text) {
    text = text.replace("§", "&");
    Set<CMIChatColor> formats = new HashSet<>();
    Matcher match = formatPattern.matcher(text);

    while (match.find()) {
      String string = match.group();
      CMIChatColor format = getFormat(string);
      if (format != null && format.isFormat()) {
        formats.add(format);
      }
    }

    return formats;
  }

  public static CMIChatColor getFormat(String text) {
    if (text == null) {
      return null;
    }

    String or = deColorize(text);
    text = text.replace("§", "&");
    if (text.length() > 1) {
      String formated = text.toLowerCase().replace("_", "");
      CMIChatColor got = BY_NAME.get(formated);
      if (got != null) {
        return got;
      }

      got = CUSTOM_BY_NAME.get(formated);
      if (got != null) {
        return got;
      }
    }

    if (or.length() > 1 && String.valueOf(or.charAt(or.length() - 2)).equalsIgnoreCase("&")) {
      text = text.substring(text.length() - 1, text.length()).toLowerCase();
      CMIChatColor byChar = BY_CHAR.get(text);
      if (byChar != null && byChar.isFormat()) {
        return byChar;
      }
    }

    return null;
  }

  public static CMIChatColor getColor(String text) {
    if (text == null) {
      return null;
    }

    String or = deColorize(text);
    if (or.contains("{#")) {
      Matcher match = hexColorRegexPatternLast.matcher(or);
      if (match.find()) {
        return new CMIChatColor(match.group(2));
      }

      match = hexColorNamePatternLast.matcher(or);
      if (match.find()) {
        return getByCustomName(match.group(2));
      }
    }

    text = or.replace("&", "");
    if (text.length() > 1) {
      String formated = text.toLowerCase().replace("_", "");
      CMIChatColor got = BY_NAME.get(formated);
      if (got != null) {
        return got;
      }

      got = CUSTOM_BY_NAME.get(formated);
      if (got != null) {
        return got;
      }
    }

    if (or.length() > 1 && String.valueOf(or.charAt(or.length() - 2)).equalsIgnoreCase("&")) {
      text = text.substring(text.length() - 1, text.length()).toLowerCase();
      CMIChatColor byChar = BY_CHAR.get(text);
      if (byChar != null) {
        return byChar;
      }
    }

    CMIChatColor hexColor = new CMIChatColor(text);
    return hexColor.getHex() != null ? hexColor : null;
  }

  public static CMIChatColor getRandomColor() {
    List<CMIChatColor> ls = new ArrayList<>();

    for (Entry<String, CMIChatColor> one : BY_NAME.entrySet()) {
      if (one.getValue().isColor()) {
        ls.add(one.getValue());
      }
    }

    Collections.shuffle(ls);
    return ls.get(0);
  }

  public Pattern getPattern() {
    return this.pattern;
  }

  public Color getRGBColor() {
    if (this.blueChannel < 0) {
      return null;
    } else {
      return Version.isCurrentEqualOrHigher(Version.v1_20_R1)
          ? Color.fromARGB(this.alpha, this.redChannel, this.greenChannel, this.blueChannel)
          : Color.fromRGB(this.redChannel, this.greenChannel, this.blueChannel);
    }
  }

  public java.awt.Color getJavaColor() {
    return this.blueChannel < 0
        ? null
        : new java.awt.Color(this.redChannel, this.greenChannel, this.blueChannel, this.alpha);
  }

  public String getHex() {
    return this.hexCode;
  }

  public String getFormatedHex() {
    return this.getFormatedHex(null);
  }

  public String getFormatedHex(String subSuffix) {
    return "{#" + this.hexCode + (subSuffix == null ? "" : subSuffix) + "}";
  }

  public String getName() {
    return this.name;
  }

  public String getCleanName() {
    return this.name == null ? this.getHex() : this.name.replace("_", "");
  }

  public static CMIChatColor getByCustomName(String name) {
    if (name.startsWith("{#")) {
      name = name.substring("{#".length());
    }

    if (name.endsWith("}")) {
      name = name.substring(0, name.length() - "}".length());
    }

    if (name.equalsIgnoreCase("random")) {
      List<CMIChatColor> valuesList = new ArrayList<>(CUSTOM_BY_NAME.values());
      int randomIndex = new Random().nextInt(valuesList.size());
      return valuesList.get(randomIndex);
    } else {
      return CUSTOM_BY_NAME.get(name.toLowerCase().replace("_", ""));
    }
  }

  public static CMIChatColor getByHex(String hex) {
    if (hex.startsWith("{#")) {
      hex = hex.substring("{#".length());
    }

    if (hex.endsWith("}")) {
      hex = hex.substring(0, hex.length() - "}".length());
    }

    return CUSTOM_BY_HEX.get(hex.toLowerCase().replace("_", ""));
  }

  public static Map<String, CMIChatColor> getByName() {
    return BY_NAME;
  }

  public static Map<String, CMIChatColor> getByCustomName() {
    return CUSTOM_BY_NAME;
  }

  public static String getHexFromCoord(int x, int y) {
    x = x < 0 ? 0 : (x > 255 ? 255 : x);
    y = y < 0 ? 0 : (y > 255 ? 255 : y);
    int blue = (int) (255.0 - y * 255 * (1.0 + Math.sin(6.3 * x)) / 2.0);
    int green = (int) (255.0 - y * 255 * (1.0 + Math.cos(6.3 * x)) / 2.0);
    int red = (int) (255.0 - y * 255 * (1.0 - Math.sin(6.3 * x)) / 2.0);
    StringBuilder hex =
        new StringBuilder()
            .append(Integer.toHexString((red << 16) + (green << 8) + blue & 16777215));

    while (hex.length() < 6) {
      hex.append("0" + hex);
    }

    return "#" + hex.toString();
  }

  public static String getHexRedGreenByPercent(int percentage, int parts) {
    float percent = percentage * 33.0F / 100.0F / 100.0F;
    java.awt.Color color = java.awt.Color.getHSBColor(percent, 1.0F, 1.0F);
    StringBuilder hex =
        new StringBuilder()
            .append(
                Integer.toHexString(
                    (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue() & 16777215));

    while (hex.length() < 6) {
      hex.append("0" + hex);
    }

    return "#" + hex.toString();
  }

  public int getRed() {
    return this.redChannel;
  }

  public int getGreen() {
    return this.greenChannel;
  }

  public int getBlue() {
    return this.blueChannel;
  }

  public int getAlpha() {
    return this.alpha;
  }

  public int getARGB() {
    return this.alpha << 24 | this.getRed() << 16 | this.getGreen() << 8 | this.getBlue();
  }

  public int getRGBA() {
    return this.getRed() << 24 | this.getGreen() << 16 | this.getBlue() << 8 | this.alpha;
  }

  public int getRGB() {
    return this.getRed() << 16 | this.getGreen() << 8 | this.getBlue();
  }

  public static CMIChatColor getClosest(String hex) {
    if (hex.startsWith("#")) {
      hex = hex.substring(1);
    }

    CMIChatColor closest = CUSTOM_BY_RGB.get(hex);
    if (closest != null) {
      return closest;
    }

    java.awt.Color hexAwt = null;

    try {
      hexAwt =
          new java.awt.Color(
              Integer.valueOf(hex.substring(0, 2), 16),
              Integer.valueOf(hex.substring(2, 4), 16),
              Integer.valueOf(hex.substring(4, 6), 16));
    } catch (Throwable e) {
      return null;
    }

    double distance = Double.MAX_VALUE;

    for (Entry<String, CMIChatColor> one : CUSTOM_BY_HEX.entrySet()) {
      try {
        java.awt.Color paletteAwt =
            new java.awt.Color(
                Integer.valueOf(one.getValue().hexCode.substring(0, 2), 16),
                Integer.valueOf(one.getValue().hexCode.substring(2, 4), 16),
                Integer.valueOf(one.getValue().hexCode.substring(4, 6), 16));
        int paletteRed = paletteAwt.getRed();
        int hexRed = hexAwt.getRed();
        int rmean = paletteRed + hexRed >> 1;
        int r = paletteRed - hexRed;
        int g = paletteAwt.getGreen() - hexAwt.getGreen();
        int b = paletteAwt.getBlue() - hexAwt.getBlue();
        double dist =
            Math.sqrt(((512 + rmean) * r * r >> 8) + 4 * g * g + ((767 - rmean) * b * b >> 8));
        if (dist < distance) {
          closest = one.getValue();
          distance = dist;
        }
      } catch (Throwable e) {
        e.printStackTrace();
        closest = WHITE;
        break;
      }
    }

    if (closest != null) {
      CUSTOM_BY_RGB.put(hex, closest);
      return closest;
    } else {
      CUSTOM_BY_RGB.put(hex, null);
      return null;
    }
  }

  public static String getClosestVanilla(String hex) {
    try {
      if (hex.startsWith("#")) {
        hex = hex.substring(1);
      }

      CMIChatColor old = CUSTOM_BY_HEX.get(hex);
      if (old != null && old.getChar() != '\n') {
        return "&" + old.getChar();
      }

      java.awt.Color hexAwt = null;

      try {
        hexAwt =
            new java.awt.Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
      } catch (Throwable e) {
        return null;
      }

      double distance = Double.MAX_VALUE;
      CMIChatColor closest = null;

      for (CMIChatColor one : BY_CHAR.values()) {
        if (one.isColor()) {
          java.awt.Color paletteAwt =
              new java.awt.Color(one.getRed(), one.getGreen(), one.getBlue());
          int paletteRed = paletteAwt.getRed();
          int hexRed = hexAwt.getRed();
          int rmean = paletteRed + hexRed >> 1;
          int r = paletteRed - hexRed;
          int g = paletteAwt.getGreen() - hexAwt.getGreen();
          int b = paletteAwt.getBlue() - hexAwt.getBlue();
          double dist =
              Math.sqrt(((512 + rmean) * r * r >> 8) + 4 * g * g + ((767 - rmean) * b * b >> 8));
          if (dist < distance) {
            closest = one;
            distance = dist;
          }
        }
      }

      if (closest != null) {
        if (old != null) {
          old.setChar(closest.getChar());
        } else {
          CUSTOM_BY_HEX.put(hex, closest);
        }

        return "&" + closest.getChar();
      }
    } catch (Throwable ee) {
      ee.printStackTrace();
    }

    return null;
  }

  public CMIChatColor mixColors(CMIChatColor color, double percent) {
    return mixColors(this, color, percent);
  }

  public static CMIChatColor mixColors(
      CMIChatColor startColor, CMIChatColor endColor, double percent) {
    return new CMIChatColor(GradientColor.mixColors(startColor, endColor, percent, false));
  }

  private static String escape(String text) {
    return text.replace("#", "\\#").replace("{", "\\{").replace("}", "\\}");
  }

  static {
    for (CMICustomColors one : CMICustomColors.values()) {
      CUSTOM_BY_NAME.put(
          one.name().toLowerCase().replace("_", ""),
          new CMIChatColor(one.toString(), one.getHex()));
      CUSTOM_BY_HEX.put(one.getHex().toLowerCase(), new CMIChatColor(one.toString(), one.getHex()));
    }

    globalColorPrefix = "{gc";
    cleanOfficialColorRegexPattern =
        Pattern.compile("(?<!\\{|:\"|＆)#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    cleanQuirkyHexColorRegexPattern = Pattern.compile("&#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    hexColorRegexPattern = Pattern.compile("(\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})");
    hexColorRegexPatternLast =
        Pattern.compile("(\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})(?!.*\\{#)");
    hexDeColorNamePattern = Pattern.compile("((&|§)x)(((&|§)[0-9A-Fa-f]){6})");
    hexColorNamePattern = Pattern.compile("(\\{#)([a-zA-Z_]{3,})(\\})");
    hexColorNamePatternLast = Pattern.compile("(\\{#)([a-zA-Z_]{3,})(\\})(?!.*\\{#)");
    gradientPattern = Pattern.compile("(\\{(#[^\\{\\}]*?)>\\})(.*?)(\\{(#[^\\{\\}]*?)<(>?)\\})");
    postGradientPattern =
        Pattern.compile(
            "((\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\}))(.)((\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\}))");
    post2GradientPattern =
        Pattern.compile(
            "((\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\}))(.)(((\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\}))(.))+");
    fullPattern =
        Pattern.compile(
            "(&[0123456789abcdefklmnorABCDEFKLMNOR])|(\\{#)([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\})|(\\{#)([a-zA-Z_]{3,})(\\})|(\\{@)([a-zA-Z_]{2,})(\\})");
    formatPattern = Pattern.compile("(&[klmnorKLMNOR])");
    BLACK = new CMIChatColor('0', "Black", 0, 0, 0);
    DARK_BLUE = new CMIChatColor('1', "Dark_Blue", 0, 0, 170);
    DARK_GREEN = new CMIChatColor('2', "Dark_Green", 0, 170, 0);
    DARK_AQUA = new CMIChatColor('3', "Dark_Aqua", 0, 170, 170);
    DARK_RED = new CMIChatColor('4', "Dark_Red", 170, 0, 0);
    DARK_PURPLE = new CMIChatColor('5', "Dark_Purple", 170, 0, 170);
    GOLD = new CMIChatColor('6', "Gold", 255, 170, 0);
    GRAY = new CMIChatColor('7', "Gray", 170, 170, 170);
    DARK_GRAY = new CMIChatColor('8', "Dark_Gray", 85, 85, 85);
    BLUE = new CMIChatColor('9', "Blue", 85, 85, 255);
    GREEN = new CMIChatColor('a', "Green", 85, 255, 85);
    AQUA = new CMIChatColor('b', "Aqua", 85, 255, 255);
    RED = new CMIChatColor('c', "Red", 255, 85, 85);
    LIGHT_PURPLE = new CMIChatColor('d', "Light_Purple", 255, 85, 255);
    YELLOW = new CMIChatColor('e', "Yellow", 255, 255, 85);
    WHITE = new CMIChatColor('f', "White", 255, 255, 255);
    OBFUSCATED = new CMIChatColor('k', "Obfuscated");
    BOLD = new CMIChatColor('l', "Bold");
    STRIKETHROUGH = new CMIChatColor('m', "Strikethrough");
    UNDERLINE = new CMIChatColor('n', "Underline");
    ITALIC = new CMIChatColor('o', "Italic");
    RESET = new CMIChatColor('r');
    HEX = new CMIChatColor('x');
  }
}
