package com.Zrips.CMI.Locale;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Containers.SndUser;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Locale.YmlMaker;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Language {
  private CMI plugin;
  private ConcurrentHashMap<String, FileConfiguration> locales = new ConcurrentHashMap<>();
  private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\\\n");

  public Language(CMI cmi) {
    this.plugin = cmi;
  }

  public void reload() {
    try {
      this.locales.put(
          this.plugin.getConfigManager().Lang.toLowerCase(),
          new YmlMaker(
                  this.plugin,
                  "Translations"
                      + File.separator
                      + "Locale_"
                      + this.plugin.getConfigManager().Lang
                      + ".yml")
              .getConfig());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private FileConfiguration getEN() {
    if (this.locales.get("en") == null) {
      try {
        this.locales.put(
            "en",
            new YmlMaker(this.plugin, "Translations" + File.separator + "Locale_EN.yml")
                .getConfig());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return this.locales.get("en");
  }

  public FileConfiguration getCustom() {
    if (this.locales.get(this.plugin.getConfigManager().Lang.toLowerCase()) == null) {
      try {
        this.locales.put(
            this.plugin.getConfigManager().Lang.toLowerCase(),
            new YmlMaker(
                    this.plugin,
                    "Translations"
                        + File.separator
                        + "Locale_"
                        + this.plugin.getConfigManager().Lang
                        + ".yml")
                .getConfig());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return this.locales.get(this.plugin.getConfigManager().Lang.toLowerCase());
  }

  private FileConfiguration getLocale(String string) {
    if (string.length() > 10) {
      return this.getEN();
    }

    if (this.locales.get(string) == null) {
      try {
        File file =
            new File(
                this.plugin.getDataFolder(),
                "Translations" + File.separator + "Locale_" + string.toUpperCase() + ".yml");
        if (!file.isFile()) {
          return this.getEN();
        }

        this.locales.put(
            string.toUpperCase(),
            new YmlMaker(
                    this.plugin,
                    "Translations" + File.separator + "Locale_" + string.toUpperCase() + ".yml")
                .getConfig());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return this.locales.getOrDefault(string, this.getEN());
  }

  @Deprecated
  public String getMessage(String string, Object... objects) {
    return this.getLMessage(this.plugin.getConfigManager().Lang, string, objects);
  }

  public String getLMessage(String string, String key, Object... objects) {
    string =
        string == null ? this.plugin.getConfigManager().Lang.toLowerCase() : string.toLowerCase();
    StringBuilder stringBuilder = new StringBuilder();
    FileConfiguration fileConfiguration = this.getLocale(string);
    if (fileConfiguration.isString(key)) {
      stringBuilder.append(fileConfiguration.getString(key));
    } else if (fileConfiguration.isList(key)) {
      for (String line : fileConfiguration.getStringList(key)) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append("\n");
        }

        stringBuilder.append(line);
      }
    } else {
      stringBuilder.append(
          this.getEN().isString(key)
              ? this.getEN().getString(key)
              : "Missing locale for " + key + " ");
    }

    Snd snd = null;
    CMIUser cmiUser = null;
    Location location = null;
    ArrayList<Object> arrayList = new ArrayList();
    Object[] formatArgs = objects;
    int value = objects.length;

    for (int index = 0; index < value; index++) {
      Object object = formatArgs[index];
      if (object instanceof Snd) {
        if (snd == null) {
          snd = (Snd) object;
          arrayList.add(object);
        }
      } else if (object instanceof CMIUser) {
        if (cmiUser == null) {
          cmiUser = (CMIUser) object;
          snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
          arrayList.add(object);
        }
      } else if (object instanceof Location) {
        if (location == null) {
          location = (Location) object;
          arrayList.add(object);
        }
      } else if (object instanceof Player && snd == null) {
        snd = new Snd().setSender((Player) object).setTarget((Player) object);
      }
    }

    String result = stringBuilder.toString();
    if (location != null) {
      result = SndUser.replaceLocation("", location, result);
    }

    if (cmiUser != null && cmiUser.getPlayer(false) != null) {
      Snd sound = new Snd().setSender(cmiUser).setTarget(cmiUser);
      result = sound.updateLocale(result);
    }

    if (objects.length > 0) {
      for (int argIndex = 0; argIndex < objects.length; argIndex++) {
        if (!arrayList.contains(objects[argIndex])) {
          if (objects.length >= argIndex + 2) {
            Object argValue = objects[argIndex + 1];
            if (argValue instanceof Boolean) {
              argValue =
                  (Boolean) argValue
                      ? LC.info_variables_Enabled.getLocale(new Object[0])
                      : LC.info_variables_Disabled.getLocale(new Object[0]);
            }

            if (argValue instanceof LC) {
              argValue = ((LC) argValue).getText();
            }

            result = outReplace(result, objects[argIndex], argValue);
          }

          argIndex++;
        }
      }
    }

    if (snd != null) {
      result = this.updateSnd(snd, result);
    }

    result = result.replace("!prefix!", LC.info_prefix.getLocale(new Object[0]));
    result = this.filterNewLine(result);
    return CMIChatColor.translate(result);
  }

  public String filterNewLine(String string) {
    return string != null && !string.isEmpty()
        ? NEWLINE_PATTERN.matcher(string).replaceAll("\n")
        : string;
  }

  public List<String> updateSnd(Snd snd, List<String> list) {
    int value = 0;

    for (int listSize = list.size(); value < listSize; value++) {
      list.set(value, this.updateSnd(snd, (String) list.get(value)));
    }

    return list;
  }

  public String updateSnd(Snd snd, String string) {
    return snd.updateLocale(string);
  }

  private static String outReplace(String string, Object object, Object replacement) {
    if (object == null) {
      return string;
    }

    if (replacement == null) {
      replacement = "";
    }

    String replacementText = String.valueOf(replacement);
    replacementText =
        CMI.getInstance().getPlaceholderAPIManager().flatenPlaceHolder(replacementText);
    replacement = CTextManager.clean(replacementText);
    return string.replace(String.valueOf(object), replacementText);
  }

  public String getDefaultMessage(String string) {
    String missingText = "Missing locale for " + string + " ";
    String lines = "";
    lines = this.getEN().isString(string) ? this.getEN().getString(string) : missingText;
    StringBuilder stringBuilder = new StringBuilder();
    if (lines.contains("/n")) {
      int value = 0;
      String[] strings;
      int lineCount = (strings = lines.split("/n")).length;

      for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
        String line = strings[lineIndex];
        value++;
        stringBuilder.append(line);
        if (value < lines.split("/n").length) {
          stringBuilder.append("\n  ");
        }
      }

      lines = stringBuilder.toString();
    }

    return CMIChatColor.translate(lines);
  }

  @Deprecated
  public List<String> getMessageList(String string, Object... objects) {
    return this.getMessageLList(this.plugin.getConfigManager().Lang, string, objects);
  }

  public List<String> getMessageLList(String string, String key, Object... objects) {
    string =
        string == null ? this.plugin.getConfigManager().Lang.toLowerCase() : string.toLowerCase();
    String missingText = "Missing locale for " + key + " ";
    FileConfiguration fileConfiguration = this.getLocale(string);
    List list;
    if (fileConfiguration.isList(key)) {
      list = fileConfiguration.getStringList(key);
    } else {
      list =
          !this.getEN().getStringList(key).isEmpty()
              ? this.getEN().getStringList(key)
              : Arrays.asList(missingText);
    }

    if (objects != null && objects.length > 0) {
      for (int value = 0; value < list.size(); value++) {
        String item = (String) list.get(value);

        for (byte b = 0; b < objects.length; b += 2) {
          item = item.replace(String.valueOf(objects[b]), String.valueOf(objects[b + 1]));
        }

        item = this.filterNewLine(item);
        list.set(value, CMIChatColor.translate(item));
      }
    }

    int index = 0;

    for (String listItem : ((Iterable<String>) list)) {
      list.set(
          index,
          CMIChatColor.translate(
              listItem.replace("!prefix!", LC.info_prefix.getLocale(new Object[0]))));
      index++;
    }

    return list;
  }

  @Deprecated
  public boolean isList(String string) {
    return this.isLList(this.plugin.getConfigManager().Lang, string);
  }

  public boolean isLList(String string, String key) {
    string =
        string == null ? this.plugin.getConfigManager().Lang.toLowerCase() : string.toLowerCase();
    FileConfiguration fileConfiguration = this.getLocale(string);
    if (fileConfiguration != null && fileConfiguration.contains(key)) {
      return fileConfiguration.isList(key);
    } else {
      return this.getEN().contains(key) ? this.getEN().isList(key) : false;
    }
  }

  @Deprecated
  public boolean containsKey(String string) {
    return this.containsLKey(this.plugin.getConfigManager().Lang, string);
  }

  public boolean containsLKey(String string, String key) {
    string =
        string == null ? this.plugin.getConfigManager().Lang.toLowerCase() : string.toLowerCase();
    FileConfiguration fileConfiguration = this.getLocale(string);
    return fileConfiguration != null && fileConfiguration.contains(key)
        ? true
        : this.getEN().contains(key);
  }

  public boolean isString(String string) {
    return this.getEN().isString(string);
  }

  @Deprecated
  public Set<String> getKeys(String string) {
    return this.getLKeys(this.plugin.getConfigManager().Lang, string);
  }

  public Set<String> getLKeys(String string, String key) {
    string =
        string == null ? this.plugin.getConfigManager().Lang.toLowerCase() : string.toLowerCase();
    FileConfiguration fileConfiguration = this.getLocale(string);
    if (fileConfiguration.isConfigurationSection(key)) {
      return fileConfiguration.getConfigurationSection(key).getKeys(false);
    } else {
      return this.getEN() != null && this.getEN().isConfigurationSection(key)
          ? this.getEN().getConfigurationSection(key).getKeys(false)
          : new HashSet<>();
    }
  }
}
