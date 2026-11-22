package com.Zrips.CMI.Modules.CmdCooldown;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.SpecializedCommands.SpecCMDCooldown;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CooldownManager {
  private HashMap<String, CMICmdCooldown> cooldowns = new HashMap<>();
  CMI plugin;
  HashMap<UUID, CmdCooldown> playerCustomCooldowns = new HashMap<>();
  CmdCooldown globalCooldowns = new CmdCooldown();

  public CooldownManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Cooldowns.Enabled",
        new String[] {
          "You can enable any command cooldown to prevent instant usage of it",
          "cmi heal:180 means that player can use /cmi heal command only once every 180 seconds",
          "if cooldown set to -1 then this command can be performed only one time",
          "Administration can bypass limitations with cmi.cooldownbypass.[commandname] permission"
              + " node",
          "Always use full command name and not its alias",
          "----------",
          "Alternatively you can use "
              + PermissionsManager.CMIPerm.cooldown_$1_$2.getPermission("[some_Command]", "[timer]")
              + " permission node. For example: cmi.cooldown.cmi_heal.30 which will set 30 second"
              + " cooldown on /cmi heal command",
          "If you want to apply cooldown on command when variable is provided, add extra _ at end."
              + " For example cmi.cooldown.cmi_heal_.30 which will only use this cooldown when"
              + " healing some one else",
          "KEEP IN MIND that for permission to work you need to set base command cooldown in this"
              + " list, otherwise permission node will have no effect",
          "-----",
          "ATTENTION! If you have command like \"/cmi home\" and you want to prevent teleportation"
              + " to home but allow gui opening without restrictions, use space after command, in"
              + " example \"cmi home :10\""
        });
    long longValue = System.currentTimeMillis();
    boolean flag = configReader.get("Cooldowns.Enabled", false);
    var list = configReader.get("Cooldowns.List", Arrays.asList("cmi heal:180", "cmi feed:120"));
    this.getCooldowns().clear();
    if (flag) {
      for (String string : list) {
        if (string.contains(":")) {
          int value = -2;

          try {
            value = Integer.parseInt(string.split(":")[1]);
          } catch (NumberFormatException numberFormatException) {
            continue;
          }

          if (value >= -1) {
            this.addCooldown(string.split(":")[0].toLowerCase(), value);
          }
        }
      }
    }

    this.plugin.loadMessage(
        this.plugin.getCooldownManager().getCooldownCount(),
        "cooldowns",
        System.currentTimeMillis() - longValue);
  }

  public int getCooldownCount() {
    return this.cooldowns.size();
  }

  @Deprecated
  public void addCD(String string, Long time) {
    this.addCooldown(new CMICmdCooldown(string, time.intValue()));
  }

  public void addCooldown(String string, int value) {
    this.addCooldown(new CMICmdCooldown(string, value));
  }

  public void addCooldown(CMICmdCooldown cmiCmdCooldown) {
    this.cooldowns.put(cmiCmdCooldown.getCommand().toLowerCase(), cmiCmdCooldown);
  }

  private CMICmdCooldown getCooldown(String string) {
    string = string.toLowerCase();
    string = string.startsWith(":") ? string.substring(1) : string;
    CommandAlias commandAlias = this.plugin.getAliasManager().getAliasForCommand(string);
    if (commandAlias != null && commandAlias.getType() != CommandAliasType.custom) {
      return null;
    }

    CMICmdCooldown cmiCmdCooldown = null;

    for (var entry : this.cooldowns.entrySet()) {
      if (string.equalsIgnoreCase((String) entry.getKey())) {
        cmiCmdCooldown = (CMICmdCooldown) entry.getValue();
        break;
      }
    }

    if (cmiCmdCooldown != null) {
      return cmiCmdCooldown;
    }

    CMICmdCooldown cmiCmdCooldownValue = null;

    for (var varSecond : this.cooldowns.entrySet()) {
      if (string.startsWith((String) varSecond.getKey())) {
        String text = string.substring(((String) varSecond.getKey()).length());
        if (text.isEmpty() || text.startsWith(" ") || ((String) varSecond.getKey()).endsWith(" ")) {
          cmiCmdCooldownValue = (CMICmdCooldown) varSecond.getValue();
          break;
        }
      }
    }

    return cmiCmdCooldownValue;
  }

  private static Long getUsedTime(String string, CmdCooldown cmdCooldown) {
    return getUsedTime(string, cmdCooldown, false);
  }

  private static Long getUsedTime(String string, CmdCooldown cmdCooldown, boolean flag) {
    string = string.toLowerCase();
    String text = getCommandMatch(string, cmdCooldown, flag);
    if (text != null) {
      CmdCooldown.CMICooldown cmiCooldown = cmdCooldown.getList().get(text);
      if (cmiCooldown != null) {
        return cmiCooldown.getInitialized();
      }
    }

    return null;
  }

  private static String getCommandMatch(String string, CmdCooldown cmdCooldown) {
    return getCommandMatch(string, cmdCooldown, false);
  }

  private static String getCommandMatch(String string, CmdCooldown cmdCooldown, boolean flag) {
    string = string.toLowerCase();

    for (var entry : cmdCooldown.getList().entrySet()) {
      if (string.equals(entry.getKey())) {
        return (String) entry.getKey();
      }
    }

    if (flag) {
      return null;
    }

    for (var varSecond : cmdCooldown.getList().entrySet()) {
      if (string.startsWith((String) varSecond.getKey())) {
        return (String) varSecond.getKey();
      }
    }

    for (var varThird : cmdCooldown.getList().entrySet()) {
      if (string.startsWith((String) varThird.getKey())) {
        String text = string.substring(((String) varThird.getKey()).length());
        if (text.isEmpty() || text.startsWith(" ") || ((String) varThird.getKey()).endsWith(" ")) {
          return (String) varThird.getKey();
        }
      }
    }

    return null;
  }

  public boolean removeCooldown(CommandSender commandSender, String string) {
    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    if (commandSender != null && commandSender instanceof Player) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
      if (cmiUser == null) {
        return false;
      }

      CMICmdCooldown cmiCmdCooldown = this.getCooldown(string);
      if (cmiCmdCooldown == null) {
        return true;
      }

      cmiUser.getCommandCooldowns().removeCooldown(cmiCmdCooldown.getCommand());
      return true;
    } else {
      return false;
    }
  }

  @Deprecated
  public boolean isCmdInCooldown(CommandSender commandSender, String string) {
    return this.canUseCmd(commandSender, string);
  }

  public boolean canUseCmd(CommandSender commandSender, String string) {
    return this.canUseCmd(commandSender, string, true, false);
  }

  public boolean canUseCmd(
      CommandSender commandSender, String string, boolean flag, boolean flagSecond) {
    if (!CMIModule.cooldown.isEnabled()) {
      return true;
    }

    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    string = string.endsWith(" ") ? string.substring(0, string.length() - 1) : string;
    string =
        string.startsWith(CommandsHandler.getLabel() + " :")
            ? CommandsHandler.getLabel()
                + " "
                + string.substring((CommandsHandler.getLabel() + " :").length())
            : string;
    string =
        string.startsWith(CommandsHandler.getLabel() + ":" + CommandsHandler.getLabel())
            ? CommandsHandler.getLabel()
                + string.substring(CommandsHandler.getLabel().length() * 2 + 1)
            : string;
    if (string.split(" ", 2)[0].contains(":")) {
      string = string.split(":", 2)[1];
    }

    if (!(commandSender instanceof Player)) {
      return true;
    }

    CMIUser cmiUser = CMIUser.getUser((Player) commandSender);
    if (cmiUser == null) {
      return true;
    }

    CMICmdCooldown cmiCmdCooldown = this.getCooldown(string);
    if (cmiCmdCooldown != null
        && !PermissionsManager.CMIPerm.cooldownbypass_$1.hasPermission(
            commandSender, cmiCmdCooldown.getSubCommand().replace(" ", ""))) {
      CmdCooldown cmdCooldown = cmiUser.getCommandCooldowns();
      Long time =
          getUsedTime(string, cmdCooldown, cmiCmdCooldown.getCommand().equalsIgnoreCase(string));
      if (time != null) {
        if (cmiCmdCooldown.getCooldownSeconds() == -1) {
          if (flag) {
            CMILC.info_CooldownOneTime.sendMessage(commandSender);
          }

          return false;
        }

        long longValue = cmiCmdCooldown.getCooldownSeconds();
        string =
            getCommandMatch(
                string, cmdCooldown, cmiCmdCooldown.getCommand().equalsIgnoreCase(string));
        if (string != null) {
          PermissionInfo permissionInfo =
              this.plugin
                  .getPermissionsManager()
                  .getPermissionInfo(
                      (Player) commandSender,
                      PermissionsManager.CMIPerm.cooldown_$1_$2.getPermission(
                          string.replace(" ", "_"), ""),
                      false);
          if (permissionInfo != null && permissionInfo.getMinValue() != null) {
            longValue =
                (long)
                    (permissionInfo.getMinValue() < longValue
                        ? permissionInfo.getMinValue()
                        : longValue);
          }
        }

        long timeSecond = System.currentTimeMillis() - (time + longValue * 1000L) + 50L;
        if (timeSecond < 0L) {
          if (flag) {
            CMILC.info_Cooldowns.sendMessage(
                commandSender,
                "[cmd]",
                cmiCmdCooldown.getSubCommand(),
                "[time]",
                CMITimeManager.to24hourShort(-timeSecond + 1000L));
            CMISoundPreset.CommandCooldown.play((Player) commandSender);
          }

          return false;
        }
      }

      if (flagSecond) {
        this.recordCommandUsage(commandSender, string);
      }

      return true;
    } else {
      return true;
    }
  }

  public void recordCommandUsage(CommandSender commandSender, String string) {
    if (commandSender instanceof Player && CMIModule.cooldown.isEnabled()) {
      if (string.startsWith("/")) {
        string = string.substring(1);
      }

      CMIUser cmiUser = CMIUser.getUser((Player) commandSender);
      if (cmiUser != null) {
        CMICmdCooldown cmiCmdCooldown = this.getCooldown(string);
        if (cmiCmdCooldown != null) {
          cmiUser
              .getCommandCooldowns()
              .addCooldown(
                  cmiCmdCooldown.getCommand(),
                  Long.valueOf(System.currentTimeMillis()),
                  cmiCmdCooldown.getCooldownSeconds());
        }
      }
    }
  }

  public boolean canUseSpecCommandGlobal(
      CommandSender commandSender, SpecCMDCooldown specCMDCooldown, boolean flag) {
    Long time = getUsedTime(specCMDCooldown.getCmd(), this.globalCooldowns);
    if (time != null) {
      if (specCMDCooldown.getCD() == -1.0) {
        if (flag) {
          CMILC.info_CooldownOneTime.sendMessage(commandSender);
        }

        return false;
      }

      long longValue = specCMDCooldown.untilNextUse(time);
      if (longValue > 0L) {
        if (flag) {
          CMILC.info_specializedCooldowns.sendMessage(
              commandSender,
              "[cmd]",
              specCMDCooldown.getCmd(),
              "[time]",
              CMITimeManager.to24hourShort(longValue + 1000L));
        }

        return false;
      }
    }

    this.globalCooldowns.addCooldown(
        specCMDCooldown.getCmd().toLowerCase(),
        Long.valueOf(System.currentTimeMillis()),
        specCMDCooldown.getCD());
    return true;
  }

  public boolean canUseSpecCommand(Player player, SpecCMDCooldown specCMDCooldown, boolean flag) {
    if (player == null) {
      return true;
    }

    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      return true;
    }

    CmdCooldown cmdCooldown = cmiUser.getCommandCooldowns();
    Long time = getUsedTime(specCMDCooldown.getCmd(), cmdCooldown);
    if (time != null) {
      if (specCMDCooldown.getCD() == -1.0) {
        CMILC.info_CooldownOneTime.sendMessage(player);
        return false;
      }

      long longValue = specCMDCooldown.untilNextUse(time);
      if (longValue > 0L) {
        if (flag) {
          CMILC.info_specializedCooldowns.sendMessage(
              player,
              "[cmd]",
              specCMDCooldown.getCmd(),
              "[time]",
              CMITimeManager.to24hourShort(longValue + 1000L));
        }

        return false;
      }
    }

    cmdCooldown.addCooldown(
        specCMDCooldown.getCmd().toLowerCase(),
        Long.valueOf(System.currentTimeMillis()),
        specCMDCooldown.getCD());
    cmiUser.addForDelayedSave();
    return true;
  }

  public HashMap<String, CMICmdCooldown> getCooldowns() {
    return this.cooldowns;
  }
}
