package io.lumine.mythic.api.mobs;

import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.core.config.DropMethod;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import io.lumine.mythic.core.mobs.model.MobModel;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public interface MythicMob extends Comparable<MythicMob> {
   String getInternalName();

   Pack getPack();

   File getFile();

   void buildCache();

   boolean isHidden();

   PlaceholderString getDisplayName();

   void setDisplayName(PlaceholderString placeholderString);

   MythicEntityType getEntityType();

   void setEntityType(MythicEntityType mythicEntityType);

   String getEntityTypeString();

   BukkitEntityType getMythicEntity();

   DespawnMode getDespawnMode();

   PlaceholderString getEggDisplay();

   MobModel getModel();

   DropTable getDropTable();

   DropMethod getDropMethod();

   Boolean getDamageLeaderboard();

   Boolean getShowHologramLeaderboardOnDeath();

   Integer getHologramLeaderboardTimeout();

   Boolean getShowChatLeaderboardOnDeath();

   List<String> getFancyDropHologramMessage();

   List<String> getFancyDropChatMessage();

   Boolean getDropsAreClientsideByDefault();

   DropTable getEquipmentTable();

   Boolean getOptionLockPitch();

   Map<String, Double> getDamageModifiers();

   Map<String, Double> getEntityDamageModifiers();

   String getAiNavigator();

   boolean isUsingTimers();

   int getNoDamageTicks();

   int getMaxAttackRange();

   int getMaxAttackableRange();

   int getMaxThreatDistance();

   boolean isUseCustomNameplate();

   Boolean getRepeatAllSkills();

   Boolean getPreventOtherDrops();

   Boolean getPreventRandomEquipment();

   Boolean getPreventLeashing();

   Boolean getPreventKnockback();

   Boolean getPreventRename();

   Boolean getPreventEndermanTeleport();

   Boolean getPreventItemPickup();

   Boolean getPreventSilverfishInfection();

   Boolean getPreventSunburn();

   Boolean getPreventExploding();

   Boolean getPreventMobKillDrops();

   Boolean getPreventTransformation();

   Boolean getPreventMounts();

   Boolean getPassthroughDamage();

   Boolean getApplyInvisibility();

   Boolean getDigOutOfGround();

   Boolean getUsesHealthBar();

   double getSpawnVelocityX();

   double getSpawnVelocityXMax();

   double getSpawnVelocityY();

   double getSpawnVelocityYMax();

   double getSpawnVelocityZ();

   double getSpawnVelocityZMax();

   Boolean getSpawnVelocityXRange();

   Boolean getSpawnVelocityYRange();

   Boolean getSpawnVelocityZRange();

   PlaceholderString getDisguise();

   MythicConfig getConfig();

   RandomInt getDefaultLevel();

   boolean isLevelConfigured();

   default ActiveMob spawn(AbstractLocation location, double level) {
      return this.spawn(location, level, SpawnReason.OTHER);
   }

   ActiveMob spawn(AbstractLocation abstractLocation, double value, SpawnReason spawnReason);

   ActiveMob spawn(AbstractLocation abstractLocation, double value, SpawnReason spawnReason, Consumer<Entity> consumer);

   ActiveMob spawn(AbstractLocation abstractLocation, double value, SpawnReason spawnReason, Consumer<Entity> consumer, MythicSpawner mythicSpawner);

   default ActiveMob spawn(
      AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc, MythicSpawner spawner, Set<UUID> executionAudience
   ) {
      ActiveMob am = this.spawn(location, level, reason, prespawnFunc, spawner);
      if (am != null && executionAudience != null) {
         am.setExecutionAudience(executionAudience);
      }

      return am;
   }

   default ActiveMob spawn(
      AbstractLocation location,
      double level,
      SpawnReason reason,
      Consumer<Entity> prespawnFunc,
      MythicSpawner spawner,
      Set<UUID> executionAudience,
      SkillCaster parent
   ) {
      ActiveMob am = this.spawn(location, level, reason, prespawnFunc, spawner, executionAudience);
      if (am != null && parent != null) {
         am.setParent(parent);
      }

      return am;
   }

   ActiveMob applyMobOptions(ActiveMob activeMob, double value);

   ActiveMob applyMobVolatileOptions(ActiveMob activeMob);

   void reapplyAIGoalSelectors(ActiveMob activeMob);

   ActiveMob applySpawnModifiers(ActiveMob activeMob);

   void executeSkills(SkillTrigger skillTrigger, SkillMetadata skillMetadata);

   void executeTimerSkills(SkillMetadata skillMetadata, long value);

   void executeSignalSkill(String str, SkillMetadata skillMetadata);

   Queue<SkillMechanic> getSkills(SkillTrigger skillTrigger);

   boolean hasSkills(SkillTrigger skillTrigger);

   Queue<SkillMechanic> getTimerSkills();

   double getPerLevelHealth();

   double getPerLevelDamage();

   double getPerLevelPower();

   Optional<Double> getBaseStatValue(ActiveMob activeMob, StatType statType);

   PlaceholderDouble getArmor();

   double getPerLevelArmor();

   double getArmor(ActiveMob activeMob);

   PlaceholderDouble getDamage();

   double getDamage(ActiveMob activeMob);

   double getMovementSpeed(ActiveMob activeMob);

   double getReviveHealth(ActiveMob activeMob);

   double getFlyingSpeed(ActiveMob activeMob);

   double getKnockbackResistance(ActiveMob activeMob);

   double getAttackSpeed(ActiveMob activeMob);

   boolean hasFaction();

   String getFaction();

   void setFaction(String str);

   PlaceholderDouble getHealth();

   double getHealth(ActiveMob activeMob);

   boolean getIsInvincible();

   boolean usesThreatTable();

   boolean usesImmunityTable();

   boolean usesHearing();

   boolean getThreatTableUseDamageTaken();

   boolean getThreatTableDecaysUnreachable();

   int getThreatTableDropUnreachableTicks();

   List<String> getAIGoalSelectors();

   List<String> getAITargetSelectors();

   boolean hasKillMessages();

   PlaceholderString getKillMessage();

   boolean getIsInteractable();

   boolean usesBossBar();

   int getBossBarRangeSquared();

   Optional<AbstractBossBar> getBossBar();

   PlaceholderString getBossBarTitle();

   boolean getShowHealthInChat();

   boolean getShowNameOnDamaged();

   Map<StatType, PlaceholderDouble> getStats();

   default Optional<PlaceholderDouble> getStatValue(StatType type) {
      return Optional.ofNullable(this.getStats().getOrDefault(type, null));
   }

   default String getStatValueDisplay(StatType type) {
      PlaceholderDouble value = this.getStats().getOrDefault(type, null);
      return value == null ? "Unset" : value.toString();
   }

   default void setStatValue(StatType type, String value) {
      this.setStatValue(type, PlaceholderDouble.of(value));
   }

   default void setStatValue(StatType type, PlaceholderDouble value) {
      this.getStats().put(type, value);
   }

   float getSpeechBubbleOffset();

   VariableRegistry getDefaultVariables();

   Boolean getPreventVanillaDamage();

   boolean isDropsPerPlayer();

   Boolean getDropsDoLootsplosion();

   Boolean getDropsShowNameByDefault();

   Boolean getDropsGlowByDefault();

   Boolean getDropsHaveBeamByDefault();

   Boolean getDropsHaveVFXByDefault();

   double getDropsPerPlayerRequiredDamagePercent();

   Material getDropsItemVFXMaterial();

   int getDropsItemVFXModel();

   AbstractItemStack getEgg();
}
