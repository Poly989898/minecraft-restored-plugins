package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class nameplate implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have updated nameplate for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("targetfeedback", "{gcp}Your nameplate got updated");
    configReader.get("namePlate", "{gcp}[playerDisplayName] name plate: {gcs}[namePlate]");
  }

  @CAnnotation(
      priority = 70,
      info = "Set players name plate prefix, suffix or its color",
      args =
          "(playerName) (-pref:[some_prefix]) (-suf:[some_suffix]) (-c:[colorCode]) (reset) (-s)",
      tab = {"playername", "reset%%-pref:", "%%-suf:", "%%-c:"},
      explanation = "Use _ to add space and __ to add underscore for placeholder and similar",
      regVar = -666,
      consoleVar = -100,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      commandSender.sendMessage(CMIChatColor.RED + "Only for 1.9+ servers");
      return false;
    }

    if (Config.DisableTeamManagement) {
      CMILC.info_TeamManagementDisabled.sendMessage(commandSender);
      return null;
    }

    boolean flag = false;
    String string = null;
    String prefix = null;
    String suffix = null;
    String nameColor = null;
    boolean clearPrefix = false;
    boolean clearSuffix = false;
    boolean clearColor = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-c:")) {
        nameColor = arg.substring("-c:".length());
      } else if (arg.startsWith("reset")) {
        clearColor = true;
      } else if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if ((arg.startsWith("-pref:") || clearPrefix) && !arg.startsWith("-suf:")) {
        if (prefix == null) {
          prefix = "";
        }

        if (arg.startsWith("-pref:")) {
          prefix = prefix + arg.substring("-pref:".length());
        } else {
          prefix = prefix + " " + arg;
        }

        clearPrefix = true;
      } else if (!arg.startsWith("-suf:") && !clearSuffix) {
        if (string == null) {
          string = arg;
        }
      } else {
        if (suffix == null) {
          suffix = "";
        }

        if (arg.startsWith("-suf:")) {
          suffix = suffix + arg.substring("-suf:".length());
        } else {
          suffix = suffix + " " + arg;
        }

        clearPrefix = false;
        clearSuffix = true;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    if (strings.length == 0 || string != null && strings.length == 1) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      String joined =
          (cmiUser.getNamePlateNameColor() == null ? "" : cmiUser.getNamePlateNameColor())
              + (cmiUser.getNamePlatePrefix() == null ? "" : cmiUser.getNamePlatePrefix())
              + cmiUser.getName()
              + (cmiUser.getNamePlateSuffix() == null ? "" : cmiUser.getNamePlateSuffix());
      CMILC.info(this, commandSender, "namePlate", snd, "[namePlate]", joined);
      return true;
    }

    if (clearColor) {
      prefix = "";
      suffix = "";
      nameColor = "";
    }

    if (prefix == null) {
      prefix = cmiUser.getNamePlatePrefix();
    } else {
      prefix = CMIText.replaceUnderScoreSpace(prefix);
    }

    if (nameColor != null) {
      if (CMIChatColor.getColor(nameColor) != null) {
        cmiUser.setNamePlateNameColor(CMIChatColor.getColor(nameColor).getColor());
      } else {
        cmiUser.setNamePlateNameColor(null);
        cmiUser.setGlow(cmiUser.getGlow(), false);
      }
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      if (prefix != null
          && prefix.length() > (cmiUser.getNamePlateNameColor() != null ? 254 : 256)) {
        prefix = prefix.substring(0, cmiUser.getNamePlateNameColor() != null ? 254 : 256);
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (prefix != null && prefix.length() > (cmiUser.getNamePlateNameColor() != null ? 62 : 64)) {
        prefix = prefix.substring(0, cmiUser.getNamePlateNameColor() != null ? 62 : 64);
      }
    } else if (prefix != null
        && prefix.length() > (cmiUser.getNamePlateNameColor() != null ? 14 : 16)) {
      prefix = prefix.substring(0, cmiUser.getNamePlateNameColor() != null ? 14 : 16);
    }

    if (suffix == null) {
      suffix = cmiUser.getNamePlateSuffix();
    } else {
      suffix = CMIText.replaceUnderScoreSpace(suffix);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      if (suffix != null && suffix.length() >= 256) {
        suffix = suffix.substring(0, 255);
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (suffix != null && suffix.length() >= 64) {
        suffix = suffix.substring(0, 63);
      }
    } else if (suffix != null && suffix.length() > 15) {
      suffix = suffix.substring(0, 15);
    }

    cmiUser.setNamePlatePrefix(prefix);
    cmiUser.setNamePlateSuffix(suffix);
    cmiUser.reapplyNamePlate();
    Snd sound = new Snd().setSender(commandSender).setTarget(player);
    cmi.save(player);
    if (!flag) {
      CMILC.info(this, commandSender, "feedback", sound);
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(this, player, "targetfeedback", sound);
    }

    return true;
  }
}
