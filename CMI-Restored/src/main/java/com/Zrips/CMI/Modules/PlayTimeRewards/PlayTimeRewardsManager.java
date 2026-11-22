package com.Zrips.CMI.Modules.PlayTimeRewards;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayTimeRewardsManager {
  private CMI plugin;
  private HashMap<String, PTRRepeat> repeatableRewards = new HashMap<>();
  private TreeMap<Long, PTROneTime> oneTimeRewards = new TreeMap<>();
  private TreeMap<String, PTROneTime> oneTimeRewardsByName = new TreeMap<>();
  private CMITask sched = null;
  private int interval = 60;
  private int OneTimeAmount = 1;
  private Long RewardInform = 1000L;
  private boolean Enabled = false;
  private boolean ExcludeAfk = false;
  public static boolean RequiresPermission = false;
  private HashMap<CMIUser, Long> lastAction = new HashMap<>();
  HashMap<UUID, Long> informMap = new HashMap<>();
  private String fileName = "PlayTimeRewards.yml";
  ConcurrentHashMap<UUID, PlaytimeClaimCache> cache = new ConcurrentHashMap<>();

  public PlayTimeRewardsManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  public void loadConfig() {
    this.stop();
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "PlaytimeRewards.Enabled",
        new String[] {
          "Enable or disable playtime rewards.", "This is required if you want to have auto rewards"
        });
    this.Enabled = configReader.get("PlaytimeRewards.Enabled", false);
    configReader.addComment(
        "PlaytimeRewards.ExcludeAfk",
        new String[] {
          "When enabled, while player is in afk mode, repeatable playtime rewards will not increase"
              + " in playtime",
          "ATTENTION! this setting will not have any effect if you have Afk.StopPlayTime set to"
              + " true",
          "When StopPlayTime set to true, afk will be expluded automatically"
        });
    this.ExcludeAfk = configReader.get("PlaytimeRewards.ExcludeAfk", false);
    configReader.addComment(
        "PlaytimeRewards.RewardInform",
        new String[] {
          "Defines time in minutes to inform player about pending reward which needs to be claimed"
        });
    this.RewardInform = configReader.get("PlaytimeRewards.RewardInform", 15) * 60 * 1000L;
    configReader.addComment(
        "PlaytimeRewards.OneTimeAmount",
        new String[] {
          "Defines how many one time rewards you want to show in list",
          "This will show next X amount rewards from your current playtime",
          "No point in listing all rewards if player is still far away"
        });
    this.OneTimeAmount = configReader.get("PlaytimeRewards.OneTimeAmount", 2);
    configReader.addComment(
        "PlaytimeRewards.RequiresPermission",
        new String[] {
          "When set to true, the player will be required to have cmi.prewards.[name] permission"
              + " node to get particular playtime reward"
        });
    RequiresPermission = configReader.get("PlaytimeRewards.RequiresPermission", false);
    if (this.plugin.isEnabled() && this.Enabled) {
      this.tasker();
    } else {
      this.stop();
    }
  }

  public void removeFromAfkAction(CMIUser cmiUser) {
    this.lastAction.remove(cmiUser);
  }

  public void updateAfkAction(CMIUser cmiUser) {
    this.lastAction.put(cmiUser, System.currentTimeMillis());
  }

  public Long getAfkAction(CMIUser cmiUser) {
    return this.lastAction.get(cmiUser);
  }

  private void tasker() {
    if (this.sched == null) {
      this.sched =
          CMIScheduler.runTimerAsync(
              this.plugin,
              () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                  CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
                  long longValue = cmiUser.getTotalPlayTime(false);
                  long elapsed = 0L;
                  if (this.isExcludeAfk()
                      && cmiUser.isAfk()
                      && !this.plugin.getAfkManager().isStopPlayTime()) {
                    Long afkAction = this.plugin.getAfkManager().getLastAction(cmiUser);
                    if (afkAction != null) {
                      Long lastActionTime = this.lastAction.get(cmiUser);
                      if (lastActionTime != null) {
                        elapsed = System.currentTimeMillis() - lastActionTime;
                      } else {
                        elapsed = System.currentTimeMillis() - afkAction;
                      }

                      this.lastAction.put(cmiUser, System.currentTimeMillis());
                    } else if (cmiUser.isAfk()) {
                      this.plugin.getAfkManager().updateUser(cmiUser);
                    }
                  }

                  for (var entry : this.repeatableRewards.entrySet()) {
                    PTRRepeat ptrRepeat = (PTRRepeat) entry.getValue();
                    Long repeatTime = cmiUser.getRepeatableRewardTime(ptrRepeat.getName());
                    if (repeatTime != null && repeatTime > longValue) {
                      cmiUser.addRepeatableReward(ptrRepeat.getName(), longValue);
                      repeatTime = longValue;
                    }

                    if (repeatTime != null
                        && this.isExcludeAfk()
                        && cmiUser.isAfk()
                        && !this.plugin.getAfkManager().isStopPlayTime()) {
                      repeatTime = repeatTime + elapsed;
                      if (repeatTime > longValue) {
                        repeatTime = longValue;
                      }

                      cmiUser.addRepeatableReward(ptrRepeat.getName(), repeatTime);
                    } else if (repeatTime == null) {
                      cmiUser.addRepeatableReward(ptrRepeat.getName(), longValue);
                      cmiUser.addForPlayTimeRewardSave();
                    } else {
                      long sinceRepeat = longValue - repeatTime;
                      int value = (int) (sinceRepeat / ptrRepeat.getPayEvery().doubleValue());
                      if (value >= 1) {
                        if (ptrRepeat.isAutoClaim() && !cmiUser.isJailed() && !cmiUser.isCuffed()) {
                          if (player != null && player.isOnline()) {
                            long payInterval = ptrRepeat.getPayEvery() * value;
                            payInterval =
                                repeatTime + payInterval > longValue
                                    ? longValue
                                    : repeatTime + payInterval;
                            cmiUser.addRepeatableReward(ptrRepeat.getName(), payInterval);
                            cmiUser.addForPlayTimeRewardSave();
                            if (!RequiresPermission
                                || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
                                    player, ptrRepeat.getName().toLowerCase())) {
                              CMIScheduler.runTask(
                                  this.plugin,
                                  () -> {
                                    if (player.isOnline()) {
                                      ptrRepeat.performCommands(player);
                                    }
                                  });
                            }
                          }
                        } else if (player != null && player.isOnline()) {
                          this.informPlayer(player, ptrRepeat.getName());
                        }
                      }
                    }
                  }

                  for (var oneTimeEntry : this.oneTimeRewards.entrySet()) {
                    if (!cmiUser.isClaimedReward((PTROneTime) oneTimeEntry.getValue())) {
                      if ((Long) oneTimeEntry.getKey() > longValue) {
                        break;
                      }

                      PTROneTime ptrOneTime = (PTROneTime) oneTimeEntry.getValue();
                      if (!ptrOneTime.isAutoClaim() || cmiUser.isJailed() || cmiUser.isCuffed()) {
                        this.informPlayer(player, ((PTROneTime) oneTimeEntry.getValue()).getName());
                      } else if (player != null
                          && player.isOnline()
                          && (!RequiresPermission
                              || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
                                  player, ptrOneTime.getName().toLowerCase()))) {
                        CMIScheduler.runTask(
                            this.plugin,
                            () -> {
                              if (player.isOnline()) {
                                ptrOneTime.performCommands(player);
                              }
                            });
                        cmiUser.addOneTimeReward(ptrOneTime.getName());
                        cmiUser.addForPlayTimeRewardSave();
                      }
                    }
                  }
                }
              },
              0L,
              this.interval * 20L);
    }
  }

  public void informPlayer(Player player, String string) {
    Long informTime = this.informMap.get(player.getUniqueId());
    if ((informTime == null || informTime < System.currentTimeMillis())
        && PermissionsManager.CMIPerm.prewards_notification.hasPermission(player)) {
      if (!RequiresPermission
          || PermissionsManager.CMIPerm.prewards_$1.hasPermission(player, string.toLowerCase())) {
        CMILC.info("prewards", player, "inform");
      }

      informTime =
          informTime == null ? System.currentTimeMillis() + 60000L : informTime + this.RewardInform;
      this.informMap.put(player.getUniqueId(), informTime);
    }
  }

  public void load() {
    Long informTime = System.currentTimeMillis();
    var hashMap = new HashMap<>(this.repeatableRewards);
    var oneTimeCopy = new HashMap<>(this.oneTimeRewardsByName);
    this.repeatableRewards.clear();
    this.oneTimeRewards.clear();
    this.oneTimeRewardsByName.clear();
    CMI.getInstance().safeRenameFile(this.fileName, "Settings" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Settings" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
      }
    } else {
      YamlConfiguration yamlConfiguration = null;

      try {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      } catch (Exception exception) {
        exception.printStackTrace();
        return;
      }

      if (!yamlConfiguration.getKeys(false).isEmpty()) {
        var set = yamlConfiguration.getKeys(false);
        int value = 60;

        for (String string : set) {
          try {
            String rewardName = string.replace(" ", "_");
            String displayName = yamlConfiguration.getString(string + ".DisplayName");
            if (displayName == null) {
              displayName = yamlConfiguration.getString(string + ".displayName", string);
            }

            boolean flag = false;
            if (yamlConfiguration.isBoolean(string + ".AutoClaim")) {
              flag = yamlConfiguration.getBoolean(string + ".AutoClaim");
            }

            var list = yamlConfiguration.getStringList(string + ".Description");
            var commands = yamlConfiguration.getStringList(string + ".Commands");
            boolean repeatable = true;
            Long lastTime = null;
            Boolean enabled = true;
            if (yamlConfiguration.isInt(string + ".PayEvery")) {
              repeatable = true;
              lastTime = (long) yamlConfiguration.getInt(string + ".PayEvery");
            } else if (yamlConfiguration.isInt(string + ".PayFor")) {
              repeatable = false;
              lastTime = (long) yamlConfiguration.getInt(string + ".PayFor");
            } else if (yamlConfiguration.isLong(string + ".PayEvery")) {
              repeatable = true;
              lastTime = yamlConfiguration.getLong(string + ".PayEvery");
            } else if (yamlConfiguration.isLong(string + ".PayFor")) {
              repeatable = false;
              lastTime = yamlConfiguration.getLong(string + ".PayFor");
            }

            if (yamlConfiguration.isBoolean(string + ".StackRewards")) {
              enabled = yamlConfiguration.getBoolean(string + ".StackRewards");
            }

            if (repeatable) {
              PTRRepeat ptrRepeat = new PTRRepeat(rewardName);
              if (hashMap.containsKey(rewardName)) {
                ptrRepeat = (PTRRepeat) hashMap.get(rewardName);
              }

              ptrRepeat.setAutoClaim(flag);
              ptrRepeat.setCommands(commands);
              ptrRepeat.setDescription(list);
              ptrRepeat.setDisplayName(displayName);
              ptrRepeat.setPayEvery(lastTime);
              ptrRepeat.setStackRewards(enabled);
              this.repeatableRewards.put(ptrRepeat.getName().toLowerCase(), ptrRepeat);
              if (lastTime != null && value > lastTime) {
                value = lastTime.intValue();
              }
            } else {
              PTROneTime ptrOneTime = new PTROneTime(rewardName);
              if (oneTimeCopy.containsKey(rewardName)) {
                ptrOneTime = (PTROneTime) oneTimeCopy.get(rewardName);
              }

              ptrOneTime.setAutoClaim(flag);
              ptrOneTime.setCommands(commands);
              ptrOneTime.setDescription(list);
              ptrOneTime.setDisplayName(displayName);
              ptrOneTime.setPayFor(lastTime);
              long longValue = ptrOneTime.getPayFor();

              while (this.oneTimeRewards.containsKey(longValue)) {
                longValue++;
              }

              this.oneTimeRewards.put(longValue, ptrOneTime);
              this.oneTimeRewardsByName.put(ptrOneTime.getName().toLowerCase(), ptrOneTime);
            }
          } catch (Exception exception2) {
            this.plugin.consoleMessage(
                "&cCould not load play time reward (&4" + string + "&c) Double check spelling");
          }
        }

        if (value > 0) {
          this.interval = value;
          if (this.interval != 60) {
            this.stop();
            if (this.plugin.isEnabled() && this.Enabled) {
              this.tasker();
            }
          }
        }

        this.plugin.loadMessage(
            this.repeatableRewards.size() + this.oneTimeRewards.size(),
            "playtime rewards",
            System.currentTimeMillis() - informTime);
      }
    }
  }

  public void clearCache(UUID uuid) {
    this.cache.remove(uuid);
  }

  public int getClaimableRewardCount(CMIUser cmiUser) {
    PlaytimeClaimCache playtimeClaimCache =
        this.cache.getOrDefault(cmiUser.getUniqueId(), new PlaytimeClaimCache());
    if (playtimeClaimCache.getLastUpdate() + 5000L > System.currentTimeMillis()) {
      return playtimeClaimCache.getCount();
    }

    long longValue = cmiUser.getTotalPlayTime(false);
    long elapsed = 0L;
    if (this.isExcludeAfk() && cmiUser.isAfk()) {
      Long afkAction = this.plugin.getAfkManager().getLastAction(cmiUser);
      if (afkAction != null) {
        Long afkTime = this.getAfkAction(cmiUser);
        if (afkTime != null) {
          elapsed = System.currentTimeMillis() - afkTime;
        } else {
          elapsed = System.currentTimeMillis() - afkAction;
        }

        this.updateAfkAction(cmiUser);
      } else if (cmiUser.isAfk()) {
        this.plugin.getAfkManager().updateUser(cmiUser);
      }
    }

    int value = 0;

    for (var entry : this.getRepeatableRewards().entrySet()) {
      Long repeatTime = cmiUser.getRepeatableRewardTime((String) entry.getKey());
      if (!RequiresPermission
          || !cmiUser.isOnline()
          || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
              cmiUser.getPlayer(), ((String) entry.getKey()).toLowerCase())) {
        if (repeatTime == null) {
          cmiUser
              .getRepeatableRewards()
              .put(((PTRRepeat) entry.getValue()).getName().toLowerCase(), longValue);
          repeatTime = longValue;
        }

        PTRRepeat ptrRepeat = (PTRRepeat) entry.getValue();
        Long lastRepeat = cmiUser.getRepeatableRewardTime(ptrRepeat.getName());
        if (lastRepeat == null) {
          cmiUser.addRepeatableReward(ptrRepeat.getName(), longValue);
        } else {
          if (this.isExcludeAfk() && cmiUser.isAfk()) {
            lastRepeat = lastRepeat + elapsed;
            if (lastRepeat > longValue) {
              lastRepeat = longValue;
            }

            cmiUser.addRepeatableReward(ptrRepeat.getName(), lastRepeat);
          }

          int payCount = (int) ((longValue - lastRepeat) / ptrRepeat.getPayEvery().doubleValue());
          if (!((PTRRepeat) entry.getValue()).isAutoClaim() && payCount > 0) {
            value += payCount;
          }
        }
      }
    }

    for (var oneTimeEntry : this.getOneTimeRewards().entrySet()) {
      if (!cmiUser.isClaimedReward((PTROneTime) oneTimeEntry.getValue())
          && (!RequiresPermission
              || !cmiUser.isOnline()
              || PermissionsManager.CMIPerm.prewards_$1.hasPermission(
                  cmiUser.getPlayer(),
                  ((PTROneTime) oneTimeEntry.getValue()).getName().toLowerCase()))
          && (Long) oneTimeEntry.getKey() - longValue <= 0L
          && (!((PTROneTime) oneTimeEntry.getValue()).isAutoClaim()
              || ((PTROneTime) oneTimeEntry.getValue()).isAutoClaim()
                  && !cmiUser.isClaimedReward((PTROneTime) oneTimeEntry.getValue()))) {
        value++;
      }
    }

    playtimeClaimCache.setLastUpdate(System.currentTimeMillis());
    playtimeClaimCache.setCount(value);
    this.cache.put(cmiUser.getUniqueId(), playtimeClaimCache);
    return value;
  }

  public HashMap<String, PTRRepeat> getRepeatableRewards() {
    return this.repeatableRewards;
  }

  public void setRepeatableRewards(HashMap<String, PTRRepeat> hashMap) {
    this.repeatableRewards = hashMap;
  }

  public TreeMap<Long, PTROneTime> getOneTimeRewards() {
    return this.oneTimeRewards;
  }

  public void setOneTimeRewards(TreeMap<Long, PTROneTime> treeMap) {
    this.oneTimeRewards = treeMap;
  }

  public PTROneTime getOneTimeRewardByName(String string) {
    return this.oneTimeRewardsByName.get(string.toLowerCase());
  }

  public PTRRepeat getRepeatingRewardByName(String string) {
    return this.repeatableRewards.get(string.toLowerCase());
  }

  public int getOneTimeAmount() {
    return this.OneTimeAmount;
  }

  public void setOneTimeAmount(int value) {
    this.OneTimeAmount = value;
  }

  public boolean isExcludeAfk() {
    return this.ExcludeAfk;
  }

  public boolean isEnabled() {
    return this.Enabled;
  }
}
