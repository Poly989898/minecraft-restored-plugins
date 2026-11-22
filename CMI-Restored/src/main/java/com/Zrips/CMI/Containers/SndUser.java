package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SndUser {
  private String name = null;
  private CMIUser user = null;
  private CommandSender commandSender = null;
  private List<String> types = new ArrayList<>();

  public SndUser(String... strings) {
    String[] argsCopy = strings;
    int value = strings.length;

    for (int index = 0; index < value; index++) {
      String string = argsCopy[index];
      this.types.add(string);
    }
  }

  public SndUser(String string, String targetName) {
    this.types.add(string);
    this.types.add(targetName);
  }

  public Player getPlayer() {
    return this.user == null ? null : this.user.getPlayer(false);
  }

  public CommandSender getCommandSender() {
    return this.commandSender;
  }

  public void reset(CMIUser cmiUser) {
    if (cmiUser != null && this.name != null && !this.name.equals(cmiUser.getName())) {
      this.name = cmiUser.getName();
    }

    if (cmiUser != null
        && this.commandSender != null
        && !this.commandSender.getName().equals(cmiUser.getName())) {
      this.commandSender = null;
    }

    this.user = cmiUser;
  }

  public void reset(CommandSender sender) {
    if (sender != null && this.name != null && !this.name.equals(sender.getName())) {
      this.name = sender.getName();
    }

    if (sender instanceof Player) {
      this.user = CMIUser.getUser((Player) sender);
    } else {
      this.user = null;
    }

    this.commandSender = sender;
  }

  public void reset(String string) {
    if (string != null && this.user != null && !this.user.getName().equals(string)) {
      this.user = null;
    }

    if (string != null
        && this.commandSender != null
        && !this.commandSender.getName().equals(string)) {
      this.commandSender = null;
    }

    this.name = string;
  }

  public void reset() {
    this.name = null;
    this.user = null;
    this.commandSender = null;
  }

  public SndUser set(Player player) {
    this.reset(player);
    if (player == null) {
      return this;
    }

    this.commandSender = player;
    this.name = player.getName();
    if (this.user == null) {
      this.user = CMIUser.getUser(player);
    }

    return this;
  }

  public SndUser set(CMIUser cmiUser) {
    this.reset(cmiUser);
    this.user = cmiUser;
    if (cmiUser == null) {
      return this;
    }

    this.name = cmiUser.getName();
    return this;
  }

  public SndUser set(CommandSender sender) {
    this.reset(sender);
    if (sender instanceof Player) {
      if (this.user == null) {
        this.user = CMIUser.getUser((Player) sender);
      }

      this.name = sender.getName();
      this.commandSender = sender;
    } else if (sender instanceof ConsoleCommandSender) {
      this.commandSender = sender;
    } else if (sender instanceof BlockCommandSender
        || sender instanceof RemoteConsoleCommandSender) {
      this.commandSender = Bukkit.getConsoleSender();
    }

    return this;
  }

  public CMIUser getUser() {
    return this.user;
  }

  public void setUser(CMIUser cmiUser) {
    this.reset(cmiUser);
    this.user = cmiUser;
  }

  public String getName() {
    return this.name;
  }

  public SndUser setName(String string) {
    this.reset(string);
    this.name = string;
    return this;
  }

  public String updateLocale(String string) {
    return this.updateLocale(string, null);
  }

  public String updateLocale(String string, @Nullable CommandSender sender) {
    if (string != null && string.contains("[")) {
      if (this.getUser() != null) {
        string = this.replaceUser(this.user, sender, string);
      } else if (this.getCommandSender() != null) {
        String senderName = this.getCommandSender().getName();
        if (senderName.equalsIgnoreCase("Console")
            || senderName.equalsIgnoreCase(
                CMI.getInstance().getPlayerManager().getFakeUserName())) {
          senderName = LC.info_Console.getLocale(new Object[0]);
        }

        string = replace(string, "name", senderName);

        for (String type : this.types) {
          string = replace(string, type + "Name", senderName);
          string = replace(string, type + "DisplayName", senderName);
          string = replace(string, type + "Prefix", "");
          string = replace(string, type + "Suffix", "");
        }

        string = replace(string, "prefix", "");
        string = replace(string, "suffix", "");
      }

      if (this.getName() != null) {
        String name = this.getName();
        if (name.equalsIgnoreCase("Console")
            || name.equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
          name = LC.info_Console.getLocale(new Object[0]);
        }

        for (String type : this.types) {
          string = replace(string, type + "Name", name);
          string = replace(string, type + "DisplayName", name);
          string = replace(string, type + "Prefix", "");
          string = replace(string, type + "Suffix", "");
        }
      }

      return string;
    } else {
      return string;
    }
  }

  private String replaceUser(CMIUser cmiUser, @Nullable CommandSender sender, String string) {
    string =
        replace(
            string,
            "serverName",
            CMI.getInstance().getBungeeCordManager().isBungeeCord()
                ? CMI.getInstance().getBungeeCordManager().getThisServerName()
                : CMILib.getInstance().getReflectionManager().getServerName());
    if (string == null || cmiUser == null) {
      return string;
    }

    if (cmiUser.isOnline()) {
      for (String type : this.types) {
        string = this.replacePlayer(type, cmiUser.getPlayer(false), sender, string);
      }

      return string;
    } else {
      for (String type : this.types) {
        string = replace(string, type + "offon", this.offOn(cmiUser.getPlayer(false), sender));
        string = this.userTranslate(cmiUser, type, string);
      }

      if (cmiUser.getLogOutLocation() != null) {
        for (String type : this.types) {
          string = replaceLocation(type, cmiUser.getLogOutLocation(), string);
        }
      }

      return string;
    }
  }

  private String userTranslate(CMIUser cmiUser, String string, String replacement) {
    if (cmiUser
        .getName()
        .equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName())) {
      replacement = replace(replacement, string + "Name", LC.info_Console.getLocale(new Object[0]));
      replacement = replace(replacement, string + "Nick", LC.info_Console.getLocale(new Object[0]));
      replacement =
          replace(replacement, string + "NickName", LC.info_Console.getLocale(new Object[0]));
      replacement =
          replace(replacement, string + "DisplayName", LC.info_Console.getLocale(new Object[0]));
      replacement = replace(replacement, string + "Prefix", "");
      replacement = replace(replacement, string + "Suffix", "");
    } else {
      replacement = replace(replacement, string + "Name", cmiUser.getName());
      replacement = replace(replacement, string + "Nick", cmiUser.getNickName());
      replacement = replace(replacement, string + "NickName", cmiUser.getDisplayName(true));
      replacement = replace(replacement, string + "DisplayName", cmiUser.getDisplayName(true));
      replacement =
          replace(replacement, string + "Prefix", CMIChatColor.translate(cmiUser.getPrefix()));
      replacement =
          replace(replacement, string + "Suffix", CMIChatColor.translate(cmiUser.getSuffix()));
    }

    replacement =
        replace(
            replacement, string + "Lvl", cmiUser.getPlayer(false) != null ? cmiUser.getLevel() : 0);
    return replace(
        replacement, string + "Exp", cmiUser.getPlayer(false) != null ? cmiUser.getTotalExp() : 0L);
  }

  private String offOn(Player player, CommandSender sender) {
    if (player != null && sender instanceof Player) {
      Player senderPlayer = (Player) sender;
      return !senderPlayer.canSee(player) ? CMILC.getOffOn(false) : CMILC.getOffOn(player);
    } else {
      return CMILC.getOffOn(player);
    }
  }

  private String replacePlayer(
      String string, Player player, CommandSender sender, String replacement) {
    if (replacement != null && player != null) {
      replacement = replace(replacement, string + "offon", this.offOn(player, sender));
      replacement = replace(replacement, string + "Hp", (int) player.getHealth());
      replacement = replace(replacement, string + "MaxHp", (int) CMIPlayer.getMaxHealth(player));
      replacement = replace(replacement, string + "Hunger", player.getFoodLevel());
      if (player.getGameMode() != null) {
        replacement =
            replace(
                replacement,
                string + "GameMode",
                CMILib.getInstance()
                    .getLM()
                    .getMessage(
                        "info.variables." + player.getGameMode().name().toLowerCase(),
                        new Object[0]));
      }

      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null) {
        for (String type : this.types) {
          replacement = this.userTranslate(cmiUser, type, replacement);
        }
      }

      if (player.getLocation() != null) {
        replacement = replaceLocation(string, player.getLocation(), replacement);
      }

      return replacement;
    } else {
      return replacement;
    }
  }

  public static String replaceLocation(String string, Location location, String replacement) {
    if (replacement == null || !replacement.contains("[")) {
      return replacement;
    }

    if (location == null) {
      return replacement;
    }

    replacement = replace(replacement, string + "X", location.getBlockX());
    replacement = replace(replacement, string + "Y", location.getBlockY());
    replacement = replace(replacement, string + "Z", location.getBlockZ());
    replacement = replace(replacement, string + "Yaw", (int) location.getYaw());
    replacement = replace(replacement, string + "Pitch", (int) location.getPitch());
    if (location.getWorld() != null) {
      replacement = replace(replacement, string + "WorldName", location.getWorld().getName());
      replacement = replace(replacement, string + "World", location.getWorld().getName());
    }

    return replacement;
  }

  private static String replace(String string, Object object, Object replacementValue) {
    if (string != null && string.contains("[") && object != null) {
      String placeholder = "[" + object + "]";
      String replacementText =
          replacementValue == null ? "" : CTextManager.clean(String.valueOf(replacementValue));
      int value = placeholder.length();
      StringBuilder stringBuilder = new StringBuilder(string.length());
      int index = 0;

      while (index < string.length()) {
        if (index + value <= string.length()
            && string.regionMatches(true, index, placeholder, 0, value)) {
          stringBuilder.append(replacementText);
          index += value;
        } else {
          stringBuilder.append(string.charAt(index));
          index++;
        }
      }

      return stringBuilder.toString();
    } else {
      return string;
    }
  }
}
