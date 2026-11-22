package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.commands.list.rankup;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Shadow.ShadowCommand;
import net.Zrips.CMILib.Shadow.ShadowCommandType;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class RankManager {
  private LinkedHashMap<String, CMIRank> ranks = new LinkedHashMap<>();
  private HashMap<UUID, Long> nextCheck = new HashMap<>();
  private HashMap<UUID, Long> nextAutoRecalculate = new HashMap<>();
  private HashMap<UUID, InformTimer> nextInform = new HashMap<>();
  private CMI plugin;
  BukkitScheduler scheduler;
  private HashMap<UUID, CMIRank> ranksCache = new HashMap<>();
  CMITask sched = null;
  CMITask recSched = null;
  HashMap<UUID, RankManager.rankCache> percentCache = new HashMap<>();
  NumberFormat formatter = new DecimalFormat("#0.00");
  private String fileName = "Ranks.yml";
  private int Delay = 30;
  private int Recalculation = 0;
  private boolean OnlyHours = false;
  private boolean includeMinutes = false;
  private boolean async = false;
  private boolean ListSamePathOnly = false;
  private int PlayerDelay = 120;
  private boolean progressBar = true;
  private boolean permissionCheck = false;
  private boolean strictPermissionCheck = false;
  private ParticleManager.CMIPresetAnimations RanksEffect =
      ParticleManager.CMIPresetAnimations.GColumn;

  public CMIRank getNullRank(CMIUser cmiUser) {
    return this.ranksCache.get(cmiUser.getUniqueId());
  }

  public CMIRank getRank(CMIUser cmiUser) {
    return this.ranksCache.computeIfAbsent(
        cmiUser.getUniqueId(),
        uuid -> CMI.getInstance().getRankManager().getDefaultRank(cmiUser.getPlayer(false)));
  }

  public CMIRank recalculateRank(CMIUser cmiUser) {
    CMIRank cmiRank = null;
    if (cmiUser.isOnline() && CMI.getInstance().getRankManager().isPermissionCheck()) {
      PermissionInfo permissionInfo =
          CMI.getInstance()
              .getPermissionsManager()
              .getPermissionInfo(cmiUser.getPlayer(), PermissionsManager.CMIPerm.rank_$1, 0L);
      if (permissionInfo != null && !permissionInfo.getValues().isEmpty()) {
        for (String string : permissionInfo.getValues()) {
          if (PermissionsManager.CMIPerm.rank_$1.hasPermission(cmiUser.getPlayer(), string)) {
            CMIRank namedRank = CMI.getInstance().getRankManager().getRank(string);
            if (namedRank != null
                && (cmiRank == null || cmiRank.getWeight() < namedRank.getWeight())) {
              cmiRank = namedRank;
            }
          }
        }
      }
    }

    for (var entry : CMI.getInstance().getRankManager().getRanks().entrySet()) {
      if (((CMIRank) entry.getValue()).isDefaultRank()
          && (cmiRank == null || cmiRank.getWeight() < ((CMIRank) entry.getValue()).getWeight())) {
        cmiRank = (CMIRank) entry.getValue();
      }
    }

    CMIRank bestRank = this.getNullRank(cmiUser);
    if (bestRank == null || cmiRank != null && bestRank.getWeight() < cmiRank.getWeight()) {
      bestRank = cmiRank;
    }

    return bestRank;
  }

  public void setRank(CMIUser cmiUser, CMIRank cmiRank) {
    this.ranksCache.put(cmiUser.getUniqueId(), cmiRank);
  }

  public RankManager(CMI cmi) {
    this.plugin = cmi;
  }

  private boolean isNextCheck(UUID uuid) {
    Long nextCheckTime = this.nextCheck.get(uuid);
    if (nextCheckTime != null && nextCheckTime >= System.currentTimeMillis()) {
      return false;
    }

    this.nextCheck.put(uuid, System.currentTimeMillis() + this.PlayerDelay);
    return true;
  }

  private boolean isNextAutoRecalculate(UUID uuid) {
    Long nextRecalculateTime = this.nextAutoRecalculate.get(uuid);
    if (nextRecalculateTime != null && nextRecalculateTime >= System.currentTimeMillis()) {
      return false;
    }

    this.nextAutoRecalculate.put(uuid, System.currentTimeMillis() + this.Recalculation);
    return true;
  }

  private boolean isNextInform(UUID uuid) {
    InformTimer informTimer = this.nextInform.get(uuid);
    if (informTimer == null) {
      this.nextInform.put(uuid, new InformTimer(System.currentTimeMillis() + this.PlayerDelay));
      return true;
    } else if (informTimer.getNextCheck() < System.currentTimeMillis()) {
      informTimer.addTimesInformed();
      informTimer.setNextCheck(
          System.currentTimeMillis()
              + this.PlayerDelay
              + informTimer.getTimesInformed() * this.PlayerDelay);
      return true;
    } else {
      return false;
    }
  }

  public void removeFromCheck(UUID uuid) {
    this.nextCheck.remove(uuid);
    this.nextAutoRecalculate.remove(uuid);
    this.nextInform.remove(uuid);
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }

    if (this.recSched != null) {
      this.recSched.cancel();
      this.recSched = null;
    }

    this.percentCache.clear();
  }

  public void autoRecheck() {
    if (CMIModule.ranks.isEnabled()) {
      if (this.Recalculation > 0) {
        this.recSched =
            CMIScheduler.scheduleSyncRepeatingTask(
                this.plugin,
                () -> {
                  for (Player player : Bukkit.getOnlinePlayers()) {
                    if (this.isNextAutoRecalculate(player.getUniqueId())) {
                      CMIUser cmiUser = CMIUser.getUser(player);
                      if (cmiUser != null) {
                        this.recalculateRank(cmiUser);
                      }
                    }
                  }
                },
                0L,
                200L);
      }
    }
  }

  private void cycle() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (this.isNextCheck(player.getUniqueId())) {
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          CMIRank cmiRank = cmiUser.getRank();
          if (cmiRank != null) {
            var list = cmiRank.getNextValidRankUps(cmiUser);
            if (!list.isEmpty()) {
              boolean flag = this.isNextInform(player.getUniqueId());
              int value = 0;

              for (CMIRank candidateRank : list) {
                if (candidateRank.isAutoRankup() && !candidateRank.isRankUpConfirmation()) {
                  value++;
                }
              }

              int rankupCount = value;
              if (rankupCount > 1) {
                this.plugin.consoleMessage(
                    "&cUser "
                        + player.getName()
                        + " can rankup into "
                        + rankupCount
                        + " ranks from "
                        + cmiRank.getName()
                        + " at once. This needs to be fixed");
              }

              for (CMIRank autoRank : list) {
                if (autoRank.isAutoRankup()) {
                  CMIScheduler.runTask(
                      this.plugin,
                      () -> {
                        if (autoRank.isRankUpConfirmation() && flag) {
                          String string =
                              ShadowCommand.addShadowCmd(
                                  player,
                                  "cmi rankup "
                                      + cmiUser.getName()
                                      + " "
                                      + autoRank.getName()
                                      + " confirm",
                                  false,
                                  ShadowCommandType.Player);
                          String shadowCommand = "cmi shadowcmd " + string;
                          RawMessage rawMessage = new RawMessage();
                          rawMessage
                              .addText(
                                  CMILC.getIM(
                                      rankup.class.getSimpleName(),
                                      "confirm",
                                      "[ranks]",
                                      autoRank.getDisplayName(),
                                      "[rank]",
                                      autoRank.getDisplayName()))
                              .addHover("&6" + autoRank.getName())
                              .addCommand(shadowCommand);
                          rawMessage.show(player);
                        }

                        if (autoRank.isAutoRankup()
                            && !autoRank.isRankUpConfirmation()
                            && rankupCount == 1) {
                          cmiUser.setRank(autoRank);
                          autoRank.finalizeRankup(cmiUser);
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

  public void run() {
    if (CMIModule.ranks.isEnabled()) {
      if (this.Delay > 0) {
        if (this.async) {
          this.sched = CMIScheduler.runTimerAsync(this.plugin, this::cycle, 0L, this.Delay * 20L);
        } else {
          this.sched =
              CMIScheduler.scheduleSyncRepeatingTask(
                  this.plugin, this::cycle, 0L, this.Delay * 20L);
        }
      }
    }
  }

  public void addRank(CMIRank cmiRank) {
    this.ranks.put(cmiRank.getName().toLowerCase(), cmiRank);
    var linkedHashMap = cmiRank.getStatRequirements();
    LinkedHashMap<
            com.Zrips.CMI.Modules.Statistics.StatsManager.CMIStatistic, LinkedHashMap<Object, Long>>
        noneStatRequirements = new LinkedHashMap<>();

    for (Entry entry : ((Iterable<Entry>) new LinkedHashMap(linkedHashMap).entrySet())) {
      LinkedHashMap statRequirement = (LinkedHashMap) entry.getValue();
      if (statRequirement.get("none") != null) {
        LinkedHashMap noneRequirement = (LinkedHashMap) linkedHashMap.remove(entry.getKey());
        noneStatRequirements.put((StatsManager.CMIStatistic) entry.getKey(), noneRequirement);
      }
    }

    linkedHashMap.putAll(noneStatRequirements);
  }

  public HashMap<String, CMIRank> getRanks() {
    return this.ranks;
  }

  public CMIRank getRank(String string) {
    return string == null ? null : this.ranks.get(string.toLowerCase());
  }

  public CMIRank getDefaultRank(Player player) {
    CMIRank cmiRank = null;
    if (player != null && player.isOnline()) {
      for (var entry : this.ranks.entrySet()) {
        if (PermissionsManager.CMIPerm.rank_$1.hasSetPermission(player, (String) entry.getKey())
            && (cmiRank == null
                || cmiRank.getWeight() < ((CMIRank) entry.getValue()).getWeight())) {
          cmiRank = (CMIRank) entry.getValue();
        }
      }
    }

    if (cmiRank == null) {
      for (var rankEntry : this.ranks.entrySet()) {
        if (((CMIRank) rankEntry.getValue()).isDefaultRank()) {
          return (CMIRank) rankEntry.getValue();
        }
      }
    }

    return cmiRank;
  }

  public boolean canRankUpAuto(CMIUser cmiUser) {
    CMIRank cmiRank = cmiUser.getRank();
    if (cmiRank == null) {
      return false;
    }

    for (CMIRank nextRank : cmiRank.getNextValidRankUps(cmiUser)) {
      if (nextRank != null && nextRank.isAutoRankup()) {
        return true;
      }
    }

    return false;
  }

  public RankManager.rankupFailType canRankUp(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank == null) {
      return RankManager.rankupFailType.NoRank;
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return RankManager.rankupFailType.NoRank;
    }

    if (cmiRank.getMoneyCost() > 0.0 && !cmiUser.hasMoney(cmiRank.getMoneyCost())) {
      return RankManager.rankupFailType.Money;
    }

    if (cmiRank.getExpCost() > 0.0 && cmiRank.getExpCost() > cmiUser.getExp()) {
      return RankManager.rankupFailType.Exp;
    }

    if (cmiRank.getVotes() > 0
        && CMIHookType.Votifier.isEnabled()
        && cmiRank.getVotes() > cmiUser.getVotifierVotes()) {
      return RankManager.rankupFailType.Votes;
    }

    for (var entry : cmiRank.getStatRequirements().entrySet()) {
      for (Entry statEntry : ((Iterable<Entry>) ((LinkedHashMap) entry.getValue()).entrySet())) {
        Long statValue =
            cmiUser.getStatistic((StatsManager.CMIStatistic) entry.getKey(), statEntry.getKey());
        if (statValue < (Long) statEntry.getValue()) {
          return RankManager.rankupFailType.Stats;
        }
      }
    }

    for (var permEntry : cmiRank.getPermRequirements().entrySet()) {
      if (!player.hasPermission((String) permEntry.getKey())) {
        return RankManager.rankupFailType.Perm;
      }
    }

    if (CMIHookType.mcMMO.isEnabled()) {
      for (var mcmmoEntry : cmiRank.getMcmmoRequirements().entrySet()) {
        int value = McMMOManager.getSkillLevel(player, (String) mcmmoEntry.getKey());
        if (value < (Integer) mcmmoEntry.getValue()) {
          return RankManager.rankupFailType.McMMO;
        }
      }
    }

    if (CMIHookType.AureliumSkills.isEnabled()) {
      for (var aureliumEntry : cmiRank.getAureliumRequirements().entrySet()) {
        int aureliumLevel = AureliumManager.getSkillLevel(player, (String) aureliumEntry.getKey());
        if (aureliumLevel < (Integer) aureliumEntry.getValue()) {
          return RankManager.rankupFailType.Aurelium;
        }
      }
    }

    if (CMIHookType.AuraSkills.isEnabled()) {
      for (var auraSkillsEntry : cmiRank.getAuraSkillsRequirements().entrySet()) {
        int auraSkillsLevel =
            AuraSkillsManager.getSkillLevel(player, (String) auraSkillsEntry.getKey());
        if (auraSkillsLevel < (Integer) auraSkillsEntry.getValue()) {
          return RankManager.rankupFailType.AuraSkills;
        }
      }
    }

    if (CMIHookType.Jobs.isEnabled()) {
      for (var jobsEntry : cmiRank.getJobsRequirements().entrySet()) {
        int jobsLevel = JobsManager.getJobsLevel(player, (String) jobsEntry.getKey());
        if (jobsLevel < (Integer) jobsEntry.getValue()) {
          return RankManager.rankupFailType.Jobs;
        }
      }
    }

    if (!cmiRank.getItemRequirements().isEmpty()) {
      var hashMap = getInvContentsAmounts(player);

      for (var itemEntry : cmiRank.getItemRequirements().entrySet()) {
        CMIItemStack cmiItemStack =
            (CMIItemStack) hashMap.get(((CMIItemStack) itemEntry.getKey()).getCMIType().toString());
        if (cmiItemStack == null) {
          return RankManager.rankupFailType.Items;
        }

        if (cmiItemStack.getAmount() < (Integer) itemEntry.getValue()) {
          return RankManager.rankupFailType.Items;
        }
      }
    }

    if (!cmiRank.getPlaceholderRequirements().isEmpty()) {
      for (var placeholderEntry : cmiRank.getPlaceholderRequirements().entrySet()) {
        if (((CMIRankPlaceholder) placeholderEntry.getValue()).percent(player) < 100.0) {
          return RankManager.rankupFailType.Placeholoder;
        }
      }
    }

    return RankManager.rankupFailType.None;
  }

  private static HashMap<String, CMIItemStack> getInvContentsAmounts(Player player) {
    HashMap<String, CMIItemStack> hashMap = new HashMap();
    ItemStack[] itemStacks;
    int value = (itemStacks = player.getInventory().getContents()).length;

    label44:
    for (int slotIndex = 0; slotIndex < value; slotIndex++) {
      ItemStack itemStack = itemStacks[slotIndex];
      if (itemStack != null) {
        CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
        String string = cmiMaterial.toString();
        if (cmiMaterial.isShulkerBox()) {
          Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
          if (inventory != null) {
            ItemStack[] inventoryContents;
            int contentCount = (inventoryContents = inventory.getContents()).length;

            for (int contentIndex = 0; contentIndex < contentCount; contentIndex++) {
              ItemStack inventoryItem = inventoryContents[contentIndex];
              if (inventoryItem != null) {
                continue label44;
              }
            }
          }
        }

        if (!cmiMaterial.isEquipment() || itemStack.getDurability() <= 0) {
          CMIItemStack cmiItemStack = (CMIItemStack) hashMap.get(string);
          if (cmiItemStack == null) {
            cmiItemStack = new CMIItemStack(itemStack.clone());
          } else {
            cmiItemStack.setAmount(cmiItemStack.getAmount() + itemStack.getAmount());
          }

          hashMap.put(string, cmiItemStack);
        }
      }
    }

    return hashMap;
  }

  public boolean removeContents(Player player, LinkedHashMap<CMIItemStack, Integer> linkedHashMap) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      return false;
    }

    for (var entry : linkedHashMap.entrySet()) {
      cmiUser.getInventory().removeItemByCriteria(((CMIItemStack) entry.getKey()).getItemStack());
    }

    return true;
  }

  public void listStatsRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    for (var entry : cmiRank.getStatRequirements().entrySet()) {
      for (Entry statEntry : ((Iterable<Entry>) ((LinkedHashMap) entry.getValue()).entrySet())) {
        long longValue =
            cmiUser.getStatistic((StatsManager.CMIStatistic) entry.getKey(), statEntry.getKey());
        String string = "none";
        if (statEntry.getKey() instanceof CMIEntityType) {
          string = ((CMIEntityType) statEntry.getKey()).getTranslatedName();
        } else if (statEntry.getKey() instanceof CMIMaterial) {
          string = ((CMIMaterial) statEntry.getKey()).getName();
        } else {
          string = (String) statEntry.getKey();
        }

        String colorCode =
            longValue >= (Long) statEntry.getValue()
                ? LC.info_variables_TrueColor.getLocale(new Object[0])
                : LC.info_variables_FalseColor.getLocale(new Object[0]);
        String extraText =
            !string.equalsIgnoreCase("none")
                ? CMILC.getIM("rankinfo", "requiredStatsExtra", "[type]", firstCap(string))
                : "";
        CMILC.info(
            "rankinfo",
            commandSender,
            "requiredStats",
            "[name]",
            CMILC.getIM("stats", "Statistics." + entry.getKey()),
            "[extra]",
            extraText,
            "[color]",
            colorCode,
            "[current]",
            this.translateValue((StatsManager.CMIStatistic) entry.getKey(), longValue),
            "[needed]",
            this.translateValue(
                (StatsManager.CMIStatistic) entry.getKey(), (Long) statEntry.getValue()));
      }
    }
  }

  public HashMap<
          StatsManager.CMIStatistic, LinkedHashMap<Object, RankManager.rankCurrentRequirement>>
      getStatsRequirements(CMIUser cmiUser, CMIRank cmiRank) {
    HashMap<
            com.Zrips.CMI.Modules.Statistics.StatsManager.CMIStatistic,
            LinkedHashMap<Object, rankCurrentRequirement>>
        hashMap = new HashMap<>();

    for (var entry : cmiRank.getStatRequirements().entrySet()) {
      for (Entry statEntry : ((Iterable<Entry>) ((LinkedHashMap) entry.getValue()).entrySet())) {
        RankManager.rankCurrentRequirement currentRequirement =
            new RankManager.rankCurrentRequirement(
                (Long) statEntry.getValue(),
                cmiUser.getStatistic(
                    (StatsManager.CMIStatistic) entry.getKey(), statEntry.getKey()));
        LinkedHashMap<Object, rankCurrentRequirement> linkedHashMap = hashMap.get(entry.getKey());
        if (linkedHashMap == null) {
          linkedHashMap = new LinkedHashMap<>();
        }

        linkedHashMap.put(statEntry.getKey(), currentRequirement);
        hashMap.put((StatsManager.CMIStatistic) entry.getKey(), linkedHashMap);
      }
    }

    return hashMap;
  }

  public Double getStatsDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    var hashMap = this.plugin.getRankManager().getStatsRequirements(cmiUser, cmiRank);
    if (hashMap.isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Stats)) {
      return cache.getCache(RankManager.rankupType.Stats);
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (Entry entry : hashMap.entrySet()) {
      for (Entry statEntry : ((Iterable<Entry>) ((LinkedHashMap) entry.getValue()).entrySet())) {
        requirementCount = requirementCount + 1.0;
        Double requirementPercent = 100.0;
        if (((RankManager.rankCurrentRequirement) statEntry.getValue()).getNeed()
            > ((RankManager.rankCurrentRequirement) statEntry.getValue()).getHave()) {
          requirementPercent =
              ((RankManager.rankCurrentRequirement) statEntry.getValue()).getHave().longValue()
                  * 100.0
                  / ((RankManager.rankCurrentRequirement) statEntry.getValue())
                      .getNeed()
                      .doubleValue();
        }

        percentSum = percentSum + requirementPercent;
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.Stats, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return percentSum;
  }

  private static String firstCap(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }

  private void calculate(Double percent, RankManager.donePercentage overallDone, int value) {
    if (percent != null) {
      overallDone.addPercent(percent);
      overallDone.addIncrement(value);
    }
  }

  public Double getOverallDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    RankManager.donePercentage overallDone = new RankManager.donePercentage();
    this.calculate(this.getMoneyDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getExpDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getVoteDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getStatsDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getPermDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getPlaceholderDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getAureliumDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getAuraSkillsDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getJobsDonePercentage(cmiUser, cmiRank), overallDone, 1);
    this.calculate(this.getItemDonePercentage(cmiUser, cmiRank), overallDone, 1);
    return overallDone.getTimes() == 0.0 ? 0.0 : overallDone.getPercent() / overallDone.getTimes();
  }

  public Double getMoneyDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getMoneyCost() <= 0.0) {
      return null;
    }

    double number = cmiUser.getBalance();
    return number > cmiRank.getMoneyCost() ? 100.0 : number * 100.0 / cmiRank.getMoneyCost();
  }

  public void listMoneyRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getMoneyCost() > 0.0) {
      double number = cmiUser.getBalance();
      String string = LC.info_variables_FalseColor.getLocale(new Object[0]);
      if (number >= cmiRank.getMoneyCost()) {
        string = LC.info_variables_TrueColor.getLocale(new Object[0]);
      }

      CMILC.info(
          "rankinfo",
          commandSender,
          "requiredMoney",
          "[name]",
          cmiRank.getName(),
          "[color]",
          string,
          "[current]",
          String.format("%.0f", number),
          "[needed]",
          String.format("%.0f", cmiRank.getMoneyCost()));
    }
  }

  public Double getExpDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getExpCost() <= 0.0) {
      return null;
    }

    double number = cmiUser.getTotalExp();
    return number > cmiRank.getExpCost() ? 100.0 : number * 100.0 / cmiRank.getExpCost();
  }

  public void listExpRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getExpCost() > 0.0) {
      double number = cmiUser.getTotalExp();
      String string = LC.info_variables_FalseColor.getLocale(new Object[0]);
      if (number >= cmiRank.getExpCost()) {
        string = LC.info_variables_TrueColor.getLocale(new Object[0]);
      }

      CMILC.info(
          "rankinfo",
          commandSender,
          "requiredExp",
          "[name]",
          cmiRank.getName(),
          "[color]",
          string,
          "[current]",
          number,
          "[needed]",
          cmiRank.getExpCost());
    }
  }

  public Double getVoteDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getVotes() > 0 && CMIHookType.Votifier.isEnabled()) {
      int value = cmiUser.getVotifierVotes();
      return value > cmiRank.getVotes() ? 100.0 : value * 100.0 / cmiRank.getVotes();
    } else {
      return null;
    }
  }

  public void listVoteRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getVotes() > 0 && CMIHookType.Votifier.isEnabled()) {
      int value = cmiUser.getVotifierVotes();
      String string = LC.info_variables_FalseColor.getLocale(new Object[0]);
      if (value >= cmiRank.getVotes()) {
        string = LC.info_variables_TrueColor.getLocale(new Object[0]);
      }

      CMILC.info(
          "rankinfo",
          commandSender,
          "requiredVotes",
          "[name]",
          cmiRank.getName(),
          "[color]",
          string,
          "[current]",
          value,
          "[needed]",
          cmiRank.getVotes());
    }
  }

  public void listPermRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (!cmiRank.getPermRequirements().isEmpty()) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM("rankinfo", "requiredPerm"));
      String string = "";

      for (var entry : cmiRank.getPermRequirements().entrySet()) {
        String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
        if (cmiUser.getPlayer().hasPermission((String) entry.getKey())) {
          colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
        }

        String displayText =
            entry.getValue() == null ? (String) entry.getKey() : (String) entry.getValue();
        if (!string.isEmpty()) {
          rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
        }

        rawMessage.addText(colorCode + displayText).addHover(colorCode + (String) entry.getKey());
        string = string + " ";
      }

      rawMessage.show(commandSender);
    }
  }

  public Double getPermDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getPermRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Perm)) {
      return cache.getCache(RankManager.rankupType.Perm);
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (var entry : cmiRank.getPermRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      if (cmiUser.getPlayer().hasPermission((String) entry.getKey())) {
        percentSum = percentSum + 100.0;
      } else {
        percentSum = percentSum + 0.0;
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.Perm, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public Double getPlaceholderDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getPlaceholderRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Placeholoder)) {
      return cache.getCache(RankManager.rankupType.Placeholoder);
    }

    Double percentSum = 0.0;

    for (var entry : cmiRank.getPlaceholderRequirements().entrySet()) {
      percentSum =
          percentSum + ((CMIRankPlaceholder) entry.getValue()).percent(cmiUser.getPlayer(false));
    }

    percentSum = percentSum / cmiRank.getPlaceholderRequirements().size();
    if (cache == null) {
      cache = new RankManager.rankCache(2000L);
    }

    cache.setCache(RankManager.rankupType.Placeholoder, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listPlaceholderRequirements(
      CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (!cmiRank.getPlaceholderRequirements().isEmpty()) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM("rankinfo", "requiredPlaceholder"));
      Player player = cmiUser.getPlayer();
      if (player != null) {
        String string = "";

        for (var entry : cmiRank.getPlaceholderRequirements().entrySet()) {
          CMIRankPlaceholder cmiRankPlaceholder = (CMIRankPlaceholder) entry.getValue();
          String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
          if (cmiRankPlaceholder.percent(player) >= 100.0) {
            colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
          }

          if (!string.isEmpty()) {
            rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          string = "s";
          rawMessage.addText(
              CMILC.getIM(
                  "rankinfo",
                  "requiredPlaceholderList",
                  "[message]",
                  cmiRankPlaceholder.getMessage(),
                  "[color]",
                  colorCode));
          Object object = cmiRankPlaceholder.getValue();
          if (object instanceof Double && (Double) object % 1.0 == 0.0) {
            object = ((Double) object).intValue();
          }

          rawMessage.addHover(
              CMILC.getIM(
                  "rankinfo",
                  "requiredPlaceholderHover",
                  "[needed]",
                  object,
                  "[color]",
                  colorCode,
                  "[current]",
                  CMI.getInstance()
                      .getPlaceholderAPIManager()
                      .updatePlaceHolders(player, cmiRankPlaceholder.getPlaceholder())));
        }

        rawMessage.show(commandSender);
      }
    }
  }

  public Double getAureliumDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (!CMIHookType.AureliumSkills.isEnabled()) {
      return null;
    }

    if (cmiRank.getAureliumRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Aurelium)) {
      return cache.getCache(RankManager.rankupType.Aurelium);
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (var entry : cmiRank.getAureliumRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      String string = (String) entry.getKey();
      if (AureliumManager.isSkillCorrect(string) || string.equalsIgnoreCase("Power")) {
        int value = AureliumManager.getSkillLevel(player, string);
        if (value >= (Integer) entry.getValue()) {
          percentSum = percentSum + 100.0;
        } else {
          percentSum = percentSum + value * 100.0 / ((Integer) entry.getValue()).doubleValue();
        }
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.Aurelium, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listAureliumRequirements(
      CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (CMIHookType.AureliumSkills.isEnabled()) {
      if (!cmiRank.getAureliumRequirements().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM("rankinfo", "requiredAurelium"));
        Player player = cmiUser.getPlayer();
        if (player != null) {
          String string = "";

          for (var entry : cmiRank.getAureliumRequirements().entrySet()) {
            String skillName = (String) entry.getKey();
            if (AureliumManager.isSkillCorrect(skillName) || skillName.equalsIgnoreCase("Power")) {
              int value = AureliumManager.getSkillLevel(player, skillName);
              String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
              if (value >= (Integer) entry.getValue()) {
                colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
              }

              if (!string.isEmpty()) {
                rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
              }

              string = "s";
              rawMessage
                  .addText(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredAureliumList",
                          "[name]",
                          AureliumManager.getRealSkillName(commandSender, (String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          AureliumManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()))
                  .addHover(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredAureliumListHover",
                          "[name]",
                          AureliumManager.getRealSkillName(commandSender, (String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          AureliumManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()));
            }
          }

          rawMessage.show(commandSender);
        }
      }
    }
  }

  public Double getAuraSkillsDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (!CMIHookType.AuraSkills.isEnabled()) {
      return null;
    }

    if (cmiRank.getAuraSkillsRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.AuraSkills)) {
      return cache.getCache(RankManager.rankupType.AuraSkills);
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (var entry : cmiRank.getAuraSkillsRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      String string = (String) entry.getKey();
      if (AuraSkillsManager.isSkillCorrect(string) || string.equalsIgnoreCase("Power")) {
        int value = AuraSkillsManager.getSkillLevel(player, string);
        if (value >= (Integer) entry.getValue()) {
          percentSum = percentSum + 100.0;
        } else {
          percentSum = percentSum + value * 100.0 / ((Integer) entry.getValue()).doubleValue();
        }
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.AuraSkills, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listAuraSkillsRequirements(
      CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (CMIHookType.AuraSkills.isEnabled()) {
      if (!cmiRank.getAuraSkillsRequirements().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM("rankinfo", "requiredAuraSkills"));
        Player player = cmiUser.getPlayer();
        if (player != null) {
          String string = "";

          for (var entry : cmiRank.getAuraSkillsRequirements().entrySet()) {
            String skillName = (String) entry.getKey();
            if (AuraSkillsManager.isSkillCorrect(skillName)
                || skillName.equalsIgnoreCase("Power")) {
              int value = AuraSkillsManager.getSkillLevel(player, skillName);
              String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
              if (value >= (Integer) entry.getValue()) {
                colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
              }

              if (!string.isEmpty()) {
                rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
              }

              string = "s";
              rawMessage
                  .addText(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredAuraSkillsList",
                          "[name]",
                          AuraSkillsManager.getRealSkillName(
                              commandSender, (String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          AuraSkillsManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()))
                  .addHover(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredAuraSkillsListHover",
                          "[name]",
                          AuraSkillsManager.getRealSkillName(
                              commandSender, (String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          AuraSkillsManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()));
            }
          }

          rawMessage.show(commandSender);
        }
      }
    }
  }

  public Double getMcMMODonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (!CMIHookType.mcMMO.isEnabled()) {
      return null;
    }

    if (cmiRank.getMcmmoRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.McMMO)) {
      return cache.getCache(RankManager.rankupType.McMMO);
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (var entry : cmiRank.getMcmmoRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      String string = (String) entry.getKey();
      if (McMMOManager.isSkillCorrect(string) || string.equalsIgnoreCase("Power")) {
        int value = McMMOManager.getSkillLevel(player, string);
        if (value >= (Integer) entry.getValue()) {
          percentSum = percentSum + 100.0;
        } else {
          percentSum = percentSum + value * 100.0 / ((Integer) entry.getValue()).doubleValue();
        }
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.McMMO, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listMcmmoRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (CMIHookType.mcMMO.isEnabled()) {
      if (!cmiRank.getMcmmoRequirements().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM("rankinfo", "requiredMcmmo"));
        Player player = cmiUser.getPlayer();
        if (player != null) {
          String string = "";

          for (var entry : cmiRank.getMcmmoRequirements().entrySet()) {
            String skillName = (String) entry.getKey();
            if (McMMOManager.isSkillCorrect(skillName) || skillName.equalsIgnoreCase("Power")) {
              int value = McMMOManager.getSkillLevel(player, skillName);
              String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
              if (value >= (Integer) entry.getValue()) {
                colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
              }

              if (!string.isEmpty()) {
                rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
              }

              string = "s";
              rawMessage
                  .addText(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredMcmmoList",
                          "[name]",
                          McMMOManager.getRealSkillName((String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          McMMOManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()))
                  .addHover(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredMcmmoListHover",
                          "[name]",
                          McMMOManager.getRealSkillName((String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          McMMOManager.getSkillLevel(player, skillName),
                          "[needed]",
                          entry.getValue()));
            }
          }

          rawMessage.show(commandSender);
        }
      }
    }
  }

  public Double getJobsDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (!CMIHookType.Jobs.isEnabled()) {
      return null;
    }

    if (cmiRank.getJobsRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Jobs)) {
      return cache.getCache(RankManager.rankupType.Jobs);
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;

    for (var entry : cmiRank.getJobsRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      String string = (String) entry.getKey();
      if (JobsManager.isJobCorrect(string) || string.equalsIgnoreCase("TotalLevel")) {
        int value = JobsManager.getJobsLevel(player, string);
        if (value >= (Integer) entry.getValue()) {
          percentSum = percentSum + 100.0;
        } else {
          percentSum = percentSum + value * 100.0 / ((Integer) entry.getValue()).doubleValue();
        }
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.Jobs, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listJobsRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (CMIHookType.Jobs.isEnabled()) {
      if (!cmiRank.getJobsRequirements().isEmpty()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(CMILC.getIM("rankinfo", "requiredJobs"));
        Player player = cmiUser.getPlayer();
        if (player != null) {
          for (var entry : cmiRank.getJobsRequirements().entrySet()) {
            String string = (String) entry.getKey();
            if (JobsManager.isJobCorrect(string) || string.equalsIgnoreCase("TotalLevel")) {
              int value = JobsManager.getJobsLevel(player, string);
              String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
              if (value >= (Integer) entry.getValue()) {
                colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
              }

              rawMessage
                  .addText(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredJobsList",
                          "[name]",
                          JobsManager.getRealJobName((String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          JobsManager.getJobsLevel(player, string),
                          "[needed]",
                          entry.getValue()))
                  .addHover(
                      CMILC.getIM(
                          "rankinfo",
                          "requiredJobsListHover",
                          "[name]",
                          JobsManager.getRealJobName((String) entry.getKey()),
                          "[color]",
                          colorCode,
                          "[current]",
                          JobsManager.getJobsLevel(player, string),
                          "[needed]",
                          entry.getValue()));
            }
          }

          rawMessage.show(commandSender);
        }
      }
    }
  }

  public Double getItemDonePercentage(CMIUser cmiUser, CMIRank cmiRank) {
    if (cmiRank.getItemRequirements().isEmpty()) {
      return null;
    }

    RankManager.rankCache cache = this.percentCache.get(cmiUser.getUniqueId());
    if (cache != null && !cache.timeToCheck(RankManager.rankupType.Items)) {
      return cache.getCache(RankManager.rankupType.Items);
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    Double requirementCount = 0.0;
    Double percentSum = 0.0;
    var hashMap = getInvContentsAmounts(player);

    for (var entry : cmiRank.getItemRequirements().entrySet()) {
      requirementCount = requirementCount + 1.0;
      CMIItemStack cmiItemStack =
          (CMIItemStack) hashMap.get(((CMIItemStack) entry.getKey()).getCMIType().toString());
      int value = cmiItemStack != null ? cmiItemStack.getAmount() : 0;
      if (value >= (Integer) entry.getValue()) {
        percentSum = percentSum + 100.0;
      } else {
        percentSum = percentSum + value * 100.0 / ((Integer) entry.getValue()).doubleValue();
      }
    }

    if (requirementCount == 0.0) {
      return null;
    }

    percentSum = percentSum / requirementCount;
    if (cache == null) {
      cache = new RankManager.rankCache();
    }

    cache.setCache(RankManager.rankupType.Items, percentSum);
    this.percentCache.put(cmiUser.getUniqueId(), cache);
    return CMINumber.clamp(percentSum, 0.0, 100.0);
  }

  public void listItemRequirements(CommandSender commandSender, CMIUser cmiUser, CMIRank cmiRank) {
    if (!cmiRank.getItemRequirements().isEmpty()) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM("rankinfo", "requiredItem"));
      Player player = cmiUser.getPlayer();
      if (player != null) {
        var hashMap = getInvContentsAmounts(player);
        String string = "";

        for (var entry : cmiRank.getItemRequirements().entrySet()) {
          CMIItemStack cmiItemStack = (CMIItemStack) entry.getKey();
          CMIItemStack inventoryStack =
              (CMIItemStack) hashMap.get(((CMIItemStack) entry.getKey()).getCMIType().toString());
          int value = inventoryStack != null ? inventoryStack.getAmount() : 0;
          String colorCode = LC.info_variables_FalseColor.getLocale(new Object[0]);
          if (value >= (Integer) entry.getValue()) {
            colorCode = LC.info_variables_TrueColor.getLocale(new Object[0]);
          }

          if (!string.isEmpty()) {
            rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          rawMessage
              .addText(
                  CMILC.getIM(
                      "rankinfo",
                      "requiredItemsList",
                      "[name]",
                      cmiItemStack.getRealName(),
                      "[color]",
                      colorCode,
                      "[current]",
                      value,
                      "[needed]",
                      entry.getValue()))
              .addHover(
                  CMILC.getIM(
                      "rankinfo",
                      "requiredItemListHover",
                      "[name]",
                      cmiItemStack.getRealName(),
                      "[color]",
                      colorCode,
                      "[current]",
                      value,
                      "[needed]",
                      entry.getValue()));
          string = string + ",";
        }

        rawMessage.show(commandSender);
      }
    }
  }

  public String translateValue(StatsManager.CMIStatistic cmiStatistic, Long statValue) {
    switch (cmiStatistic.getType()) {
      case Time:
        if (!this.OnlyHours) {
          return CMIChatColor.stripColor(CMITimeManager.to24hourShort(statValue));
        }

        return CMIChatColor.stripColor(
            CMITimeManager.toOnlyHoursShort(statValue, true, this.includeMinutes));
      case Distance:
        return this.convertDistance(statValue);
      case Damage:
        return this.formatter.format(statValue.longValue() / 10.0);
      case Number:
      default:
        return statValue.toString();
    }
  }

  private String convertDistance(long longValue) {
    String string;
    if (longValue < 100L) {
      string = CMILC.getIM("rankinfo", "distanceCM", "[value]", longValue);
    } else if (longValue / 100L < 1000L) {
      string = CMILC.getIM("rankinfo", "distanceM", "[value]", longValue / 100.0);
    } else {
      string =
          CMILC.getIM("rankinfo", "distanceKm", "[value]", Math.round(longValue / 1000.0) / 100.0);
    }

    return string;
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    CMI.getInstance()
        .safeRenameFile(this.fileName.toLowerCase(), "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(
              new File(this.plugin.getDataFolder(), "Settings" + File.separator + this.fileName));
      configReader.load();
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      this.percentCache.clear();
      configReader.header(
          Arrays.asList(
              "Main server folder spigot.yml and check that stats saving prevention its set to"
                  + " false stats->disable-saving: false",
              "Player will get default rank group depending on set permission node"
                  + " cmi.rank.[groupName]",
              "Not enabled ranks will be ignored",
              "DisplayName section is optional and it will be used to represent rank ingame. This"
                  + " is NOT defining required permission node and you still need to use node name"
                  + " to rankup to this rank",
              "DefaultRank section defines if player can be assigned to this rank if he don't have"
                  + " any. There can be more than one default rank, and depending on permision"
                  + " node, first rank will be assigned to that player when needed",
              "PermissionRequirement section defines list of permissions player should have to"
                  + " rankup to that rank. Optional - For each permission node you can add its name"
                  + " which will be shown in /cmi rankinfo with : In example cmi.command.fly:Fly",
              "McMMORequirement section defines list of McMMO skills and levels player should have"
                  + " to rankup to that rank.",
              "AureliumRequirement section defines list of Aurelium skills and levels player should"
                  + " have to rankup to that rank.",
              "AuraSkillsRequirement section defines list of Aura skills and levels player should"
                  + " have to rankup to that rank.",
              "McMMO and Aurelium sections can use skill name as Power to indicate total sum of all"
                  + " skills",
              "JobsRequirement section defines list of Jobs and levels player should have to rankup"
                  + " to that rank.",
              "ItemRequirement section defines list of items player should have to rankup to that"
                  + " rank. Items will be taken after succesfull rankup from players inventory.",
              "AutoRankup section defines if player will be auto ranked to this rank if possible."
                  + " Keep in mind that if there is more than one legit rankup, player will have to"
                  + " confirm to which rank he wants to rankup",
              "RankupConfirmation section defines if you want to add additional confirmation for"
                  + " this rankup. Usefull to avoid unintentional rankups. Keep in mind that"
                  + " autorankup will request confirmation for rankup to this rank even if there is"
                  + " only one legit rankup option",
              "NextRanks is list of possible next ranks from this rank. Can be one or can be"
                  + " dozens. Keep in mind that each rank requirements will be shown in chat and it"
                  + " can clutter quite allot if there is bunch of them at once",
              "MoneyCost defines how much player will have to pay to rankup to this rank",
              "ExpCost defines how much player will have to pay in exp to rankup to this rank",
              "Votes defines how much player will need to have votes to rankup to this rank",
              "Commands is list which will be performed on rankup. Can be additional actions, like"
                  + " message broadcast or anything alse",
              "Main section is StatsRequirements and it can have allot of different values in it:",
              "  In general format goes like this [mainStat](:optionalSubStat):amount",
              "  In example MonsterKills:100 will require from player to have 100 monster kills"
                  + " while MonsterKills:Zombie:100 will require to have 100 zombie kills. Both of"
                  + " them can be used if needed.",
              "    List of possible requirement and basic explanations:",
              "      Distance in blocks:",
              "        Travel - travel in total",
              "        Walk - has to walk",
              "        Sneak - has to sneak",
              "        Sprint - has to sprint",
              "        Swim - has to swim",
              "        Fall - has to fall",
              "        Climb - has to climb",
              "        Fly - has to fly",
              "        Dive - has to swim under water",
              "        WalkOn Water - has to walk on water",
              "        WalkUnderWater - has to walk under water",
              "        MinecartTravel - has to travel with minecart",
              "        BoatTravel - has to travel with boat",
              "        PigTravel - has to ride pig",
              "        HorseTravel - has to ride horse",
              "        ElytraTravel - has to fly with elytra",
              "        GhastTravel - has to travel with ghast",
              "        StriderTravel - has to travel with strider",
              "      Time in sec:",
              "        PlayTime - total play time",
              "        FromLastDeath - from last death",
              "        SneakTime - sneak time",
              "        TotalWorldTime - total world time",
              "      Amounts:",
              "        LeaveGame - how many times player left game",
              "        Jump - jump count",
              "        DamageDealt - total damage made",
              "        DamageTaken - total damage taken",
              "        DamageDealtAbsorbed - total damage absorbed",
              "        DamageDealtResisted - total damage resisted",
              "        DamageBlockedByShield - total damage blocked with shield",
              "        DamageAbsorbed - total damage absorbed",
              "        DamageResisted - total damage resisted",
              "        Deaths - count of deaths",
              "        KilledBy - defines amount of deaths by monster. If specific monster is not"
                  + " defined, then total amount is used",
              "        MobKills - total amount of mobs killed by player",
              "        PlayerKills - total amount of players killed. Indirect kills don't count",
              "        ItemEnchanted - amount of enchanted items",
              "        AnimalsBred - animals bred",
              "        FishCaught - fish caught",
              "        TalkedToVillager - times talked to villager",
              "        TradedWithVillager - traded with willager",
              "        CakeSlicesEaten - cake slices eaten",
              "        CauldronFilled - cauldron filled",
              "        CouldronUsed - couldron used",
              "        ArmorCleaned - times armor cleaned (leather armor)",
              "        BannerCleaned - times banner cleaned",
              "        BrewingStandInteraction - brewingstand interactions",
              "        BeaconInteraction - beacon interactions",
              "        CraftingTableInteraction - crafting table interactions",
              "        FurnaceInteraction - furnace interactions",
              "        DispenserInspected - dispenser inspected",
              "        DropperInspected - dropper inspected",
              "        HopperInspected - hopper inspected",
              "        ChestOpened - chest open",
              "        TrappedChestTriggered - trapped chest triggered",
              "        EnderchestOpened - enderchest opened",
              "        NoteblockPlayed - noteblock played",
              "        NoteblockTuned - noteblock tuned",
              "        FlowerPotted - flower potted",
              "        RecordPlayed - record played",
              "        SleeptInBed - sleept in bed",
              "        ShulkerBoxOpened - shulker box opened",
              "        CleanShulkerBox - shulker box cleaned",
              "        Drop - defines amount of dropped items. If specific item is not defined,"
                  + " then total amount is used",
              "        MineBlock - defines amount of blocks mined. If specific block is not"
                  + " defined, then total amount is used",
              "        PlaceBlock - defines amount of blocks placed. If specific block is not"
                  + " defined, then total amount is used",
              "        BreakItem - defines amount of broken items. If specific item is not defined,"
                  + " then total amount is used",
              "        CraftItem - defines amount of crafted items. If specific item is not"
                  + " defined, then total amount is used",
              "        UseItem - defines amount of item usage. like mining with pickaxe. If"
                  + " specific item is not defined, then total amount is used",
              "        Pickup - defines amount of picked up items. If specific item is not defined,"
                  + " then total amount is used",
              "        KillEntity - defines amount of monster kills. If specific monster is not"
                  + " defined, then total amount is used",
              "        InteractWithBlastFurnace - defines amount of interactions with blast"
                  + " furnace.",
              "        InteractWithSmoker - defines amount of interactions with smoker.",
              "        InteractWithLectern - defines amount of interactions with lectern.",
              "        InteractWithCampfire - defines amount of interactions with campfire.",
              "        InteractWithCartographyTable - defines amount of interactions with"
                  + " cartography table.",
              "        InteractWithLoom - defines amount of interactions with loom.",
              "        InteractWithStonecutter - defines amount of interactions with stonecutter.",
              "        InteractWithAnvil - defines amount of interactions with anvil.",
              "        InteractWithGrindstone - defines amount of interactions with grindstone.",
              "        InteractWithSmithingTable - defines amount of interactions with smithing"
                  + " table.",
              "        BellRing - defines amount of interactions with bell.",
              "        RaidTrigger - defines amount of interactions with raid trigger.",
              "        RaidWin - defines amount of interactions with raid win.",
              "        TargetHit - defines amount of interactions with target hit.",
              "        OpenBarrel - defines amount of interactions with open barrel.",
              "        DropCount - defines amount of interactions with drop count."));
      configReader.save();
      var linkedHashMap = new LinkedHashMap<>(this.ranks);
      this.ranks.clear();
      this.nextCheck.clear();
      this.nextInform.clear();
      if (!configReader.getC().getKeys(false).isEmpty()) {
        var map = configReader.getC().getValues(false);

        for (Entry entry : map.entrySet()) {
          CMIRank cmiRank = (CMIRank) linkedHashMap.get(((String) entry.getKey()).toLowerCase());
          if (cmiRank == null) {
            cmiRank = new CMIRank((String) entry.getKey());
          } else {
            cmiRank.reset();
          }

          try {
            var rankValues =
                configReader
                    .getC()
                    .getConfigurationSection((String) entry.getKey())
                    .getValues(false);
            if (rankValues.containsKey("Enabled")) {
              cmiRank.setEnabled((Boolean) rankValues.get("Enabled"));
            }

            if (!cmiRank.isEnabled()) {
              continue;
            }

            if (rankValues.containsKey("DisplayName")) {
              cmiRank.setDisplayName((String) rankValues.get("DisplayName"));
            }

            if (rankValues.containsKey("DefaultRank")) {
              cmiRank.setDefaultRank((Boolean) rankValues.get("DefaultRank"));
            }

            if (rankValues.containsKey("AutoRankup")) {
              cmiRank.setAutoRankup((Boolean) rankValues.get("AutoRankup"));
            }

            if (rankValues.containsKey("RankupConfirmation")) {
              cmiRank.setRankUpConfirmation((Boolean) rankValues.get("RankupConfirmation"));
            }

            if (rankValues.containsKey("NextRanks")) {
              if (rankValues.get("NextRanks") instanceof String) {
                cmiRank.setNextRanksT(Arrays.asList((String) rankValues.get("NextRanks")));
              } else {
                cmiRank.setNextRanksT((List<String>) rankValues.get("NextRanks"));
              }
            }

            if (rankValues.containsKey("MoneyCost")) {
              if (rankValues.get("MoneyCost") instanceof Double) {
                cmiRank.setMoneyCost((Double) rankValues.get("MoneyCost"));
              } else if (rankValues.get("MoneyCost") instanceof Integer) {
                cmiRank.setMoneyCost(((Integer) rankValues.get("MoneyCost")).intValue());
              } else if (rankValues.get("MoneyCost") instanceof Long) {
                cmiRank.setMoneyCost(((Long) rankValues.get("MoneyCost")).longValue());
              }
            }

            if (rankValues.containsKey("ExpCost")) {
              if (rankValues.get("ExpCost") instanceof Double) {
                cmiRank.setExpCost((Double) rankValues.get("ExpCost"));
              } else if (rankValues.get("ExpCost") instanceof Integer) {
                cmiRank.setExpCost(((Integer) rankValues.get("ExpCost")).intValue());
              }
            }

            if (rankValues.containsKey("Votes")) {
              if (rankValues.get("Votes") instanceof Double) {
                cmiRank.setVotes(((Double) rankValues.get("Votes")).intValue());
              } else if (rankValues.get("Votes") instanceof Integer) {
                cmiRank.setVotes((Integer) rankValues.get("Votes"));
              }
            }

            if (rankValues.containsKey("Commands")) {
              if (rankValues.get("Commands") instanceof String) {
                cmiRank.setCommands(Arrays.asList((String) rankValues.get("Commands")));
              } else {
                cmiRank.setCommands((List<String>) rankValues.get("Commands"));
              }
            }

            if (rankValues.containsKey("CommandsOnRankDown")) {
              if (rankValues.get("CommandsOnRankDown") instanceof String) {
                cmiRank.setCommandsOnRankDown(
                    Arrays.asList((String) rankValues.get("CommandsOnRankDown")));
              } else {
                cmiRank.setCommandsOnRankDown((List<String>) rankValues.get("CommandsOnRankDown"));
              }
            }

            if (rankValues.containsKey("PermissionRequirement")) {
              LinkedHashMap<String, String> permRequirements = new LinkedHashMap();
              ArrayList<String> arrayList = new ArrayList();
              if (rankValues.get("PermissionRequirement") instanceof String) {
                String string = (String) rankValues.get("PermissionRequirement");
                arrayList.add(string);
              } else {
                arrayList.addAll((List) rankValues.get("PermissionRequirement"));
              }

              for (String requirement : arrayList) {
                String requirementValue = null;
                String requirementKey = requirement;
                if (requirement.contains(":")) {
                  requirementValue = requirement.split(":")[1];
                  requirementKey = requirement.split(":")[0];
                }

                permRequirements.put(requirementKey, requirementValue);
              }

              cmiRank.setPermRequirements(permRequirements);
            }

            if (rankValues.containsKey("PlaceholderRequirements")) {
              LinkedHashMap<String, CMIRankPlaceholder> placeholderRequirements =
                  new LinkedHashMap();
              ArrayList<String> placeholderList = new ArrayList();
              if (rankValues.get("PlaceholderRequirements") instanceof String) {
                String singlePlaceholder = (String) rankValues.get("PlaceholderRequirements");
                placeholderList.add(singlePlaceholder);
              } else {
                placeholderList.addAll((List) rankValues.get("PlaceholderRequirements"));
              }

              for (String placeholderText : placeholderList) {
                CMIRankPlaceholder cmiRankPlaceholder = new CMIRankPlaceholder(placeholderText);
                if (cmiRankPlaceholder.getType().equals(rankPlaceholderType.invalid)) {
                  CMIMessages.consoleMessage(
                      "&cCould not decode ranks placeholder requirement as ("
                          + placeholderText
                          + ")");
                } else {
                  placeholderRequirements.put(
                      cmiRankPlaceholder.getPlaceholder(), cmiRankPlaceholder);
                }
              }

              cmiRank.setPlaceholderRequirements(placeholderRequirements);
            }

            if (rankValues.containsKey("McMMORequirement")) {
              LinkedHashMap<String, Integer> mcmmoRequirements = new LinkedHashMap();
              ArrayList<String> mcmmoList = new ArrayList();
              if (rankValues.get("McMMORequirement") instanceof String) {
                String singleMcmmo = (String) rankValues.get("McMMORequirement");
                mcmmoList.add(singleMcmmo);
              } else {
                mcmmoList.addAll((List) rankValues.get("McMMORequirement"));
              }

              Iterator iterator = mcmmoList.iterator();

              label362:
              while (true) {
                String skillName;
                Integer integer;
                while (true) {
                  if (!iterator.hasNext()) {
                    cmiRank.setMcmmoRequirements(mcmmoRequirements);
                    break label362;
                  }

                  String rawMcmmo = (String) iterator.next();
                  skillName = rawMcmmo;
                  integer = 0;
                  if (!rawMcmmo.contains(":")) {
                    break;
                  }

                  try {
                    integer = Integer.parseInt(rawMcmmo.split(":")[1]);
                  } catch (Exception exception2) {
                    continue;
                  }

                  skillName = rawMcmmo.split(":")[0];
                  break;
                }

                if (integer != 0) {
                  mcmmoRequirements.put(skillName, integer);
                }
              }
            }

            if (rankValues.containsKey("AureliumRequirement")) {
              LinkedHashMap<String, Integer> aureliumRequirements = new LinkedHashMap();
              ArrayList<String> aureliumList = new ArrayList();
              if (rankValues.get("AureliumRequirement") instanceof String) {
                String singleAurelium = (String) rankValues.get("AureliumRequirement");
                aureliumList.add(singleAurelium);
              } else {
                aureliumList.addAll((List) rankValues.get("AureliumRequirement"));
              }

              Iterator aureliumIterator = aureliumList.iterator();

              label377:
              while (true) {
                String aureliumSkill;
                Integer aureliumLevelValue;
                while (true) {
                  if (!aureliumIterator.hasNext()) {
                    cmiRank.setAureliumRequirements(aureliumRequirements);
                    break label377;
                  }

                  String rawAurelium = (String) aureliumIterator.next();
                  aureliumSkill = rawAurelium;
                  aureliumLevelValue = 0;
                  if (!rawAurelium.contains(":")) {
                    break;
                  }

                  try {
                    aureliumLevelValue = Integer.parseInt(rawAurelium.split(":")[1]);
                  } catch (Exception exception3) {
                    continue;
                  }

                  aureliumSkill = rawAurelium.split(":")[0];
                  break;
                }

                if (aureliumLevelValue != 0) {
                  aureliumRequirements.put(aureliumSkill, aureliumLevelValue);
                }
              }
            }

            if (rankValues.containsKey("AuraSkillsRequirement")) {
              LinkedHashMap<String, Integer> auraSkillsRequirements = new LinkedHashMap();
              ArrayList<String> auraSkillsList = new ArrayList();
              if (rankValues.get("AuraSkillsRequirement") instanceof String) {
                String singleAuraSkills = (String) rankValues.get("AuraSkillsRequirement");
                auraSkillsList.add(singleAuraSkills);
              } else {
                auraSkillsList.addAll((List) rankValues.get("AuraSkillsRequirement"));
              }

              Iterator auraSkillsIterator = auraSkillsList.iterator();

              label392:
              while (true) {
                String auraSkillName;
                Integer auraSkillLevel;
                while (true) {
                  if (!auraSkillsIterator.hasNext()) {
                    cmiRank.setAuraSkillsRequirements(auraSkillsRequirements);
                    break label392;
                  }

                  String rawAuraSkills = (String) auraSkillsIterator.next();
                  auraSkillName = rawAuraSkills;
                  auraSkillLevel = 0;
                  if (!rawAuraSkills.contains(":")) {
                    break;
                  }

                  try {
                    auraSkillLevel = Integer.parseInt(rawAuraSkills.split(":")[1]);
                  } catch (Exception exception4) {
                    continue;
                  }

                  auraSkillName = rawAuraSkills.split(":")[0];
                  break;
                }

                if (auraSkillLevel != 0) {
                  if (!auraSkillName.contains("/") && !auraSkillName.equalsIgnoreCase("power")) {
                    auraSkillName = "auraskills/" + auraSkillName;
                  }

                  auraSkillsRequirements.put(auraSkillName, auraSkillLevel);
                }
              }
            }

            if (rankValues.containsKey("JobsRequirement")) {
              LinkedHashMap<String, Integer> jobsRequirements = new LinkedHashMap();
              ArrayList<String> jobsList = new ArrayList();
              if (rankValues.get("JobsRequirement") instanceof String) {
                String singleJob = (String) rankValues.get("JobsRequirement");
                jobsList.add(singleJob);
              } else {
                jobsList.addAll((List) rankValues.get("JobsRequirement"));
              }

              Iterator jobsIterator = jobsList.iterator();

              label407:
              while (true) {
                String jobName;
                Integer jobLevel;
                while (true) {
                  if (!jobsIterator.hasNext()) {
                    cmiRank.setJobsRequirements(jobsRequirements);
                    break label407;
                  }

                  String rawJob = (String) jobsIterator.next();
                  jobName = rawJob;
                  jobLevel = 0;
                  if (!rawJob.contains(":")) {
                    break;
                  }

                  try {
                    jobLevel = Integer.parseInt(rawJob.split(":")[1]);
                  } catch (Exception exception5) {
                    continue;
                  }

                  jobName = rawJob.split(":")[0];
                  break;
                }

                if (jobLevel != 0) {
                  jobsRequirements.put(jobName, jobLevel);
                }
              }
            }

            if (rankValues.containsKey("ItemRequirement")) {
              LinkedHashMap<CMIItemStack, Integer> itemRequirements = new LinkedHashMap();
              ArrayList<String> itemList = new ArrayList();
              if (rankValues.get("ItemRequirement") instanceof String) {
                String singleItem = (String) rankValues.get("ItemRequirement");
                itemList.add(singleItem);
              } else {
                itemList.addAll((List) rankValues.get("ItemRequirement"));
              }

              for (String itemRequirement : itemList) {
                String itemKey = null;
                int value = 0;
                if (itemRequirement.contains(":")) {
                  String[] strings = itemRequirement.split(":");
                  itemKey = strings[0];
                  String amountText = strings[1];
                  if (strings.length == 3) {
                    itemKey = itemKey + ":" + strings[1];
                    amountText = strings[2];
                  }

                  try {
                    value = Integer.parseInt(amountText);
                  } catch (Exception exception6) {
                    continue;
                  }

                  CMIItemStack cmiItemStack =
                      CMILib.getInstance().getItemManager().getItem(itemKey);
                  if (cmiItemStack == null) {
                    this.plugin.consoleMessage(
                        "&cCan't determine item from "
                            + itemKey
                            + " name for "
                            + cmiRank.getName()
                            + " rank item requirement");
                  } else {
                    cmiItemStack.setAmount(value);
                    itemRequirements.put(cmiItemStack, value);
                  }
                }
              }

              cmiRank.setItemRequirements(itemRequirements);
            }

            if (rankValues.containsKey("StatsRequirements")) {
              ArrayList<String> statsList = new ArrayList();
              Object object = rankValues.get("StatsRequirements");
              if (object instanceof String) {
                statsList.add((String) object);
              } else {
                statsList.addAll((List) object);
              }

              for (String statsRequirement : statsList) {
                if (statsRequirement.contains(":")) {
                  String[] statParts = statsRequirement.split(":");
                  StatsManager.CMIStatistic cmiStatistic =
                      this.plugin.getStatsManager().getStatisticByName(statParts[0]);
                  if (cmiStatistic == null) {
                    this.plugin.consoleMessage("&eCant determine stat type " + statParts[0]);
                  } else {
                    String statExtra = "none";
                    if (statParts.length > 2) {
                      statExtra = statParts[1];
                    }

                    Long statAmount = 0L;

                    try {
                      if (statParts.length == 2) {
                        statAmount = Long.parseLong(statParts[1]);
                      } else {
                        statAmount = Long.parseLong(statParts[2]);
                      }
                    } catch (Exception exception7) {
                      exception7.printStackTrace();
                      this.plugin.consoleMessage("Cant determine amount " + statsRequirement);
                      continue;
                    }

                    if (cmiStatistic.getType() == StatsManager.svt.Time) {
                      statAmount = statAmount * 1000L;
                    }

                    if (cmiStatistic.getType() == StatsManager.svt.Distance) {
                      statAmount = statAmount * 100L;
                    }

                    cmiRank.addStatRequirement(cmiStatistic, statExtra, statAmount);
                  }
                }
              }
            }
          } catch (Exception exception8) {
            this.plugin.consoleMessage("Cant load rank (" + (String) entry.getKey() + ")");
            exception8.printStackTrace();
            continue;
          }

          this.addRank(cmiRank);
        }

        for (var rankEntry : this.ranks.entrySet()) {
          ((CMIRank) rankEntry.getValue()).calculateNextRanks();
        }

        HashSet<CMIRank> hashSet = new HashSet();

        for (var rankEntry : this.ranks.entrySet()) {
          if (((CMIRank) rankEntry.getValue()).getPreviousRanks().isEmpty()) {
            hashSet.add((CMIRank) rankEntry.getValue());
          }
        }

        for (CMIRank rootRank : hashSet) {
          this.calculateWeight(rootRank, 0);
        }

        this.plugin.loadMessage(this.ranks.size(), "ranks", System.currentTimeMillis() - startTime);
      }
    }
  }

  private void calculateWeight(CMIRank cmiRank, int value) {
    if (++value <= 1000) {
      cmiRank.setWeight(value);

      for (CMIRank nextRank : cmiRank.getNextRanks()) {
        this.calculateWeight(nextRank, value);
      }
    }
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Ranks.PermissionCheck",
        new String[] {
          "If set to true we will check players rank by his access to rank by permission node and"
              + " prioritize any rank which has more weight over current one",
          "This means that in case player has access to third rank while recorded one is first one,"
              + " we will prioritize third one",
          "While this is active using command like /cmi setrank will set players rank to highest"
              + " one he has access too, so as long as player has permission access to third rank"
              + " you cant go lower than this, ",
          "you will have to remove access to specific rank by removing permission node before"
              + " trying to derank him"
        });
    this.permissionCheck = configReader.get("Ranks.PermissionCheck", false);
    configReader.addComment(
        "Ranks.Recalculation",
        new String[] {
          "Defines how often in minutes plugin will recalculate players group",
          "Keeping it at 0 will disable this feature"
        });
    this.Recalculation = configReader.get("Ranks.Recalculation", 0) * 60 * 1000;
    configReader.addComment(
        "Ranks.AutoRankUp.Delay",
        new String[] {
          "Defines how often in seconds plugin will check for possible player rankups",
          "Set it to 0 or less to disable auto rankup checks"
        });
    this.Delay = configReader.get("Ranks.AutoRankUp.Delay", 60);
    configReader.addComment(
        "Ranks.AutoRankUp.Async",
        new String[] {
          "EXPERIMENTAL. When set to true, player rankup checks will be done in async mode",
          "In case of errors related to this feature being turned on, turn it off and report issue"
              + " with error log to github"
        });
    this.async = configReader.get("Ranks.AutoRankUp.Async", false);
    configReader.addComment(
        "Ranks.ListSamePathOnly",
        new String[] {
          "When set to true, command /cmi ranklist will output ranks from your rankup path which"
              + " will exclude any rank from different paths or different rankup trees all"
              + " together",
          "When set to false, all set ranks will be shown in the list"
        });
    this.ListSamePathOnly = configReader.get("Ranks.ListSamePathOnly", false);
    configReader.addComment(
        "Ranks.AutoRankUp.PlayerDelay",
        new String[] {
          "Defines how often in seconds each separate player will be checked for rankup",
          "This is different than general check just to avoid couple players ranking up at same"
              + " time",
          "This also defines how often player will be notified about possible rankup and it will"
              + " proportionaly increase with each time player get notification to avoid annoying"
              + " spam in chat",
          "Keep it longer or same as general delay time"
        });
    this.PlayerDelay = configReader.get("Ranks.AutoRankUp.PlayerDelay", 120);
    this.PlayerDelay *= 1000;
    configReader.addComment(
        "Ranks.AutoRankUp.progressBar",
        new String[] {"Enable or disable progression bar in rank info window"});
    this.progressBar = configReader.get("Ranks.AutoRankUp.progressBar", true);
    configReader.addComment(
        "Ranks.Effect",
        new String[] {
          "Name of particle animation as defined at Particles config section",
          "if you want to disable it, then set it to empty field"
        });
    this.RanksEffect =
        ParticleManager.CMIPresetAnimations.getByName(
            configReader.get("Ranks.EffectUp", "GColumn"));
    configReader.addComment(
        "Ranks.OnlyHours",
        new String[] {
          "When set to true requirements which involve time will be shown in short format like '50"
              + " hours' instead of '2 days 2 hours'"
        });
    this.OnlyHours = configReader.get("Ranks.OnlyHours", false);
    configReader.addComment(
        "Ranks.includeMinutes",
        new String[] {
          "When OnlyHours set to true you can add minutes to the output message instead of showing"
              + " fractions. So you can have '5 hours 30 minutes' instead of 5.5hours'"
        });
    this.includeMinutes = configReader.get("Ranks.includeMinutes", true);
  }

  public boolean isProgressBar() {
    return this.progressBar;
  }

  public boolean isListSamePathOnly() {
    return this.ListSamePathOnly;
  }

  public ParticleManager.CMIPresetAnimations getRankEffect() {
    return this.RanksEffect;
  }

  public boolean isPermissionCheck() {
    return this.permissionCheck;
  }

  public boolean isStrictPermissionCheck() {
    return this.strictPermissionCheck;
  }

  private class donePercentage {
    private double percent = 0.0;
    private int times = 0;

    private donePercentage() {}

    public double getPercent() {
      return this.percent;
    }

    public void addPercent(double number) {
      this.percent += number;
    }

    public int getTimes() {
      return this.times;
    }

    public void incrementTimes() {
      this.times++;
    }

    public void addIncrement(int value) {
      this.times += value;
    }
  }

  public class rankCache {
    HashMap<RankManager.rankupType, Double> percentage = new HashMap<>();
    HashMap<RankManager.rankupType, Long> nextPercentageCheck = new HashMap<>();
    long time = 5000L;

    public rankCache(long longValue) {
      this.time = longValue;
    }

    public rankCache() {}

    public boolean timeToCheck(RankManager.rankupType type) {
      return !this.nextPercentageCheck.containsKey(type)
          || this.nextPercentageCheck.get(type) < System.currentTimeMillis();
    }

    public Double getCache(RankManager.rankupType type) {
      return this.percentage.get(type);
    }

    public void setCache(RankManager.rankupType type, Double percent) {
      this.percentage.put(type, percent);
      this.nextPercentageCheck.put(type, System.currentTimeMillis() + this.time);
    }
  }

  public class rankCurrentRequirement {
    private Long need;
    private Long have;

    public rankCurrentRequirement(Long needValue, Long haveValue) {
      this.have = haveValue;
      this.need = needValue;
    }

    public Long getNeed() {
      return this.need;
    }

    public void setNeed(Long needValue) {
      this.need = needValue;
    }

    public Long getHave() {
      return this.have;
    }

    public void setHave(Long needValue) {
      this.have = needValue;
    }
  }

  public enum rankupFailType {
    Money,
    Exp,
    Stats,
    McMMO,
    Aurelium,
    AuraSkills,
    Jobs,
    Placeholoder,
    Perm,
    None,
    NoRank,
    Items,
    Votes,
    SameRank;
  }

  public enum rankupType {
    Money,
    Exp,
    Stats,
    McMMO,
    Aurelium,
    AuraSkills,
    Jobs,
    Perm,
    Placeholoder,
    Items,
    Votes;
  }
}
