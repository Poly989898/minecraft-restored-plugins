package net.Zrips.CMILib.Colors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMICachedLinkedMap;

public class GradientColor {
  private String startingColor = null;
  private String startingTextFormat = null;
  private String endingColor = null;
  private String nextChar = null;
  private String prevHexColor = null;
  private double prevDistance = Double.MAX_VALUE;
  private double prevDelta = Double.MAX_VALUE;
  private StringBuilder modifiedText = new StringBuilder();
  private StringBuilder toBeReplacedText = new StringBuilder();
  private String replaceWith = null;
  private static Pattern gradientDetectionPattern =
      Pattern.compile(
          "(?=(\\{#([0-9A-Fa-f]{6}|[a-zA-Z_]{3,})\\}((?:&[0-9a-fk-or])*)(.)\\{#([0-9A-Fa-f]{6}|[a-zA-Z_]{3,})\\}((?:&[0-9a-fk-or])*)(.)))");
  private static Pattern cleanupPattern = Pattern.compile("\\{#([0-9A-Za-z_]{3,})<\\}\\{#\\1>\\}");
  private static final Pattern gradientPattern =
      Pattern.compile("(\\{(#([^\\{\\}]*)?)>\\})(.*?)(\\{(#([^\\{\\}]*)?)<(>?)\\})");
  static Pattern pattern = Pattern.compile("(\\{#[^}]+})(.)(.)(\\{#[^}]+})");
  private static CMICachedLinkedMap<String, String> cache = new CMICachedLinkedMap<>(100);
  private static Map<String, String> gradientcache =
      new LinkedHashMap<String, String>(100, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, String> eldest) {
          return this.size() > 100;
        }
      };
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public GradientColor(String startingColor, String startingTextFormat) {
    this.startingColor = startingColor;
    this.startingTextFormat = startingTextFormat;
  }

  public String getStartingColor() {
    return this.startingColor;
  }

  public GradientColor setStartingColor(String startingColor) {
    this.startingColor = startingColor;
    return this;
  }

  public String getStartingTextFormat() {
    return this.startingTextFormat;
  }

  public GradientColor setStartingTextFormat(String startingTextFormat) {
    this.startingTextFormat = startingTextFormat;
    return this;
  }

  public String getPrevHexColor() {
    return this.prevHexColor;
  }

  public GradientColor setPrevHexColor(String prevHexColor) {
    this.prevHexColor = prevHexColor;
    return this;
  }

  public double getPrevDistance() {
    return this.prevDistance;
  }

  public GradientColor setPrevDistance(double prevDistance) {
    this.prevDistance = prevDistance;
    return this;
  }

  public String getModifiedText() {
    return this.modifiedText.toString();
  }

  public GradientColor addToModifiedText(String modifiedText) {
    this.modifiedText.append(modifiedText);
    return this;
  }

  public StringBuilder getReplaceWhat() {
    return this.toBeReplacedText;
  }

  public GradientColor addReplaceWhat(String toBeReplacedText) {
    this.toBeReplacedText.append(toBeReplacedText);
    return this;
  }

  public String getEndingColor() {
    return this.endingColor;
  }

  public GradientColor setEndingColor(String endingColor) {
    this.endingColor = endingColor;
    return this;
  }

  public String getNextChar() {
    return this.nextChar;
  }

  public GradientColor setNextChar(String nextChar) {
    this.nextChar = nextChar;
    return this;
  }

  public String getReplaceWith() {
    return this.replaceWith;
  }

  public GradientColor setReplaceWith(String replaceWith) {
    this.replaceWith = replaceWith;
    return this;
  }

  public void calculateFinalString() {
    CMIChatColor startingColor = CMIChatColor.getColor(this.getStartingColor());
    CMIChatColor endColor = CMIChatColor.getColor(this.getEndingColor());
    this.addReplaceWhat(
        "{#"
            + this.getEndingColor()
            + "}"
            + (this.getStartingTextFormat() == null ? "" : this.getStartingTextFormat())
            + this.getNextChar());
    this.addToModifiedText(this.getNextChar());
    StringBuilder with = new StringBuilder();
    StringBuilder formatedStarting = new StringBuilder();
    if (this.getModifiedText().length() > 1) {
      formatedStarting.append("{#");
      formatedStarting.append(startingColor.getCleanName());
      formatedStarting.append(">");
      formatedStarting.append("}");
      if (this.getStartingTextFormat() != null) {
        formatedStarting.append(this.getStartingTextFormat());
      }

      formatedStarting.append(this.getModifiedText());
      formatedStarting.append("{#");
      formatedStarting.append(endColor.getCleanName());
      formatedStarting.append("<");
      formatedStarting.append("}");
    }

    with.append(formatedStarting);
    this.setReplaceWith(with.toString());
  }

  public double getPreviousDelta() {
    return this.prevDelta;
  }

  public GradientColor setPreviousDelta(double prevDelta) {
    this.prevDelta = prevDelta;
    return this;
  }

  public static String deconvert(String text) {
    text = CMIChatColor.deColorize(text, true);
    text = convertLegacyToHex(text);
    String cached = cache.get(text);
    if (cached != null) {
      return cached;
    }

    Matcher matcher = pattern.matcher(text);
    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      String firstColor = matcher.group(1);
      String firstChar = matcher.group(2);
      String secondChar = matcher.group(3);
      String secondColor = matcher.group(4);
      matcher.appendReplacement(
          result,
          Matcher.quoteReplacement(firstColor + firstChar + firstColor + secondChar + secondColor));
    }

    matcher.appendTail(result);
    text = result.toString();
    String res = de(text);
    cache.put(text, res);
    return res;
  }

  private static String de(String text) {
    String original = text;
    text = cycleOver(text);
    if (text.contains("<}")) {
      Matcher match = cleanupPattern.matcher(text);

      while (match.find()) {
        String fullMatch = match.group();
        String color = match.group(1);
        text = text.replace(fullMatch, "{#" + color + "<>" + "}");
      }
    }

    if (!CMIChatColor.translate(text).equalsIgnoreCase(CMIChatColor.translate(original))) {
      text = original;
    }

    return text;
  }

  private static String cycleOver(String text) {
    if (!text.contains("{#")) {
      return text;
    }

    String original = text;
    Matcher match = gradientDetectionPattern.matcher(text);
    List<GradientColor> gradients = new ArrayList<>();
    GradientColor gc = null;

    while (match.find()) {
      String firstHex = match.group(2);
      String firstFormats = match.group(3);
      String firstChar = match.group(4);
      String secondHex = match.group(5);
      String secondChar = match.group(7);
      CMIChatColor fromColor = CMIChatColor.getColor(firstHex);
      CMIChatColor toColor = CMIChatColor.getColor(secondHex);
      if (gc == null) {
        gc = new GradientColor(firstHex, firstFormats);
      }

      int rDiff = fromColor.getRed() - toColor.getRed();
      int gDiff = fromColor.getGreen() - toColor.getGreen();
      int bDiff = fromColor.getBlue() - toColor.getBlue();
      double dist = Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
      if (gc.getPrevDistance() == Double.MAX_VALUE) {
        gc.setPrevDistance(dist);
      }

      double delta = Math.abs(gc.getPrevDistance() - dist);
      if (gc.getPreviousDelta() == Double.MAX_VALUE) {
        gc.setPreviousDelta(delta);
      }

      if (delta > 2.0) {
        gc.calculateFinalString();
        gradients.add(gc);
        gc = null;
        break;
      }

      gc.addReplaceWhat(
          "{#"
              + firstHex
              + "}"
              + (gc.getStartingTextFormat() == null ? "" : gc.getStartingTextFormat())
              + firstChar);
      gc.addToModifiedText(firstChar);
      gc.setEndingColor(secondHex);
      gc.setNextChar(secondChar);
      gc.setPreviousDelta(delta);
      gc.setPrevDistance(dist);
    }

    if (gc != null) {
      gc.calculateFinalString();
      gradients.add(gc);
    }

    for (GradientColor gradient : gradients) {
      text = text.replace(gradient.getReplaceWhat(), gradient.getReplaceWith());
    }

    if (!gradients.isEmpty() && !original.equals(text)) {
      text = de(text);
    }

    return text;
  }

  private static String legacyCodeToHex(char code) {
    switch (Character.toLowerCase(code)) {
      case '0':
        return CMICustomColors.Black.getName();
      case '1':
        return CMICustomColors.Dark_Blue.getName();
      case '2':
        return CMICustomColors.Dark_Green.getName();
      case '3':
        return CMICustomColors.Dark_Aqua.getName();
      case '4':
        return CMICustomColors.Dark_Red.getName();
      case '5':
        return CMICustomColors.Dark_Purple.getName();
      case '6':
        return CMICustomColors.Gold.getName();
      case '7':
        return CMICustomColors.Gray.getName();
      case '8':
        return CMICustomColors.Dark_Gray.getName();
      case '9':
        return CMICustomColors.Blue.getName();
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
        return null;
      case 'a':
        return CMICustomColors.Green.getName();
      case 'b':
        return CMICustomColors.Aqua.getName();
      case 'c':
        return CMICustomColors.Red.getName();
      case 'd':
        return CMICustomColors.Light_Purple.getName();
      case 'e':
        return CMICustomColors.Yellow.getName();
      case 'f':
        return CMICustomColors.White.getName();
    }
  }

  private static boolean isHexDigit(char c) {
    return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
  }

  public static String convertLegacyToHex(String input) {
    StringBuilder output = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '&') {
        if (i + 13 < input.length() && (input.charAt(i + 1) == 'x' || input.charAt(i + 1) == 'X')) {
          StringBuilder hex = new StringBuilder();
          boolean valid = true;

          for (int j = 0; j < 6; j++) {
            int pos = i + 2 + j * 2;
            if (input.charAt(pos) != '&') {
              valid = false;
              break;
            }

            char hexChar = input.charAt(pos + 1);
            if (!isHexDigit(hexChar)) {
              valid = false;
              break;
            }

            hex.append(hexChar);
          }

          if (valid) {
            CMIChatColor color = CMIChatColor.getByHex(hex.toString());
            if (color != null) {
              output.append("{#").append(color.getCleanName()).append("}");
            } else {
              output.append("{#").append(hex).append("}");
            }

            i += 13;
            continue;
          }
        }

        if (i + 1 < input.length()) {
          char code = input.charAt(i + 1);
          String hex = legacyCodeToHex(code);
          if (hex != null) {
            output.append("{#").append(hex).append("}");
            i++;
            continue;
          }
        }
      }

      output.append(c);
    }

    return output.toString();
  }

  public static void clearCache() {
    gradientcache.clear();
  }

  public static String mixColors(
      CMIChatColor startColor, CMIChatColor endColor, double percent, boolean vanilla) {
    double inverse_percent = 1.0 - percent;
    int redPart = (int) (endColor.getRed() * percent + startColor.getRed() * inverse_percent);
    int greenPart = (int) (endColor.getGreen() * percent + startColor.getGreen() * inverse_percent);
    int bluePart = (int) (endColor.getBlue() * percent + startColor.getBlue() * inverse_percent);
    return vanilla
        ? toHexColorVanilla(redPart, greenPart, bluePart)
        : toHexColor(redPart, greenPart, bluePart);
  }

  private static String toHexColor(int red, int green, int blue) {
    char[] hexChars =
        new char[] {
          HEX_ARRAY[red >>> 4 & 15],
          HEX_ARRAY[red & 15],
          HEX_ARRAY[green >>> 4 & 15],
          HEX_ARRAY[green & 15],
          HEX_ARRAY[blue >>> 4 & 15],
          HEX_ARRAY[blue & 15]
        };
    return new String(hexChars);
  }

  private static String toHexColorVanilla(int red, int green, int blue) {
    StringBuilder sb = new StringBuilder(14);
    sb.append('§').append('x');
    int[] rgb = new int[] {red, green, blue};

    for (int color : rgb) {
      sb.append('§').append(HEX_ARRAY[color >>> 4 & 15]);
      sb.append('§').append(HEX_ARRAY[color & 15]);
    }

    return sb.toString();
  }

  public static String processGradient(String text) {
    return processGradient(text, false);
  }

  public static String processGradient(String text, boolean vanilla) {
    if (!text.contains("{#")) {
      return text;
    }

    String cached = gradientcache.get(text);
    if (cached != null) {
      return cached;
    }

    Matcher gradientMatch = gradientPattern.matcher(text);
    String original = text;

    while (gradientMatch.find()) {
      String fullmatch = gradientMatch.group();
      String startColorCode = gradientMatch.group(3);
      String endColorCode = gradientMatch.group(7);
      boolean startIsHex = startColorCode.matches("(?i)#[0-9a-f]{6}");
      boolean endIsHex = endColorCode.matches("(?i)#[0-9a-f]{6}");
      CMIChatColor startChatColor = CMIChatColor.getColor(startColorCode);
      CMIChatColor endChatColor = CMIChatColor.getColor(endColorCode);
      if (startChatColor != null && endChatColor != null) {
        String gtext = gradientMatch.group(4);
        boolean continuous = !gradientMatch.group(8).isEmpty();
        StringBuilder updated = new StringBuilder();
        Set<CMIChatColor> formats = CMIChatColor.getFormats(gtext);
        gtext = CMIChatColor.stripColor(gtext);
        int length = gtext.length();
        length = length < 2 ? 2 : length - 1;

        for (int i = 0; i < gtext.length(); i++) {
          char ch = gtext.charAt(i);
          double percent = (double) i / length;
          if (vanilla) {
            updated.append(mixColors(startChatColor, endChatColor, percent, vanilla));
          } else {
            String mix = mixColors(startChatColor, endChatColor, percent, vanilla);
            if (!startIsHex && !endIsHex) {
              CMIChatColor mixedColor = CMIChatColor.getColor(mix);
              if (mixedColor != null) {
                mix = mixedColor.getCleanName();
              }
            }

            updated.append("{#").append(mix).append("}");
          }

          if (!formats.isEmpty()) {
            for (CMIChatColor one : formats) {
              if (vanilla) {
                updated.append("§").append(one.getChar());
              } else {
                updated.append("&").append(one.getChar());
              }
            }
          }

          int codePoint = Character.codePointAt(gtext, i);
          if (codePoint > 127) {
            if (Character.isSurrogate(ch)) {
              updated.append(ch).append(gtext.charAt(i + 1));
              i++;
            } else {
              updated.append(ch);
            }
          } else {
            updated.append(ch);
          }
        }

        if (continuous) {
          updated.append("{#").append(gradientMatch.group(7)).append(">").append("}");
        }

        text = text.replace(fullmatch, updated.toString());
        if (continuous) {
          text = processGradient(text, vanilla);
        }
      }
    }

    gradientcache.put(original, text);
    return text;
  }
}
