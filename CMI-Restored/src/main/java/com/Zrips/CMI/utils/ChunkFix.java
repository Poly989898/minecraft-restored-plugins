package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.ChunkInfo;
import com.Zrips.CMI.Containers.FixChunkInfo;
import com.Zrips.CMI.Containers.RegionFiles;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class ChunkFix {
  public Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");
  public ConcurrentHashMap<String, FixChunkInfo> ChunkFix = new ConcurrentHashMap<>();
  private CMI plugin;

  public ChunkFix(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadRegionFile(FixChunkInfo fixChunkInfo) {
    if (!fixChunkInfo.isOnlyChunks()) {
      if (fixChunkInfo.getRegionChecked() >= fixChunkInfo.getRegionFiles().size()) {
        if (fixChunkInfo.getSender() instanceof Player
                && ((Player) fixChunkInfo.getSender()).isOnline()
            || !(fixChunkInfo.getSender() instanceof Player)) {
          Bukkit.dispatchCommand(fixChunkInfo.getSender(), "cmi fixchunk info 1");
        }

        return;
      }

      Matcher matcher =
          this.regionPattern.matcher(
              fixChunkInfo.getRegionFiles().get((int) fixChunkInfo.getRegionChecked()).getName());
      if (!matcher.matches()) {
        fixChunkInfo
            .getSender()
            .sendMessage(
                "FilenameFilter accepted unmatched filename: "
                    + fixChunkInfo
                        .getRegionFiles()
                        .get((int) fixChunkInfo.getRegionChecked())
                        .getName());
        return;
      }

      int value = Integer.parseInt(matcher.group(1));
      int zValue = Integer.parseInt(matcher.group(2));
      fixChunkInfo.setX(value);
      fixChunkInfo.setZ(zValue);
      fixChunkInfo.setShowRegionInfo(true);
    }

    if (fixChunkInfo.getStartTime() == 0L) {
      fixChunkInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(fixChunkInfo);
  }

  private void loadChunk(FixChunkInfo fixChunkInfo) {
    if (fixChunkInfo.getScheduleTask() != null) {
      fixChunkInfo.getScheduleTask().cancel();
    }

    fixChunkInfo.setScheduleTask(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.ChunkFix.containsKey(fixChunkInfo.getSender().getName())
                  && fixChunkInfo.isRunning()) {
                for (int value = 0; value < fixChunkInfo.getSpeed(); value++) {
                  boolean flag = false;
                  Chunk chunk = null;
                  double number = 0.0;
                  double progress = 0.0;
                  boolean finished = false;
                  int spiralX = 0;
                  int spiralZ = 0;
                  if (fixChunkInfo.isOnlyChunks()) {
                    number = fixChunkInfo.getChunkChecked();
                    progress = fixChunkInfo.getTotal();
                    if (progress <= fixChunkInfo.getChunkChecked()) {
                      fixChunkInfo.setRunning(false);
                      if (fixChunkInfo.getScheduleTask() != null) {
                        fixChunkInfo.getScheduleTask().cancel();
                      }

                      if (fixChunkInfo.getSender() instanceof Player
                              && ((Player) fixChunkInfo.getSender()).isOnline()
                          || !(fixChunkInfo.getSender() instanceof Player)) {
                        Bukkit.dispatchCommand(fixChunkInfo.getSender(), "cmi fixchunk info 1");
                      }

                      return;
                    }

                    double spiralIndex = number;
                    int ringSide = (int) Math.floor(Math.sqrt(spiralIndex));
                    int halfRing = (int) Math.round(ringSide / 2.0);
                    double sign = Math.pow(-1.0, ringSide + 1);
                    double offset = ringSide * (ringSide + 1) - spiralIndex;
                    double distance = Math.abs(ringSide * (ringSide + 1) - spiralIndex);
                    spiralX =
                        (int)
                            (halfRing * Math.pow(-1.0, ringSide + 1)
                                + sign * (offset - distance) / 2.0);
                    spiralZ =
                        (int)
                            (halfRing * Math.pow(-1.0, ringSide)
                                + sign * (offset + distance) / 2.0);
                    spiralX += fixChunkInfo.getCenter().getBlockX() >> 4;
                    spiralZ += fixChunkInfo.getCenter().getBlockZ() >> 4;
                    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                      try {
                        if (!fixChunkInfo.getWorld().isChunkGenerated(spiralX, spiralZ)) {
                          fixChunkInfo.addChunkCkecked();
                          continue;
                        }
                      } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        return;
                      }
                    }

                    flag = fixChunkInfo.getWorld().isChunkLoaded(spiralX, spiralZ);

                    try {
                      chunk = fixChunkInfo.getWorld().getChunkAt(spiralX, spiralZ);
                    } catch (Throwable throwable2) {
                      finished = true;
                    }

                    fixChunkInfo.addChunkCkecked();
                  } else {
                    number =
                        fixChunkInfo.getRegionChecked() * 1024.0
                            + fixChunkInfo.getChunksCkeckedLast();
                    progress = fixChunkInfo.getRegionFiles().size() * 1024;
                    RegionFiles regionFiles = fixChunkInfo.getCurrentRegion();
                    if (regionFiles == null
                        || fixChunkInfo.getChunks().size() <= fixChunkInfo.getChunksCkeckedLast()) {
                      if (fixChunkInfo.getRegionChecked() >= fixChunkInfo.getRegionFiles().size()) {
                        fixChunkInfo.setRunning(false);
                        if (fixChunkInfo.getScheduleTask() != null) {
                          fixChunkInfo.getScheduleTask().cancel();
                        }

                        if (fixChunkInfo.getSender() instanceof Player
                                && ((Player) fixChunkInfo.getSender()).isOnline()
                            || !(fixChunkInfo.getSender() instanceof Player)) {
                          Bukkit.dispatchCommand(fixChunkInfo.getSender(), "cmi fixchunk info 1");
                        }

                        return;
                      }

                      if (regionFiles != null) {
                        try {
                          regionFiles.close();
                        } catch (IOException ioException) {
                        }
                      }

                      File file =
                          fixChunkInfo.getRegionFiles().get((int) fixChunkInfo.getRegionChecked());
                      regionFiles = new RegionFiles(file);
                      ArrayList<ChunkInfo> arrayList = new ArrayList();

                      for (int localX = 0; localX < 32; localX++) {
                        for (int localZ = 0; localZ < 32; localZ++) {
                          if (regionFiles.hasChunk(localX, localZ)) {
                            arrayList.add(new ChunkInfo(localX, localZ));
                          }
                        }
                      }

                      fixChunkInfo.setCurrentRegion(regionFiles);
                      fixChunkInfo.addRegionChecked();
                      fixChunkInfo.addChunks(arrayList);
                      fixChunkInfo.setChunksCkeckedLast(0.0);
                      Matcher matcher = this.regionPattern.matcher(file.getName());
                      if (!matcher.matches()) {
                        fixChunkInfo
                            .getSender()
                            .sendMessage(
                                "FilenameFilter accepted unmatched filename: " + file.getName());
                        return;
                      }

                      int regionX = Integer.parseInt(matcher.group(1));
                      int regionZ = Integer.parseInt(matcher.group(2));
                      fixChunkInfo.setX(regionX);
                      fixChunkInfo.setZ(regionZ);
                    }

                    if (fixChunkInfo.getChunks().size() <= fixChunkInfo.getChunksCkeckedLast()) {
                      continue;
                    }

                    ChunkInfo chunkInfo =
                        fixChunkInfo.getChunks().get((int) fixChunkInfo.getChunksCkeckedLast());
                    fixChunkInfo.setChunksCkeckedLast(fixChunkInfo.getChunksCkeckedLast() + 1.0);
                    spiralX = chunkInfo.getX() + fixChunkInfo.getX() * 32;
                    spiralZ = chunkInfo.getZ() + fixChunkInfo.getZ() * 32;
                    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                      try {
                        if (!fixChunkInfo.getWorld().isChunkGenerated(spiralX, spiralZ)) {
                          fixChunkInfo.addChunkCkecked();
                          continue;
                        }
                      } catch (Throwable throwable3) {
                        throwable3.printStackTrace();
                        return;
                      }
                    }

                    flag = fixChunkInfo.getWorld().isChunkLoaded(spiralX, spiralZ);

                    try {
                      chunk = fixChunkInfo.getWorld().getChunkAt(spiralX, spiralZ);
                    } catch (Exception exception) {
                      finished = true;
                    }
                  }

                  if (fixChunkInfo.isMessages()
                      && fixChunkInfo.getShowInfo() + 2000L < System.currentTimeMillis()
                      && number > 0.0) {
                    fixChunkInfo.setShowRegionInfo(false);
                    double remaining = progress - number;
                    long longValue = System.currentTimeMillis() - fixChunkInfo.getStartTime();
                    long elapsedMs = (long) (longValue / number);
                    int seconds = (int) (remaining * elapsedMs / 1000.0);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;
                    double checked = number - fixChunkInfo.getChunksCkeckedLast();
                    double elapsed = System.currentTimeMillis() - fixChunkInfo.getShowInfo();
                    double rate = elapsed / checked;
                    double percent = rate * 100.0;
                    CMIActionBar.send(
                        fixChunkInfo.getSender(),
                        this.plugin
                            .getLM()
                            .getMessage(
                                "command.fixchunk.info.InfoShow",
                                "[tps]",
                                String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                "[left]",
                                remaining,
                                "[min]",
                                minutes,
                                "[sec]",
                                seconds,
                                "[speed]",
                                fixChunkInfo.getSpeed(),
                                "[found]",
                                fixChunkInfo.getFound(),
                                "[cps]",
                                String.format("%.2f", percent)));
                    if (fixChunkInfo.isAutoSpeed()) {
                      if (this.plugin.getLagMeter().getTPS()
                          > this.plugin.getScanManager().getScanSoftCap()) {
                        fixChunkInfo.setSpeed(fixChunkInfo.getSpeed() + 1);
                      } else if (fixChunkInfo.getSpeed() - 2 > 0) {
                        fixChunkInfo.setSpeed(fixChunkInfo.getSpeed() - 2);
                      }
                    }

                    fixChunkInfo.setShowInfo(System.currentTimeMillis());
                  }

                  if (!flag && chunk != null) {
                    chunk.unload(false);
                  }

                  if (finished) {
                    ChunkInfo chunkInfo = new ChunkInfo(spiralX, spiralZ);
                    chunkInfo.setW(fixChunkInfo.getWorld());
                    fixChunkInfo.addChunkResult(chunkInfo);
                    fixChunkInfo.addFound();
                  }
                }

                this.loadChunk(fixChunkInfo);
              }
            },
            5L));
  }
}
