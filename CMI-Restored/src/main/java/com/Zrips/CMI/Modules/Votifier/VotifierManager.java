package com.Zrips.CMI.Modules.Votifier;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class VotifierManager {
  private CMI plugin;
  private List<String> CommandsOnVote = new ArrayList<>();
  private List<String> ExcludeList = new ArrayList<>();
  private boolean CountVotes = true;
  private boolean PerformCommands = true;
  private int cooldown = 0;
  private int MaxVotesInADay = 0;
  private HashMap<Integer, List<String>> rewards = new HashMap<>();
  private HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();
  private HashMap<UUID, VotifierManager.voteReminder> voteReminder = new HashMap<>();
  private ConcurrentSkipListMap<Double, CMIUser> Top =
      new ConcurrentSkipListMap<>(Collections.reverseOrder());
  private Set<CMIUser> withVotes = new HashSet<>();
  Long lastUpdate = 0L;
  CMITask sched = null;
  int updateDelay = 5;
  boolean calculating = false;

  public void removeVoteReminder(UUID uuid) {
    this.voteReminder.remove(uuid);
  }

  public void addToVoteReminder(UUID uuid) {
    this.voteReminder.put(uuid, new VotifierManager.voteReminder());
  }

  public VotifierManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Votifier.CountVotes",
        new String[] {"When set to true votifier votes will be counted for player"});
    this.CountVotes = configReader.get("Votifier.CountVotes", true);
    configReader.addComment(
        "Votifier.MaxVotesInADay",
        new String[] {
          "Number of votes one IP can make in last 24 hours", "Set it to 0 to have unlimited amount"
        });
    this.MaxVotesInADay = configReader.get("Votifier.MaxVotesInADay", 0);
    this.MaxVotesInADay = this.MaxVotesInADay < 0 ? 0 : this.MaxVotesInADay;
    configReader.addComment(
        "Votifier.GeneralCooldown",
        new String[] {
          "Cooldown between votes from same service",
          "In most cases voting service will have its own cooldown setup",
          "But if you need extra one to prevent rapid voting you can define time in seconds over"
              + " here"
        });
    this.cooldown = configReader.get("Votifier.GeneralCooldown", 0);
    this.cooldown = this.cooldown < 0 ? 0 : this.cooldown;
    configReader.addComment(
        "Votifier.PerformCommands",
        new String[] {"When set to false, commands on sucessfull vote will not be performed"});
    this.PerformCommands = configReader.get("Votifier.PerformCommands", true);
    configReader.addComment(
        "Votifier.CommandsOnVote",
        new String[] {
          "Defines commands to be performed when player votes",
          "Supports specialized commands and placeholders",
          "[serviceName] variable can be used to insert address"
        });
    this.CommandsOnVote =
        configReader.get(
            "Votifier.CommandsOnVote",
            Arrays.asList(
                "cmi broadcast !&6[playerDisplayName] &evoted!", "cmi give [playerName] diamond"));
    configReader.addComment(
        "Votifier.ExtraRewardsEnabled",
        new String[] {"Keep it at false if you don't want to give out extra rewards"});
    boolean flag = configReader.get("Votifier.ExtraRewardsEnabled", false);
    configReader.addComment(
        "Votifier.ExtraRewards",
        new String[] {"Commands will be performed when player collect determined amount of votes"});
    if (configReader.getC().isConfigurationSection("Votifier.ExtraRewards")) {
      for (String string :
          configReader.getC().getConfigurationSection("Votifier.ExtraRewards").getKeys(false)) {
        try {
          int value = Integer.parseInt(string);
          List list = configReader.get("Votifier.ExtraRewards." + string, new ArrayList());
          if (flag && value > 0 && list != null && !list.isEmpty()) {
            this.rewards.put(value, list);
          }
        } catch (Exception exception) {
          this.plugin.consoleMessage("&cCan't load vote rewards (" + string + ")");
        }
      }
    } else {
      configReader.get(
          "Votifier.ExtraRewards.10",
          Arrays.asList("cmi heal [playerName]", "cmi money give [playerName] 100"));
      configReader.get(
          "Votifier.ExtraRewards.100",
          Arrays.asList(
              "cmi heal [playerName]",
              "cmi money give [playerName] 1000",
              "cmi give [playerName] diamond 32"));
    }

    configReader.addComment(
        "Votifier.ExcludeList",
        new String[] {"List of players to be excluded from top voter list"});
    this.ExcludeList = configReader.get("Votifier.ExcludeList", Arrays.asList("None"));

    for (int index = 0; index < this.ExcludeList.size(); index++) {
      this.ExcludeList.set(index, this.ExcludeList.get(index).toLowerCase());
    }
  }

  @Deprecated
  public void processVote(CMIUser cmiUser, String string) {
    this.processVote(cmiUser, string, null);
  }

  public void processVote(CMIUser cmiUser, String string, String serviceName) {
    if (CMIModule.votifier.isEnabled()) {
      if (this.cooldown > 0) {
        var hashMap = this.cooldowns.get(cmiUser.getUniqueId());
        String normalizedName = string == null ? "None" : string;
        if (hashMap != null) {
          Long lastVote = (Long) hashMap.get(normalizedName);
          if (lastVote != null && lastVote + this.cooldown * 1000L > System.currentTimeMillis()) {
            hashMap.put(normalizedName, System.currentTimeMillis());
            this.cooldowns.put(cmiUser.getUniqueId(), hashMap);
            cmiUser.sendMessage(
                "votes",
                "voteCooldown",
                "[time]",
                CMITimeManager.to24hourShort(
                    lastVote + this.cooldown * 1000L - System.currentTimeMillis()));
            return;
          }
        }

        HashMap<String, Long> voteMap = new HashMap();
        voteMap.put(normalizedName, System.currentTimeMillis());
        this.cooldowns.put(cmiUser.getUniqueId(), voteMap);
      }

      boolean flag = false;
      if (this.CountVotes) {
        flag = cmiUser.addVotifierVote();
        cmiUser.addForDelayedSave();
      }

      if (!flag && cmiUser.getLastDailyVoteTime() != null) {
        cmiUser.sendMessage(
            "votes",
            "voteLimit",
            "[time]",
            CMITimeManager.to24hourShort(
                cmiUser.getLastDailyVoteTime() + 86400000L - System.currentTimeMillis()));
      } else {
        if (this.PerformCommands) {
          Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
          var arrayList = new ArrayList<>(this.CommandsOnVote);

          for (int value = 0; value < arrayList.size(); value++) {
            String message = this.plugin.getLM().updateSnd(snd, (String) arrayList.get(value));
            message = message.replace("[serviceName]", string == null ? "" : string);
            message =
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(cmiUser.getPlayer(), message);
            arrayList.set(value, message);
          }

          this.plugin.getSpecializedCommandManager().processCmds(arrayList, cmiUser.getPlayer());
        }

        var list = this.rewards.get(cmiUser.getVotifierVotes());
        if (list != null) {
          Snd voteSound = new Snd().setSender(cmiUser).setTarget(cmiUser);
          ArrayList commandList = new ArrayList(list);

          for (int index = 0; index < commandList.size(); index++) {
            String command =
                this.plugin.getLM().updateSnd(voteSound, (String) commandList.get(index));
            command = command.replace("[serviceName]", string == null ? "" : string);
            command =
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(cmiUser.getPlayer(), command);
            commandList.set(index, command);
          }

          this.plugin.getSpecializedCommandManager().processCmds(commandList, cmiUser.getPlayer());
        }
      }
    }
  }

  public void updateVoteCountList(CMIUser cmiUser) {
    if (cmiUser.getVotifierVotes() <= 0) {
      this.withVotes.remove(cmiUser);
    } else {
      this.withVotes.add(cmiUser);
    }
  }

  private void delayUpdate() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
      if (this.lastUpdate + (this.updateDelay * 1000 + 1000) < System.currentTimeMillis()) {
        this.updateTopList();
      }
    }

    this.sched =
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              this.recalculate();
              this.sched = null;
            },
            this.updateDelay * 20);
  }

  public void updateTopList() {
    if (this.lastUpdate + (this.updateDelay * 1000 + 1000) <= System.currentTimeMillis()
        && this.sched == null) {
      if (this.sched != null) {
        this.sched.cancel();
        this.sched = null;
      }

      this.recalculate();
      this.lastUpdate = System.currentTimeMillis();
    } else {
      this.delayUpdate();
    }
  }

  private void recalculate() {
    if (!this.calculating) {
      int value = this.withVotes.size();
      Random random = new Random(System.nanoTime());
      var hashSet = new HashSet<>(this.withVotes);
      this.calculating = true;
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            ConcurrentSkipListMap<Double, CMIUser> concurrentSkipListMap =
                new ConcurrentSkipListMap(Collections.reverseOrder());

            try {
              for (CMIUser cmiUser : hashSet) {
                if (cmiUser.getName(false) != null) {
                  Double voteKey = (double) cmiUser.getVotifierVotes();
                  if (voteKey != 0.0
                      && !this.ExcludeList.contains(cmiUser.getName().toLowerCase())) {
                    for (int probeIndex = 0;
                        probeIndex < value - concurrentSkipListMap.size()
                            && concurrentSkipListMap.containsKey(voteKey);
                        probeIndex++) {
                      voteKey = voteKey + random.nextInt(10000) / 1.0E8;
                    }

                    concurrentSkipListMap.put(voteKey, cmiUser);
                  }
                }
              }
            } catch (Exception | Error value3) {
              value3.printStackTrace();
            }

            CMIScheduler.runTask(
                this.plugin,
                () -> {
                  this.Top.clear();
                  this.Top.putAll(concurrentSkipListMap);
                  this.calculating = false;
                });
          });
    }
  }

  public ConcurrentSkipListMap<Double, CMIUser> getTop() {
    if (this.lastUpdate == 0L) {
      this.recalculate();
    }

    return this.Top;
  }

  public int getMaxVotesInADay() {
    return this.MaxVotesInADay;
  }

  private class voteReminder {
    private int times = 0;
    private long last = 0L;

    public voteReminder() {
      this.last = System.currentTimeMillis();
    }

    public void recordNotified() {
      this.times++;
      this.last = System.currentTimeMillis();
    }

    public int getTimes() {
      return this.times;
    }

    public long getLast() {
      return this.last;
    }
  }
}
