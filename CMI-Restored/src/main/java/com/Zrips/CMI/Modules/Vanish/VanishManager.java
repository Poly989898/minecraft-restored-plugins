package com.Zrips.CMI.Modules.Vanish;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIVanish;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Afk.AfkManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.SilentChest.silentChestListener;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.list.vanishedit;
import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanishManager {
  private CMI plugin;
  private Set<UUID> vanishedOnlineList = new HashSet<>();
  private Set<UUID> vanishedList = new HashSet<>();
  private static HashMap<UUID, CMIVanish> vanishedCache = new HashMap<>();
  private static final String vanishBossBar = "CMIVanishBossBar";
  private boolean checkEnabled = false;
  public static String defaultString = "";
  private CMITask playtimeSched = null;
  List<CMIUser> playtimeList = new ArrayList<>();

  @Nullable
  public CMIVanish getVanish(UUID uuid) {
    return vanishedCache.get(uuid);
  }

  public CMIVanish getOrCreateVanish(CMIUser cmiUser) {
    return vanishedCache.computeIfAbsent(cmiUser.getUniqueId(), uuid -> new CMIVanish(cmiUser));
  }

  public CMIVanish getOrCreateVanish(UUID uuid) {
    return vanishedCache.computeIfAbsent(uuid, vanishKey -> new CMIVanish(CMIUser.getUser(uuid)));
  }

  public static boolean is(UUID uuid, VanishAction vanishAction) {
    return getState(uuid, vanishAction).is();
  }

  @Nonnull
  public static VanishActionState getState(UUID uuid, VanishAction vanishAction) {
    CMIVanish cmiVanish = vanishedCache.get(uuid);
    if (cmiVanish == null) {
      return vanishAction.getMaxValue() > 0
          ? new VanishActionStateInt()
          : new VanishActionStateBoolean(vanishAction.isEnabled());
    } else {
      return cmiVanish.getState(vanishAction);
    }
  }

  @Deprecated
  @Nullable
  public CMIVanish getVanishRaw(UUID uuid) {
    return vanishedCache.get(uuid);
  }

  public VanishManager(CMI cmi) {
    this.plugin = cmi;
  }

  public Set<UUID> getAllVanished() {
    return this.vanishedList;
  }

  public void addPlayer(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.addPlayer(cmiUser.getUniqueId());
    }
  }

  public void addPlayer(Player player) {
    if (player != null) {
      this.addPlayer(player.getUniqueId());
    }
  }

  public void addPlayer(UUID uuid) {
    if (uuid != null) {
      this.vanishedOnlineList.add(uuid);
      this.vanishedList.add(uuid);
      Player player = CMIUser.getOnlinePlayer(uuid);
      if (player == null) {
        this.vanishedOnlineList.remove(uuid);
        this.recheckMobSpawningCheck();
      } else {
        this.hidePlayerForEveryone(player);
      }
    }
  }

  public void recheckMobSpawningCheck() {
    this.checkEnabled = false;

    for (UUID uuid : this.vanishedOnlineList) {
      CMIUser cmiUser = CMIUser.getUser(uuid);
      if (cmiUser != null
          && cmiUser.getVanishOrNull() != null
          && cmiUser.isVanished()
          && !is(cmiUser.getUniqueId(), VanishAction.mobSpawning)) {
        this.checkEnabled = true;
        break;
      }
    }
  }

  public boolean nearActivePlayer(Location location) {
    if (this.vanishedOnlineList.isEmpty()) {
      return true;
    }

    if (!this.checkEnabled) {
      return true;
    }

    ArrayList<Player> arrayList = new ArrayList();

    for (Player player : location.getWorld().getPlayers()) {
      if (player != null && AfkManager.isNear(player.getLocation(), location)) {
        arrayList.add(player);
      }
    }

    boolean flag = true;

    for (Player playerSecond : arrayList) {
      if (playerSecond != null) {
        CMIUser cmiUser = CMIUser.getUser(playerSecond);
        if (cmiUser != null && !cmiUser.isAfk()) {
          if (cmiUser.getVanishOrNull() == null) {
            continue;
          }

          if (cmiUser.isCMIVanished() && !is(cmiUser.getUniqueId(), VanishAction.mobSpawning)) {
            flag = false;
            continue;
          }

          return true;
        }

        return true;
      }
    }

    return flag;
  }

  public void applyVanish(CMIUser cmiUser) {
    this.applyVanish(cmiUser, true, true, false);
  }

  public void applyVanish(CMIUser cmiUser, boolean flag) {
    this.applyVanish(cmiUser, flag, true, false);
  }

  public void applyVanish(CMIUser cmiUser, boolean flag, boolean flagSecond) {
    this.applyVanish(cmiUser, flag, flagSecond, false);
  }

  public void applyVanish(CMIUser cmiUser, boolean flag, boolean flagSecond, boolean flagThird) {
    if (cmiUser.isOnline()) {
      Player player = cmiUser.getPlayer();
      if (player != null) {
        if (cmiUser.isCMIVanished()) {
          CMIPlayerVanishEvent cmiPlayerVanishEvent = new CMIPlayerVanishEvent(player);
          if (flag) {
            Bukkit.getPluginManager().callEvent(cmiPlayerVanishEvent);
          }

          if (!cmiPlayerVanishEvent.isCancelled() || !flag) {
            if (cmiUser.getVanish().getVanishedFrom() == null) {
              cmiUser.getVanish().setVanishedFrom(System.currentTimeMillis());
            }

            if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)
                && player.getPotionEffect(PotionEffectType.NIGHT_VISION) == null) {
              CMIScheduler.runAtEntity(
                  this.plugin,
                  player,
                  () -> {
                    if (cmiUser.getVanish().is(VanishAction.nightVision)) {
                      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                      player.addPotionEffect(
                          new PotionEffect(PotionEffectType.NIGHT_VISION, 864000, 1));
                    } else {
                      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                  });
            }

            try {
              if (cmiUser.getVanish().is(VanishAction.sleepIgnore)) {
                player.setSleepingIgnored(true);
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }

            if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
              CMIScheduler.runTaskLater(
                  this.plugin,
                  () -> {
                    if (cmiUser.getVanish().is(VanishAction.bossbar)) {
                      if (flagThird) {
                        cmiUser.removeBossBar("CMIVanishBossBar");
                      }

                      BossBarInfo bossBarInfo = cmiUser.getBossBar("CMIVanishBossBar");
                      if (bossBarInfo == null) {
                        bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIVanishBossBar");
                      }

                      bossBarInfo.setPercentage(1.0);
                      bossBarInfo.setColor(BarColor.WHITE);
                      bossBarInfo.setStyle(BarStyle.SOLID);
                      bossBarInfo.setAuto(20);
                      bossBarInfo.setTitleOfBar(CMILC.getIM(vanishedit.class, "bossbarTitle"));
                      bossBarInfo.setKeepForTicks(-1);
                      cmiUser.addBossBar(bossBarInfo);
                    } else {
                      BossBarInfo bossBarInfoValue = cmiUser.getBossBar("CMIVanishBossBar");
                      if (bossBarInfoValue != null) {
                        bossBarInfoValue.remove();
                      }
                    }
                  },
                  2L);
            }

            if (flag && flagSecond && cmiUser.getVanish().is(VanishAction.fakeJoinLeave)) {
              Snd snd = new Snd().setSender(player).setTarget(player);
              String string = CMILC.info_LogoutCustom.getLocale(snd);
              string = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, string);
              CMIMessages.broadcastMessage(player, string);
            }

            if (cmiUser.getVanish().is(VanishAction.noisyChest)) {
              silentChestListener.toggledSilentChest.remove(player.getUniqueId());
            } else {
              silentChestListener.toggledSilentChest.put(player.getUniqueId(), true);
            }

            if (cmiUser.getVanish().is(VanishAction.stopPlaytime)) {
              this.addToPlayTimePreventer(cmiUser);
            }
          }

          cmiUser.getVanish().setWasCollidable(cmiUser.isCollidable());
          if (flagThird) {
            CMIScheduler.runTaskLater(this.plugin, () -> cmiUser.setCollision(false), 20L);
          } else {
            cmiUser.setCollision(false);
          }

          this.recheckMobSpawningCheck();
          if (cmiUser.isOnline()) {
            cmiUser
                .getPlayer()
                .setMetadata("vanished", new FixedMetadataValue(CMI.getInstance(), true));
          }
        } else if (!flagThird) {
          CMIPlayerUnVanishEvent cmiPlayerUnVanishEvent = new CMIPlayerUnVanishEvent(player);
          if (flag) {
            Bukkit.getPluginManager().callEvent(cmiPlayerUnVanishEvent);
          }

          if (!cmiPlayerUnVanishEvent.isCancelled() || !flag) {
            if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)
                && is(cmiUser.getUniqueId(), VanishAction.nightVision)
                && flag) {
              CMIScheduler.runAtEntity(
                  this.plugin,
                  player,
                  () -> player.removePotionEffect(PotionEffectType.NIGHT_VISION));
            }

            BossBarInfo bossBarInfoSecond = cmiUser.getBossBar("CMIVanishBossBar");
            if (bossBarInfoSecond != null) {
              bossBarInfoSecond.remove();
            }

            try {
              if (is(cmiUser.getUniqueId(), VanishAction.sleepIgnore)) {
                player.setSleepingIgnored(false);
              }
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }

            if (flag && flagSecond && is(cmiUser.getUniqueId(), VanishAction.fakeJoinLeave)) {
              Snd sndSecond = new Snd().setSender(player).setTarget(player);
              String text = CMILC.info_LoginCustom.getLocale(sndSecond);
              text = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, text);
              CMIMessages.broadcastMessage(player, text);
            }

            if (cmiUser.getGlow() != null) {
              cmiUser.setGlow(cmiUser.getGlow(), false);
            }

            cmiUser.reapplyNamePlate();
            this.removeFromPlayTimePreventer(cmiUser);
          }

          CMIVanish cmiVanish = cmiUser.getVanishOrNull();
          if (cmiVanish != null) {
            cmiUser.setCollision(cmiVanish.getWasCollidable());
            cmiVanish.setWasCollidable(null);
          }

          silentChestListener.toggledSilentChest.remove(player.getUniqueId());
        }

        this.plugin.getBungeeCordManager().sendNewPlayerInfoToNetwork(player);
      }
    }
  }

  public void loadConfig() {
    defaultString = "0";
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Vanish.Defaults",
        new String[] {
          "Defines default states of vanish edit options for players",
          "This will not have any effect if player already edited his vanish mode with vanishedit"
              + " command"
        });
    VanishAction[] vanishActions;
    int value = (vanishActions = VanishAction.values()).length;

    for (int count = 0; count < value; count++) {
      VanishAction vanishAction = vanishActions[count];
      if (!defaultString.isEmpty()) {
        defaultString = defaultString + ":";
      }

      defaultString =
          defaultString
              + (vanishAction.getMaxValue() > 0 ? "0" : (vanishAction.isEnabled() ? "1" : "0"));
      if (vanishAction != VanishAction.isVanished && vanishAction.getMaxValue() <= 0) {
        vanishAction.setEnabled(
            configReader.get(
                "Vanish.Defaults." + vanishAction.toString(), vanishAction.isEnabled()));
      }
    }
  }

  public void hideVanishedPlayersFor(Player player) {
    if (player != null) {
      boolean flag = PermissionsManager.CMIPerm.seevanished.hasPermission(player);

      for (UUID uuid : this.vanishedOnlineList) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
        if (cmiUser != null && cmiUser.isOnline() && !uuid.equals(player.getUniqueId())) {
          VanishActionStateInt vanishActionStateInt =
              (VanishActionStateInt) getState(cmiUser.getUniqueId(), VanishAction.level);
          int value = vanishActionStateInt.getValue();
          if (flag) {
            if (value <= 0) {
              continue;
            }

            int count =
                CMINumber.clamp(
                    PermissionsManager.CMIPerm.vanishlevel_$1
                        .getPermissionInfo(player, "")
                        .getMaxValue(1),
                    1,
                    VanishAction.level.getMaxValue());
            if (count >= value) {
              continue;
            }
          }

          if (cmiUser.isCMIVanished()) {
            player.hidePlayer(cmiUser.getPlayer());
          }
        }
      }
    }
  }

  public void hidePlayerForEveryone(Player player) {
    if (player != null) {
      VanishActionStateInt vanishActionStateInt =
          (VanishActionStateInt) getState(player.getUniqueId(), VanishAction.level);
      int value = vanishActionStateInt.getValue();
      this.vanishedOnlineList.add(player.getUniqueId());
      this.recheckMobSpawningCheck();

      for (Player playerSecond : Bukkit.getOnlinePlayers()) {
        if (!playerSecond.getUniqueId().equals(player.getUniqueId())) {
          if (PermissionsManager.CMIPerm.seevanished.hasPermission(playerSecond)) {
            if (value <= 0) {
              continue;
            }

            int count =
                CMINumber.clamp(
                    PermissionsManager.CMIPerm.vanishlevel_$1
                        .getPermissionInfo(playerSecond, "")
                        .getMaxValue(1),
                    1,
                    VanishAction.level.getMaxValue());
            if (count >= value) {
              playerSecond.showPlayer(player);
              continue;
            }
          }

          playerSecond.hidePlayer(player);
        }
      }
    }
  }

  public void removePlayer(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.removePlayer(cmiUser.getUniqueId(), true);
    }
  }

  public void removePlayer(Player player) {
    if (player != null) {
      this.removePlayer(player.getUniqueId(), true);
    }
  }

  public void removePlayer(UUID uuid) {
    this.removePlayer(uuid, true);
  }

  public void removePlayer(UUID uuid, boolean flag) {
    if (uuid != null) {
      this.vanishedOnlineList.remove(uuid);
      this.vanishedList.remove(uuid);
      this.recheckMobSpawningCheck();
      Player player = CMIUser.getOnlinePlayer(uuid);
      if (player != null) {
        if (flag) {
          for (Player playerSecond : Bukkit.getOnlinePlayers()) {
            if (!playerSecond.getUniqueId().equals(uuid)) {
              playerSecond.showPlayer(player);
            }
          }
        }
      }
    }
  }

  public Set<UUID> getVanishedOnlineList() {
    return this.vanishedOnlineList;
  }

  public void removeFromPlayTimePreventer(CMIUser cmiUser) {
    this.playtimeList.remove(cmiUser);
  }

  public void addToPlayTimePreventer(CMIUser cmiUser) {
    this.playtimeList.add(cmiUser);
    cmiUser.getVanish().setPlaytimeAtVanishStart(cmiUser.getTotalPlayTime() / 1000L * 1000L);
    this.playTimePreventer();
  }

  private void playTimePreventer() {
    if (this.playtimeSched == null && CMIModule.vanish.isEnabled()) {
      this.playtimeSched =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                try {
                  if (this.playtimeList.isEmpty()) {
                    if (this.playtimeSched != null) {
                      this.playtimeSched.cancel();
                      this.playtimeSched = null;
                    }

                    return;
                  }

                  for (CMIUser cmiUser : new ArrayList<>(this.playtimeList)) {
                    if (cmiUser.isOnline() && cmiUser.isCMIVanished()) {
                      long longValue = cmiUser.getVanish().getPlaytimeAtVanishStart();
                      if (longValue >= 1000L) {
                        Long time = longValue / 50L;
                        if (CMI.getInstance().getConfigManager().PlayTimeFromStats) {
                          this.plugin
                              .getStatsManager()
                              .changeTo(
                                  cmiUser.getPlayer(),
                                  StatsManager.CMIStatistic.PLAY_ONE_TICK,
                                  "none",
                                  time);
                        } else {
                          cmiUser.getPlayer().setTicksLived(time.intValue());
                        }
                      }
                    } else {
                      this.playtimeList.remove(cmiUser);
                    }
                  }
                } catch (Exception exception) {
                  this.playtimeList = new ArrayList<>();
                  if (this.playtimeSched != null) {
                    this.playtimeSched.cancel();
                    this.playtimeSched = null;
                  }

                  exception.printStackTrace();
                }
              },
              0L,
              19L);
    }
  }
}
