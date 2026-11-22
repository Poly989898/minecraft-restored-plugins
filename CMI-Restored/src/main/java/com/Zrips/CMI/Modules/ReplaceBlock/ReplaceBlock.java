package com.Zrips.CMI.Modules.ReplaceBlock;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.itemInfo;
import com.Zrips.CMI.Modules.Region.WorldInfo;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ReplaceBlock {
  public Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");
  public ConcurrentHashMap<String, BRInfo> BRInfo = new ConcurrentHashMap<>();
  private CMI plugin;

  public ReplaceBlock(CMI cmi) {
    this.plugin = cmi;
  }

  public void start(BRInfo brInfo) {
    if (brInfo.getStartTime() == 0L) {
      brInfo.setShowInfo(System.currentTimeMillis());
    }

    this.loadChunk(brInfo);
  }

  private void loadChunk(BRInfo brInfo) {
    if (brInfo.getScheduleTask() != null) {
      brInfo.getScheduleTask().cancel();
    }

    brInfo.setScheduleTask(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.BRInfo.containsKey(brInfo.getPlayerName()) && brInfo.isRunning()) {
                for (int value = 0; value < brInfo.getSpeed(); value++) {
                  boolean flag = false;
                  WorldInfo worldInfo = brInfo.getRI();
                  Chunk chunk = worldInfo.getNextChunk(true);
                  worldInfo.addLastChecked();
                  if (worldInfo.allChecked()) {
                    brInfo.setRunning(false);
                    if (brInfo.getScheduleTask() != null) {
                      brInfo.getScheduleTask().cancel();
                    }

                    if (brInfo.getPlayer().isOnline()) {
                      long longValue = System.currentTimeMillis() - brInfo.getStartTime();
                      brInfo
                          .getPlayer()
                          .sendMessage(
                              this.plugin
                                  .getLM()
                                  .getMessage(
                                      "command.replaceblock.info.finished",
                                      "[amount]",
                                      brInfo.getReplaced(),
                                      "[time]",
                                      CMITimeManager.to24hourShort(longValue)));
                    }

                    return;
                  }

                  if (brInfo.isMessages()
                      && brInfo.getShowInfo() + 2000L < System.currentTimeMillis()) {
                    brInfo.setShowInfo(false);
                    long lastChecked = worldInfo.getLastChecked();
                    long leftToScan = worldInfo.getLeftToScan();
                    long elapsedMillis = System.currentTimeMillis() - brInfo.getStartTime();
                    double number = elapsedMillis / lastChecked;
                    double estimatedMillis = leftToScan * number;
                    CMIActionBar.send(
                        brInfo.getPlayer(),
                        this.plugin
                            .getLM()
                            .getMessage(
                                "command.replaceblock.info.infoShow",
                                "[tps]",
                                String.format("%.2f", this.plugin.getLagMeter().getTPS()),
                                "[left]",
                                leftToScan,
                                "[time]",
                                CMITimeManager.to24hourShort((long) estimatedMillis),
                                "[speed]",
                                brInfo.getSpeed(),
                                "[found]",
                                brInfo.getReplaced()));
                    brInfo.setShowInfo(System.currentTimeMillis());
                    if (brInfo.isAutoSpeed()) {
                      if (this.plugin.getLagMeter().getTPS()
                          > this.plugin.getScanManager().getScanSoftCap()) {
                        brInfo.setSpeed(brInfo.getSpeed() + 1);
                      } else if (brInfo.getSpeed() - 2 > 0) {
                        brInfo.setSpeed(brInfo.getSpeed() - 2);
                      }
                    }
                  }

                  if (chunk != null) {
                    Random random = new Random(System.currentTimeMillis());
                    int chunkBaseX = chunk.getX() * 16;
                    int chunkBaseZ = chunk.getZ() * 16;

                    for (int localX = 0; localX < 16; localX++) {
                      for (int localZ = 0; localZ < 16; localZ++) {
                        int highestY =
                            worldInfo
                                .getWorld()
                                .getHighestBlockYAt(chunkBaseX + localX, chunkBaseZ + localZ);
                        if (highestY > brInfo.getY()) {
                          highestY = brInfo.getY();
                        }

                        for (int blockY = CMIWorld.getMinHeight(worldInfo.getWorld());
                            blockY <= highestY;
                            blockY++) {
                          Block block = chunk.getBlock(localX, blockY, localZ);
                          CMIMaterial cmiMaterial = CMIMaterial.get(block);
                          if (cmiMaterial != CMIMaterial.NONE) {
                            for (itemInfo itemInfo : brInfo.getItemInfo()) {
                              if (cmiMaterial == itemInfo.getMat()
                                  && (itemInfo.getChance() == 100
                                      || random.nextInt(100) < itemInfo.getChance())) {
                                itemInfo chosenItem = new itemInfo(CMIMaterial.NONE);

                                for (itemInfo candidateItem : brInfo.getReplaceInfo()) {
                                  if (candidateItem.getChance() == 100
                                      || random.nextInt(100) < candidateItem.getChance()) {
                                    chosenItem = candidateItem;
                                    break;
                                  }
                                }

                                if (chosenItem.getMat() != CMIMaterial.NONE) {
                                  block.setType(chosenItem.getMat().getMaterial());
                                } else {
                                  block.setType(Material.AIR);
                                }

                                brInfo.addReplaced();
                              }
                            }
                          }
                        }
                      }
                    }

                    if (!flag) {
                      chunk.unload();
                    }
                  }
                }

                this.loadChunk(brInfo);
              }
            },
            30L));
  }
}
