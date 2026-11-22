package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.entity.Player;

public class CMIRank {
  private String name;
  private String displayName;
  private boolean enabled;
  private boolean defaultRank;
  private boolean AutoRankup;
  private boolean rankUpConfirmation;
  private List<CMIRank> nextRanks;
  private List<String> nextRanksT;
  private List<String> Commands;
  private List<String> CommandsOnRankDown;
  private LinkedHashMap<StatsManager.CMIStatistic, LinkedHashMap<Object, Long>> statRequirements;
  private LinkedHashMap<String, String> permRequirements;
  private LinkedHashMap<String, CMIRankPlaceholder> placeholderRequirements;
  private LinkedHashMap<String, Integer> mcmmoRequirements;
  private LinkedHashMap<String, Integer> aureliumRequirements;
  private LinkedHashMap<String, Integer> auraSkillsRequirements;
  private LinkedHashMap<String, Integer> JobsRequirements;
  private LinkedHashMap<CMIItemStack, Integer> ItemRequirements;
  private double moneyCost;
  private double expCost;
  private int votes;
  private int weight = 0;

  public void reset() {
    this.enabled = false;
    this.defaultRank = false;
    this.AutoRankup = false;
    this.rankUpConfirmation = false;
    this.nextRanks = new ArrayList<>();
    this.nextRanksT = new ArrayList<>();
    this.Commands = new ArrayList<>();
    this.CommandsOnRankDown = new ArrayList<>();
    this.statRequirements = new LinkedHashMap<>();
    this.permRequirements = new LinkedHashMap<>();
    this.placeholderRequirements = new LinkedHashMap<>();
    this.mcmmoRequirements = new LinkedHashMap<>();
    this.aureliumRequirements = new LinkedHashMap<>();
    this.auraSkillsRequirements = new LinkedHashMap<>();
    this.JobsRequirements = new LinkedHashMap<>();
    this.ItemRequirements = new LinkedHashMap<>();
    this.moneyCost = 0.0;
    this.expCost = 0.0;
    this.votes = 0;
  }

  public CMIRank(String string) {
    this.name = string;
    this.reset();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public boolean isDefaultRank() {
    return this.defaultRank;
  }

  public void setDefaultRank(boolean flag) {
    this.defaultRank = flag;
  }

  public boolean isRankUpConfirmation() {
    return this.rankUpConfirmation;
  }

  public void setRankUpConfirmation(boolean flag) {
    this.rankUpConfirmation = flag;
  }

  public List<CMIRank> getNextRanks() {
    return this.nextRanks;
  }

  public List<CMIRank> getNextValidRankUps(CMIUser cmiUser) {
    ArrayList<CMIRank> arrayList = new ArrayList();

    for (CMIRank cmiRank : this.getNextRanks()) {
      if (cmiRank.canRankup(cmiUser) == RankManager.rankupFailType.None) {
        arrayList.add(cmiRank);
      }
    }

    return arrayList;
  }

  public boolean isOnSamePathWith(CMIRank cmiRank) {
    return this.checkPrevious(this, cmiRank) ? true : this.checkNext(this, cmiRank);
  }

  private boolean checkPrevious(CMIRank cmiRank, CMIRank target) {
    var list = cmiRank.getPreviousRanks();
    if (list.contains(target)) {
      return true;
    }

    for (CMIRank candidate : list) {
      if (candidate != cmiRank && this.checkPrevious(candidate, target)) {
        return true;
      }
    }

    return false;
  }

  private boolean checkNext(CMIRank cmiRank, CMIRank target) {
    var list = cmiRank.getNextRanks();
    if (list.contains(target)) {
      return true;
    }

    for (CMIRank candidate : list) {
      if (candidate != cmiRank && this.checkNext(candidate, target)) {
        return true;
      }
    }

    return false;
  }

  public List<CMIRank> getPreviousRanks() {
    ArrayList<CMIRank> arrayList = new ArrayList();

    for (var entry : CMI.getInstance().getRankManager().getRanks().entrySet()) {
      for (CMIRank cmiRank : ((CMIRank) entry.getValue()).getNextRanks()) {
        if (cmiRank.getName().equals(this.getName())) {
          arrayList.add((CMIRank) entry.getValue());
        }
      }
    }

    return arrayList;
  }

  public List<CMIRank> getValidPreviousRanks(CMIUser cmiUser) {
    CMIRank cmiRank = cmiUser.getRank();
    ArrayList<CMIRank> arrayList = new ArrayList();

    for (var entry : CMI.getInstance().getRankManager().getRanks().entrySet()) {
      for (CMIRank nextRank : ((CMIRank) entry.getValue()).getNextRanks()) {
        if (nextRank.getName().equals(cmiRank.getName())) {
          arrayList.add((CMIRank) entry.getValue());
        }
      }
    }

    return arrayList;
  }

  public String getValidPreviousRanksAsString() {
    String string = "";

    for (var entry : CMI.getInstance().getRankManager().getRanks().entrySet()) {
      for (CMIRank cmiRank : ((CMIRank) entry.getValue()).getNextRanks()) {
        if (cmiRank.getName().equals(this.getName())) {
          if (!string.isEmpty()) {
            string = string + LC.info_ListSpliter.getLocale(new Object[0]);
          }

          string = string + ((CMIRank) entry.getValue()).getDisplayName();
        }
      }
    }

    return string;
  }

  public RankManager.rankupFailType canRankup(CMIUser cmiUser) {
    return cmiUser.getRank() == this
        ? RankManager.rankupFailType.SameRank
        : CMI.getInstance().getRankManager().canRankUp(cmiUser, this);
  }

  public double getMoneyCost() {
    return this.moneyCost;
  }

  public void setMoneyCost(double number) {
    this.moneyCost = number;
  }

  public double getExpCost() {
    return this.expCost;
  }

  public void setExpCost(double number) {
    this.expCost = number;
  }

  public List<String> getCommands() {
    return this.Commands;
  }

  public void setCommands(List<String> list) {
    this.Commands = list;
  }

  public LinkedHashMap<StatsManager.CMIStatistic, LinkedHashMap<Object, Long>>
      getStatRequirements() {
    return this.statRequirements;
  }

  public LinkedHashMap<Object, Long> getStatRequirement(StatsManager.CMIStatistic cmiStatistic) {
    return this.statRequirements.get(cmiStatistic);
  }

  public CMIRank addStatRequirement(
      StatsManager.CMIStatistic cmiStatistic, String string, Long amount) {
    var linkedHashMap = this.getStatRequirement(cmiStatistic);
    if (linkedHashMap == null) {
      linkedHashMap = new LinkedHashMap();
    }

    if (string.equalsIgnoreCase("none")) {
      linkedHashMap.put(string, amount);
      this.statRequirements.put(cmiStatistic, linkedHashMap);
    } else if (cmiStatistic.getSubType() == StatsManager.CMIType.Block
        || cmiStatistic.getSubType() == StatsManager.CMIType.Material) {
      CMIMaterial cmiMaterial = CMIMaterial.get(string);
      if (cmiMaterial != null && cmiMaterial != CMIMaterial.NONE) {
        linkedHashMap.put(cmiMaterial, amount);
        this.statRequirements.put(cmiStatistic, linkedHashMap);
      }
    } else if (cmiStatistic.getSubType() == StatsManager.CMIType.Entity) {
      CMIEntityType cmiEntityType = CMIEntityType.getByName(string);
      if (cmiEntityType != null) {
        linkedHashMap.put(cmiEntityType, amount);
        this.statRequirements.put(cmiStatistic, linkedHashMap);
      }
    }

    return this;
  }

  public void setStatRequirements(
      LinkedHashMap<StatsManager.CMIStatistic, LinkedHashMap<Object, Long>> linkedHashMap) {
    this.statRequirements = linkedHashMap;
  }

  public boolean isAutoRankup() {
    return this.AutoRankup;
  }

  public void setAutoRankup(boolean flag) {
    this.AutoRankup = flag;
  }

  public void setNextRanksT(List<String> list) {
    this.nextRanksT = list;
  }

  public void calculateNextRanks() {
    this.nextRanks.clear();

    for (String string : this.nextRanksT) {
      CMIRank cmiRank = CMI.getInstance().getRankManager().getRank(string);
      if (cmiRank != null) {
        this.nextRanks.add(cmiRank);
      }
    }
  }

  public void finalizeRankup(CMIUser cmiUser) {
    this.finalizeRankup(cmiUser, true, true);
  }

  public void finalizeRankup(CMIUser cmiUser, Boolean runCommand, Boolean withCost) {
    if (runCommand) {
      this.performCommands(cmiUser);
    }

    if (withCost) {
      this.takeMoney(cmiUser);
      this.takeExp(cmiUser);
      CMI.getInstance()
          .getRankManager()
          .removeContents(cmiUser.getPlayer(), this.getItemRequirements());
    }

    CMI.getInstance().getRankManager().removeFromCheck(cmiUser.getUniqueId());
    cmiUser.addForDelayedSave();
    if (cmiUser.isOnline()) {
      ParticleManager.CMIPresetAnimations cmiPresetAnimations =
          CMI.getInstance().getRankManager().getRankEffect();
      if (cmiPresetAnimations != null && cmiPresetAnimations.isEnabled()) {
        cmiPresetAnimations.show(cmiUser.getPlayer(false));
      }
    }
  }

  @Deprecated
  public void takeExp(CMIUser cmiUser) {
    if (!(this.getExpCost() <= 0.0)) {
      Player player = cmiUser.getPlayer();
      int value = (int) (cmiUser.getExp() - this.getExpCost());
      if (value >= 0) {
        player.setLevel(0);
        player.setExp(0.0F);
        player.setTotalExperience(0);
        player.giveExp(value);
      }
    }
  }

  @Deprecated
  public void takeMoney(CMIUser cmiUser) {
    if (!(this.getMoneyCost() <= 0.0)) {
      cmiUser.withdraw(this.getMoneyCost());
    }
  }

  public void takeItems(CMIUser cmiUser) {
    CMI.getInstance()
        .getRankManager()
        .removeContents(cmiUser.getPlayer(), this.getItemRequirements());
  }

  public void performCommands(CMIUser cmiUser) {
    Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
    List<String> list = new ArrayList<>(this.Commands);
    list = CMI.getInstance().getLM().updateSnd(snd, list);
    CMI.getInstance()
        .getSpecializedCommandManager()
        .processCmds(list, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
  }

  public void performCommandsOnRankDown(CMIUser cmiUser) {
    Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
    List<String> list = new ArrayList<>(this.CommandsOnRankDown);
    list = CMI.getInstance().getLM().updateSnd(snd, list);
    CMI.getInstance()
        .getSpecializedCommandManager()
        .processCmds(list, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
  }

  public String getDisplayName() {
    return this.displayName == null ? this.getName() : CMIChatColor.translate(this.displayName);
  }

  public void setDisplayName(String string) {
    this.displayName = string;
  }

  public LinkedHashMap<String, String> getPermRequirements() {
    return this.permRequirements;
  }

  public void setPermRequirements(LinkedHashMap<String, String> linkedHashMap) {
    this.permRequirements = linkedHashMap;
  }

  public LinkedHashMap<String, Integer> getMcmmoRequirements() {
    return this.mcmmoRequirements;
  }

  public void setMcmmoRequirements(LinkedHashMap<String, Integer> linkedHashMap) {
    this.mcmmoRequirements = linkedHashMap;
  }

  public LinkedHashMap<String, Integer> getJobsRequirements() {
    return this.JobsRequirements;
  }

  public void setJobsRequirements(LinkedHashMap<String, Integer> linkedHashMap) {
    this.JobsRequirements = linkedHashMap;
  }

  public LinkedHashMap<CMIItemStack, Integer> getItemRequirements() {
    return this.ItemRequirements;
  }

  public void setItemRequirements(LinkedHashMap<CMIItemStack, Integer> linkedHashMap) {
    this.ItemRequirements = linkedHashMap;
  }

  public int getVotes() {
    return this.votes;
  }

  public void setVotes(int value) {
    this.votes = value;
  }

  public List<String> getCommandsOnRankDown() {
    return this.CommandsOnRankDown;
  }

  public void setCommandsOnRankDown(List<String> list) {
    this.CommandsOnRankDown = list;
  }

  public int getWeight() {
    return this.weight;
  }

  public void setWeight(int value) {
    this.weight = value;
  }

  public LinkedHashMap<String, Integer> getAureliumRequirements() {
    return this.aureliumRequirements;
  }

  public void setAureliumRequirements(LinkedHashMap<String, Integer> linkedHashMap) {
    this.aureliumRequirements = linkedHashMap;
  }

  public LinkedHashMap<String, Integer> getAuraSkillsRequirements() {
    return this.auraSkillsRequirements;
  }

  public void setAuraSkillsRequirements(LinkedHashMap<String, Integer> linkedHashMap) {
    this.auraSkillsRequirements = linkedHashMap;
  }

  public LinkedHashMap<String, CMIRankPlaceholder> getPlaceholderRequirements() {
    return this.placeholderRequirements;
  }

  public void setPlaceholderRequirements(LinkedHashMap<String, CMIRankPlaceholder> linkedHashMap) {
    this.placeholderRequirements = linkedHashMap;
  }
}
