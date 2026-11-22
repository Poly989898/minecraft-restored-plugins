package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.LightFix.LightFixInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Region.CMIRegion;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class lfix implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "NoMore", "{gcp}Range can't be more than server view range. Changed to maximum allowed.");
    configReader.get("startFixing", "{gcp}Found {gcs}[amount] {gcp}chunks to fix. Starting!");
    configReader.get(
        "InfoShow",
        "{gcp}TPS: {gcs}[tps] {gcp}Left {gcs}[left] {gcp}({gcs}[min]m:[sec]s{gcp}{gcp}) Speed:"
            + " {gcs}[speed]");
    configReader.get("Finished", "{gcp}Light fixing has finished");
    configReader.get("Stopped", "{gcp}Stopped");
  }

  @CAnnotation(
      info = "Fix light in chunks around you",
      args = "(range) (playerName) (stop/stopall)",
      tab = "halfViewRange",
      regVar = {1, 2},
      consoleVar = {1, 2})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    LightFixInfo lightFixInfo = new LightFixInfo();
    Integer integer = 3;
    Player player = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = commandArgs[argIndex];

      try {
        integer = Integer.parseInt(string);
      } catch (NumberFormatException numberFormatException) {
        if (string.equalsIgnoreCase("g")) {
          integer = null;
        } else {
          if (string.equalsIgnoreCase("stop")) {
            LightFixInfo runningLightFix =
                cmi.getLightFixManager()
                    .LFInfo
                    .get(
                        commandSender instanceof Player
                            ? ((Player) commandSender).getUniqueId()
                            : cmi.getPlayerManager().getFakeUserUUID());
            if (runningLightFix != null) {
              runningLightFix.setRunning(false);
              if (runningLightFix.getScheduleTask() != null) {
                runningLightFix.getScheduleTask().cancel();
              }

              if (runningLightFix.getPlayer().isOnline()) {
                runningLightFix
                    .getPlayer()
                    .sendMessage(cmi.getLM().getMessage("command.lfix.info.Stopped"));
              }
            }

            return true;
          }

          if (string.equalsIgnoreCase("stopall")) {
            for (var entry : cmi.getLightFixManager().LFInfo.entrySet()) {
              LightFixInfo activeLightFix = (LightFixInfo) entry.getValue();
              activeLightFix.setRunning(false);
              if (activeLightFix.getScheduleTask() != null) {
                activeLightFix.getScheduleTask().cancel();
              }

              CMILC.info("lfix", activeLightFix.getPlayer(), "Stopped");
            }

            return true;
          }

          player = CMIUser.getOnlinePlayer(string);
        }
      }
    }

    try {
      Bukkit.spigot().getConfig();
    } catch (Exception exception) {
      commandSender.sendMessage(CMIChatColor.RED + "Works only with spigot servers");
      return false;
    }

    if (player == null && commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    if (player == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Chunk chunk = player.getLocation().getChunk();
    if (integer != null
        && integer > Bukkit.getViewDistance()
        && !PermissionsManager.CMIPerm.command_lfix_admin.hasPermission(commandSender)) {
      CMILC.info(this, commandSender, "NoMore");
      integer = Bukkit.getViewDistance();
    }

    if (integer != null && integer > 10000) {
      integer = null;
    }

    WorldInfo worldInfo =
        cmi.getRegionManager()
            .getWorldInfoInRange(player.getWorld(), new CMIRegion(chunk), integer);
    if (worldInfo == null) {
      CMILC.info_ChunksLoading.sendMessage(commandSender);
      return true;
    }

    lightFixInfo.setRI(worldInfo);
    CMIActionBar.send(
        player, CMILC.getIM(this, "startFixing", "[amount]", worldInfo.getTotalChunks()));
    lightFixInfo.setPlayer(player);
    lightFixInfo.setStartTime();
    lightFixInfo.setRunning(true);
    lightFixInfo.setShowInfo(System.currentTimeMillis());
    if (cmi.getLightFixManager().LFInfo.containsKey(player.getUniqueId())
        && cmi.getLightFixManager().LFInfo.get(player.getUniqueId()).getScheduleTask() != null) {
      cmi.getLightFixManager().LFInfo.get(player.getUniqueId()).getScheduleTask().cancel();
    }

    cmi.getLightFixManager().LFInfo.put(player.getUniqueId(), lightFixInfo);
    cmi.getLightFixManager().start(lightFixInfo);
    return true;
  }
}
