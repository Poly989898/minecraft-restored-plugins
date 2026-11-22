package net.Zrips.CMILib.Locale;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Language {
  public FileConfiguration enlocale;
  public FileConfiguration Customlocale;
  private CMILib plugin;
  private String lang;
  private static final Pattern NEWLINE_PATTERN = Pattern.compile(" ?(?:/n(?=$|\\s)|\\\\n)");

  public Language(CMILib plugin, String lang) {
    this.plugin = plugin;
    this.lang = lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public void reload() {
    try {
      this.Customlocale =
          new YmlMaker(
                  this.plugin, "Translations" + File.separator + "Locale_" + this.lang + ".yml")
              .getConfig();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private FileConfiguration getEN() {
    if (this.enlocale == null) {
      try {
        this.enlocale =
            new YmlMaker(this.plugin, "Translations" + File.separator + "Locale_EN.yml")
                .getConfig();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return this.enlocale;
  }

  public String getMessage(String key, Object... variables) {
    String missing = "Missing locale for " + key + " ";
    String msg = "";
    if (this.Customlocale.isString(key)) {
      msg = this.Customlocale.getString(key);
    } else if (this.Customlocale.isList(key)) {
      msg = CMIList.listToString(this.Customlocale.getStringList(key));
    } else {
      msg = this.getEN().isString(key) ? this.getEN().getString(key) : missing;
    }

    Snd snd = null;
    Location customLoc = null;
    List<Object> remove = new ArrayList<>();

    for (Object one : variables) {
      if (one instanceof Snd) {
        if (snd == null) {
          snd = (Snd) one;
          remove.add(one);
        }
      } else if (CMILib.getInstance().isCmiPresent()
          && one instanceof com.Zrips.CMI.Containers.Snd) {
        com.Zrips.CMI.Containers.Snd cmisnd = (com.Zrips.CMI.Containers.Snd) one;
        msg = this.updateCmiSnd(cmisnd, msg);
        remove.add(one);
      } else if (one instanceof CMIVector3D) {
        msg = ((CMIVector3D) one).translateVariables(msg);
        remove.add(one);
      } else if (one instanceof Vector) {
        if (customLoc == null) {
          msg = new CMIVector3D((Vector) one).translateVariables(msg);
          remove.add(one);
        }
      } else if (one instanceof Location) {
        if (customLoc == null) {
          customLoc = (Location) one;
          remove.add(one);
        }
      } else if (one instanceof Player && snd == null) {
        snd = new Snd().setTarget((Player) one).setTarget((Player) one);
      }
    }

    if (customLoc != null) {
      msg = this.replacePlayer(customLoc, msg);
    }

    if (snd != null) {
      msg = this.updateSnd(snd, msg);
    }

    if (variables.length > 0) {
      for (int i = 0; i < variables.length; i++) {
        if (!remove.contains(variables[i])) {
          if (variables.length >= i + 2) {
            Object var = variables[i + 1];
            if (var instanceof Boolean) {
              var =
                  (Boolean) var
                      ? LC.info_variables_True.getLocale()
                      : LC.info_variables_False.getLocale();
            }

            if (var instanceof LC) {
              var = ((LC) var).getText();
            }

            msg = outReplace(msg, variables[i], var);
          }

          i++;
        }
      }
    }

    msg = msg.replace("!prefix!", this.getM(LC.info_prefix));
    msg = filterNewLine(msg);
    return CMIChatColor.translate(msg);
  }

  public static String filterNewLine(String msg) {
    return msg != null && !msg.isEmpty() ? NEWLINE_PATTERN.matcher(msg).replaceAll("\n") : msg;
  }

  private String getM(LC lc) {
    String key = lc.getPt();
    String missing = "Missing locale for &7" + key + " ";
    String msg = "";
    if (this.Customlocale.isString(key)) {
      msg = this.Customlocale.getString(key);
    } else if (this.Customlocale.isList(key)) {
      msg = CMIList.listToString(this.Customlocale.getStringList(key));
    } else {
      msg = this.getEN().isString(key) ? this.getEN().getString(key) : missing;
    }

    return msg;
  }

  public List<String> updateSnd(Snd snd, List<String> msg) {
    int i = 0;

    for (int l = msg.size(); i < l; i++) {
      msg.set(i, this.updateSnd(snd, msg.get(i)));
    }

    return msg;
  }

  public String updateSnd(Snd snd, String msg) {
    if (msg == null) {
      return null;
    }

    if (!msg.contains("[")) {
      return filterNewLine(msg);
    }

    if (snd.getConsoleSender() != null) {
      String name = snd.getConsoleSender().getName();
      if (name.equalsIgnoreCase("Console") || name.equalsIgnoreCase(CMILib.getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "senderName", name);
      msg = replace(msg, "senderDisplayName", name);
      msg = replace(msg, "senderPrefix", "");
      msg = replace(msg, "senderSuffix", "");
    }

    if (snd.getPlayerSender() != null) {
      msg = this.replacePlayer("sender", snd.getPlayerSender(), snd.getPlayerTarget(), msg);
      if (snd.getPlayerSender().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerSender().getLocation(), msg);
      }
    }

    if (snd.getConsoleTarget() != null) {
      String name = snd.getConsoleTarget().getName();
      if (name.equalsIgnoreCase("Console") || name.equalsIgnoreCase(CMILib.getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "targetName", name);
      msg = replace(msg, "targetDisplayName", name);
    }

    if (snd.getPlayerTarget() != null) {
      msg = this.replacePlayer("", snd.getPlayerTarget(), snd.getPlayerSender(), msg);
      msg = this.replacePlayer("player", snd.getPlayerTarget(), snd.getPlayerSender(), msg);
      if (snd.getPlayerTarget().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerTarget().getLocation(), msg);
      }

      msg = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(snd.getPlayerTarget(), msg);
    }

    if (snd.getConsoleSource() != null) {
      String name = snd.getConsoleSource().getName();
      if (name.equalsIgnoreCase("Console") || name.equalsIgnoreCase(CMILib.getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "sourceName", name);
      msg = replace(msg, "sourceDisplayName", name);
    }

    if (snd.getSenderName() != null) {
      String name = snd.getSenderName();
      if (name.equalsIgnoreCase("Console") || name.equalsIgnoreCase(CMILib.getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "senderName", name);
      msg = replace(msg, "senderDisplayName", name);
    }

    if (snd.getTargetName() != null) {
      String name = snd.getTargetName();
      if (name.equalsIgnoreCase("Console") || name.equalsIgnoreCase(CMILib.getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "Name", name);
      msg = replace(msg, "DisplayName", name);
      msg = replace(msg, "playerName", name);
      msg = replace(msg, "playerDisplayName", name);
    }

    if (snd.getPlayerSource() != null) {
      msg = this.replacePlayer("source", snd.getPlayerSource(), snd.getPlayerTarget(), msg);
      if (snd.getPlayerSource().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerSource().getLocation(), msg);
      }
    }

    return filterNewLine(msg);
  }

  public String updateCmiSnd(com.Zrips.CMI.Containers.Snd snd, String msg) {
    if (msg == null) {
      return null;
    }

    if (!msg.contains("[")) {
      return filterNewLine(msg);
    }

    msg =
        replace(
            msg,
            "serverName",
            CMI.getInstance().getBungeeCordManager().isBungeeCord()
                ? CMI.getInstance().getBungeeCordManager().getThisServerName()
                : this.plugin.getReflectionManager().getServerName());
    if (snd.getConsoleSender() != null) {
      String name = snd.getConsoleSender().getName();
      if (name.equalsIgnoreCase("Console")
          || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "senderName", name);
      msg = replace(msg, "senderDisplayName", name);
      msg = replace(msg, "senderPrefix", "");
      msg = replace(msg, "senderSuffix", "");
    }

    if (snd.getPlayerSender() != null) {
      msg = this.replacePlayer("sender", snd.getPlayerSender(), snd.getPlayerTarget(), msg);
      if (snd.getPlayerSender().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerSender().getLocation(), msg);
      }
    }

    if (snd.getSenderUser() != null) {
      msg = this.replaceCmiUser("sender", snd.getSenderUser(), msg);
    }

    if (snd.getConsoleTarget() != null) {
      String name = snd.getConsoleTarget().getName();
      if (name.equalsIgnoreCase("Console")
          || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "targetName", name);
      msg = replace(msg, "targetDisplayName", name);
    }

    if (snd.getPlayerTarget() != null) {
      msg = this.replacePlayer("", snd.getPlayerTarget(), snd.getPlayerSender(), msg);
      msg = this.replacePlayer("player", snd.getPlayerTarget(), snd.getPlayerSender(), msg);
      if (snd.getPlayerTarget().isOnline() && snd.getPlayerTarget().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerTarget().getLocation(), msg);
      }
    }

    if (snd.getTargetUser() != null) {
      msg = this.replaceCmiUser("", snd.getTargetUser(), msg);
    }

    if (snd.getConsoleSource() != null) {
      String name = snd.getConsoleSource().getName();
      if (name.equalsIgnoreCase("Console")
          || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "sourceName", name);
      msg = replace(msg, "sourceDisplayName", name);
    }

    if (snd.getSenderName() != null) {
      String name = snd.getSenderName();
      if (name.equalsIgnoreCase("Console")
          || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "senderName", name);
      msg = replace(msg, "senderDisplayName", name);
    }

    if (snd.getTargetName() != null) {
      String name = snd.getTargetName();
      if (name.equalsIgnoreCase("Console")
          || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
        name = this.getM(LC.info_Console);
      }

      msg = replace(msg, "Name", name);
      msg = replace(msg, "DisplayName", name);
      msg = replace(msg, "playerName", name);
      msg = replace(msg, "playerDisplayName", name);
    }

    if (snd.getPlayerSource() != null) {
      msg = this.replacePlayer("source", snd.getPlayerSource(), snd.getPlayerTarget(), msg);
      if (snd.getPlayerSource().getLocation() != null) {
        msg = this.replacePlayer(snd.getPlayerSource().getLocation(), msg);
      }
    }

    if (snd.getSourceUser() != null) {
      msg = this.replaceCmiUser("source", snd.getSourceUser(), msg);
    }

    msg = this.cleanVariables("", msg);
    msg = this.cleanVariables("target", msg);
    msg = this.cleanVariables("source", msg);
    return filterNewLine(msg);
  }

  private String cleanVariables(String type, String msg) {
    msg = replace(msg, "serverName", "");
    msg = replace(msg, type + "offon", "");
    msg = replace(msg, type + "Prefix", "");
    return replace(msg, type + "Suffix", "");
  }

  public String replaceCmiUser(String type, CMIUser user, String msg) {
    msg =
        replace(
            msg,
            "serverName",
            CMI.getInstance().getBungeeCordManager().isBungeeCord()
                ? CMI.getInstance().getBungeeCordManager().getThisServerName()
                : this.plugin.getReflectionManager().getServerName());
    if (msg != null && user != null) {
      if (user.isOnline()) {
        msg = this.replacePlayer(type, user.getPlayer(), null, msg);
      }

      msg = replace(msg, type + "offon", this.getOffOn(user.isOnline()));
      msg = replace(msg, type + "Name", user.getName(false));
      msg = replace(msg, type + "Nick", user.getNickName());
      msg = replace(msg, type + "NickName", user.getNickName());
      msg = replace(msg, type + "DisplayName", user.getDisplayName(true));
      msg = replace(msg, type + "Prefix", CMIChatColor.translate(user.getPrefix()));
      msg = replace(msg, type + "Suffix", CMIChatColor.translate(user.getSuffix()));
      if (type.isEmpty()) {
        msg = replace(msg, "playerName", user.getName(false));
        msg = replace(msg, "playerNick", user.getNickName());
        msg = replace(msg, "playerNickName", user.getNickName());
        msg = replace(msg, "playerDisplayName", user.getDisplayName(true));
        msg = replace(msg, "Prefix", CMIChatColor.translate(user.getPrefix()));
        msg = replace(msg, "Suffix", CMIChatColor.translate(user.getSuffix()));
      }

      if (user.getLogOutLocation() != null) {
        msg = this.replacePlayer(type, user.getLogOutLocation(), msg);
      }

      return msg;
    } else {
      return msg;
    }
  }

  public String replacePlayer(String type, Player player, Player whoGets, String msg) {
    if (msg != null && player != null) {
      msg = replace(msg, type + "Name", player.getName());
      msg = replace(msg, type + "DisplayName", player.getDisplayName());
      msg = replace(msg, type + "offon", this.getOffOn(player, whoGets));
      msg = replace(msg, type + "Hp", (int) player.getHealth());
      msg = replace(msg, type + "MaxHp", (int) player.getMaxHealth());
      msg = replace(msg, type + "Hunger", player.getFoodLevel());
      if (player.getGameMode() != null) {
        msg =
            replace(
                msg,
                type + "GameMode",
                this.getMessage("info.variables." + player.getGameMode().name().toLowerCase()));
      }

      if (player.isOnline() && player.getLocation() != null) {
        msg = this.replacePlayer(type, player.getLocation(), msg);
      }

      return msg;
    } else {
      return msg;
    }
  }

  public String replacePlayer(String type, Location loc, String msg) {
    if (msg != null && loc != null) {
      msg = replace(msg, type + "X", loc.getBlockX());
      msg = replace(msg, type + "Y", loc.getBlockY());
      msg = replace(msg, type + "Z", loc.getBlockZ());
      msg = replace(msg, type + "Yaw", (int) loc.getYaw());
      msg = replace(msg, type + "Pitch", (int) loc.getPitch());
      if (loc.getWorld() != null) {
        msg = replace(msg, type + "WorldName", loc.getWorld().getName());
        msg = replace(msg, type + "World", loc.getWorld().getName());
      }

      return msg;
    } else {
      return msg;
    }
  }

  public String replacePlayer(Location loc, String msg) {
    if (msg != null && loc != null) {
      msg = replace(msg, "x", loc.getBlockX());
      msg = replace(msg, "y", loc.getBlockY());
      msg = replace(msg, "z", loc.getBlockZ());
      msg = replace(msg, "yaw", (int) loc.getYaw());
      msg = replace(msg, "pitch", (int) loc.getPitch());
      if (loc.getWorld() != null) {
        msg = replace(msg, "worldName", loc.getWorld().getName());
        msg = replace(msg, "world", loc.getWorld().getName());
      }

      return msg;
    } else {
      return msg;
    }
  }

  private static String outReplace(String msg, Object what, Object with) {
    if (what == null) {
      return msg;
    }

    if (with == null) {
      with = "";
    }

    return msg.replace(String.valueOf(what), String.valueOf(with));
  }

  private static String replace(String msg, Object what, Object with) {
    if (what == null) {
      return msg;
    }

    if (with == null) {
      with = "";
    }

    return msg.replaceAll(
        String.valueOf("(?i)(\\[" + what + "\\])"), Matcher.quoteReplacement(String.valueOf(with)));
  }

  public String getDefaultMessage(String key) {
    String missing = "Missing locale for " + key + " ";
    String msg = "";
    msg = this.getEN().isString(key) ? this.getEN().getString(key) : missing;
    StringBuilder s = new StringBuilder();
    if (msg.contains("/n")) {
      int i = 0;

      for (String one : msg.split("/n")) {
        i++;
        s.append(one);
        if (i < msg.split("/n").length) {
          s.append("\n  ");
        }
      }

      msg = s.toString();
    }

    return CMIChatColor.translate(msg);
  }

  public List<String> getMessageList(String key, Object... variables) {
    String missing = "Missing locale for " + key + " ";
    List<String> ls;
    if (this.Customlocale.isList(key)) {
      ls = this.Customlocale.getStringList(key);
    } else {
      ls =
          !this.getEN().getStringList(key).isEmpty()
              ? this.getEN().getStringList(key)
              : Arrays.asList(missing);
    }

    if (variables != null && variables.length > 0) {
      for (int i = 0; i < ls.size(); i++) {
        String msg = ls.get(i);

        for (int y = 0; y < variables.length; y += 2) {
          msg = msg.replace(String.valueOf(variables[y]), String.valueOf(variables[y + 1]));
        }

        msg = filterNewLine(msg);
        ls.set(i, CMIChatColor.translate(msg));
      }
    }

    int i = 0;

    for (String one : ls) {
      ls.set(i, CMIChatColor.translate(one.replace("!prefix!", this.getM(LC.info_prefix))));
      i++;
    }

    return ls;
  }

  public boolean isList(String key) {
    if (this.Customlocale != null && this.Customlocale.contains(key)) {
      return this.Customlocale.isList(key);
    } else {
      return this.getEN().contains(key) ? this.getEN().isList(key) : false;
    }
  }

  public boolean containsKey(String key) {
    return this.Customlocale != null && this.Customlocale.contains(key)
        ? true
        : this.getEN().contains(key);
  }

  public boolean isString(String key) {
    return this.getEN().isString(key);
  }

  public Set<String> getKeys(String path) {
    if (!this.lang.equalsIgnoreCase("EN")
        && this.Customlocale != null
        && this.Customlocale.isConfigurationSection(path)) {
      return this.Customlocale.getConfigurationSection(path).getKeys(false);
    } else {
      return this.getEN() != null && this.getEN().isConfigurationSection(path)
          ? this.getEN().getConfigurationSection(path).getKeys(false)
          : new HashSet<>();
    }
  }

  public String getOffOn(Player player, Player whoGets) {
    return this.getOffOn(
        whoGets == null ? player.isOnline() : whoGets.canSee(player) && player.isOnline());
  }

  public String getOffOn(Player player) {
    return this.getOffOn(player.isOnline());
  }

  public String getOffOn(boolean state) {
    return state ? LC.info_variables_Online.getLocale() : LC.info_variables_Offline.getLocale();
  }
}
