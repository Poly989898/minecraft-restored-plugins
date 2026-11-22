package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMICompass;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.utils.SpawnUtil;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BossBarCompassListener implements Listener {
  private CMI plugin;
  ConcurrentHashMap<UUID, Long> compassCheck = new ConcurrentHashMap<>();

  public BossBarCompassListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerItemHeldEvent(PlayerJoinEvent playerJoinEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerItemHeldEvent(PlayerItemHeldEvent playerItemHeldEvent) {
    if (!playerItemHeldEvent.isCancelled()
        && CMICompass.isBossBarCompassEnabled()
        && CMICompass.isCompassRequireCompass()) {
      ItemStack itemStack =
          playerItemHeldEvent.getPlayer().getInventory().getItem(playerItemHeldEvent.getNewSlot());
      if (itemStack == null || !itemStack.getType().equals(Material.COMPASS)) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerItemHeldEvent.getPlayer());
        if (cmiUser != null) {
          itemStack = cmiUser.getItemInOffHand();
          if (itemStack == null || !itemStack.getType().equals(Material.COMPASS)) {
            BossBarInfo bossBarInfo = cmiUser.getBossBar("CMICompass");
            if (bossBarInfo != null) {
              cmiUser.removeBossBar(bossBarInfo);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
    if (!playerMoveEvent.isCancelled() && CMICompass.isBossBarCompassEnabled()) {
      Location location = playerMoveEvent.getTo();
      if (location != null) {
        Long lastCheck = this.compassCheck.get(playerMoveEvent.getPlayer().getUniqueId());
        if (lastCheck == null
            || lastCheck + CMICompass.getCompassUpdateInterval() <= System.currentTimeMillis()) {
          this.compassCheck.put(
              playerMoveEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerMoveEvent.getPlayer());
          if (cmiUser != null) {
            if (cmiUser.getOptionState(PlayerOption.bossBarCompass)) {
              if (System.currentTimeMillis() - cmiUser.getLastLogin() >= 2000L) {
                boolean flag = false;
                boolean needsUpdate = false;
                if (!CMICompass.isCompassRequireCompass()
                    && !CMICompass.isRecoveryAsRegularCompass()) {
                  flag = true;
                } else {
                  Material material = cmiUser.getItemInHand().getType();
                  Material offHandMaterial = cmiUser.getItemInOffHand().getType();
                  if (CMICompass.isCompassRequireCompass()
                      && (material != null && material.equals(Material.COMPASS)
                          || offHandMaterial != null && offHandMaterial.equals(Material.COMPASS))) {
                    flag = true;
                  }

                  if (CMICompass.isRecoveryAsRegularCompass()
                      && !flag
                      && Version.isCurrentEqualOrHigher(Version.v1_19_R1)
                      && (material != null && material.equals(Material.RECOVERY_COMPASS)
                          || offHandMaterial != null
                              && offHandMaterial.equals(Material.RECOVERY_COMPASS))) {
                    flag = true;
                    needsUpdate = true;
                  }
                }

                if (flag) {
                  BossBarInfo bossBarInfo = cmiUser.getBossBar("CMICompass");
                  if (bossBarInfo == null) {
                    bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMICompass");
                    bossBarInfo.setKeepForTicks(20);

                    try {
                      bossBarInfo.setColor(BarColor.WHITE);
                    } catch (Exception exception) {
                    }

                    try {
                      bossBarInfo.setStyle(BarStyle.SOLID);
                    } catch (Exception exception2) {
                    }

                    bossBarInfo.setAuto(1);
                    cmiUser.addBossBar(bossBarInfo);
                  }

                  bossBarInfo.setPercentage(0.01);
                  bossBarInfo.setStarted(System.currentTimeMillis());
                  BossBarInfo barInfo = bossBarInfo;
                  boolean wasUpdated = needsUpdate;
                  CMIScheduler.runTaskAsynchronously(
                      this.plugin,
                      () -> {
                        String string = CMICompass.getCompassShape();
                        double number = 360.0 / string.length();
                        Location targetLocation =
                            CMICompass.isCompassShowDeath()
                                ? (CMICompass.isRecoveryRequireCompass() && !wasUpdated
                                    ? null
                                    : cmiUser.getDeathLoc())
                                : null;
                        CMILocation cmiLocation = null;
                        if (CMICompass.isCompassShowHome()) {
                          try {
                            cmiLocation =
                                cmiUser.getHome(
                                            CMI.getInstance().getHomeManager().getDefaultHomeName())
                                        != null
                                    ? cmiUser
                                        .getHome(
                                            CMI.getInstance().getHomeManager().getDefaultHomeName())
                                        .getLoc()
                                    : (cmiUser.getHomes().isEmpty()
                                        ? null
                                        : cmiUser.getHome(cmiUser.getHomesList().get(0)).getLoc());
                          } catch (Exception exception3) {
                          }
                        }

                        if (cmiUser.isOnline()) {
                          CMILocation spawnLocation =
                              CMICompass.isCompassShowSpawn()
                                  ? SpawnUtil.getSpawnPoint(cmiUser.getPlayer())
                                  : null;
                          Location playerLocation =
                              CMICompass.isCompassShowCompass()
                                  ? cmiUser.getPlayer().getCompassTarget()
                                  : null;
                          Integer integer =
                              spawnLocation != null
                                  ? process(
                                      cmiUser.getPlayer().getLocation(), spawnLocation, number)
                                  : null;
                          Integer startValue =
                              spawnLocation != null
                                  ? process(
                                      cmiUser.getPlayer().getLocation(), spawnLocation, number)
                                  : null;
                          Integer endValue =
                              spawnLocation != null
                                  ? process(
                                      cmiUser.getPlayer().getLocation(), spawnLocation, number)
                                  : null;
                          Integer centerValue =
                              playerLocation != null
                                  ? process(
                                      cmiUser.getPlayer().getLocation(), playerLocation, number)
                                  : null;
                          int value = (int) location.getYaw() - 2;
                          value = (value + 540) % 360;
                          int steps = (int) Math.floor(value / number);
                          int half = string.length() / 2;
                          int start = steps;
                          start = start < 0 ? string.length() - -start : start;
                          start = start < 0 ? 0 : start;
                          int offset = start - half - 1;
                          offset = offset < 0 ? string.length() - -offset : offset;
                          String compassColor = CMICompass.getCompassColor();
                          StringBuilder stringBuilder = new StringBuilder().append(compassColor);

                          for (int index = 0; index < string.length(); index++) {
                            if (start >= string.length()) {
                              start = 0;
                            }

                            if (startValue != null && start == startValue) {
                              String colorPrefix =
                                  CMIChatColor.getLastColors(stringBuilder.toString());
                              if (start == offset) {
                                stringBuilder.append(
                                    "&f&n" + CMICompass.getCompassHomeIcon() + colorPrefix);
                              } else {
                                stringBuilder.append(
                                    "&2" + CMICompass.getCompassHomeIcon() + colorPrefix);
                              }
                            } else if (endValue != null && start == endValue) {
                              String colorTail =
                                  CMIChatColor.getLastColors(stringBuilder.toString());
                              if (start == offset) {
                                stringBuilder.append(
                                    "&f&n" + CMICompass.getCompassSpawnIcon() + colorTail);
                              } else {
                                stringBuilder.append(
                                    "&5&l" + CMICompass.getCompassSpawnIcon() + colorTail);
                              }
                            } else if (integer != null && start == integer) {
                              String colorMid =
                                  CMIChatColor.getLastColors(stringBuilder.toString());
                              if (start == offset) {
                                stringBuilder.append(
                                    "&f&n" + CMICompass.getCompassDeathIcon() + colorMid);
                              } else {
                                stringBuilder.append(
                                    "&c" + CMICompass.getCompassDeathIcon() + colorMid);
                              }
                            } else if (centerValue != null && start == centerValue) {
                              String colorEnd =
                                  CMIChatColor.getLastColors(stringBuilder.toString());
                              if (start == offset) {
                                stringBuilder.append(
                                    "&f&n" + CMICompass.getCompassTargetIcon() + colorEnd);
                              } else {
                                stringBuilder.append(
                                    "&5" + CMICompass.getCompassTargetIcon() + colorEnd);
                              }
                            } else if (start == offset) {
                              String colorStart =
                                  CMIChatColor.getLastColors(stringBuilder.toString());
                              stringBuilder.append("&f&n" + string.charAt(start) + colorStart);
                            } else {
                              if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)
                                  && CMICompass.getCompassColors().size() - 1 >= start) {
                                stringBuilder.append(
                                    "{#" + CMICompass.getCompassColors().get(start) + "}");
                              }

                              stringBuilder.append(string.charAt(start));
                            }

                            start++;
                          }

                          barInfo.setTitleOfBar(stringBuilder.toString());
                        }
                      });
                }
              }
            }
          }
        }
      }
    }
  }

  private static Integer process(Location location, Location target, double number) {
    if (location != null && target != null && location.getWorld().equals(target.getWorld())) {
      double yaw = getYaw(location, target);
      yaw = (yaw + 540.0) % 360.0;
      return (int) Math.floor(yaw / number);
    } else {
      return null;
    }
  }

  private static double getYaw(Location location, Location target) {
    Vector vector = location.toVector().subtract(target.toVector());
    Location origin = location.clone();
    origin.setDirection(vector);
    return origin.getYaw();
  }
}
