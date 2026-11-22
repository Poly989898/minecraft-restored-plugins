package com.Zrips.CMI.Modules.Afk;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.list.afk;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkKickEvent;
import com.Zrips.CMI.utils.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class AfkManager {
  private HashMap<CMIUser, Long> lastAction = new HashMap<>();
  private HashMap<UUID, AfkInfo> afkPlayersMap = new HashMap<>();
  private final NavigableMap<Integer, Long> afkKickTimers = new TreeMap<>();
  private CMI plugin;
  private CMITask sched = null;
  private long awayTrigerTime = 3000L;
  private List<String> awayTrigerCommands = new ArrayList<>();
  private List<String> manualAwayTrigerCommands = new ArrayList<>();
  private List<String> manualLeaveCommands = new ArrayList<>();
  private List<String> autoLeaveCommands = new ArrayList<>();
  private long defaultKickTime = 5000L;
  private int AutoKickFromPlayerCount = 0;
  private long RepeatingAutoKickInterval = 5000L;
  private List<String> kickTrigerCommands = new ArrayList<>();
  private List<String> disabledWorlds = new ArrayList<>();
  private boolean AfkEnabled = true;
  private boolean PreventJumping = true;
  private boolean TitleMessage = true;
  private boolean SubTitleMessage = true;
  private boolean PreventDamage = true;
  private boolean PreventMobDamage = true;
  private boolean StopPlayTime = false;
  private int interval = 1;
  private boolean disableOnPublicChat = true;
  private boolean disableOnPrivateChat = true;
  private boolean disableOnInteract = true;
  private boolean SmartInteractCheck = true;
  private boolean AntiAfkMachines = true;
  private boolean DisableOnInventoryClick = true;
  private boolean DisableOnitemDrop = true;
  private boolean RepeatKickCommand = false;
  private boolean disableOnCommand = true;
  private boolean disableOnMove = true;
  private boolean disableOnLookAround = true;
  private boolean disableItemPickup = false;
  private boolean disableFishing = true;
  private boolean PreventMobSpawning = false;
  private boolean PreventMobSpawningNatural = false;
  private boolean PreventMobSpawningSpawners = true;
  private boolean PreventMobVillageDefence = true;
  private boolean PreventPushing = true;
  private boolean PreventHook = true;
  private boolean disableExpPickup = false;
  private boolean disableShulkerTargeting = false;
  int messageSize = -1;
  List<String> afkSubTitle = new ArrayList<>();
  private CMITask afkSched = null;
  List<CMIUser> afkList = new ArrayList<>();

  public long getClosestKickTime() {
    if (this.defaultKickTime < 0L) {
      return this.defaultKickTime;
    }

    int value = Util.getOnlinePlayersCount();
    Integer integer = this.afkKickTimers.floorKey(value);
    return integer != null ? this.afkKickTimers.get(integer) : this.defaultKickTime;
  }

  public boolean isAfk(UUID uuid) {
    return this.afkPlayersMap.containsKey(uuid) && this.getAfkInfo(uuid) != null
        ? this.getAfkInfo(uuid).getAfkFrom() > 0L
        : false;
  }

  public AfkInfo getAfkInfo(UUID uuid) {
    return this.afkPlayersMap.get(uuid);
  }

  public AfkInfo createAfkInfo(UUID uuid) {
    return this.afkPlayersMap.computeIfAbsent(uuid, key -> new AfkInfo());
  }

  public void removeAfkInfo(UUID uuid) {
    this.afkPlayersMap.remove(uuid);
  }

  public AfkManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }

    if (this.afkSched != null) {
      this.afkSched.cancel();
      this.afkSched = null;
    }

    this.afkKickTimers.clear();
  }

  public void loadConfig() {
    this.stop();
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Afk.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

    ConfigReader freshConfig = this.plugin.getConfigManager().getConfig();
    if (freshConfig.getC().isConfigurationSection("Afk")
        && !configReader.getC().isConfigurationSection("Afk")) {
      configReader.set("Afk", freshConfig.getC().getConfigurationSection("Afk"));
      configReader.save();

      try {
        configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Afk.yml");
      } catch (Exception exception2) {
        exception2.printStackTrace();
        return;
      }
    }

    configReader.addComment(
        "Afk.Enabled", new String[] {"Enable or disable auto afk system entirely"});
    this.AfkEnabled = configReader.get("Afk.Enabled", false);
    configReader.addComment(
        "Afk.TitleMessage",
        new String[] {"When enabled shows title message informing that player is in afk mode"});
    this.TitleMessage = configReader.get("Afk.TitleMessage", true);
    configReader.addComment(
        "Afk.SubTitleMessage", new String[] {"When enabled shows random subtitle message"});
    this.SubTitleMessage = configReader.get("Afk.SubTitleMessage", true);
    configReader.addComment(
        "Afk.PreventJumping", new String[] {"Prevents jumping in one place to avoid afk status"});
    this.PreventJumping = configReader.get("Afk.PreventJumping", true);
    configReader.addComment(
        "Afk.PreventDamage", new String[] {"Prevents damage to player who is afk"});
    this.PreventDamage = configReader.get("Afk.PreventDamage", true);
    configReader.addComment(
        "Afk.PreventMobDamage", new String[] {"Prevents damage to mobs from player who is afk"});
    this.PreventMobDamage = configReader.get("Afk.PreventMobDamage", true);
    configReader.addComment(
        "Afk.CheckInterval",
        new String[] {"Defines how often in seconds plugin will check for afk players state"});
    this.interval = configReader.get("Afk.CheckInterval", 10);
    this.interval = this.interval < 1 ? 1 : this.interval;
    if (this.plugin.isEnabled()) {
      this.tasker();
    }

    configReader.addComment(
        "Afk.StopPlayTime",
        new String[] {
          "When set to true, players playtime counter stops",
          "As of nature how this system works you can see +-1second jumping up and down while"
              + " checking players playtime"
        });
    this.StopPlayTime = configReader.get("Afk.StopPlayTime", false);
    configReader.addComment(
        "Afk.AutoAfkIn",
        new String[] {
          "Defines how long to wait after player stops moving to set him as afk",
          "Player needs to have cmi.command.afk.auto permission node",
          "Set to 0 if you want to disable it"
        });
    this.awayTrigerTime = configReader.get("Afk.AutoAfkIn", 300) * 1000L;
    configReader.addComment(
        "Afk.AutoAfkCmds",
        new String[] {
          "Defines commands to be performed when player enters afk mode automatically while"
              + " addling",
          "Supports specialized commands"
        });
    this.awayTrigerCommands =
        configReader.get(
            "Afk.AutoAfkCmds", Arrays.asList("cmi broadcast !&6[playerDisplayName] &eis now AFK"));
    configReader.addComment(
        "Afk.ManualAfkCmds",
        new String[] {
          "Defines commands to be performed when player enters /cmi afk",
          "Supports specialized commands"
        });
    this.manualAwayTrigerCommands =
        configReader.get(
            "Afk.ManualAfkCmds",
            configReader.get(
                "Afk.AutoAfkCmds",
                Arrays.asList("cmi broadcast !&6[playerDisplayName] &eis now AFK")));
    configReader.addComment(
        "Afk.LeaveCmds.Manual",
        new String[] {
          "Defines commands to be performed when player leaves afk mode after he entered it"
              + " manually"
        });
    this.manualLeaveCommands =
        configReader.get(
            "Afk.LeaveCmds.Manual",
            configReader.getC().isList("Afk.AfkLeaveCmds")
                ? configReader.getC().getStringList("Afk.AfkLeaveCmds")
                : Arrays.asList("cmi broadcast !&6[playerDisplayName] &eis no longer AFK"));
    configReader.addComment(
        "Afk.LeaveCmds.Auto",
        new String[] {
          "Defines commands to be performed when player leaves afk mode after he entered it"
              + " automatically"
        });
    this.autoLeaveCommands =
        configReader.get(
            "Afk.LeaveCmds.Auto",
            configReader.getC().isList("Afk.AfkLeaveCmds")
                ? configReader.getC().getStringList("Afk.AfkLeaveCmds")
                : Arrays.asList("cmi broadcast !&6[playerDisplayName] &eis no longer AFK"));
    configReader.addComment(
        "Afk.AutoKickTimes.Default",
        new String[] {
          "Defines how long to wait after player stops moving to kick player",
          "This is additional timer to AutoAfkIn and in case player entered afk mode manually he"
              + " will get kicked after AutoAfkIn+AutoKickIn seconds",
          "This can be used not only to kick but to perform repeating action every x seconds if"
              + " needed",
          "Keep it at -1 to disable auto kick",
          "Can be bypassed with cmi.command.afk.kickbypass permission node",
          "Additionally players kick time can be changed with "
              + PermissionsManager.CMIPerm.command_afk_kickOutIn_$1.getPermission("[seconds]")
              + " permission node where bigger value takes priority"
        });
    this.defaultKickTime =
        configReader.get(
                "Afk.AutoKickTimes.Default", configReader.getC().getInt("Afk.AutoKickIn", -1))
            * 1000L;
    configReader.addComment(
        "Afk.AutoKickTimes.FromPlayerCount",
        new String[] {
          "Player count from which auto kick should start working",
          "Setting it to 0 will kick players independent of how many are online",
          "Recommended value: "
              + CMINumber.clamp(Bukkit.getMaxPlayers() - 5, 1, Bukkit.getMaxPlayers())
        });
    this.AutoKickFromPlayerCount = configReader.get("Afk.AutoKickTimes.FromPlayerCount", 0);
    this.afkKickTimers.clear();
    configReader.addComment(
        "Afk.AutoKickTimes.ByPlayerCount",
        new String[] {
          "List of autoKick times based by online player count ([playerCount]-[seconds])",
          "This only aplies if default kick time is set to more than -1",
          "This will pick closest timer (in seconds) from provided list going down. For example"
              + " having entries as 30-400 and 40-350 while having 35 online players we will use"
              + " value of 400 seconds",
          "Keep in mind that kick out timer is picked at the moment player enters afk mode and"
              + " isint being rechecked during afk mode even if player count changes during that"
              + " time",
          "If we cant determine correct value then default one will be used"
        });

    for (String string :
        configReader.get(
            "Afk.AutoKickTimes.ByPlayerCount",
            Arrays.asList("10-600", "20-500", "30-400", "40-350", "50-300"))) {
      try {
        String[] strings = string.split("-");
        this.afkKickTimers.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) * 1000L);
      } catch (Throwable throwable) {
        CMIMessages.consoleMessage(
            "Incorrectly set value in Afk.AutoKickTimes.ByPlayerCount: " + string);
      }
    }

    configReader.addComment(
        "Afk.RepeatingAutoKickInterval",
        new String[] {"This will define how long to wait before performing kick commands again"});
    this.RepeatingAutoKickInterval = configReader.get("Afk.RepeatingAutoKickInterval", 300) * 1000L;
    configReader.addComment(
        "Afk.RepeatKickCommand",
        new String[] {
          "When set to true, kick command will be repeated each RepeatingAutoKickInterval seconds"
        });
    this.RepeatKickCommand = configReader.get("Afk.RepeatKickCommand", false);
    configReader.addComment(
        "Afk.AutoKickCmds",
        new String[] {
          "Defines commands to be performed when player can be kicked",
          "If player is not kicked then commands will be repeated every RepeatingAutoKickInterval"
              + " seconds"
        });
    this.kickTrigerCommands =
        configReader.get(
            "Afk.AutoKickCmds",
            Arrays.asList(
                "cmi kick [playerName] &eYou have been kicked for idling more than [time]"));
    configReader.addComment(
        "Afk.DisabledWorlds",
        new String[] {
          "Defines worlds where players will not be placed into afk mode after they idled for"
              + " defined time"
        });
    this.disabledWorlds =
        configReader.get("Afk.DisabledWorlds", Arrays.asList("oneTestWorld", "secondTestWorld"));
    CMIList.toLowerCase(this.disabledWorlds);
    configReader.addComment("Afk.DisableOnInteract", new String[] {"Disables afk on interaction"});
    this.disableOnInteract = configReader.get("Afk.DisableOnInteract", true);
    configReader.addComment(
        "Afk.SmartInteractCheck",
        new String[] {
          "Prevents player from going bypassing afk mode while continuously holding one button with"
              + " particular items or on particular blocks"
        });
    this.SmartInteractCheck = configReader.get("Afk.SmartInteractCheck", true);
    configReader.addComment(
        "Afk.AntiAfkMachines",
        new String[] {"Prevents from players abusing afk by constantly moving in afk machine"});
    this.AntiAfkMachines = configReader.get("Afk.AntiAfkMachines", true);
    configReader.addComment(
        "Afk.PreventHook",
        new String[] {"Prevents players from being  pooled around while player is in afk mode"});
    this.PreventHook = configReader.get("Afk.PreventHook", true);
    configReader.addComment(
        "Afk.PreventPushing",
        new String[] {
          "EXPERIMENTAL! Prevents players from being pushed around while player is in afk mode",
          "Keep in mind that player can still be moved around the same block he is in"
        });
    this.PreventPushing = configReader.get("Afk.PreventPushing", false);
    configReader.addComment(
        "Afk.DisableOnInventoryClick", new String[] {"Disables afk on inventory click"});
    this.DisableOnInventoryClick = configReader.get("Afk.DisableOnInventoryClick", true);
    configReader.addComment("Afk.DisableOnitemDrop", new String[] {"Disables afk on item drop"});
    this.DisableOnitemDrop = configReader.get("Afk.DisableOnitemDrop", true);
    configReader.addComment("Afk.DisableOnCommand", new String[] {"Disables afk on command usage"});
    this.disableOnCommand = configReader.get("Afk.DisableOnCommand", true);
    configReader.addComment(
        "Afk.DisableOnPublicChat", new String[] {"Disables afk on public chat message"});
    this.disableOnPublicChat = configReader.get("Afk.DisableOnPublicChat", true);
    configReader.addComment(
        "Afk.DisableOnPrivateChat", new String[] {"Disables afk on private chat message"});
    this.disableOnPrivateChat = configReader.get("Afk.DisableOnPrivateChat", true);
    configReader.addComment("Afk.DisableOnMove", new String[] {"Disables afk on move"});
    this.disableOnMove = configReader.get("Afk.DisableOnMove", true);
    configReader.addComment(
        "Afk.DisableOnLookAround", new String[] {"Disables afk on camera movement"});
    this.disableOnLookAround = configReader.get("Afk.DisableOnLookAround", false);
    configReader.addComment(
        "Afk.DisableOnFishing", new String[] {"Disables afk on fishing when you catch fish"});
    this.disableFishing =
        configReader.get(
            "Afk.DisableOnFishing", configReader.getC().getBoolean("Afk.DisableFishing", false));
    configReader.addComment(
        "Afk.DisableItemPickup", new String[] {"Disables item pickup while afk"});
    this.disableItemPickup = configReader.get("Afk.DisableItemPickup", false);
    configReader.addComment(
        "Afk.PreventMobSpawning.Enabled",
        new String[] {"When enabled we can prevent mob spawning near players who are afk"});
    this.PreventMobSpawning = configReader.get("Afk.PreventMobSpawning.Enabled", false);
    configReader.addComment(
        "Afk.PreventMobSpawning.Natural",
        new String[] {
          "Prevents natural mob spawning",
          "This can be more on heavy side of the server as it will try constantly to spawn in"
              + " monsters near afk players"
        });
    this.PreventMobSpawningNatural = configReader.get("Afk.PreventMobSpawning.Natural", false);
    configReader.addComment(
        "Afk.PreventMobSpawning.Spawners", new String[] {"Prevent mob spawning from spawners"});
    this.PreventMobSpawningSpawners = configReader.get("Afk.PreventMobSpawning.Spawners", true);
    configReader.addComment(
        "Afk.PreventMobSpawning.VillageDefence",
        new String[] {"Usually responsible for spawning in iron golems"});
    this.PreventMobVillageDefence = configReader.get("Afk.PreventMobSpawning.VillageDefence", true);
    configReader.addComment(
        "Afk.DisableExpPickup",
        new String[] {
          "Disables exp pickup while afk",
          "Attention! Because of weird minecraft handling of exp orbs, best way is to set orb to"
              + " 0exp and allow it to be obsorbed",
          "So by enabling this exp obsorbed by afk players will have no effect"
        });
    this.disableExpPickup = configReader.get("Afk.DisableExpPickup", false);
    configReader.addComment(
        "Afk.Disable.ShulkerTargeting",
        new String[] {"Disables shulker targeting at the player who is in afk mode"});
    this.disableShulkerTargeting = configReader.get("Afk.Disable.ShulkerTargeting", true);
    this.afkSubTitle.clear();
    this.messageSize = -1;
    configReader.save();
  }

  private void loadAfk() {
    this.afkSubTitle = CMILC.getIML("afk", "afkSubTitle");
    this.messageSize = this.afkSubTitle.size();
  }

  private void tasker() {
    if (this.sched == null && CMIModule.afk.isEnabled()) {
      this.sched =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                for (var entry : new HashMap<>(this.lastAction).entrySet()) {
                  CMIUser cmiUser = (CMIUser) entry.getKey();
                  if (!cmiUser.isOnline()) {
                    this.removeUser(cmiUser);
                    cmiUser.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                  } else {
                    long longValue =
                        System.currentTimeMillis()
                            - (this.isAfk(cmiUser.getUniqueId())
                                    && cmiUser.getAfkInfo().getAfkFrom() != 0L
                                ? cmiUser.getAfkInfo().getAfkFrom()
                                : (Long) entry.getValue());
                    if (this.awayTrigerTime > 0L) {
                      if (!cmiUser.isAfk()
                          && longValue < this.awayTrigerTime
                          && cmiUser.getAfkInfo() != null) {
                        if (cmiUser.getAfkInfo().getType().equals(CMIAfkEnterEvent.AfkType.auto)) {
                          cmiUser.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                        }
                        continue;
                      }

                      if (!cmiUser.isAfk() && this.AfkEnabled && longValue > this.awayTrigerTime) {
                        cmiUser.setAfk(true, CMIAfkEnterEvent.AfkType.auto);
                      }
                    }

                    if (cmiUser.isAfk()) {
                      if (this.AutoKickFromPlayerCount > 0
                          && Util.getOnlinePlayersCount() <= this.AutoKickFromPlayerCount) {
                        this.showTitle(cmiUser, false);
                      } else {
                        long kickOutMillis = cmiUser.getAfkInfo().getKickOutInSec() * 1000L;
                        if (kickOutMillis > 0L
                            && longValue
                                > kickOutMillis
                                    + cmiUser.getAfkInfo().getKicksPerformed()
                                        * this.RepeatingAutoKickInterval
                            && cmiUser.isAfk()) {
                          if (cmiUser.getAfkInfo().getKicksPerformed() > 0 && this.RepeatKickCommand
                              || cmiUser.getAfkInfo().getKicksPerformed() == 0) {
                            CMIAfkKickEvent cmiAfkKickEvent =
                                new CMIAfkKickEvent(
                                    cmiUser.getPlayer(false),
                                    new ArrayList<>(this.kickTrigerCommands));
                            Bukkit.getServer().getPluginManager().callEvent(cmiAfkKickEvent);
                            if (!cmiAfkKickEvent.isCancelled()) {
                              Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
                              List<String> arrayList =
                                  new ArrayList<>(cmiAfkKickEvent.getKickTrigerCommands());

                              for (int value = 0; value < arrayList.size(); value++) {
                                String string =
                                    this.plugin
                                        .getLM()
                                        .updateSnd(snd, (String) arrayList.get(value));
                                string =
                                    string.replace(
                                        "[time]", CMITimeManager.to24hourShort(kickOutMillis));
                                arrayList.set(value, string);
                              }

                              this.plugin
                                  .getSpecializedCommandManager()
                                  .processCmds(
                                      arrayList, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
                            }
                          }

                          cmiUser.getAfkInfo().addKicksPerformed();
                        }

                        this.showTitle(cmiUser, false);
                      }
                    }
                  }
                }
              },
              20L,
              this.interval * 20L);
    }
  }

  public void showTitle(CMIUser cmiUser, boolean flag) {
    if (this.TitleMessage && cmiUser.isAfk() && cmiUser.isOnline()) {
      CMITitleMessage.send(
          cmiUser.getPlayer(),
          this.getTitle(cmiUser),
          this.getSubtitle(cmiUser),
          flag ? 20 : 0,
          this.interval * 20 + 5,
          0);
    }
  }

  public void hideTitle(CMIUser cmiUser) {
    if (this.TitleMessage && cmiUser.isOnline()) {
      CMITitleMessage.send(
          cmiUser.getPlayer(), this.getTitle(cmiUser), this.getSubtitle(cmiUser), 0, 0, 20);
    }
  }

  private String getTitle(CMIUser cmiUser) {
    String string = CMILC.getIM(afk.class, "afkTitle");
    return this.plugin.getPlaceholderAPIManager().updatePlaceHolders(cmiUser.getPlayer(), string);
  }

  private String getSubtitle(CMIUser cmiUser) {
    String string = null;
    if (this.SubTitleMessage) {
      if (this.messageSize == -1) {
        this.loadAfk();
      }

      if (this.messageSize > 0 && !this.afkSubTitle.isEmpty()) {
        int value =
            this.isAfk(cmiUser.getUniqueId()) && cmiUser.getAfkInfo().getAfkFrom() != 0L
                ? (int) (cmiUser.getAfkInfo().getAfkFrom() % this.messageSize)
                : 1;
        if (value >= this.messageSize) {
          if (this.afkSubTitle.isEmpty()) {
            string = "";
          } else {
            string = this.afkSubTitle.get(0);
          }
        } else if (value + 1 <= this.afkSubTitle.size()) {
          string = this.afkSubTitle.get(value);
        }
      }
    }

    return this.plugin.getPlaceholderAPIManager().updatePlaceHolders(cmiUser.getPlayer(), string);
  }

  public void setUserToAfk(CMIUser cmiUser, List<String> list) {
    if (!cmiUser.isAfk()) {
      Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
      list = new ArrayList(list);
      PermissionInfo permissionInfo =
          this.plugin
              .getPermissionsManager()
              .getPermissionInfo(
                  cmiUser.getPlayer(),
                  PermissionsManager.CMIPerm.command_afk_kickOutIn_$1.getPermission(""));
      long longValue =
          permissionInfo.getMaxValue(this.getClosestKickTime() / 1000.0).longValue() * 1000L;
      cmiUser.getAfkInfoSafe().setKickOutInSec((int) (longValue / 1000L));

      for (int value = 0; value < list.size(); value++) {
        String string = this.plugin.getLM().updateSnd(snd, (String) list.get(value));
        string = string.replace("[time]", CMITimeManager.to24hourShort(longValue));
        list.set(value, string);
      }

      ArrayList arrayList = new ArrayList(list);
      CMIScheduler.runTaskLater(
          this.plugin,
          () ->
              this.plugin
                  .getSpecializedCommandManager()
                  .processCmds(arrayList, cmiUser.isOnline() ? cmiUser.getPlayer() : null),
          1L);
    }
  }

  public void removeUserFromAfk(CMIUser cmiUser, List<String> list) {
    if (cmiUser.isOnline()) {
      this.hideTitle(cmiUser);
      if (cmiUser.isAfk()) {
        Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
        list = new ArrayList(list);
        long longValue = cmiUser.getAfkInfoSafe().getKickOutInSec() * 1000L;

        for (int value = 0; value < list.size(); value++) {
          String string = this.plugin.getLM().updateSnd(snd, (String) list.get(value));
          string = string.replace("[time]", CMITimeManager.to24hourShort(longValue));
          list.set(value, string);
        }

        this.plugin
            .getSpecializedCommandManager()
            .processCmds(list, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
        this.updateUser(cmiUser);
        this.removeAfkInfo(cmiUser.getUniqueId());
      }
    }
  }

  public void removeUser(CMIUser cmiUser) {
    this.lastAction.remove(cmiUser);
    if (this.lastAction.isEmpty() && this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  public void updateUser(CMIUser cmiUser) {
    if (cmiUser.isOnline() && this.isDisabledWorld(cmiUser.getLocation().getWorld())) {
      this.lastAction.remove(cmiUser);
    } else {
      if (this.sched == null) {
        this.tasker();
      }

      this.setLastAction(cmiUser, System.currentTimeMillis());
    }
  }

  public void setLastAction(CMIUser cmiUser, long longValue) {
    if (this.sched == null) {
      this.tasker();
    }

    this.lastAction.put(cmiUser, longValue);
  }

  public Long getLastAction(CMIUser cmiUser) {
    return this.lastAction.get(cmiUser);
  }

  public boolean isAfkEnabled() {
    return this.AfkEnabled;
  }

  public boolean isDisableOnMove() {
    return this.disableOnMove;
  }

  public boolean isDisableOnFishing() {
    return this.disableFishing;
  }

  public boolean isDisableOnInteract() {
    return this.disableOnInteract;
  }

  public boolean isDisableOnInventoryClick() {
    return this.DisableOnInventoryClick;
  }

  public boolean isDisableOnCommand() {
    return this.disableOnCommand;
  }

  public boolean isDisableItemPickup() {
    return this.disableItemPickup;
  }

  public boolean isDisableOnPublicChat() {
    return this.disableOnPublicChat;
  }

  public boolean isDisableOnPrivateChat() {
    return this.disableOnPrivateChat;
  }

  public List<String> getAwayTrigerCommands() {
    return this.awayTrigerCommands;
  }

  public void setAwayTrigerCommands(List<String> list) {
    this.awayTrigerCommands = list;
  }

  public boolean isPreventDamage() {
    return this.PreventDamage;
  }

  public boolean isPreventMobDamage() {
    return this.PreventMobDamage;
  }

  public boolean isPreventJumping() {
    return this.PreventJumping;
  }

  @Deprecated
  public List<String> getLeaveTrigerCommands() {
    return this.getManualLeaveCommands();
  }

  public List<String> getManualLeaveCommands() {
    return this.manualLeaveCommands;
  }

  public void setManualLeaveCommands(List<String> list) {
    this.manualLeaveCommands = list;
  }

  public List<String> getAutoLeaveCommands() {
    return this.autoLeaveCommands;
  }

  public void setAutoLeaveCommands(List<String> list) {
    this.autoLeaveCommands = list;
  }

  public boolean isSmartInteractCheck() {
    return this.SmartInteractCheck;
  }

  public boolean isAntiAfkMachines() {
    return this.AntiAfkMachines;
  }

  public int getCheckInterval() {
    return this.interval;
  }

  public boolean isStopPlayTime() {
    return this.StopPlayTime;
  }

  public void setStopPlayTime(boolean flag) {
    this.StopPlayTime = flag;
  }

  public void removeFromAfkTimePreventer(CMIUser cmiUser) {
    this.afkList.remove(cmiUser);
  }

  public void AddToAfkTimePreventer(CMIUser cmiUser) {
    this.afkList.add(cmiUser);
    this.AfkTimePreventer();
  }

  private void AfkTimePreventer() {
    if (this.afkSched == null && CMIModule.afk.isEnabled()) {
      this.afkSched =
          CMIScheduler.runTimerAsync(
              this.plugin,
              () -> {
                try {
                  if (this.afkList.isEmpty()) {
                    if (this.afkSched != null) {
                      this.afkSched.cancel();
                      this.afkSched = null;
                    }

                    return;
                  }

                  for (CMIUser cmiUser : new ArrayList<>(this.afkList)) {
                    if (cmiUser.isOnline() && cmiUser.isAfk()) {
                      long longValue = cmiUser.getAfkInfo().getPlaytimeAtAfkStart();
                      if (longValue >= 1000L) {
                        Long ticks = longValue / 50L;
                        if (CMI.getInstance().getConfigManager().PlayTimeFromStats) {
                          this.plugin
                              .getStatsManager()
                              .changeTo(
                                  cmiUser.getPlayer(),
                                  StatsManager.CMIStatistic.PLAY_ONE_TICK,
                                  "none",
                                  ticks);
                        } else {
                          cmiUser.getPlayer().setTicksLived(ticks.intValue());
                        }
                      }
                    } else {
                      this.afkList.remove(cmiUser);
                    }
                  }
                } catch (Exception exception) {
                  this.afkList = new ArrayList<>();
                  if (this.afkSched != null) {
                    this.afkSched.cancel();
                    this.afkSched = null;
                  }

                  exception.printStackTrace();
                }
              },
              0L,
              10L);
    }
  }

  public List<String> getManualAwayTrigerCommands() {
    return this.manualAwayTrigerCommands;
  }

  public void setManualAwayTrigerCommands(List<String> list) {
    this.manualAwayTrigerCommands = list;
  }

  public long getAwayTrigerTime() {
    return this.awayTrigerTime;
  }

  public boolean nearActivePlayer(Location location, SpawnReason spawnReason) {
    if (!this.afkPlayersMap.isEmpty() && this.isPreventMobSpawning()) {
      ArrayList<Player> arrayList = new ArrayList();

      for (Player player : location.getWorld().getPlayers()) {
        if (player != null && isNear(player.getLocation(), location)) {
          arrayList.add(player);
        }
      }

      boolean flag = true;
      if (spawnReason.equals(SpawnReason.NATURAL) && this.isPreventMobSpawningNatural()
          || spawnReason.equals(SpawnReason.SPAWNER) && this.isPreventMobSpawningSpawners()) {
        flag = !arrayList.isEmpty();
      }

      for (Player onlinePlayer : arrayList) {
        CMIUser cmiUser = CMIUser.getUser(onlinePlayer);
        if (cmiUser != null
            && !cmiUser.isVanished()
            && !onlinePlayer.getGameMode().toString().equalsIgnoreCase("SPECTATOR")) {
          if (!cmiUser.isAfk()) {
            return true;
          }

          if (spawnReason.equals(SpawnReason.SPAWNER) && this.isPreventMobSpawningSpawners()
              || spawnReason.equals(SpawnReason.NATURAL) && this.isPreventMobSpawningNatural()
              || spawnReason.equals(SpawnReason.VILLAGE_DEFENSE)
                  && this.isPreventMobVillageDefence()) {
            flag = false;
          }
        }
      }

      return flag;
    } else {
      return true;
    }
  }

  public static Boolean isNear(Location location, Location other) {
    return !(Math.abs(location.getX() - other.getX()) > 32.0)
            && !(Math.abs(location.getY() - other.getY()) > 32.0)
            && !(Math.abs(location.getZ() - other.getZ()) > 32.0)
        ? true
        : false;
  }

  public boolean isDisableExpPickup() {
    return this.disableExpPickup;
  }

  public boolean isDisableShulkerTargeting() {
    return this.disableShulkerTargeting;
  }

  public boolean isDisableOnItemDrop() {
    return this.DisableOnitemDrop;
  }

  public boolean isDisabledWorld(World world) {
    return world == null ? false : this.isDisabledWorld(world.getName());
  }

  public boolean isDisabledWorld(String string) {
    return string == null ? false : this.disabledWorlds.contains(string.toLowerCase());
  }

  public boolean isPreventMobSpawning() {
    return this.PreventMobSpawning;
  }

  public boolean isPreventMobSpawningNatural() {
    return this.PreventMobSpawningNatural;
  }

  public boolean isPreventMobSpawningSpawners() {
    return this.PreventMobSpawningSpawners;
  }

  public boolean isPreventMobVillageDefence() {
    return this.PreventMobVillageDefence;
  }

  public boolean isPreventPushing() {
    return this.PreventPushing;
  }

  public Set<CMIUser> getAfkPlayers() {
    return this.afkPlayersMap.keySet().stream()
        .map(CMIUser::getUser)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  public HashMap<UUID, AfkInfo> getAfkPlayersMap() {
    return this.afkPlayersMap;
  }

  public int getAfkPlayerCount() {
    return this.afkPlayersMap.size();
  }

  public boolean isDisableOnLookAround() {
    return this.disableOnLookAround;
  }

  public boolean isPreventHook() {
    return this.PreventHook;
  }
}
