package com.Zrips.CMI.Modules.LightFix;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Region.CMIRegion;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Chunk;
import org.bukkit.World;

public class LightFix {
  public ConcurrentHashMap<UUID, LightFixInfo> LFInfo = new ConcurrentHashMap<>();
  private CMI plugin;

  public LightFix(CMI cmi) {
    this.plugin = cmi;
  }

  public void start(LightFixInfo lightFixInfo) {
    if (lightFixInfo.getStartTime() == 0L) {
      lightFixInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(lightFixInfo);
  }

  private void loadChunk(LightFixInfo lightFixInfo) {
    if (lightFixInfo.getScheduleTask() != null) {
      lightFixInfo.getScheduleTask().cancel();
    }

    lightFixInfo.setScheduleTask(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.LFInfo.containsKey(lightFixInfo.getPlayer().getUniqueId())
                  && lightFixInfo.isRunning()) {
                WorldInfo worldInfo = lightFixInfo.getRI();
                World world = worldInfo.getWorld();

                for (int value = 0; value < lightFixInfo.getSpeed(); value++) {
                  Chunk chunk = worldInfo.getNextChunk(true);
                  worldInfo.addLastChecked();
                  if (worldInfo.allChecked()) {
                    lightFixInfo.setRunning(false);
                    if (lightFixInfo.getScheduleTask() != null) {
                      lightFixInfo.getScheduleTask().cancel();
                    }

                    if (lightFixInfo.getPlayer().isOnline()) {
                      lightFixInfo
                          .getPlayer()
                          .sendMessage(
                              this.plugin.getLM().getMessage("command.lfix.info.Finished"));
                    }

                    return;
                  }

                  if (lightFixInfo.isMessages()
                      && lightFixInfo.getShowInfo() + 2000L < System.currentTimeMillis()
                      && worldInfo.getLastChecked() > 0L) {
                    lightFixInfo.setShowInfo(false);
                    long longValue = worldInfo.getLeftToScan();
                    long lastChecked = worldInfo.getLastChecked();
                    long elapsedMs = System.currentTimeMillis() - lightFixInfo.getStartTime();
                    long rate = elapsedMs / lastChecked;
                    int seconds = (int) (longValue * rate / 1000L);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;
                    CMIActionBar.send(
                        lightFixInfo.getPlayer(),
                        this.plugin
                            .getLM()
                            .getMessage(
                                "command.lfix.info.InfoShow",
                                "[tps]",
                                String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                "[left]",
                                longValue,
                                "[min]",
                                minutes,
                                "[sec]",
                                seconds,
                                "[speed]",
                                lightFixInfo.getSpeed()));
                    if (lightFixInfo.isAutoSpeed()) {
                      if (this.plugin.getLagMeter().getTPS()
                          > this.plugin.getConfigManager().lfixSoftCap) {
                        lightFixInfo.setSpeed(lightFixInfo.getSpeed() + 1);
                      } else {
                        lightFixInfo.setSpeed(lightFixInfo.getSpeed() - 2);
                      }
                    }

                    lightFixInfo.setShowInfo(System.currentTimeMillis());
                  }

                  if (chunk != null) {
                    CMIRegion cmiRegion = worldInfo.getNextChunkLoc(true);
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();
                    ArrayList<Chunk> arrayList = new ArrayList();
                    arrayList.add(world.getChunkAt(chunkX - 1, chunkZ - 1));
                    arrayList.add(world.getChunkAt(chunkX - 1, chunkZ));
                    arrayList.add(world.getChunkAt(chunkX - 1, chunkZ + 1));
                    if ((cmiRegion == null || cmiRegion.getZ() >= chunk.getZ())
                        && cmiRegion != null) {
                      world.loadChunk(chunkX, chunkZ + 1);
                      world.loadChunk(chunkX, chunkZ);
                      world.loadChunk(chunkX, chunkZ - 1);
                      world.loadChunk(chunkX + 1, chunkZ - 1);
                      world.loadChunk(chunkX + 1, chunkZ);
                      world.loadChunk(chunkX + 1, chunkZ + 1);
                    } else {
                      arrayList.add(world.getChunkAt(chunkX, chunkZ + 1));
                      arrayList.add(world.getChunkAt(chunkX, chunkZ));
                      arrayList.add(world.getChunkAt(chunkX, chunkZ - 1));
                      arrayList.add(world.getChunkAt(chunkX + 1, chunkZ - 1));
                      arrayList.add(world.getChunkAt(chunkX + 1, chunkZ));
                      arrayList.add(world.getChunkAt(chunkX + 1, chunkZ + 1));
                    }

                    for (Chunk chunkItem : arrayList) {
                      chunkItem.load();
                    }

                    this.plugin.getNMS().relightChunk(chunk);
                    int viewRange = this.plugin.getViewRange(chunk.getWorld());
                    if (lightFixInfo.getPlayer().isOnline()) {
                      int startX =
                          lightFixInfo.getPlayer().getLocation().getChunk().getX() - chunk.getX();
                      int startZ =
                          lightFixInfo.getPlayer().getLocation().getChunk().getZ() - chunk.getZ();
                      startX = startX < 0 ? startX * -1 : startX;
                      startZ = startZ < 0 ? startZ * -1 : startZ;
                      if (startX <= viewRange - 1 && startZ <= viewRange - 1) {
                        this.plugin.getNMS().resendChunk(chunk, lightFixInfo.getPlayer());
                      }
                    }

                    for (Chunk chunkEntry : arrayList) {
                      chunkEntry
                          .getWorld()
                          .unloadChunkRequest(chunkEntry.getX(), chunkEntry.getZ());
                    }
                  }
                }

                this.loadChunk(lightFixInfo);
              }
            },
            20L));
  }
}
