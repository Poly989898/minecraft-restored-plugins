package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILibConfig;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.entity.Player;

public class CMITextCleaner {
  private static int cicles = 0;

  public static String cleanFromColorCodes(
      Object object, String string, CMIColorTypes cmiColorTypes, boolean flag) {
    return cleanFromColorCodes(object, string, cmiColorTypes, flag, false);
  }

  public static String cleanFromColorCodes(
      Object object,
      String string,
      CMIColorTypes cmiColorTypes,
      boolean flag,
      boolean removeFormat) {
    if (object == null) {
      return string;
    }

    string = CMIChatColor.deColorize(string, false);
    if (!(object instanceof Player)) {
      return CMIChatColor.translate(string);
    }

    try {
      string = CMIChatColor.removeGlobals(string);
    } catch (Throwable throwable) {
    }

    Player player = (Player) object;
    switch (cmiColorTypes) {
      case publicmessage:
        if (!CMI.getInstance().getChatManager().isColorsPublicMessages()) {
          return string;
        }
        break;
      case privatemessage:
        if (!CMI.getInstance().getChatManager().isColorsPrivateMessage()) {
          return string;
        }
        break;
      case me:
        if (!CMI.getInstance().getChatManager().isColorsMe()) {
          return string;
        }
      case nickname:
      case signs:
    }

    if (cmiColorTypes.isClean() || removeFormat) {
      switch (cmiColorTypes) {
        case publicmessage:
          if (!CMI.getInstance().getChatManager().isColorsPublicMessages()) {
            return CMIChatColor.stripColor(string);
          }
          break;
        case privatemessage:
          if (!CMI.getInstance().getChatManager().isColorsPrivateMessage()) {
            return CMIChatColor.stripColor(string);
          }
          break;
        case me:
          if (!CMI.getInstance().getChatManager().isColorsMe()) {
            return CMIChatColor.stripColor(string);
          }
        case nickname:
        case signs:
      }
    }

    boolean colorFound =
        PermissionsManager.CMIPerm.colors_$1_$star.hasSetPermission(player, cmiColorTypes.name());

    label262:
    while (cicles < 100) {
      cicles++;
      Matcher matcher = CMIChatColor.gradientPattern.matcher(string);

      while (matcher.find()) {
        String colorName = matcher.group(2).substring(1);
        CMIChatColor cmiChatColor = CMIChatColor.getByCustomName(colorName);
        if (cmiChatColor == null) {
          cmiChatColor = CMIChatColor.getByHex(colorName);
        }

        if (cmiChatColor == null) {
          if (cmiColorTypes.isClean()
              && !hasColorPermission(player, cmiColorTypes, colorName, colorFound)) {
            string = string.replace(matcher.group(1), "");
          }
        } else if (!hasColorPermission(
                player, cmiColorTypes, cmiChatColor.getCleanName(), colorFound)
            && cmiColorTypes.isClean()) {
          string = string.replace(matcher.group(1), "");
        }

        String codeGroup = matcher.group(5);
        String codeName = codeGroup.length() > 1 ? codeGroup.substring(1) : "";
        CMIChatColor namedColor = CMIChatColor.getByCustomName(codeName);
        if (namedColor == null) {
          namedColor = CMIChatColor.getByHex(codeName);
        }

        if (namedColor == null) {
          if (cmiColorTypes.isClean()
              && !hasColorPermission(player, cmiColorTypes, codeName, colorFound)) {
            string = string.replace(matcher.group(4), "");
          }
        } else if (!hasColorPermission(player, cmiColorTypes, namedColor.getCleanName(), colorFound)
            && cmiColorTypes.isClean()) {
          string = string.replace(matcher.group(4), "");
        }
      }

      String plainText = CMIChatColor.deColorize(string);
      string = plainText;
      HashMap<String, String> hashMap = new HashMap();
      switch (cmiColorTypes) {
        case publicmessage:
        case privatemessage:
        case me:
          int value = 0;

          for (String whiteEntry : CMI.getInstance().getChatManager().getCleanUpWhiteList()) {
            if (!whiteEntry.isEmpty() && string.contains(whiteEntry)) {
              String replacementToken = ++value + "%Rep%" + value;
              string = string.replace(whiteEntry, replacementToken);
              hashMap.put(replacementToken, whiteEntry);
            }
          }
        case nickname:
        case signs:
        default:
          Matcher hexMatcher = CMIChatColor.hexColorNamePattern.matcher(plainText);

          while (hexMatcher.find()) {
            if (hexMatcher.groupCount() >= 2) {
              String hexName = hexMatcher.group(2);
              CMIChatColor hexColor = CMIChatColor.getByCustomName(hexName);
              if (hexColor == null) {
                try {
                  Integer.valueOf(hexName.substring(0, 2), 16);
                  Integer.valueOf(hexName.substring(2, 4), 16);
                  Integer.valueOf(hexName.substring(4, 6), 16);
                } catch (Throwable throwable2) {
                  if (cmiColorTypes.isClean()) {
                    string = string.replace(hexMatcher.group(), "");
                  }
                }
              } else if (hasColorPermission(
                  player, cmiColorTypes, hexColor.getCleanName(), colorFound)) {
                string = string.replace(hexMatcher.group(), hexColor.getBukkitColorCode());
              } else {
                String hexMatch = hexMatcher.group();
                if (cmiColorTypes.isClean()) {
                  string = string.replace(hexMatch, "");
                } else {
                  string = string.replace(hexMatch, "{＆#" + hexName + "}");
                }
              }
            }
          }

          if (CMILibConfig.OfficialHex) {
            Matcher officialMatcher =
                CMIChatColor.cleanOfficialColorRegexPattern.matcher(plainText);

            while (officialMatcher.find()) {
              if (officialMatcher.groupCount() >= 2) {
                String officialName = officialMatcher.group(2);
                CMIChatColor officialColor = CMIChatColor.getByCustomName(officialName);
                if (officialColor == null) {
                  try {
                    Integer.valueOf(officialName.substring(0, 2), 16);
                    Integer.valueOf(officialName.substring(2, 4), 16);
                    Integer.valueOf(officialName.substring(4, 6), 16);
                  } catch (Throwable throwable3) {
                    if (cmiColorTypes.isClean()) {
                      string = string.replace(officialMatcher.group(), "");
                    }
                  }
                } else if (hasColorPermission(
                    player, cmiColorTypes, officialColor.getCleanName(), colorFound)) {
                  string =
                      string.replace(officialMatcher.group(), officialColor.getBukkitColorCode());
                } else {
                  String officialMatch = officialMatcher.group();
                  if (cmiColorTypes.isClean()) {
                    string = string.replace(officialMatch, "");
                  } else {
                    string = string.replace(officialMatch, "{＆#" + officialName + "}");
                  }
                }
              }
            }
          }

          hexMatcher = CMIChatColor.hexColorRegexPattern.matcher(plainText);

          while (hexMatcher.find()) {
            String hexCode = hexMatcher.group(2);
            CMIChatColor hexColorValue = CMIChatColor.getByHex(hexCode);
            if (hexColorValue == null) {
              hexColorValue = new CMIChatColor(hexCode);
              if (hasColorPermission(player, cmiColorTypes, hexCode, colorFound)) {
                string = string.replace(hexMatcher.group(), hexColorValue.getBukkitColorCode());
              } else {
                String hexRaw = hexMatcher.group();
                if (cmiColorTypes.isClean()) {
                  string = string.replace(hexRaw, "");
                  if (CMILibConfig.QuirkyHex) {
                    String hexCodeClean = hexRaw.replace("{#", "").replace("}", "");
                    hexCodeClean = "#" + hexCodeClean;
                    string = string.replace(hexCodeClean, "");
                  }
                } else {
                  string = string.replace(hexRaw, "{＆#" + hexCode + "}");
                }
              }
            } else if (!hasColorPermission(
                    player, cmiColorTypes, hexColorValue.getCleanName(), colorFound)
                && cmiColorTypes.isClean()) {
              string = string.replace(hexMatcher.group(), "");
            }
          }

          for (var entry : CMIChatColor.getByName().entrySet()) {
            Pattern pattern = ((CMIChatColor) entry.getValue()).getPattern();
            String entryKey = (String) entry.getKey();
            Matcher patternMatcher = pattern.matcher(string);
            boolean found = false;

            while (patternMatcher.find()) {
              found = true;
              String matchText = patternMatcher.group(0);
              if (hasColorPermission(player, cmiColorTypes, entryKey, colorFound)) {
                string =
                    string.replace(
                        matchText, ((CMIChatColor) entry.getValue()).getBukkitColorCode());
              } else if (cmiColorTypes.isClean()) {
                string = string.replace(matchText, "");
              } else {
                string =
                    string.replace(matchText, "＆" + ((CMIChatColor) entry.getValue()).getChar());
              }
            }

            if (!found && flag
                || found
                    && cmiColorTypes.equals(CMIColorTypes.books)
                    && ((CMIChatColor) entry.getValue()).getColorCode().equalsIgnoreCase("&0")) {
              string = string.replace(((CMIChatColor) entry.getValue()).getColorCode(), "");
              string =
                  string.replace(
                      ((CMIChatColor) entry.getValue()).getColorCode().toUpperCase(), "");
            }
          }

          switch (cmiColorTypes) {
            case publicmessage:
            case privatemessage:
            case me:
              for (Entry replacementEntry : hashMap.entrySet()) {
                string =
                    string.replace(
                        (CharSequence) replacementEntry.getKey(),
                        (CharSequence) replacementEntry.getValue());
              }
            case nickname:
            case signs:
            default:
              if (plainText.equals(CMIChatColor.deColorize(string))) {
                break label262;
              }
          }
      }
    }

    cicles = 0;
    string = string.replace("{#", "{＆#");
    return CMIChatColor.translate(string);
  }

  private static boolean hasColorPermission(
      Player player, CMIColorTypes cmiColorTypes, String string, boolean flag) {
    Boolean flagValue = null;
    if (string.equalsIgnoreCase("Obfuscated")) {
      if (!PermissionsManager.CMIPerm.colors_$1_$2.hasPermission(
          player, 1000L, cmiColorTypes.name(), string.toLowerCase())) {
        flagValue = false;
      }

      if (!PermissionsManager.CMIPerm.colors_$1_$2.hasPermission(
          player, 1000L, cmiColorTypes.name(), "magic")) {
        flagValue = false;
      }

      if (flagValue != null && !flagValue && flag) {
        return false;
      }

      if (flagValue == null && flag) {
        return true;
      }

      if (flagValue != null && flagValue && !flag) {
        return true;
      }
    }

    if (flagValue == null) {
      flagValue =
          PermissionsManager.CMIPerm.colors_$1_$2.hasPermission(
              player, 1000L, cmiColorTypes.name(), string.toLowerCase());
    }

    if (!flagValue && flag) {
      return false;
    } else {
      return flagValue && !flag ? true : flag;
    }
  }
}
