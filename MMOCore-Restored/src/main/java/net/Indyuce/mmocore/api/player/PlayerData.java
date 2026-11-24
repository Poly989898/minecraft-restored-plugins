package net.Indyuce.mmocore.api.player;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import io.lumine.mythic.lib.util.Closeable;
import io.lumine.mythic.lib.version.Attributes;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.event.PlayerEnterCastingModeEvent;
import net.Indyuce.mmocore.api.event.PlayerExitCastingModeEvent;
import net.Indyuce.mmocore.api.event.PlayerExperienceGainEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.event.unlocking.ItemLockedEvent;
import net.Indyuce.mmocore.api.event.unlocking.ItemUnlockedEvent;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import net.Indyuce.mmocore.api.player.profess.Subclass;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import net.Indyuce.mmocore.api.player.social.FriendRequest;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import net.Indyuce.mmocore.api.quest.PlayerQuests;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.ExperienceObject;
import net.Indyuce.mmocore.experience.PlayerProfessions;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.experience.droptable.ExperienceItem;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import net.Indyuce.mmocore.gui.skilltree.NodeIncrementResult;
import net.Indyuce.mmocore.guild.provided.Guild;
import net.Indyuce.mmocore.loot.chest.particle.SmallParticleEffect;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.provided.MMOCorePartyModule;
import net.Indyuce.mmocore.party.provided.Party;
import net.Indyuce.mmocore.player.ClassDataContainer;
import net.Indyuce.mmocore.player.CombatHandler;
import net.Indyuce.mmocore.player.Unlockable;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import net.Indyuce.mmocore.skill.binding.SkillSlot;
import net.Indyuce.mmocore.skill.cast.SkillCastingInstance;
import net.Indyuce.mmocore.skill.cast.SkillCastingMode;
import net.Indyuce.mmocore.skilltree.NodeState;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.waypoint.Waypoint;
import net.Indyuce.mmocore.waypoint.WaypointOption;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerData extends SynchronizedDataHolder implements OfflinePlayerData, Closeable, ClassDataContainer {
   @Nullable
   private PlayerClass profess;
   private int level;
   private int classPoints;
   private int skillPoints;
   private int attributePoints;
   private int attributeReallocationPoints;
   private int skillTreeReallocationPoints;
   private int skillReallocationPoints;
   private double experience;
   private double health;
   private double mana;
   private double stamina;
   private double stellium;
   private Guild guild;
   private SkillCastingInstance skillCasting;
   private final PlayerQuests questData;
   private final PlayerStats playerStats;
   private final List<UUID> friends = new ArrayList<>();
   @Deprecated
   private final Set<String> waypoints = new HashSet<>();
   private final Map<String, Integer> skills = new HashMap<>();
   private final Map<Integer, BoundSkillInfo> boundSkills = new HashMap<>();
   private final PlayerProfessions collectSkills = new PlayerProfessions(this);
   private final PlayerAttributes attributes = new PlayerAttributes(this);
   private final Map<String, SavedClassInformation> classSlots = new HashMap<>();
   private final Map<PlayerActivity, Long> lastActivity = new HashMap<>();
   private final CombatHandler combat = new CombatHandler(this);
   private final Map<SkillTreeNode, NodeState> nodeStates = new HashMap<>();
   private final Map<SkillTreeNode, Integer> nodeLevels = new HashMap<>();
   private final Map<String, Integer> skillTreePoints = new HashMap<>();
   private final Map<SkillTree, Integer> skillTreePointsSpent = new HashMap<>();
   private final Set<String> unlockedItems = new HashSet<>();
   private final Map<String, Integer> tableItemClaims = new HashMap<>();
   public boolean noCooldown;
   public long lastDropEvent;
   public int permSkillScrollIndex;

   public PlayerData(MMOPlayerData mmoData) {
      super(MMOCore.plugin, mmoData);
      this.questData = new PlayerQuests(this);
      this.playerStats = new PlayerStats(this);
   }

   public void reload() {
      try {
         this.profess = this.profess == null ? null : MMOCore.plugin.classManager.get(this.profess.getId());
      } catch (NullPointerException exception) {
         MMOCore.log(Level.SEVERE, "[Userdata] Could not find class " + this.getProfess().getId() + " while refreshing player data.");
      }

      for (Entry<Integer, BoundSkillInfo> entry : new HashMap<>(this.boundSkills).entrySet()) {
         try {
            SkillSlot skillSlot = this.getProfess().getSkillSlot(entry.getKey());
            String skillId = entry.getValue().getClassSkill().getSkill().getHandler().getId();
            ClassSkill classSkill = this.getProfess().getSkill(skillId);
            Validate.notNull(skillSlot, "Could not find skill slot n" + entry.getKey());
            Validate.notNull(classSkill, "Could not find skill with ID '" + skillId + "'");
            this.unbindSkill(entry.getKey());
            this.bindSkill(entry.getKey(), classSkill);
         } catch (Exception exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not reload data of '" + this.getPlayer().getName() + "': " + exception.getMessage());
            exception.printStackTrace();
         }
      }

      for (SkillTree skillTree : this.getProfess().getSkillTrees()) {
         for (SkillTreeNode node : skillTree.getNodes()) {
            if (!this.nodeLevels.containsKey(node)) {
               this.nodeLevels.put(node, 0);
            }
         }
      }

      this.setupSkillTrees();
      this.applyTemporaryTriggers();
      this.getStats().updateStats();
   }

   @Deprecated
   public void setupRemovableTrigger() {
      this.applyTemporaryTriggers();
   }

   public void applyTemporaryTriggers() {
      this.resetTriggerStats();
      if (this.getProfess().hasExperienceTable()) {
         this.getProfess().getExperienceTable().applyTemporaryTriggers(this, this.getProfess());
      }

      for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
         if (profession.hasExperienceTable()) {
            profession.getExperienceTable().applyTemporaryTriggers(this, profession);
         }
      }

      for (SkillTree skillTree : MMOCore.plugin.skillTreeManager.getAll()) {
         for (SkillTreeNode node : skillTree.getNodes()) {
            node.getExperienceTable().applyTemporaryTriggers(this, node);
         }
      }
   }

   public void setupSkillTrees() {
      for (SkillTree skillTree : this.getProfess().getSkillTrees()) {
         skillTree.setupNodeStates(this);
      }
   }

   public int getPointsSpent(@NotNull SkillTree skillTree) {
      return this.skillTreePointsSpent.getOrDefault(skillTree, 0);
   }

   @Deprecated
   public int getPointSpent(SkillTree skillTree) {
      return this.getPointsSpent(skillTree);
   }

   public void setSkillTreePoints(@NotNull String treeId, int points) {
      if (points <= 0) {
         this.skillTreePoints.remove(treeId);
      } else {
         this.skillTreePoints.put(treeId, points);
      }
   }

   public void giveSkillTreePoints(@NotNull String id, int val) {
      this.skillTreePoints.merge(id, Math.max(0, val), (points, ignored) -> Math.max(0, points + val));
   }

   public int countSkillTreePoints(@NotNull SkillTree skillTree) {
      return this.nodeLevels
         .keySet()
         .stream()
         .filter(node -> node.getTree().equals(skillTree))
         .mapToInt(node -> this.nodeLevels.get(node) * node.getPointConsumption())
         .sum();
   }

   @NotNull
   @Override
   public Map<String, Integer> mapSkillTreePoints() {
      return new HashMap<>(this.skillTreePoints);
   }

   @Override
   public Map<Integer, String> mapBoundSkills() {
      Map<Integer, String> result = new HashMap<>();

      for (int slot : this.boundSkills.keySet()) {
         result.put(slot, this.boundSkills.get(slot).getClassSkill().getSkill().getHandler().getId());
      }

      return result;
   }

   public Set<Entry<String, Integer>> getNodeLevelsEntrySet() {
      HashMap<String, Integer> nodeLevelsString = new HashMap<>();

      for (SkillTreeNode node : this.nodeLevels.keySet()) {
         nodeLevelsString.put(node.getFullId(), this.nodeLevels.get(node));
      }

      return nodeLevelsString.entrySet();
   }

   public void resetTriggerStats() {
      this.getMMOPlayerData().getStatMap().getInstances().forEach(statInstance -> statInstance.removeIf(Trigger.STAT_MODIFIER_KEY::equals));
      this.getMMOPlayerData().getSkillModifierMap().removeModifiers(Trigger.STAT_MODIFIER_KEY);
   }

   @Override
   public Map<String, Integer> getNodeLevels() {
      Map<String, Integer> mapped = new HashMap<>();
      this.nodeLevels.forEach((node, level) -> mapped.put(node.getFullId(), level));
      return mapped;
   }

   @Deprecated
   public void clearSkillTrees() {
      this.resetSkillTrees();
   }

   public void resetSkillTrees() {
      for (SkillTree tree : this.getProfess().getSkillTrees()) {
         for (SkillTreeNode node : tree.getNodes()) {
            node.resetAdvancement(this, false);
         }
      }

      this.nodeLevels.clear();
      this.nodeStates.clear();
      this.skillTreePointsSpent.clear();
      this.tableItemClaims.keySet().removeIf(s -> s.startsWith("node"));
      this.skillTreePoints.clear();
      this.skillTreeReallocationPoints = 0;
      this.setupSkillTrees();
   }

   public void clearNodeStates(@NotNull SkillTree skillTree) {
      for (SkillTreeNode node : skillTree.getNodes()) {
         this.nodeStates.remove(node);
      }
   }

   @NotNull
   public NodeIncrementResult canIncrementNodeLevel(@NotNull SkillTreeNode node) {
      NodeState nodeState = this.nodeStates.get(node);
      if (nodeState == NodeState.MAXED_OUT) {
         return NodeIncrementResult.MAX_LEVEL_REACHED;
      }

      if (nodeState != NodeState.UNLOCKED && nodeState != NodeState.UNLOCKABLE) {
         return NodeIncrementResult.LOCKED_NODE;
      }

      if (!node.hasPermissionRequirement(this)) {
         return NodeIncrementResult.PERMISSION_DENIED;
      }

      int skillTreePoints = this.skillTreePoints.getOrDefault(node.getTree().getId(), 0) + this.skillTreePoints.getOrDefault("global", 0);
      return skillTreePoints < node.getPointConsumption() ? NodeIncrementResult.NOT_ENOUGH_POINTS : NodeIncrementResult.SUCCESS;
   }

   public void incrementNodeLevel(@NotNull SkillTreeNode node) {
      int newLevel = this.addNodeLevels(node, 1);
      node.updateAdvancement(this, newLevel);
      AtomicInteger cost = new AtomicInteger(node.getPointConsumption());
      this.skillTreePoints.computeIfPresent(node.getTree().getId(), (key, points) -> {
         int withdrawn = Math.min(points, cost.get());
         cost.set(cost.get() - withdrawn);
         return points <= withdrawn ? null : points - withdrawn;
      });
      if (cost.get() > 0) {
         this.withdrawSkillTreePoints("global", cost.get());
      }

      this.clearNodeStates(node.getTree());
      node.getTree().setupNodeStates(this);
   }

   @Deprecated
   public int getSkillTreePoint(String treeId) {
      return this.getSkillTreePoints(treeId);
   }

   public int getSkillTreePoints(@NotNull String treeId) {
      return this.skillTreePoints.getOrDefault(treeId, 0);
   }

   public void withdrawSkillTreePoints(@NotNull String treeId, int withdrawn) {
      int cost = Math.max(0, withdrawn);
      this.skillTreePoints.computeIfPresent(treeId, (ignored, points) -> cost >= points ? null : points - cost);
   }

   public void setNodeState(SkillTreeNode node, NodeState nodeState) {
      this.nodeStates.put(node, nodeState);
   }

   public NodeState getNodeState(SkillTreeNode node) {
      return this.nodeStates.get(node);
   }

   @Deprecated
   public NodeState getNodeStatus(SkillTreeNode node) {
      return this.getNodeState(node);
   }

   public boolean hasNodeState(@NotNull SkillTreeNode node) {
      return this.nodeStates.containsKey(node);
   }

   public int getNodeLevel(@NotNull SkillTreeNode node) {
      return this.nodeLevels.getOrDefault(node, 0);
   }

   public void setNodeLevel(@NotNull SkillTreeNode node, int nodeLevel) {
      this.nodeLevels.compute(node, (ignored, currentLevelInteger) -> {
         int currentLevel = currentLevelInteger == null ? 0 : currentLevelInteger;
         int delta = (nodeLevel - currentLevel) * node.getPointConsumption();
         this.skillTreePointsSpent.merge(node.getTree(), delta, (level, newValue) -> level + delta);
         return nodeLevel;
      });
   }

   public int addNodeLevels(@NotNull SkillTreeNode node, int increment) {
      int delta = increment * node.getPointConsumption();
      this.skillTreePointsSpent.merge(node.getTree(), delta, (points, ignored) -> points + delta);
      return this.nodeLevels.merge(node, increment, (level, ignored) -> level + increment);
   }

   public void resetSkillTree(@NotNull SkillTree skillTree) {
      for (SkillTreeNode node : skillTree.getNodes()) {
         node.resetAdvancement(this, true);
         this.setNodeLevel(node, 0);
         this.nodeStates.remove(node);
      }

      skillTree.setupNodeStates(this);
   }

   @NotNull
   public Map<SkillTreeNode, NodeState> getNodeStates() {
      return new HashMap<>(this.nodeStates);
   }

   public boolean hasNodeStates() {
      return !this.nodeStates.isEmpty();
   }

   @Override
   public Map<String, Integer> getNodeTimesClaimed() {
      Map<String, Integer> result = new HashMap<>();
      this.tableItemClaims.forEach((str, val) -> {
         if (str.startsWith("node")) {
            result.put(str, val);
         }
      });
      return result;
   }

   public boolean hasUnlocked(Unlockable unlockable) {
      return unlockable.isUnlockedByDefault() || this.unlockedItems.contains(unlockable.getUnlockNamespacedKey());
   }

   public boolean unlock(Unlockable unlockable) {
      Validate.isTrue(!unlockable.isUnlockedByDefault(), "Cannot unlock an item unlocked by default");
      boolean wasLocked = this.unlockedItems.add(unlockable.getUnlockNamespacedKey());
      if (wasLocked) {
         unlockable.whenUnlocked(this);
         Bukkit.getScheduler()
            .runTask(MythicLib.plugin, () -> Bukkit.getPluginManager().callEvent(new ItemUnlockedEvent(this, unlockable.getUnlockNamespacedKey())));
      }

      return wasLocked;
   }

   public boolean lock(Unlockable unlockable) {
      Validate.isTrue(!unlockable.isUnlockedByDefault(), "Cannot lock an item unlocked by default");
      boolean wasUnlocked = this.unlockedItems.remove(unlockable.getUnlockNamespacedKey());
      if (wasUnlocked) {
         unlockable.whenLocked(this);
         Bukkit.getScheduler()
            .runTask(MythicLib.plugin, () -> Bukkit.getPluginManager().callEvent(new ItemLockedEvent(this, unlockable.getUnlockNamespacedKey())));
      }

      return wasUnlocked;
   }

   @Override
   public Set<String> getUnlockedItems() {
      return new HashSet<>(this.unlockedItems);
   }

   public void setUnlockedItems(Set<String> unlockedItems) {
      this.unlockedItems.clear();
      this.unlockedItems.addAll(unlockedItems);
   }

   @Deprecated
   public void resetTimesClaimed() {
      this.tableItemClaims.clear();
   }

   public void close() {
      this.health = this.getPlayer().getHealth();
      if (MMOCore.plugin.partyModule instanceof MMOCorePartyModule) {
         AbstractParty party = this.getParty();
         if (party != null && party instanceof Party) {
            ((Party)party).removeMember(this);
         }
      }

      this.combat.close();
      this.questData.close();
      this.boundSkills.forEach((slot, info) -> info.close());
      if (this.isCasting()) {
         this.leaveSkillCasting(true);
      }
   }

   public List<UUID> getFriends() {
      return this.friends;
   }

   public PlayerProfessions getCollectionSkills() {
      return this.collectSkills;
   }

   public PlayerQuests getQuestData() {
      return this.questData;
   }

   public long getLastActivity(PlayerActivity activity) {
      return this.lastActivity.getOrDefault(activity, 0L);
   }

   public long getActivityTimeOut(PlayerActivity activity) {
      return Math.max(0L, this.getLastActivity(activity) + activity.getTimeOut() - System.currentTimeMillis());
   }

   public void setLastActivity(PlayerActivity activity) {
      this.setLastActivity(activity, System.currentTimeMillis());
   }

   public void setLastActivity(PlayerActivity activity, long timestamp) {
      this.lastActivity.put(activity, timestamp);
   }

   @Override
   public long getLastLogin() {
      return this.getMMOPlayerData().getLastLogActivity();
   }

   @Override
   public int getLevel() {
      return Math.max(1, this.level);
   }

   @Nullable
   public AbstractParty getParty() {
      return MMOCore.plugin.partyModule.getParty(this);
   }

   public boolean hasGuild() {
      return this.guild != null;
   }

   public Guild getGuild() {
      return this.guild;
   }

   public int getClassPoints() {
      return this.classPoints;
   }

   @Override
   public int getSkillPoints() {
      return this.skillPoints;
   }

   public void giveSkillReallocationPoints(int value) {
      this.skillReallocationPoints += value;
   }

   public int countSkillPointsSpent() {
      int sum = 0;

      for (ClassSkill skill : this.getProfess().getSkills()) {
         sum += Math.max(0, this.getSkillLevel(skill.getSkill()) - 1);
      }

      return sum;
   }

   @NotNull
   public List<ClassSkill> getUnlockedSkills() {
      return this.getProfess().getSkills().stream().filter(skill -> this.hasUnlocked(skill) && this.hasUnlockedLevel(skill)).collect(Collectors.toList());
   }

   @Override
   public int getAttributePoints() {
      return this.attributePoints;
   }

   @Override
   public int getAttributeReallocationPoints() {
      return this.attributeReallocationPoints;
   }

   @Override
   public int getSkillTreeReallocationPoints() {
      return this.skillTreeReallocationPoints;
   }

   public int getClaims(@NotNull ExperienceObject object, @NotNull ExperienceItem item) {
      ExperienceTable table = object.getExperienceTable();
      return this.getClaims(object.getKey() + "." + table.getId() + "." + item.getId());
   }

   public int getClaims(@NotNull String key) {
      return this.tableItemClaims.getOrDefault(key, 0);
   }

   public void setClaims(@NotNull ExperienceObject object, @NotNull ExperienceItem item, int times) {
      ExperienceTable table = object.getExperienceTable();
      this.setClaims(object.getKey() + "." + table.getId() + "." + item.getId(), times);
   }

   public void setClaims(@NotNull String itemKey, int times) {
      if (times <= 0) {
         this.tableItemClaims.remove(itemKey);
      } else {
         this.tableItemClaims.put(itemKey, times);
      }
   }

   @Deprecated
   public void setClaims(ExperienceObject object, ExperienceTable table, ExperienceItem item, int times) {
      this.setClaims(object.getKey() + "." + table.getId() + "." + item.getId(), times);
   }

   @Deprecated
   public int getClaims(ExperienceObject object, ExperienceTable table, ExperienceItem item) {
      return this.getClaims(object.getKey() + "." + table.getId() + "." + item.getId());
   }

   public Map<String, Integer> getItemClaims() {
      return this.tableItemClaims;
   }

   public long getLevelUpExperience() {
      return this.getProfess().getExpCurve().getExperience(this.getLevel() + 1);
   }

   public boolean isOnline() {
      return this.getMMOPlayerData().isOnline();
   }

   public boolean inGuild() {
      return this.guild != null;
   }

   public void setLevel(int level) {
      this.level = Math.max(1, level);
      if (this.isOnline()) {
         if (this.isSynchronized()) {
            this.getStats().updateStats();
         }

         this.refreshVanillaExp();
      }
   }

   public void takeLevels(int value) {
      this.setLevel(this.level - value);
   }

   public void giveLevels(int value, EXPSource source) {
      long equivalentExp = 0L;

      while (value-- > 0) {
         equivalentExp += this.getProfess().getExpCurve().getExperience(this.getLevel() + value + 1);
      }

      this.giveExperience(equivalentExp, source);
   }

   public void setExperience(double value) {
      this.experience = Math.max(0.0, value);
      if (this.isOnline()) {
         this.refreshVanillaExp();
      }
   }

   public void refreshVanillaExp() {
      if (MMOCore.plugin.configManager.overrideVanillaExp) {
         this.getPlayer().sendExperienceChange(0.01F);
         this.getPlayer().setLevel(this.getLevel());
         this.getPlayer().setExp(Math.max(0.0F, Math.min(1.0F, (float)this.experience / (float)this.getLevelUpExperience())));
      }
   }

   public void setAttributePoints(int value) {
      this.attributePoints = Math.max(0, value);
   }

   public void setAttributeReallocationPoints(int value) {
      this.attributeReallocationPoints = Math.max(0, value);
   }

   public void setSkillReallocationPoints(int value) {
      this.skillReallocationPoints = Math.max(0, value);
   }

   @Override
   public int getSkillReallocationPoints() {
      return this.skillReallocationPoints;
   }

   public void setSkillPoints(int value) {
      this.skillPoints = Math.max(0, value);
   }

   public void setClassPoints(int value) {
      this.classPoints = Math.max(0, value);
   }

   public void setSkillTreeReallocationPoints(int value) {
      this.skillTreeReallocationPoints = Math.max(0, value);
   }

   public boolean hasSavedClass(PlayerClass profess) {
      return this.classSlots.containsKey(profess.getId());
   }

   public Set<String> getSavedClasses() {
      return this.classSlots.keySet();
   }

   @Nullable
   public SavedClassInformation getClassInfo(PlayerClass profess) {
      return this.getClassInfo(profess.getId());
   }

   @Nullable
   public SavedClassInformation getClassInfo(String profess) {
      return this.classSlots.get(profess);
   }

   public void applyClassInfo(PlayerClass profess, SavedClassInformation info) {
      this.classSlots.put(profess.getId(), info);
   }

   public void unloadClassInfo(PlayerClass profess) {
      this.unloadClassInfo(profess.getId());
   }

   public void unloadClassInfo(String profess) {
      this.classSlots.remove(profess);
   }

   public Set<String> getWaypoints() {
      return this.waypoints;
   }

   public boolean hasWaypoint(Waypoint waypoint) {
      return waypoint.hasOption(WaypointOption.DEFAULT) || this.waypoints.contains(waypoint.getId());
   }

   public void unlockWaypoint(Waypoint waypoint) {
      this.waypoints.add(waypoint.getId());
   }

   public void lockWaypoint(Waypoint waypoint) {
      this.waypoints.remove(waypoint.getId());
   }

   @Deprecated
   public void heal(double heal) {
      this.heal(heal, PlayerResourceUpdateEvent.UpdateReason.OTHER);
   }

   public void heal(double heal, PlayerResourceUpdateEvent.UpdateReason reason) {
      if (this.isOnline()) {
         double max = this.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue();
         double newest = Math.max(0.0, Math.min(this.getPlayer().getHealth() + heal, max));
         if (this.getPlayer().getHealth() != newest) {
            PlayerResourceUpdateEvent event = new PlayerResourceUpdateEvent(this, PlayerResource.HEALTH, heal, reason);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               this.getPlayer().setHealth(Math.max(0.0, Math.min(this.getPlayer().getHealth() + event.getAmount(), max)));
            }
         }
      }
   }

   public void addFriend(UUID uuid) {
      this.friends.add(uuid);
   }

   @Override
   public void removeFriend(UUID uuid) {
      this.friends.remove(uuid);
   }

   @Override
   public boolean hasFriend(UUID uuid) {
      return this.friends.contains(uuid);
   }

   public void setGuild(Guild guild) {
      this.guild = guild;
   }

   public void log(Level level, String message) {
      MMOCore.plugin.getLogger().log(level, "[Userdata:" + (this.isOnline() ? this.getPlayer().getName() : "Offline Player") + "] " + message);
   }

   public void sendFriendRequest(PlayerData target) {
      if (this.isOnline() && target.isOnline()) {
         this.setLastActivity(PlayerActivity.FRIEND_REQUEST);
         FriendRequest request = new FriendRequest(this, target);
         ConfigMessage.fromKey("friend-request")
            .addPlaceholders("player", this.getPlayer().getName(), "uuid", request.getUniqueId().toString())
            .send(target.getPlayer());
         MMOCore.plugin.requestManager.registerRequest(request);
      }
   }

   public void warp(final Waypoint target, final double cost) {
      this.setLastActivity(PlayerActivity.USE_WAYPOINT);
      this.giveStellium(-cost, PlayerResourceUpdateEvent.UpdateReason.USE_WAYPOINT);
      (new BukkitRunnable() {
            final int x = PlayerData.this.getPlayer().getLocation().getBlockX();
            final int y = PlayerData.this.getPlayer().getLocation().getBlockY();
            final int z = PlayerData.this.getPlayer().getLocation().getBlockZ();
            final int warpTime = target.getWarpTime();
            final boolean hasPerm = PlayerData.this.getPlayer().hasPermission("mmocore.bypass-waypoint-wait");
            int t;

            public void run() {
               if (PlayerData.this.isOnline()
                  && PlayerData.this.getPlayer().getLocation().getBlockX() == this.x
                  && PlayerData.this.getPlayer().getLocation().getBlockY() == this.y
                  && PlayerData.this.getPlayer().getLocation().getBlockZ() == this.z) {
                  if (!this.hasPerm && this.t++ < this.warpTime) {
                     ConfigMessage.fromKey("warping-comencing", "left", String.valueOf((this.warpTime - this.t) / 20)).send(PlayerData.this.getPlayer());
                     MMOCore.plugin
                        .soundManager
                        .getSound(SoundEvent.WARP_CHARGE)
                        .playTo(PlayerData.this.getPlayer(), 1.0F, (float)(0.5 + this.t * 1.5 / this.warpTime));
                     double r = Math.sin((double)this.t / this.warpTime * Math.PI);

                     for (double j = 0.0; j < Math.PI * 2; j += Math.PI / 4) {
                        PlayerData.this.getPlayer()
                           .getLocation()
                           .getWorld()
                           .spawnParticle(
                              VParticle.REDSTONE.get(),
                              PlayerData.this.getPlayer()
                                 .getLocation()
                                 .add(
                                    Math.cos(5.0 * this.t / this.warpTime + j) * r,
                                    2.0 * this.t / this.warpTime,
                                    Math.sin(5.0 * this.t / this.warpTime + j) * r
                                 ),
                              1,
                              new DustOptions(Color.PURPLE, 1.25F)
                           );
                     }
                  } else {
                     PlayerData.this.getPlayer().teleport(target.getLocation());
                     PlayerData.this.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, false, false));
                     MMOCore.plugin.soundManager.getSound(SoundEvent.WARP_TELEPORT).playTo(PlayerData.this.getPlayer());
                     this.cancel();
                  }
               } else {
                  if (PlayerData.this.isOnline()) {
                     MMOCore.plugin.soundManager.getSound(SoundEvent.WARP_CANCELLED).playTo(PlayerData.this.getPlayer());
                     ConfigMessage.fromKey("warping-canceled").send(PlayerData.this.getPlayer());
                  }

                  PlayerData.this.giveStellium(cost, PlayerResourceUpdateEvent.UpdateReason.USE_WAYPOINT);
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MMOCore.plugin, 0L, 1L);
   }

   public boolean hasReachedMaxLevel() {
      return this.getProfess().getMaxLevel() > 0 && this.getLevel() >= this.getProfess().getMaxLevel();
   }

   public void giveExperience(double value, EXPSource source) {
      this.giveExperience(value, source, null, true);
   }

   public void giveExperience(double value, @NotNull EXPSource source, @Nullable Location hologramLocation, boolean splitExp) {
      if (value <= 0.0) {
         this.experience = Math.max(0.0, this.experience + value);
      } else {
         AbstractParty party;
         if (splitExp && (party = this.getParty()) != null && MMOCore.plugin.configManager.splitMainExp) {
            List<PlayerData> nearbyMembers = party.getOnlineMembers()
               .stream()
               .filter(
                  var1x -> {
                     if (!this.equals(var1x)
                        && !var1x.hasReachedMaxLevel()
                        && Math.abs(var1x.getLevel() - this.getLevel()) <= MMOCore.plugin.configManager.maxPartyLevelDifference) {
                        double partyMaxExpSplitRange = MMOCore.plugin.configManager.partyMaxExpSplitRange;
                        return partyMaxExpSplitRange <= 0.0
                           || var1x.getPlayer().getWorld().equals(this.getPlayer().getWorld())
                              && var1x.getPlayer().getLocation().distanceSquared(this.getPlayer().getLocation()) < partyMaxExpSplitRange * partyMaxExpSplitRange;
                     } else {
                        return false;
                     }
                  }
               )
               .collect(Collectors.toList());
            value /= nearbyMembers.size() + 1;

            for (PlayerData member : nearbyMembers) {
               member.giveExperience(value, source, null, false);
            }
         }

         if (this.hasReachedMaxLevel()) {
            this.setExperience(0.0);
         } else {
            value *= (1.0 + this.getStats().getStat("ADDITIONAL_EXPERIENCE") / 100.0) * MMOCore.plugin.boosterManager.getMultiplier(null);
            PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this, value, source);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               if (hologramLocation != null && this.isOnline()) {
                  MMOCoreUtils.displayIndicator(
                     hologramLocation,
                     ConfigMessage.fromKey("exp-hologram", "exp", MythicLib.plugin.getMMOConfig().decimal.format(event.getExperience())).asLine()
                  );
               }

               this.experience = Math.max(0.0, this.experience + event.getExperience());
               int oldLevel = this.level;

               long needed;
               while (this.experience >= (needed = this.getLevelUpExperience())) {
                  if (this.hasReachedMaxLevel()) {
                     this.experience = 0.0;
                     break;
                  }

                  this.experience -= needed;
                  this.level = this.getLevel() + 1;
                  this.getProfess().updateAdvancement(this, this.level);
               }

               if (this.level > oldLevel) {
                  Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(this, null, oldLevel, this.level));
                  if (this.isOnline()) {
                     ConfigMessage.fromKey("level-up").addPlaceholders("level", String.valueOf(this.level)).send(this.getPlayer());
                     MMOCore.plugin.soundManager.getSound(SoundEvent.LEVEL_UP).playTo(this.getPlayer());
                     new SmallParticleEffect(this.getPlayer(), VParticle.INSTANT_EFFECT.get());
                  }

                  this.getStats().updateStats();
               }

               this.refreshVanillaExp();
            }
         }
      }
   }

   @Override
   public double getExperience() {
      return this.experience;
   }

   @NotNull
   @Override
   public PlayerClass getProfess() {
      return this.profess == null ? MMOCore.plugin.classManager.getDefaultClass() : this.profess;
   }

   @Deprecated
   public void giveMana(double amount) {
      this.giveMana(amount, PlayerResourceUpdateEvent.UpdateReason.OTHER);
   }

   public void giveMana(double amount, PlayerResourceUpdateEvent.UpdateReason reason) {
      double max = this.getStats().getStat("MAX_MANA");
      double newest = Math.max(0.0, Math.min(this.mana + amount, max));
      if (this.mana != newest) {
         PlayerResourceUpdateEvent event = new PlayerResourceUpdateEvent(this, PlayerResource.MANA, amount, reason);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.mana = Math.max(0.0, Math.min(this.mana + event.getAmount(), max));
         }
      }
   }

   @Deprecated
   public void giveStamina(double amount) {
      this.giveStamina(amount, PlayerResourceUpdateEvent.UpdateReason.OTHER);
   }

   public void giveStamina(double amount, PlayerResourceUpdateEvent.UpdateReason reason) {
      double max = this.getStats().getStat("MAX_STAMINA");
      double newest = Math.max(0.0, Math.min(this.stamina + amount, max));
      if (this.stamina != newest) {
         PlayerResourceUpdateEvent event = new PlayerResourceUpdateEvent(this, PlayerResource.STAMINA, amount, reason);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.stamina = Math.max(0.0, Math.min(this.stamina + event.getAmount(), max));
         }
      }
   }

   @Deprecated
   public void giveStellium(double amount) {
      this.giveStellium(amount, PlayerResourceUpdateEvent.UpdateReason.OTHER);
   }

   public void giveStellium(double amount, PlayerResourceUpdateEvent.UpdateReason reason) {
      double max = this.getStats().getStat("MAX_STELLIUM");
      double newest = Math.max(0.0, Math.min(this.stellium + amount, max));
      if (this.stellium != newest) {
         PlayerResourceUpdateEvent event = new PlayerResourceUpdateEvent(this, PlayerResource.STELLIUM, amount, reason);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            this.stellium = Math.max(0.0, Math.min(this.stellium + event.getAmount(), max));
         }
      }
   }

   @Deprecated
   @Override
   public double getHealth() {
      return this.isOnline() ? this.getPlayer().getHealth() : this.health;
   }

   @Override
   public double getMana() {
      return this.mana;
   }

   @Override
   public double getStamina() {
      return this.stamina;
   }

   @Override
   public double getStellium() {
      return this.stellium;
   }

   public double getCachedHealth() {
      return this.health;
   }

   public PlayerStats getStats() {
      return this.playerStats;
   }

   public PlayerAttributes getAttributes() {
      return this.attributes;
   }

   public void setHealth(double amount) {
      this.health = amount;
   }

   public void setMana(double amount) {
      this.mana = Math.max(0.0, Math.min(amount, this.getStats().getStat("MAX_MANA")));
   }

   public void setStamina(double amount) {
      this.stamina = Math.max(0.0, Math.min(amount, this.getStats().getStat("MAX_STAMINA")));
   }

   public void setStellium(double amount) {
      this.stellium = Math.max(0.0, Math.min(amount, this.getStats().getStat("MAX_STELLIUM")));
   }

   @Deprecated
   public boolean isFullyLoaded() {
      return this.isSynchronized();
   }

   @Deprecated
   public void setFullyLoaded() {
      this.markAsSynchronized();
   }

   public boolean isCasting() {
      return this.skillCasting != null;
   }

   @Deprecated
   public boolean setSkillCasting(@NotNull SkillCastingInstance skillCasting) {
      return this.setSkillCasting();
   }

   public boolean setSkillCasting() {
      Validate.isTrue(!this.isCasting(), "Player already in casting mode");
      PlayerEnterCastingModeEvent event = new PlayerEnterCastingModeEvent(this);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return false;
      }

      this.skillCasting = SkillCastingMode.getInstance().newInstance(this);
      return true;
   }

   @NotNull
   public SkillCastingInstance getSkillCasting() {
      return Objects.requireNonNull(this.skillCasting, "Player not in casting mode");
   }

   public boolean leaveSkillCasting() {
      return this.leaveSkillCasting(false);
   }

   public boolean leaveSkillCasting(boolean skipEvent) {
      Validate.isTrue(this.isCasting(), "Player not in casting mode");
      if (!skipEvent) {
         PlayerExitCastingModeEvent event = new PlayerExitCastingModeEvent(this);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         }
      }

      this.skillCasting.close();
      this.skillCasting = null;
      this.setLastActivity(PlayerActivity.ACTION_BAR_MESSAGE, 0L);
      return true;
   }

   public void displayActionBar(@NotNull String message) {
      this.displayActionBar(message, false);
   }

   public void displayActionBar(@NotNull String message, boolean raw) {
      if (!ChatColor.stripColor(message).isEmpty()) {
         this.setLastActivity(PlayerActivity.ACTION_BAR_MESSAGE);
         if (raw) {
            MythicLib.plugin.getVersion().getWrapper().sendActionBarRaw(this.getPlayer(), message);
         } else {
            this.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
         }
      }
   }

   @Deprecated
   public void setAttribute(PlayerAttribute attribute, int value) {
      this.setAttribute(attribute.getId(), value);
   }

   @Deprecated
   public void setAttribute(String id, int value) {
      this.attributes.getInstance(id).setBase(value);
   }

   @Override
   public Map<String, Integer> mapAttributeLevels() {
      return this.getAttributes().mapPoints();
   }

   public int getSkillLevel(RegisteredSkill skill) {
      return this.skills.getOrDefault(skill.getHandler().getId(), 1);
   }

   public void setSkillLevel(RegisteredSkill skill, int level) {
      this.setSkillLevel(skill.getHandler().getId(), level);
   }

   public void setSkillLevel(String skill, int level) {
      this.skills.put(skill, level);
   }

   public void resetSkillLevel(String skill) {
      this.skills.remove(skill);
   }

   @Deprecated
   public boolean hasSkillUnlocked(RegisteredSkill skill) {
      return this.getProfess().hasSkill(skill.getHandler().getId()) && this.hasSkillUnlocked(this.getProfess().getSkill(skill.getHandler().getId()));
   }

   @Deprecated
   public boolean hasSkillUnlocked(ClassSkill skill) {
      return this.hasUnlockedLevel(skill);
   }

   public boolean hasUnlockedLevel(ClassSkill skill) {
      return this.getLevel() >= skill.getUnlockLevel();
   }

   @Override
   public Map<String, Integer> mapSkillLevels() {
      return new HashMap<>(this.skills);
   }

   public void giveClassPoints(int value) {
      this.setClassPoints(this.classPoints + value);
   }

   public void giveSkillPoints(int value) {
      this.setSkillPoints(this.skillPoints + value);
   }

   public void giveAttributePoints(int value) {
      this.setAttributePoints(this.attributePoints + value);
   }

   public void giveAttributeReallocationPoints(int value) {
      this.setAttributeReallocationPoints(this.attributeReallocationPoints + value);
   }

   public void giveSkillTreeReallocationPoints(int value) {
      this.setSkillTreeReallocationPoints(this.skillTreeReallocationPoints + value);
   }

   public CooldownMap getCooldownMap() {
      return this.getMMOPlayerData().getCooldownMap();
   }

   public void setClass(@Nullable PlayerClass profess) {
      this.profess = profess;
      this.boundSkills.forEach((slot, info) -> info.close());
      this.boundSkills.clear();
      this.applyTemporaryTriggers();
      if (this.isOnline()) {
         this.getStats().updateStats();
      }
   }

   public boolean hasSkillBound(int slot) {
      return this.boundSkills.containsKey(slot);
   }

   @Nullable
   public ClassSkill getBoundSkill(int slot) {
      BoundSkillInfo found = this.boundSkills.get(slot);
      return found != null ? found.getClassSkill() : null;
   }

   @Deprecated
   public void setBoundSkill(int slot, ClassSkill skill) {
      this.bindSkill(slot, skill);
   }

   public void bindSkill(int slot, @NotNull ClassSkill skill) {
      Validate.notNull(skill, "Skill cannot be null");
      if (slot > 0) {
         if (skill.isPermanent()) {
            MMOCore.plugin
               .getLogger()
               .log(Level.WARNING, "Attempted to bind permanent skill " + skill.getSkill().getName() + " to player " + this.getUniqueId());
         } else {
            this.unbindSkill(slot);
            SkillSlot skillSlot = this.getProfess().getSkillSlot(slot);
            this.boundSkills.put(slot, new BoundSkillInfo(skillSlot, skill, this));
            SkillCastingMode.getInstance().onSkillBound(this);
         }
      }
   }

   @Nullable
   public BoundSkillInfo unbindSkill(int slot) {
      BoundSkillInfo boundSkillInfo = this.boundSkills.remove(slot);
      if (boundSkillInfo != null) {
         boundSkillInfo.close();
      }

      return boundSkillInfo;
   }

   @NotNull
   public Map<Integer, BoundSkillInfo> getBoundSkills() {
      return this.boundSkills;
   }

   public boolean hasActiveSkillBound() {
      for (BoundSkillInfo bound : this.boundSkills.values()) {
         if (!bound.isPassive()) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public CombatHandler getCombat() {
      return this.combat;
   }

   public boolean isInCombat() {
      return this.getCombat().isInCombat();
   }

   @Deprecated
   public boolean canChooseSubclass() {
      for (Subclass subclass : this.getProfess().getSubclasses()) {
         if (this.getLevel() >= subclass.getLevel()) {
            return true;
         }
      }

      return false;
   }

   @Deprecated
   public void updateCombat() {
      this.getCombat().update();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PlayerData that = (PlayerData)o;
         return this.getUniqueId().equals(that.getUniqueId());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getMMOPlayerData().hashCode();
   }

   public static PlayerData get(@NotNull MMOPlayerData playerData) {
      return get(playerData.getPlayer());
   }

   public static PlayerData get(@NotNull OfflinePlayer player) {
      return get(player.getUniqueId());
   }

   public static PlayerData get(@NotNull UUID uuid) {
      return (PlayerData)MMOCore.plugin.playerDataManager.get(uuid);
   }

   public static boolean has(Player player) {
      return has(player.getUniqueId());
   }

   public static boolean has(UUID uuid) {
      return MMOCore.plugin.playerDataManager.isLoaded(uuid);
   }

   public static Collection<PlayerData> getAll() {
      return MMOCore.plugin.playerDataManager.getLoaded();
   }
}
