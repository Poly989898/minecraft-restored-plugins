package com.Zrips.CMI.Modules.FindBiome;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Region.CMIRegion;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.findbiome;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class FindBiomeManager {
  public ConcurrentHashMap<UUID, FindBiomeInfo> FindBiomeInfo = new ConcurrentHashMap<>();
  private CMI plugin;

  public FindBiomeManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean stop(Player player) {
    if (this.FindBiomeInfo.containsKey(player.getUniqueId())) {
      FindBiomeInfo findBiomeInfo = this.FindBiomeInfo.get(player.getUniqueId());
      findBiomeInfo.setRunning(false);
      if (findBiomeInfo.getScheduleTask() != null) {
        findBiomeInfo.getScheduleTask().cancel();
      }

      return true;
    } else {
      return false;
    }
  }

  public void start(FindBiomeInfo findBiomeInfo) {
    if (this.FindBiomeInfo.containsKey(findBiomeInfo.getPlayer().getUniqueId())) {
      FindBiomeInfo biomeInfo = this.FindBiomeInfo.remove(findBiomeInfo.getPlayer().getUniqueId());
      biomeInfo.setRunning(false);
      if (biomeInfo.getScheduleTask() != null) {
        biomeInfo.getScheduleTask().cancel();
      }
    }

    this.FindBiomeInfo.put(findBiomeInfo.getPlayer().getUniqueId(), findBiomeInfo);
    if (findBiomeInfo.getStartTime() == 0L) {
      findBiomeInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(findBiomeInfo);
  }

  private void loadChunk(FindBiomeInfo findBiomeInfo) {
    if (findBiomeInfo.getScheduleTask() != null) {
      findBiomeInfo.getScheduleTask().cancel();
    }

    findBiomeInfo.setScheduleTask(
        CMIScheduler.runLaterAsync(
            this.plugin,
            () -> {
              if (this.FindBiomeInfo.containsKey(findBiomeInfo.getPlayer().getUniqueId())) {
                if (findBiomeInfo.isRunning()) {
                  Location location = findBiomeInfo.getRI().getWorld().getWorldBorder().getCenter();
                  double number = findBiomeInfo.getRI().getWorld().getWorldBorder().getSize() / 2.0;
                  double minX = location.getX() - number;
                  double maxX = location.getX() + number;
                  double minZ = location.getZ() - number;
                  double maxZ = location.getZ() + number;
                  int value = 0;

                  while (true) {
                    label107:
                    {
                      if (value < findBiomeInfo.getSpeed()) {
                        WorldInfo worldInfo = findBiomeInfo.getRI();
                        CMIRegion cmiRegion = worldInfo.getNextChunkLoc(true);
                        worldInfo.addLastChecked();
                        if (worldInfo.allChecked()) {
                          findBiomeInfo.setRunning(false);
                          if (findBiomeInfo.getScheduleTask() != null) {
                            findBiomeInfo.getScheduleTask().cancel();
                          }

                          this.FindBiomeInfo.remove(findBiomeInfo.getPlayer().getUniqueId());
                          if (findBiomeInfo.getPlayer().isOnline()) {
                            findBiomeInfo
                                .getPlayer()
                                .sendMessage(
                                    this.plugin
                                        .getLM()
                                        .getMessage(
                                            "command.findbiome.info.cantFindBiome",
                                            "[amount]",
                                            findBiomeInfo.getRI().getTotalChunks()));
                          }

                          return;
                        }

                        if (cmiRegion == null
                            || cmiRegion.getX() * 16 < minX
                            || cmiRegion.getX() * 16 > maxX
                            || cmiRegion.getZ() * 16 < minZ
                            || cmiRegion.getZ() * 16 > maxZ) {
                          break label107;
                        }

                        String string =
                            CMIBiome.getBiomeName(
                                findBiomeInfo
                                    .getRI()
                                    .getWorld()
                                    .getBiome(cmiRegion.getX() * 16, cmiRegion.getZ() * 16));
                        if (findBiomeInfo.isMessages()
                            && findBiomeInfo.getShowInfo() + 2000L < System.currentTimeMillis()) {
                          findBiomeInfo.setShowRegionInfo(false);
                          long longValue = worldInfo.getLastChecked();
                          longValue = longValue < 1L ? 1L : longValue;
                          Long leftToScan = worldInfo.getLeftToScan();
                          long scannedUnits =
                              System.currentTimeMillis() - findBiomeInfo.getStartTime();
                          double progressRatio = scannedUnits / longValue;
                          double estimatedMillis = leftToScan.longValue() * progressRatio;
                          CMIActionBar.send(
                              findBiomeInfo.getPlayer(),
                              this.plugin
                                  .getLM()
                                  .getMessage(
                                      "command.findbiome.info.infoShow",
                                      "[tps]",
                                      String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                      "[left]",
                                      leftToScan,
                                      "[time]",
                                      CMITimeManager.to24hourShort((long) estimatedMillis),
                                      "[speed]",
                                      findBiomeInfo.getSpeed()));
                          if (this.plugin.getLagMeter().getTPS()
                              > this.plugin.getScanManager().getScanSoftCap()) {
                            findBiomeInfo.setSpeed(findBiomeInfo.getSpeed() + 100);
                          } else if (findBiomeInfo.getSpeed() - 500 >= 0) {
                            findBiomeInfo.setSpeed(findBiomeInfo.getSpeed() - 500);
                          } else {
                            findBiomeInfo.setSpeed(1);
                          }

                          findBiomeInfo.setShowInfo(System.currentTimeMillis());
                        }

                        if (!string.equals(findBiomeInfo.getBiome())
                            || !findBiomeInfo.getPlayer().isOnline()) {
                          break label107;
                        }

                        Location candidateLocation =
                            new Location(
                                findBiomeInfo.getRI().getWorld(),
                                cmiRegion.getX() * 16 + 8,
                                64.0,
                                cmiRegion.getZ() * 16 + 8);
                        if (!candidateLocation
                                .getWorld()
                                .equals(findBiomeInfo.getPlayer().getWorld())
                            || !(candidateLocation.distance(findBiomeInfo.getPlayer().getLocation())
                                < this.plugin.getViewRange(null) * 16 / 2)) {
                          findBiomeInfo.setRunning(false);
                          if (findBiomeInfo.getScheduleTask() != null) {
                            findBiomeInfo.getScheduleTask().cancel();
                          }

                          this.FindBiomeInfo.remove(findBiomeInfo.getPlayer().getUniqueId());
                          if (!findBiomeInfo.getPlayer().isOnline()) {
                            return;
                          }

                          if (candidateLocation.getWorld().getEnvironment() == Environment.NETHER) {
                            candidateLocation.setY(64.0);
                            this.plugin
                                .getTeleportHandler()
                                .teleportPlayer(
                                    findBiomeInfo.getPlayer(),
                                    candidateLocation,
                                    CMITeleportType.Biome)
                                .thenAccept(
                                    cmiTeleportFeedback ->
                                        CMILC.info(
                                            findbiome.class,
                                            findBiomeInfo.getPlayer(),
                                            "teleportedTo",
                                            "[biome]",
                                            string.toLowerCase()));
                          } else {
                            this.plugin
                                .getTeleportHandler()
                                .getTopLocation(candidateLocation)
                                .thenAccept(
                                    topLocation ->
                                        this.plugin
                                            .getTeleportHandler()
                                            .teleportPlayer(
                                                findBiomeInfo.getPlayer(),
                                                topLocation,
                                                CMITeleportType.Biome)
                                            .thenAccept(
                                                feedback ->
                                                    CMILC.info(
                                                        findbiome.class,
                                                        findBiomeInfo.getPlayer(),
                                                        "teleportedTo",
                                                        "[biome]",
                                                        string.toLowerCase())));
                          }

                          return;
                        }
                      }

                      this.loadChunk(findBiomeInfo);
                      return;
                    }

                    value++;
                  }
                }
              }
            },
            1L));
  }
}
