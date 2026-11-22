package net.Zrips.CMILib.BossBar;

import com.Zrips.CMI.CMI;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CMICommand;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
  CMILib plugin;
  ConcurrentHashMap<String, BossBarInfo> globalBars = new ConcurrentHashMap<>();
  ConcurrentHashMap<UUID, ConcurrentHashMap<String, BossBarInfo>> barMap =
      new ConcurrentHashMap<>();

  public BossBarManager(CMILib plugin) {
    this.plugin = plugin;
  }

  private static String cleanName(String name) {
    return name.replaceAll("[^a-zA-Z0-9]", "");
  }

  public synchronized void addGlobalBar(BossBarInfo binfo) {
    if (binfo.isGlobal()) {
      BossBarInfo old = this.globalBars.get(cleanName(binfo.getNameOfBar()));
      if (old != null) {
        old.cancelAutoScheduler();
        old.cancelHideScheduler();
      }

      this.globalBars.put(cleanName(binfo.getNameOfBar()), binfo);
    }

    this.Show(binfo);
  }

  public void removeGlobalBossbar(BossBarInfo bar) {
    this.removeGlobalBossbar(bar.getNameOfBar());
  }

  public synchronized void removeGlobalBossbar(String barName) {
    BossBarInfo ret = this.globalBars.remove(cleanName(barName));
    if (ret != null) {
      ret.cancelAutoScheduler();
      ret.cancelHideScheduler();
    }
  }

  public synchronized void updateGlobalBars(Player player) {
    for (Entry<String, BossBarInfo> iter : new HashMap<>(this.globalBars).entrySet()) {
      BossBarInfo binfo = iter.getValue();
      if (!binfo.stillRunning()) {
        this.globalBars.remove(iter.getKey());
      } else {
        BossBarInfo clone = binfo.clone(player);
        clone.setBar(null);
        this.getBossBarInfo(player).remove(clone.getNameOfBar().toLowerCase());
        this.addBossBar(player, clone);
      }
    }
  }

  public synchronized void updateBossBars(Player player) {
    if (!Version.isCurrentLower(Version.v1_9_0)) {
      if (player != null) {
        HashMap<String, BossBarInfo> temp = new HashMap<>(this.getBossBarInfo(player));

        for (Entry<String, BossBarInfo> one : temp.entrySet()) {
          this.Show(one.getValue());
        }
      }
    }
  }

  private void createNewBar(String name, BossBarInfo barInfo) {
    Player player = barInfo.getPlayer();
    BarColor color = barInfo.getColor();
    if (color == null) {
      if (player == null) {
        color = BarColor.GREEN;
      } else {
        ConcurrentHashMap<String, BossBarInfo> info = this.getBossBarInfo(player);
        int size = 1;
        if (info != null) {
          size = info.size();
        }

        switch (size) {
          case 1:
            color = BarColor.GREEN;
            break;
          case 2:
            color = BarColor.RED;
            break;
          case 3:
            color = BarColor.WHITE;
            break;
          case 4:
            color = BarColor.YELLOW;
            break;
          case 5:
            color = BarColor.PINK;
            break;
          case 6:
            color = BarColor.PURPLE;
            break;
          default:
            color = BarColor.BLUE;
        }
      }
    }

    barInfo.setBar(
        Bukkit.createBossBar(
            name,
            color,
            barInfo.getStyle() != null ? barInfo.getStyle() : BarStyle.SEGMENTED_10,
            new BarFlag[0]));
  }

  private void runCommands(BossBarInfo barInfo) {
    Player player = barInfo.getPlayer();
    CMIScheduler.runTask(
        this.plugin,
        () -> {
          if (barInfo.timerRunOut()) {
            if (CMILib.getInstance().isCmiPresent()) {
              if (player != null) {
                CMI.getInstance()
                    .getSpecializedCommandManager()
                    .processCmds(barInfo.getCommands(player), player);
              } else {
                CMI.getInstance()
                    .getSpecializedCommandManager()
                    .processCmds(barInfo.getCommands(null), Bukkit.getConsoleSender());
              }
            } else if (player != null) {
              CMICommand.performCommand(player, barInfo.getCommands(player), CommandType.bossbar);
            } else {
              CMICommand.performCommand(
                  Bukkit.getConsoleSender(), barInfo.getCommands(null), CommandType.bossbar);
            }
          }
        });
  }

  public synchronized void Show(BossBarInfo barInfo) {
    if (!Version.isCurrentLower(Version.v1_9_0)) {
      if (barInfo != null) {
        barInfo.cancelAutoScheduler();
        barInfo.cancelHideScheduler();
        barInfo.setAutoId(
            CMIScheduler.scheduleSyncRepeatingTask(
                CMILib.getInstance(),
                () -> {
                  Player player = barInfo.getPlayer();
                  if (barInfo.isGlobal() || player != null && player.isOnline()) {
                    if (barInfo.isGlobal() && player != null && !player.isOnline()) {
                      barInfo.remove();
                    } else {
                      barInfo.updateCycle();
                      BossBar bar = barInfo.getBar();
                      String name = barInfo.getTitleOfBar(player);
                      boolean isNew = true;
                      if (bar == null) {
                        this.createNewBar(name, barInfo);
                        bar = barInfo.getBar();
                      } else {
                        bar.setTitle(name);
                        if (barInfo.outdatedFormat) {
                          if (barInfo.getStyle() != null) {
                            bar.setStyle(barInfo.getStyle());
                          }

                          if (barInfo.getColor() != null) {
                            bar.setColor(barInfo.getColor());
                          }

                          barInfo.outdatedFormat = false;
                        }

                        bar.setVisible(true);
                        isNew = false;
                      }

                      Double percentage = barInfo.getPercentage();
                      if (percentage == null) {
                        percentage = 1.0;
                      }

                      if (barInfo.getAdjustPerc() != null) {
                        Double curP = barInfo.getPercentage();
                        if (curP != null
                                && curP + barInfo.getAdjustPerc() <= 0.0
                                && barInfo.getAdjustPerc() < 0.0
                            || curP != null
                                && curP + barInfo.getAdjustPerc() >= 1.0
                                && barInfo.getAdjustPerc() > 0.0) {
                          if (barInfo.getCommands() != null && barInfo.getHideScheduler() == null) {
                            try {
                              if (barInfo.getAdjustPerc() < 0.0) {
                                curP = 0.0;
                              } else {
                                curP = 1.0;
                              }

                              barInfo.setPercentage(curP);
                              bar.setProgress(curP);
                            } catch (Throwable var9) {
                            }

                            this.runCommands(barInfo);
                          }

                          if (barInfo.getHideScheduler() == null) {
                            this.hideScheduler(barInfo);
                          }
                        }

                        if (curP == null) {
                          if (barInfo.getAdjustPerc() > 0.0) {
                            curP = 0.0;
                          } else {
                            curP = 1.0;
                          }
                        }

                        if (!isNew) {
                          curP = curP + barInfo.getAdjustPerc();
                          barInfo.setPercentage(curP);
                          name = barInfo.getTitleOfBar(player);
                        }
                      } else {
                        barInfo.setPercentage(percentage);
                      }

                      try {
                        bar.setProgress(barInfo.getPercentage());
                        bar.setTitle(name);
                        if (player != null && player.isOnline()) {
                          this.getBossBarInfo(player)
                              .put(barInfo.getNameOfBar().toLowerCase(), barInfo);
                          if (!isNew
                              && (!barInfo.isMakeVisible()
                                  || this.getBossBar(player, barInfo.getNameOfBar()) != null)) {
                            if (barInfo.isMakeVisible()
                                && this.getBossBar(player, barInfo.getNameOfBar()) != null) {
                              bar.removePlayer(player);
                              this.removeGlobalBossbar(barInfo);
                              barInfo.remove();
                            }
                          } else {
                            bar.addPlayer(player);
                            bar.setVisible(true);
                            barInfo.setMakeVisible(false);
                          }
                        }
                      } catch (NoSuchMethodError e) {
                        e.printStackTrace();
                      }

                      if (!barInfo.stillRunning() && barInfo.getHideScheduler() == null) {
                        this.hideScheduler(barInfo);
                      }
                    }
                  } else {
                    barInfo.setMakeVisible(true);
                    barInfo.remove();
                  }
                },
                0L,
                barInfo.getAuto().intValue()));
      }
    }
  }

  private void hideScheduler(BossBarInfo barInfo) {
    barInfo.setHideScheduler(
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              CMIBossBarHideEvent ev = new CMIBossBarHideEvent(barInfo);
              Bukkit.getServer().getPluginManager().callEvent(ev);
              if (!ev.isCancelled()) {
                barInfo.getBar().setVisible(false);
                this.removeGlobalBossbar(barInfo);
                barInfo.remove();
                barInfo.setHideScheduler(null);
              }
            },
            barInfo.getKeepFor().intValue()));
  }

  public void removeBossBar(Player player, String name) {
    BossBarInfo old = this.getBossBar(player, name);
    if (old != null) {
      this.removeBossBar(player, old);
    }
  }

  public void removeBossBar(Player player, BossBarInfo bossBar) {
    if (bossBar != null) {
      if (bossBar.getBar() != null) {
        bossBar.getBar().setVisible(false);
      }

      bossBar.cancelAutoScheduler();
      bossBar.cancelHideScheduler();
      this.getBossBarInfo(player).remove(bossBar.getNameOfBar().toLowerCase());
    }
  }

  public synchronized void addBossBar(Player player, BossBarInfo barInfo) {
    if (player != null && barInfo != null) {
      if (barInfo.getPlayer() == null) {
        barInfo.setPlayer(player);
      }

      if (!this.getBossBarInfo(player).containsKey(barInfo.getNameOfBar().toLowerCase())) {
        this.getBossBarInfo(player).put(barInfo.getNameOfBar().toLowerCase(), barInfo);
        this.Show(barInfo);
      } else {
        BossBarInfo old = this.getBossBar(player, barInfo.getNameOfBar().toLowerCase());
        if (old != null) {
          old.cancelAutoScheduler();
          old.cancelHideScheduler();
          if (barInfo.getColor() != null) {
            old.setColor(barInfo.getColor());
          }

          if (barInfo.getKeepFor() != null) {
            old.setKeepForTicks(barInfo.getKeepFor());
          }

          if (barInfo.getPercentage() != null) {
            old.setPercentage(barInfo.getPercentage());
          }

          if (barInfo.getPlayer() != null) {
            old.setPlayer(barInfo.getPlayer());
          }

          if (barInfo.getAdjustPerc() != null) {
            old.setAdjustPerc(barInfo.getAdjustPerc());
          }

          if (barInfo.getStyle() != null) {
            old.setStyle(barInfo.getStyle());
          }

          if (!barInfo.getTitleOfBar().isEmpty()) {
            old.setTitleOfBar(barInfo.getTitleOfBarClean());
          }

          if (barInfo.getBar() != null) {
            old.setBar(barInfo.getBar());
          }

          if (barInfo.getHideScheduler() != null) {
            old.setHideScheduler(barInfo.getHideScheduler());
          }

          if (barInfo.getAuto() != null) {
            old.setAuto(barInfo.getAuto());
          }

          if (barInfo.getCommands() != null) {
            old.setCmds(barInfo.getCommands());
          }

          old.setStarted(System.currentTimeMillis());
        }

        this.Show(old);
      }
    }
  }

  public synchronized BossBarInfo getBossBar(Player player, String name) {
    ConcurrentHashMap<String, BossBarInfo> info = this.getBossBarInfo(player);
    return info == null ? null : info.get(name.toLowerCase());
  }

  public synchronized ConcurrentHashMap<String, BossBarInfo> getBossBarInfo(Player player) {
    return player == null
        ? null
        : this.barMap.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
  }

  public synchronized void hideBossBars(Player player) {
    if (player != null && player.isOnline()) {
      ConcurrentHashMap<String, BossBarInfo> info = this.getBossBarInfo(player);
      if (info != null) {
        for (Entry<String, BossBarInfo> one : info.entrySet()) {
          try {
            one.getValue().getBar().setVisible(false);
          } catch (NullPointerException var6) {
          }
        }
      }
    } else {
      if (player != null) {
        this.clearBossMaps(player);
      }
    }
  }

  public void clearBossMaps(Player player) {
    ConcurrentHashMap<String, BossBarInfo> info = this.getBossBarInfo(player);
    if (info != null) {
      for (Entry<String, BossBarInfo> one : info.entrySet()) {
        one.getValue().cancelHideScheduler();
      }

      info.clear();
    }
  }
}
