package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class maintenance implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Server is in maintenance mode. Come back later.");
    configReader.get(
        "set",
        "{gcp}Server maintenance mode set to {gcs}[state] {gcp}with message: {gcs}[message]");
  }

  @CAnnotation(
      priority = 110,
      info = "Set server into maintenance mode",
      args = "(true/false) (message)",
      tab = "true%%false",
      explanation = "cmi.command.maintenance.bypass - to bypass maintenance mode",
      regVar = -66,
      consoleVar = -66,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Boolean maintenanceEnabled = null;
    String string = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string.isEmpty()) {
        if (arg.equalsIgnoreCase("true")) {
          maintenanceEnabled = true;
          continue;
        }

        if (arg.equalsIgnoreCase("false")) {
          maintenanceEnabled = false;
          continue;
        }
      }

      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + arg;
    }

    if (!string.isEmpty()) {
      cmi.getConfigManager().setMaintenanceMessage(string.replace("\\n", "\n"));
    }

    String kickMessage =
        cmi.getConfigManager().getMaintenanceMessage() == null
            ? CMILC.getIM("maintenance", "feedback")
            : cmi.getConfigManager().getMaintenanceMessage();
    if (maintenanceEnabled != null) {
      cmi.getConfigManager().setMaintenance(maintenanceEnabled);
    } else {
      cmi.getConfigManager().setMaintenance(!cmi.getConfigManager().isMaintenance());
    }

    if (cmi.getConfigManager().isMaintenance()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (!PermissionsManager.CMIPerm.command_maintenance_bypass.hasPermission(player)) {
          if (Config.maintenanceAutoKick) {
            player.kickPlayer(kickMessage.replace("\\n", "\n"));
          }
        } else {
          String rawBossBarText =
              cmi.getConfigManager().getMaintenanceMessage() == null
                  ? CMILC.info_maintenance.getLocale()
                  : cmi.getConfigManager().getMaintenanceMessage();
          String singleLineBossBarText = rawBossBarText.replace("\n", " ");
          if (Config.maintenanceBossbar) {
            try {
              CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
              String bossBarText =
                  cmi.getPlaceholderAPIManager().updatePlaceHolders(player, singleLineBossBarText);
              BossBarInfo bossBarInfo = cmiUser.getBossBar("CMIMaintenance");
              if (bossBarInfo == null) {
                bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIMaintenance");
              }

              bossBarInfo.setKeepForTicks(-1);
              bossBarInfo.setColor(BarColor.BLUE);
              bossBarInfo.setTitleOfBar(bossBarText);
              bossBarInfo.setStyle(BarStyle.SOLID);
              cmiUser.addBossBar(bossBarInfo);
            } catch (Exception | Error value3) {
            }
          }
        }
      }
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        try {
          CMIUser.getUser(onlinePlayer).removeBossBar("CMIMaintenance");
        } catch (Exception | Error value4) {
        }
      }
    }

    CMILC.info(
        this,
        commandSender,
        "set",
        "[state]",
        cmi.getConfigManager().isMaintenance(),
        "[message]",
        kickMessage);
    return true;
  }
}
