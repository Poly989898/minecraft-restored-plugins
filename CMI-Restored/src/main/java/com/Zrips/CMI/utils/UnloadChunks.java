package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.UCInfo;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class UnloadChunks {
  public ConcurrentHashMap<String, UCInfo> ucinfo = new ConcurrentHashMap<>();
  private CMI plugin;

  public UnloadChunks(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadRegionFile(UCInfo ucInfo) {
    if (ucInfo.getStartTime() == 0L) {
      ucInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(ucInfo);
  }

  private void loadChunk(UCInfo ucInfo) {
    if (ucInfo.getScheduleTask() != null) {
      ucInfo.getScheduleTask().cancel();
    }

    ucInfo.setScheduleTask(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.ucinfo.containsKey(ucInfo.getPlayerName())) {
                if (ucInfo.isRunning()) {
                  if (ucInfo.isAutoSpeed()) {
                    if (this.plugin.getLagMeter().getTPS()
                        > this.plugin.getScanManager().getScanSoftCap()) {
                      ucInfo.setSpeed(ucInfo.getSpeed() + 3);
                    } else {
                      ucInfo.setSpeed(ucInfo.getSpeed() - 6);
                    }
                  }

                  for (int value = 0; value < ucInfo.getSpeed(); value++) {
                    Chunk chunk = null;
                    int chunksChecked = 0;
                    int chunksTotal = 0;
                    chunksChecked = ucInfo.getChunkChecked();
                    chunksTotal = ucInfo.getChunks().size();
                    if (ucInfo.getChunks().size() == ucInfo.getChunkChecked()) {
                      ucInfo.setRunning(false);
                      if (ucInfo.getScheduleTask() != null) {
                        ucInfo.getScheduleTask().cancel();
                      }

                      if (ucInfo.getSender() instanceof Player
                              && ((Player) ucInfo.getSender()).isOnline()
                          || !(ucInfo.getSender() instanceof Player)) {
                        long longValue = System.currentTimeMillis() - ucInfo.getStartTime();
                        int minutes = (int) (longValue / 1000L / 60L);
                        longValue -= minutes * 60 * 1000;
                        int seconds = (int) (longValue / 1000L);
                        ucInfo
                            .getSender()
                            .sendMessage(
                                this.plugin
                                    .getLM()
                                    .getMessage(
                                        "command.unloadchunks.info.Finished",
                                        "[amount]",
                                        ucInfo.getFound(),
                                        "[minutes]",
                                        minutes,
                                        "[seconds]",
                                        seconds));
                      }

                      return;
                    }

                    chunk = ucInfo.getChunks().get(ucInfo.getChunkChecked());
                    ucInfo.addChunkCkecked();
                    if (ucInfo.isMessages()
                        && ucInfo.getShowInfo() + 2000L < System.currentTimeMillis()
                        && chunksChecked > 0) {
                      ucInfo.setShowRegionInfo(false);
                      int chunksRemaining = chunksTotal - chunksChecked;
                      long elapsedMillis = System.currentTimeMillis() - ucInfo.getStartTime();
                      long millisPerChunk = elapsedMillis / chunksChecked;
                      int etaSeconds = (int) (chunksRemaining * millisPerChunk / 1000L);
                      int etaMinutes = etaSeconds / 60;
                      etaSeconds -= etaMinutes * 60;
                      ucInfo.setChunksCkeckedLast(chunksChecked);
                      CMIActionBar.send(
                          ucInfo.getSender(),
                          this.plugin
                              .getLM()
                              .getMessage(
                                  "command.unloadchunks.info.InfoShow",
                                  "[tps]",
                                  String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                  "[left]",
                                  chunksRemaining,
                                  "[min]",
                                  etaMinutes,
                                  "[sec]",
                                  etaSeconds,
                                  "[speed]",
                                  ucInfo.getSpeed(),
                                  "[found]",
                                  ucInfo.getFound()));
                      ucInfo.setShowInfo(System.currentTimeMillis());
                    }

                    if (chunk != null && chunk.unload(true)) {
                      ucInfo.addFound();
                    }
                  }

                  this.loadChunk(ucInfo);
                }
              }
            },
            5L));
  }
}
