package com.Zrips.CMI.Modules.ChunkPreview;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.ChunkPreviewInfo;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

public class ChunkPreview {
  public ConcurrentHashMap<UUID, ChunkPreviewInfo> Info = new ConcurrentHashMap<>();
  private CMI plugin;

  public ChunkPreview(CMI cmi) {
    this.plugin = cmi;
  }

  public void clearCache(UUID uuid) {
    this.Info.remove(uuid);
  }

  public void loadRegionFile(ChunkPreviewInfo chunkPreviewInfo) {
    if (chunkPreviewInfo.getStartTime() == 0L) {
      chunkPreviewInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(chunkPreviewInfo);
  }

  private void loadChunk(ChunkPreviewInfo chunkPreviewInfo) {
    if (chunkPreviewInfo.getScheduleTask() != null) {
      chunkPreviewInfo.getScheduleTask().cancel();
    }

    chunkPreviewInfo.setScheduleTask(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.Info.containsKey(chunkPreviewInfo.getPlayer().getUniqueId())
                  && chunkPreviewInfo.isRunning()) {
                for (int value = 0; value < chunkPreviewInfo.getSpeed(); value++) {
                  Chunk chunk = null;
                  int countThird = 0;
                  int countFourth = 0;
                  countThird = chunkPreviewInfo.getChunkChecked();
                  countFourth = chunkPreviewInfo.getChunks().size();
                  if (chunkPreviewInfo.getChunks().size() == chunkPreviewInfo.getChunkChecked()) {
                    chunkPreviewInfo.setRunning(false);
                    if (chunkPreviewInfo.getScheduleTask() != null) {
                      chunkPreviewInfo.getScheduleTask().cancel();
                    }

                    if (chunkPreviewInfo.getPlayer().isOnline()) {
                      chunkPreviewInfo
                          .getPlayer()
                          .sendMessage(
                              this.plugin.getLM().getMessage("command.preview.info.Finished"));
                    }

                    return;
                  }

                  int countFifth =
                      chunkPreviewInfo.getChunks().get(chunkPreviewInfo.getChunkChecked()).getX();
                  int countSixth =
                      chunkPreviewInfo.getChunks().get(chunkPreviewInfo.getChunkChecked()).getZ();
                  chunk = chunkPreviewInfo.getWorld().getChunkAt(countFifth, countSixth);
                  chunkPreviewInfo.addChunkCkecked();
                  if (chunkPreviewInfo.isMessages()
                      && chunkPreviewInfo.getShowInfo() + 2000L < System.currentTimeMillis()
                      && countThird > 0) {
                    chunkPreviewInfo.setShowRegionInfo(false);
                    int countSeventh = countFourth - countThird;
                    long longValue = System.currentTimeMillis() - chunkPreviewInfo.getStartTime();
                    long time = longValue / countThird;
                    int countEighth = (int) (countSeventh * time / 1000L);
                    int countNinth = countEighth / 60;
                    countEighth -= countNinth * 60;
                    chunkPreviewInfo.setChunksCkeckedLast(countThird);
                    CMIActionBar.send(
                        chunkPreviewInfo.getPlayer(),
                        this.plugin
                            .getLM()
                            .getMessage(
                                "command.preview.info.InfoShow",
                                "[tps]",
                                String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                "[left]",
                                countSeventh,
                                "[min]",
                                countNinth,
                                "[sec]",
                                countEighth,
                                "[speed]",
                                chunkPreviewInfo.getSpeed()));
                    if (chunkPreviewInfo.isAutoSpeed()) {
                      if (this.plugin.getLagMeter().getTPS()
                          > this.plugin.getConfigManager().lfixSoftCap) {
                        chunkPreviewInfo.setSpeed(chunkPreviewInfo.getSpeed() + 1);
                      } else {
                        chunkPreviewInfo.setSpeed(chunkPreviewInfo.getSpeed() - 2);
                      }
                    }

                    chunkPreviewInfo.setShowInfo(System.currentTimeMillis());
                  }

                  if (chunk != null) {
                    int countTenth = Bukkit.getServer().getViewDistance();
                    int count =
                        chunkPreviewInfo.getPlayer().getLocation().getChunk().getX() - chunk.getX();
                    int countSecond =
                        chunkPreviewInfo.getPlayer().getLocation().getChunk().getZ() - chunk.getZ();
                    count = count < 0 ? count * -1 : count;
                    countSecond = countSecond < 0 ? countSecond * -1 : countSecond;
                    if ((count >= countTenth || countSecond >= countTenth)
                        && chunkPreviewInfo.getPlayer().isOnline()
                        && chunkPreviewInfo
                            .getPlayer()
                            .getWorld()
                            .getName()
                            .equals(chunkPreviewInfo.getWorld().getName())) {
                      this.plugin.getNMS().resendChunk(chunk, chunkPreviewInfo.getPlayer());
                    }

                    chunk.getWorld().unloadChunkRequest(chunk.getX(), chunk.getZ());
                  }
                }

                this.loadChunk(chunkPreviewInfo);
              }
            },
            20L));
  }
}
