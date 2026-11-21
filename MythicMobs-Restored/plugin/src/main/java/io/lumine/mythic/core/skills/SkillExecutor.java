package io.lumine.mythic.core.skills;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.ConditionProvider;
import io.lumine.mythic.api.skills.ConditionRegistry;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.IWaitableSkill;
import io.lumine.mythic.api.skills.MechanicProvider;
import io.lumine.mythic.api.skills.MechanicRegistry;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillManager;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.IEntityLocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicTargetEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.promise.PromiseRetiredException;
import io.lumine.mythic.core.adapters.VirtualEntity;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.audience.CustomAudience;
import io.lumine.mythic.core.skills.audience.InvalidAudience;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import io.lumine.mythic.core.skills.auras.AuraExecutor;
import io.lumine.mythic.core.skills.auras.components.AuraComponentFactory;
import io.lumine.mythic.core.skills.commands.CommandSkillExecutor;
import io.lumine.mythic.core.skills.conditions.CompositeCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.cooldowns.ChargeRestoreOrder;
import io.lumine.mythic.core.skills.cooldowns.CooldownRegistry;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.MetaSkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.targeters.ConsoleTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import io.lumine.mythic.core.skills.targeters.TriggerLocationTargeter;
import io.lumine.mythic.core.skills.targeters.VanillaTargeter;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicAudience;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import org.bukkit.Bukkit;

public class SkillExecutor extends ReloadableModule<MythicBukkit> implements SkillManager {
   private static final Map<String, Class<? extends SkillCondition>> CONDITIONS = new ConcurrentHashMap<>();
   private static final Map<String, Class<? extends SkillMechanic>> MECHANICS = new ConcurrentHashMap<>();
   private static final Map<String, Class<? extends SkillTargeter>> TARGETERS = new ConcurrentHashMap<>();
   private static final Map<String, Class<? extends SkillAudience>> AUDIENCES = new ConcurrentHashMap<>();
   private static final Map<String, Class<? extends AuraComponentFactory>> AURACOMPONENTS = new ConcurrentHashMap<>();
   private static final Class<?>[] DISPATCH_INTERFACES = new Class[]{
      ITargetedEntitySkill.class, ITargetedLocationSkill.class, INoTargetSkill.class, IMetaSkill.class, IWaitableSkill.class
   };
   private static final Class<?>[] DISPATCH_CONDITION_INTERFACES = new Class[]{
      IEntityCondition.class,
      ILocationCondition.class,
      ICasterCondition.class,
      ISkillMetaCondition.class,
      IEntityComparisonCondition.class,
      ILocationComparisonCondition.class,
      IEntityLocationComparisonCondition.class,
      ISkillMetaComparisonCondition.class,
      ISkillMetaLocationComparisonCondition.class
   };
   private List<File> skillFiles;
   private IOLoader<MythicBukkit> defaultSkills;
   private List<IOLoader<MythicBukkit>> skillLoaders;
   private Map<String, Skill> skills = Maps.newConcurrentMap();
   private final ConcurrentMap<String, SkillMechanic> mechanicCache = new ConcurrentHashMap<>();
   private final ConcurrentMap<String, SkillCondition> conditionCache = new ConcurrentHashMap<>();
   private final ConcurrentMap<String, SkillTargeter> targeterCache = new ConcurrentHashMap<>();
   @Getter
   private AuraExecutor auraManager;
   @Getter
   private EventExecutor eventBus;
   private CommandSkillExecutor commandSkillExecutor;
   private SkillCasterProvider skillCasterProvider = null;
   private static final ConcurrentMap<UUID, CooldownRegistry> COOLDOWNS = new ConcurrentHashMap<>();
   private static final ConcurrentMap<UUID, ConcurrentMap<UUID, Object>> COOLDOWN_LOCKS = new ConcurrentHashMap<>();

   public SkillExecutor(MythicBukkit core) {
      super(core, false);
      this.loadSkillComponents();
      this.load(core);
   }

   public void load(MythicBukkit plugin) {
      this.auraManager = new AuraExecutor(this.getPlugin(), this);
      this.eventBus = new EventExecutor(this.getPlugin(), this);
      loadMechanics();
   }

   public CommandSkillExecutor getCommandSkillExecutor() {
      if (this.commandSkillExecutor == null
         && (MythicBukkit.isVolatile() || this.getPlugin().getCompatibility().getRpg().isPresent() || Bukkit.getPluginManager().getPlugin("MythicRPG") != null)
         )
       {
         this.commandSkillExecutor = new CommandSkillExecutor();
      }

      return this.commandSkillExecutor;
   }

   @Override
   public void unload() {
      this.mechanicCache.clear();
      this.auraManager.shutdown();
      Projectile.BULLET_ENTITIES.forEach(AbstractEntity::remove);
   }

   public void loadSkills() {
      MythicLogger.log("Loading Skills...");
      this.defaultSkills = new IOLoader<>(MythicBukkit.inst(), "ExampleSkills.yml", "Skills");
      this.skillFiles = IOHandler.getAllFiles(this.defaultSkills.getFile().getParent());
      this.skills.clear();
      this.mechanicCache.clear();
      this.targeterCache.clear();
      this.conditionCache.clear();

      for (PackExecutor.ConfigFile configFile : this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.SKILL)) {
         MythicConfigImpl fileConfig = new MythicConfigImpl(configFile.file());

         for (String node : NodeListProp.getNodes(configFile.file(), "")) {
            try {
               MythicConfig config = fileConfig.getNestedConfig(node);
               if (!configFile.file().getParentFile().getName().equals("mono") || !config.isSet("Id") && !config.isSet("Type")) {
                  MetaSkill ms = new MetaSkill(this, configFile.pack(), configFile.file(), node, config);
                  this.skills.put(node, ms);
               }
            } catch (Exception e) {
               MythicLogger.error("Error loading skill '" + node + "'. Enable debugging for a stack trace.");
               MythicLogger.handleMinorError(e);
            }
         }
      }

      this.queueAfterLoad(() -> {
         CommandSkillExecutor executor = this.getCommandSkillExecutor();
         if (executor != null) {
            executor.reloadCommands();
         }

         this.mechanicCache.clear();
         this.targeterCache.clear();
         this.conditionCache.clear();
      });
   }

   @Override
   public void queueAfterLoad(Runnable r) {
      this.plugin.getClock().queueAfterLoad(r);
   }

   @Override
   public void queueSecondPass(Runnable r) {
      this.plugin.getClock().queueSecondPass(r);
   }

   @Override
   public SkillCaster getCaster(AbstractEntity entity) {
      if (entity.isPlayer()) {
         return this.getPlugin().getPlayerManager().getProfile(entity.asPlayer());
      } else if (entity instanceof VirtualEntity) {
         return new GenericCaster(entity);
      } else {
         ActiveMob maybeMob = this.getPlugin().getMobManager().getMythicMobInstance(entity);
         if (maybeMob != null) {
            return maybeMob;
         } else {
            if (this.getPlugin().getCompatibility().getCrucible().isPresent()) {
               Optional<SkillCaster> maybeFurniture = this.getPlugin().getCompatibility().getCrucible().get().getFurnitureCaster(entity);
               if (maybeFurniture.isPresent()) {
                  return maybeFurniture.get();
               }
            }

            if (this.skillCasterProvider != null) {
               Optional<SkillCaster> caster = this.skillCasterProvider.getCaster(entity);
               if (caster.isPresent()) {
                  return caster.get();
               }
            }

            return new GenericCaster(entity);
         }
      }
   }

   public SkillCaster getCasterNullable(AbstractEntity entity) {
      if (entity.isPlayer()) {
         return this.getPlugin().getPlayerManager().getProfile(entity.asPlayer());
      } else {
         ActiveMob maybeMob = this.getPlugin().getMobManager().getMythicMobInstance(entity);
         if (maybeMob != null) {
            return maybeMob;
         } else {
            if (this.skillCasterProvider != null) {
               Optional<SkillCaster> caster = this.skillCasterProvider.getCaster(entity);
               if (caster.isPresent()) {
                  return caster.get();
               }
            }

            return null;
         }
      }
   }

   @Override
   public void registerSkill(String internalName, Skill skill) {
      this.skills.put(internalName, skill);
   }

   @Override
   public Optional<Skill> getSkill(String internalName) {
      return this.getSkill(null, internalName);
   }

   @Override
   public Optional<Skill> getSkill(File file, String internalName) {
      return this.getSkill(file, null, internalName);
   }

   @Override
   public Optional<Skill> getSkill(File file, SkillHolder parent, String internalName) {
      if (internalName == null) {
         return Optional.empty();
      } else {
         internalName = internalName.trim();
         if (internalName.startsWith("[") && internalName.endsWith("]")) {
            internalName = internalName.substring(1, internalName.length() - 1);
            internalName = MythicLineConfigImpl.unparseBlock(internalName);
            String uncommentedLines = internalName.replaceAll("<#> *-.*?(?=<#>|-|$)", "");
            String[] split = uncommentedLines.split("-");
            List<String> elements = Arrays.stream(split).map(String::trim).filter(trim -> !trim.isEmpty()).collect(Collectors.toList());
            MetaSkill skill = new MetaSkill(this, file, elements);
            if (parent != null) {
               skill.addParent(parent);
               skill.setPack(parent.getPack());
            }

            skill.setInlineSkill(true);
            return Optional.of(skill);
         } else {
            return this.skills.containsKey(internalName) ? Optional.of(this.skills.get(internalName)) : Optional.empty();
         }
      }
   }

   public Optional<Skill> getSkill(File file, Collection<String> elements) {
      MetaSkill skill = new MetaSkill(this, file, elements);
      String skillName = skill.getInternalName();
      this.skills.put(skillName, skill);
      skill.setInlineSkill(true);
      return Optional.of(skill);
   }

   public MetaSkillMechanic getWrappedSkill(File file, Collection<String> elements) {
      Optional<Skill> maybeSkill = this.getSkill(file, elements);
      Skill skill = maybeSkill.get();
      return new MetaSkillMechanic(this, file, skill);
   }

   @Override
   public Collection<String> getSkillNames() {
      return this.skills.keySet();
   }

   @Override
   public Collection<Skill> getSkills() {
      return this.skills.values();
   }

   public void setCasterProvider(SkillCasterProvider provider) {
      this.skillCasterProvider = provider;
   }

   public void runTimerSkills(long timer) {
      int clockInterval = this.getPlugin().getConfiguration().getClockIntervalMain();

      for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
         if (am != null && !am.isDead() && am.getEntity() != null && am.getType() != null) {
            am.tick(timer, clockInterval);
            if (!am.isDead() && am.getEntity() != null && am.getEntity().isValid() && am.getType().isUsingTimers()) {
               this.executeMobTimerSkills(am, timer);
               MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "---- Executed TIMER skills");
            }
         }
      }
   }

   public void runTimerSkillsFolia(long timer) {
      int clockInterval = this.getPlugin().getConfiguration().getClockIntervalMain();

      for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
         if (am != null && !am.isDead() && am.getEntity() != null && am.getType() != null) {
            Schedulers.of(am.getEntity().getBukkitEntity()).run(() -> {
               am.tick(timer, clockInterval);
               if (!am.isDead() && am.getEntity() != null && am.getEntity().isValid() && am.getType().isUsingTimers()) {
                  this.executeMobTimerSkills(am, timer);
                  MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "---- Executed TIMER skills");
               }
            }).exceptionallyAsync(t -> {
               if (t instanceof PromiseRetiredException) {
                  am.setDespawnedOnOwningRegion();
               }

               return null;
            });
         }
      }
   }

   public void executeMobTimerSkills(ActiveMob am, long timer) {
      SkillMetadata data = new SkillMetadataImpl(SkillTriggers.TIMER, am, null);
      data.setPower(am.getPower());
      am.getType().executeTimerSkills(data, timer);
   }

   @Override
   public void setSkillCooldown(SkillCaster caster, AbstractSkill skill, double millis) {
      if (caster instanceof PlayerData playerData) {
         COOLDOWNS.remove(playerData.getUniqueId());
         playerData.setSkillCooldownMillis(skill, millis);
      } else if (caster instanceof ActiveMob activeMob) {
         COOLDOWNS.remove(activeMob.getUniqueId());
         activeMob.setSkillCooldownMillis(skill, millis);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         if (millis > 0.0) {
            COOLDOWNS.computeIfAbsent(casterId, k -> new CooldownRegistry())
               .setCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), (long)millis);
         } else {
            this.removeSkillCooldown(caster, skill);
         }
      }
   }

   @Override
   public boolean isSkillOnCooldown(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         return playerData.isSkillOnCooldown(skill);
      } else if (caster instanceof ActiveMob activeMob) {
         return activeMob.isSkillOnCooldown(skill);
      } else if (caster.getEntity() == null) {
         return false;
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry == null) {
            return false;
         } else {
            boolean onCooldown = registry.isOnCooldown(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }

            return onCooldown;
         }
      }
   }

   boolean isSkillRecharging(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         return playerData.isSkillRecharging(skill);
      } else if (caster instanceof ActiveMob activeMob) {
         return activeMob.isSkillRecharging(skill);
      } else if (caster.getEntity() == null) {
         return false;
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry == null) {
            return false;
         } else {
            boolean recharging = registry.isRecharging(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }

            return recharging;
         }
      }
   }

   @Override
   public void clearSkillCooldowns(SkillCaster caster) {
      if (caster instanceof PlayerData playerData) {
         COOLDOWNS.remove(playerData.getUniqueId());
         COOLDOWN_LOCKS.remove(playerData.getUniqueId());
         playerData.clearSkillCooldowns();
      } else if (caster instanceof ActiveMob activeMob) {
         COOLDOWNS.remove(activeMob.getUniqueId());
         COOLDOWN_LOCKS.remove(activeMob.getUniqueId());
         activeMob.clearSkillCooldowns();
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         COOLDOWNS.remove(casterId);
         COOLDOWN_LOCKS.remove(casterId);
      }
   }

   @Override
   public long getSkillCooldownRemainingMillis(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         return (long)playerData.getSkillCooldownRemainingMillis(skill);
      } else if (caster instanceof ActiveMob activeMob) {
         return (long)activeMob.getSkillCooldownRemainingMillis(skill);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry == null) {
            return 0L;
         } else {
            long remaining = registry.getRemainingCooldownMillis(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }

            return remaining;
         }
      }
   }

   @Override
   public void removeSkillCooldown(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         COOLDOWNS.remove(playerData.getUniqueId());
         playerData.setSkillCooldownMillis(skill, 0.0);
      } else if (caster instanceof ActiveMob activeMob) {
         COOLDOWNS.remove(activeMob.getUniqueId());
         activeMob.setSkillCooldownMillis(skill, 0.0);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry != null) {
            registry.clearCooldown(skill.getUuid());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }
         }
      }
   }

   int getAvailableSkillCharges(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         return playerData.getAvailableSkillCharges(skill);
      } else if (caster instanceof ActiveMob activeMob) {
         return activeMob.getAvailableSkillCharges(skill);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry == null) {
            return skill.getCharges();
         } else {
            int charges = registry.getAvailableCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }

            return charges;
         }
      }
   }

   int getOverflowSkillCharges(SkillCaster caster, AbstractSkill skill) {
      if (caster instanceof PlayerData playerData) {
         return playerData.getOverflowSkillCharges(skill);
      } else if (caster instanceof ActiveMob activeMob) {
         return activeMob.getOverflowSkillCharges(skill);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.get(casterId);
         if (registry == null) {
            return 0;
         } else {
            int overflowCharges = registry.getOverflowCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode());
            if (registry.isEmpty()) {
               COOLDOWNS.remove(casterId, registry);
            }

            return overflowCharges;
         }
      }
   }

   boolean consumeSkillCharge(SkillCaster caster, AbstractSkill skill, long cooldownMillis) {
      if (caster instanceof PlayerData playerData) {
         return playerData.consumeSkillCharge(skill, cooldownMillis);
      } else if (caster instanceof ActiveMob activeMob) {
         return activeMob.consumeSkillCharge(skill, cooldownMillis);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.computeIfAbsent(casterId, k -> new CooldownRegistry());
         boolean consumed = registry.consumeCharge(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), cooldownMillis);
         if (registry.isEmpty()) {
            COOLDOWNS.remove(casterId, registry);
         }

         return consumed;
      }
   }

   void addSkillCharges(SkillCaster caster, AbstractSkill skill, int amount, ChargeRestoreOrder order, boolean overflow) {
      if (caster instanceof PlayerData playerData) {
         playerData.addSkillCharges(skill, amount, order, overflow);
      } else if (caster instanceof ActiveMob activeMob) {
         activeMob.addSkillCharges(skill, amount, order, overflow);
      } else {
         UUID casterId = caster.getEntity().getUniqueId();
         CooldownRegistry registry = COOLDOWNS.computeIfAbsent(casterId, k -> new CooldownRegistry());
         registry.addCharges(skill.getUuid(), skill.getCharges(), skill.getCooldownMode(), amount, order, overflow);
         if (registry.isEmpty()) {
            COOLDOWNS.remove(casterId, registry);
         }
      }
   }

   Object tryAcquireCooldownLock(SkillCaster caster, AbstractSkill skill) {
      UUID casterId = caster.getEntity().getUniqueId();
      UUID skillId = skill.getUuid();
      Object token = new Object();
      boolean[] acquired = new boolean[1];
      COOLDOWN_LOCKS.compute(casterId, (id, skills) -> {
         if (skills == null) {
            skills = new ConcurrentHashMap<>();
         }

         acquired[0] = skills.putIfAbsent(skillId, token) == null;
         return skills;
      });
      return acquired[0] ? token : null;
   }

   void releaseCooldownLock(SkillCaster caster, AbstractSkill skill, Object token) {
      if (token != null) {
         UUID casterId = caster.getEntity().getUniqueId();
         UUID skillId = skill.getUuid();
         COOLDOWN_LOCKS.computeIfPresent(casterId, (id, skills) -> {
            skills.remove(skillId, token);
            return skills.isEmpty() ? null : skills;
         });
      }
   }

   @Override
   public void purgeExpiredCooldowns() {
      for (Entry<UUID, CooldownRegistry> entry : COOLDOWNS.entrySet()) {
         CooldownRegistry registry = entry.getValue();
         registry.purgeExpired();
         if (registry.isEmpty()) {
            COOLDOWNS.remove(entry.getKey(), registry);
         }
      }
   }

   public ImmutableMap<String, Class<? extends SkillMechanic>> getMechanics() {
      return ImmutableMap.copyOf(MECHANICS);
   }

   private static void loadMechanics() {
   }

   @Override
   public SkillMechanic getMechanic(String skill) {
      return this.getMechanic(null, skill);
   }

   public SkillMechanic getMechanic(File file, String skill) {
      return this.getMechanic(null, file, null, skill);
   }

   public SkillMechanic getMechanic(Pack pack, File file, SkillHolder parent, String skill) {
      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Matching mechanic to string: {0}", skill);
      String[] s = skill.split(" ");
      String name = null;
      MythicLineConfig mlc = file != null ? new MythicLineConfigImpl(file, s[0]) : new MythicLineConfigImpl(s[0]);
      if (s[0].contains("{")) {
         name = s[0].substring(0, s[0].indexOf("{"));
      } else {
         name = s[0];
      }

      MythicLogger.debug(MythicLogger.DebugLevel.INFO, "-- Matching MythicSkill type to {0}", name.toUpperCase());
      String cacheKey = null;
      if (this.getPlugin().getConfiguration().isCacheComponents()) {
         cacheKey = skill.trim();
         SkillMechanic cached = this.mechanicCache.get(cacheKey);
         if (cached != null) {
            return cached;
         }
      }

      if (MECHANICS.containsKey(name.toUpperCase())) {
         Class<? extends SkillMechanic> clazz = MECHANICS.get(name.toUpperCase());

         try {
            SkillMechanic mechanic = clazz.getConstructor(SkillExecutor.class, File.class, String.class, MythicLineConfig.class)
               .newInstance(this, file, skill, mlc);
            mechanic.setPack(pack);
            mechanic.setParent(parent);
            this.applyMechanicOverrides(name.toUpperCase(), mlc, mechanic);
            if (this.getPlugin().getConfiguration().isCacheComponents() && cacheKey != null && mechanic.getCooldown() == null) {
               this.mechanicCache.putIfAbsent(cacheKey, mechanic);
            }

            return mechanic;
         } catch (Exception e) {
            MythicLogger.error("Failed to construct mechanic {0}", skill);
            e.printStackTrace();
         }
      }

      try {
         if (!name.toUpperCase().startsWith("SKILL:") && !name.toUpperCase().startsWith("META:")) {
            SkillMechanic mechanic = new CustomMechanic(this, file, name.toUpperCase(), skill, mlc);
            mechanic.setPack(pack);
            mechanic.setParent(parent);
            return mechanic;
         } else {
            String skillName = name.substring(name.indexOf(":") + 1);
            SkillMechanic mechanic = new MetaSkillMechanic(this, file, skill, skillName, mlc);
            mechanic.setPack(pack);
            mechanic.setParent(parent);
            if (this.getPlugin().getConfiguration().isCacheComponents() && cacheKey != null && mechanic.getCooldown() == null) {
               this.mechanicCache.putIfAbsent(cacheKey, mechanic);
            }

            return mechanic;
         }
      } catch (Exception ex) {
         MythicLogger.error("Failed to load skill line due to bad syntax: {0} ", skill);
         MythicLogger.handleMinorError(ex);
         return null;
      }
   }

   private void applyMechanicOverrides(String name, MythicLineConfig config, SkillMechanic mechanic) {
      MechanicProvider[] providers = MechanicRegistry.snapshot();
      if (providers.length != 0) {
         ISkillMechanic current = mechanic;
         boolean syncOnly = false;

         for (int i = providers.length - 1; i >= 0; i--) {
            MechanicProvider provider = providers[i];

            try {
               ISkillMechanic next = provider.provideMechanic(name, config, current);
               if (next != null && next != current) {
                  if (!implementsDispatchOf(mechanic, next)) {
                     MythicLogger.error(
                        "Mechanic provider {0} returned an override of {1} that drops one of the mechanic''s skill interfaces. Ignoring it.",
                        provider.getClass().getName(),
                        name
                     );
                  } else {
                     syncOnly |= next.getThreadSafetyLevel() == ThreadSafetyLevel.SYNC_ONLY;
                     current = next;
                  }
               }
            } catch (Throwable e) {
               MythicLogger.error("Mechanic provider " + provider.getClass().getName() + " failed while overriding " + name, e);
            }
         }

         if (current != mechanic) {
            mechanic.dispatchOverride = current;
            if (syncOnly) {
               mechanic.setThreadSafetyLevel(ThreadSafetyLevel.SYNC_ONLY);
            }
         }
      }
   }

   private void applyConditionOverrides(String name, MythicLineConfig config, SkillCondition condition) {
      ConditionProvider[] providers = ConditionRegistry.snapshot();
      if (providers.length != 0) {
         ISkillCondition current = condition;

         for (int i = providers.length - 1; i >= 0; i--) {
            ConditionProvider provider = providers[i];

            try {
               ISkillCondition next = provider.provideCondition(name, config, current);
               if (next != null && next != current) {
                  if (!implementsDispatchOf(condition, next)) {
                     MythicLogger.error(
                        "Condition provider {0} returned an override of {1} that drops one of the condition''s interfaces. Ignoring it.",
                        provider.getClass().getName(),
                        name
                     );
                  } else {
                     current = next;
                  }
               }
            } catch (Throwable e) {
               MythicLogger.error("Condition provider " + provider.getClass().getName() + " failed while overriding " + name, e);
            }
         }

         if (current != condition) {
            condition.dispatchOverride = current;
         }
      }
   }

   private static boolean implementsDispatchOf(ISkillCondition original, ISkillCondition override) {
      for (Class<?> dispatchInterface : DISPATCH_CONDITION_INTERFACES) {
         if (dispatchInterface.isInstance(original) && !dispatchInterface.isInstance(override)) {
            return false;
         }
      }

      return true;
   }

   private static boolean implementsDispatchOf(ISkillMechanic original, ISkillMechanic override) {
      for (Class<?> dispatchInterface : DISPATCH_INTERFACES) {
         if (dispatchInterface.isInstance(original) && !dispatchInterface.isInstance(override)) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public File getSchematic(String schematicName) {
      PackExecutor.ConfigFile mappedSchematicFile = this.getPlugin().getPackManager().getConfigFile(ConfigFileType.SCHEMATIC, schematicName);
      if (mappedSchematicFile != null && mappedSchematicFile.file() != null && mappedSchematicFile.file().exists()) {
         return mappedSchematicFile.file();
      } else {
         for (Pack pack : this.getPlugin().getPackManager().getPacks()) {
            File schematicFolder = pack.getSchematicDirectory();
            File schematicFile = new File(schematicFolder, schematicName);
            if (schematicFile.exists()) {
               return schematicFile;
            }
         }

         File pluginsFolder = MythicBukkit.inst().getDataFolder().getParentFile();
         String[] externalSchematicFolders = new String[]{"FastAsyncWorldEdit", "WorldEdit"};

         for (String externalFolder : externalSchematicFolders) {
            File schematicFile = new File(pluginsFolder, externalFolder + "/schematics/" + schematicName);
            if (schematicFile.exists()) {
               return schematicFile;
            }
         }

         return null;
      }
   }

   public ImmutableMap<String, Class<? extends SkillTargeter>> getTargeters() {
      return ImmutableMap.copyOf(TARGETERS);
   }

   @Override
   public SkillTargeter getTargeter(String search, MythicLineConfig mlc) {
      if (search == null) {
         return null;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "? Matching MythicTargeter type to " + search);
         if (TARGETERS.containsKey(search.toUpperCase())) {
            Class<? extends SkillTargeter> clazz = TARGETERS.get(search.toUpperCase());

            try {
               return clazz.getConstructor(SkillExecutor.class, MythicLineConfig.class).newInstance(this, mlc);
            } catch (Exception e) {
               MythicLogger.error("Failed to construct targeter {0}", search);
               e.printStackTrace();
            }
         }

         if (search.substring(1, 2).equals("[")) {
            String toLowerCase = search.substring(0, 1).toLowerCase();
            switch (toLowerCase) {
               case "p":
               case "r":
               case "a":
               case "e":
                  return new VanillaTargeter(this, mlc, mlc.getLine());
            }
         }

         return new CustomTargeter(this, search.toUpperCase(), mlc);
      }
   }

   @Override
   public SkillTargeter getTargeter(String strTarget) {
      String search = strTarget.substring(1).trim();
      if (this.getPlugin().getConfiguration().isCacheComponents() && this.targeterCache.containsKey(search)) {
         return this.targeterCache.get(search);
      } else {
         MythicLineConfig mlc = new MythicLineConfigImpl(search);
         String name;
         if (search.contains("{")) {
            name = search.substring(0, search.indexOf("{"));
         } else {
            name = search;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, ": Parsing SkillTargeter {0}", search);
         SkillTargeter constructedTargeter = this.getPlugin().getSkillManager().getTargeter(name, mlc);
         if (this.getPlugin().getConfiguration().isCacheComponents()) {
            this.targeterCache.put(search, constructedTargeter);
         }

         return constructedTargeter;
      }
   }

   @Override
   public SkillMetadata processTargets(SkillMetadata data, SkillTargeter targeter) {
      return this.processTargets(data, targeter, false, false);
   }

   @Override
   public SkillMetadata processTargets(SkillMetadata data, SkillTargeter targeter, boolean targetCreative, boolean splitPower) {
      SkillCaster originalCaster = data.getCaster();
      if (data.getCaster() instanceof ActiveMob activeCaster) {
         if (targeter.isSudoParent()) {
            activeCaster.getParent().ifPresent(parent -> data.setCaster(this.getPlugin().getSkillManager().getCaster(parent)));
         }

         if (targeter.isSudoOwner()) {
            activeCaster.getOwnerUUID().ifPresent(owner -> {
               AbstractEntity ownerEntity = MythicBukkit.inst().getBootstrap().getEntity(owner);
               if (ownerEntity != null) {
                  data.setCaster(this.getPlugin().getSkillManager().getCaster(ownerEntity));
               }
            });
         }

         if (targeter.isSudoTrigger() && data.getTrigger() != null) {
            data.setCaster(this.getPlugin().getSkillManager().getCaster(data.getTrigger()));
         }
      }

      if (targeter instanceof CustomTargeter customTargeter && customTargeter.getTargeter().isPresent()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillTargeter is a CUSTOM targeter");
         targeter = customTargeter.getTargeter().get();
      }

      if (targeter instanceof IEntitySelector entitySelector) {
         try {
            data.setEntityTargets(entitySelector.getEntities(data));
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": EntityTargeter found {0} targets", data.getEntityTargets().size());
            entitySelector.filter(data, targetCreative);
         } catch (IllegalArgumentException e) {
            data.setEntityTargets(Sets.newHashSet());
            MythicLogger.handleMinorError(e);
         }
      } else if (targeter instanceof ILocationSelector locationSelector) {
         data.setLocationTargets(locationSelector.getLocations(data));
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": LocationTargeter found {0} targets", data.getLocationTargets().size());
         locationSelector.filter(data);
      } else if (targeter instanceof ConsoleTargeter) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": Targeting console");
         data.setEntityTargets(null);
         data.setLocationTargets(null);
         data.setConsoleTarget(true);
      }

      Events.call(new MythicTargetEvent(data));
      if (!data.hasTarget() && targeter.getFallback() != null) {
         return this.processTargets(data, targeter.getFallback(), targetCreative, splitPower);
      } else {
         if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty() && splitPower) {
            data.setPower(data.getPower() / data.getEntityTargets().size());
         }

         if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty() && splitPower) {
            data.setPower(data.getPower() / data.getLocationTargets().size());
         }

         data.setCaster(originalCaster);
         return data;
      }
   }

   @Override
   public AbstractLocation getLocationTarget(SkillTargeter targeter, SkillMetadata data) {
      if (targeter instanceof CustomTargeter && ((CustomTargeter)targeter).getTargeter().isPresent()) {
         targeter = ((CustomTargeter)targeter).getTargeter().get();
      }

      AbstractLocation newOrigin;
      if (targeter instanceof ILocationSelector) {
         Collection<AbstractLocation> locations = ((ILocationSelector)targeter).getLocations(data);
         if (!locations.isEmpty()) {
            int size = locations.size();
            if (size == 1) {
               newOrigin = locations.iterator().next();
            } else if (locations instanceof List<AbstractLocation> list) {
               newOrigin = list.get(ThreadLocalRandom.current().nextInt(size));
            } else {
               int idx = ThreadLocalRandom.current().nextInt(size);
               Iterator<AbstractLocation> it = locations.iterator();

               for (int i = 0; i < idx; i++) {
                  it.next();
               }

               newOrigin = it.next();
            }
         } else {
            newOrigin = null;
         }
      } else if (targeter instanceof TriggerLocationTargeter) {
         newOrigin = data.getTrigger().getLocation();
      } else if (targeter instanceof IEntityTargeter entityTargeter) {
         Collection<AbstractEntity> entities;
         if (targeter instanceof IEntitySelector entitySelector) {
            Collection<AbstractEntity> previousTargets = data.getEntityTargets();

            try {
               data.setEntityTargets(entitySelector.getEntities(data));
               entitySelector.filter(data, false);
               entities = data.getEntityTargets();
            } finally {
               data.setEntityTargets(previousTargets);
            }
         } else {
            entities = entityTargeter.getEntities(data);
         }

         newOrigin = entities != null && !entities.isEmpty() ? entities.iterator().next().getLocation() : null;
      } else {
         newOrigin = null;
      }

      return newOrigin == null && targeter.getFallback() != null ? this.getLocationTarget(targeter.getFallback(), data) : newOrigin;
   }

   public ImmutableMap<String, Class<? extends SkillCondition>> getConditions() {
      return ImmutableMap.copyOf(CONDITIONS);
   }

   @Override
   public SkillCondition getCondition(String condition) {
      condition = condition.trim();
      if (this.getPlugin().getConfiguration().isCacheComponents() && this.conditionCache.containsKey(condition)) {
         return this.conditionCache.get(condition);
      } else if (condition.startsWith("(")) {
         try {
            CompositeCondition constructedCondition = new CompositeCondition(this, condition);
            if (this.getPlugin().getConfiguration().isCacheComponents()) {
               this.conditionCache.put(condition, constructedCondition);
            }

            return constructedCondition;
         } catch (Error | Exception e) {
            MythicLogger.error("Failed to construct composite condition {0}", condition);
            e.printStackTrace();
            return new InvalidCondition(condition);
         }
      } else {
         condition = normalizeConditionLine(MythicLineConfigImpl.unparseBlock(condition));
         if (condition.contains("}")) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, ": Normalized Condition string to: " + condition);
         }

         String[] s = condition.split(" ");
         String name = null;
         MythicLineConfig mlc = new MythicLineConfigImpl(s[0]);
         if (s[0].contains("{")) {
            name = s[0].substring(0, s[0].indexOf("{"));
         } else {
            name = s[0];
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "? Matching MythicCondition type to " + name);
         if (CONDITIONS.containsKey(name.toUpperCase())) {
            Class<? extends SkillCondition> clazz = CONDITIONS.get(name.toUpperCase());

            try {
               SkillCondition constructedCondition = clazz.getConstructor(String.class, MythicLineConfig.class).newInstance(condition, mlc);
               this.applyConditionOverrides(name.toUpperCase(), mlc, constructedCondition);
               if (this.getPlugin().getConfiguration().isCacheComponents()) {
                  this.conditionCache.put(condition, constructedCondition);
               }

               return constructedCondition;
            } catch (Error | Exception ex) {
               MythicLogger.error("Failed to construct condition {0}", condition);
               ex.printStackTrace();
               return new InvalidCondition(condition);
            }
         } else {
            try {
               return new CustomCondition(name, condition, mlc);
            } catch (Error | Exception ex2) {
               MythicLogger.error("Failed to load condition '" + name + "'");
               ex2.printStackTrace();
               return new InvalidCondition(condition);
            }
         }
      }
   }

   static String normalizeConditionLine(String condition) {
      int firstOpen = condition.indexOf("{");
      int firstClose = condition.indexOf("}");
      String beforeOpen = firstOpen > -1 ? condition.substring(0, firstOpen).trim() : "";
      if (firstOpen > -1 && firstClose > firstOpen && !beforeOpen.contains(" ")) {
         String sp1 = condition.substring(0, firstClose);
         String sp2 = condition.substring(firstClose);
         return sp1.replace(" ", "") + sp2;
      } else {
         return condition;
      }
   }

   @Override
   public List<SkillCondition> getConditions(List<String> block) {
      List<SkillCondition> conditions = null;

      for (String s : block) {
         s = MythicLineConfigImpl.unparseBlock(s);
         SkillCondition sc = this.getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (conditions == null) {
               conditions = new ArrayList<>();
            }

            conditions.add(sc);
         }
      }

      return conditions;
   }

   @Override
   public List<SkillCondition> getConditions(String block) {
      if (block.startsWith("[") && block.endsWith("]")) {
         block = block.substring(1, block.length() - 1);
         block = MythicLineConfigImpl.unparseBlock(block);
         String[] split = block.split("-");
         List<String> elements = new ArrayList<>();

         for (String e : split) {
            if (e.trim().length() != 0) {
               elements.add(e.trim());
            }
         }

         return this.getConditions(elements);
      } else {
         return null;
      }
   }

   public ImmutableMap<String, Class<? extends SkillAudience>> getAudiences() {
      return ImmutableMap.copyOf(AUDIENCES);
   }

   public ImmutableMap<String, Class<? extends AuraComponentFactory>> getAuraComponents() {
      return ImmutableMap.copyOf(AURACOMPONENTS);
   }

   public AuraComponentFactory getAuraComponentFactory(String name, AuraMechanic mechanic, MythicLineConfig mlc) {
      if (name == null) {
         return null;
      } else {
         Class<? extends AuraComponentFactory> clazz = AURACOMPONENTS.get(name.toUpperCase());
         if (clazz == null) {
            return null;
         } else {
            try {
               return clazz.getConstructor(AuraMechanic.class, MythicLineConfig.class).newInstance(mechanic, mlc);
            } catch (Exception e) {
               MythicLogger.error("Failed to construct aura component {0}", name);
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   @Override
   public SkillAudience getAudience(String line) {
      if (line == null) {
         return null;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "? Matching Audience type to " + line);
         MythicLineConfig mlc = new MythicLineConfigImpl(line);
         if (line.startsWith("@")) {
            return new TargeterAudience(mlc, line);
         } else {
            String search;
            if (line.contains("{")) {
               search = line.substring(0, line.indexOf("{"));
            } else {
               search = line;
            }

            if (AUDIENCES.containsKey(search.toUpperCase())) {
               Class<? extends SkillAudience> clazz = AUDIENCES.get(search.toUpperCase());

               try {
                  return clazz.getConstructor(MythicLineConfig.class).newInstance(mlc);
               } catch (Exception e) {
                  MythicLogger.error("Failed to construct audience {0}", search);
                  e.printStackTrace();
               }
            }

            try {
               return new CustomAudience(search, mlc);
            } catch (Error | Exception ex) {
               MythicLogger.error("Failed to load audience '" + search + "'");
               ex.printStackTrace();
               return new InvalidAudience(mlc);
            }
         }
      }
   }

   private final void loadSkillComponents() {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.conditions.all", MythicCondition.class)) {
         try {
            String name = clazz.getAnnotation(MythicCondition.class).name();
            String[] aliases = clazz.getAnnotation(MythicCondition.class).aliases();
            if (SkillCondition.class.isAssignableFrom(clazz)) {
               CONDITIONS.put(name.toUpperCase(), (Class<? extends SkillCondition>)clazz);

               for (String alias : aliases) {
                  CONDITIONS.put(alias.toUpperCase(), (Class<? extends SkillCondition>)clazz);
               }
            }
         } catch (Exception e) {
            MythicLogger.error("Failed to load condition {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.mechanics", MythicMechanic.class)) {
         try {
            String name = clazz.getAnnotation(MythicMechanic.class).name();
            String[] aliases = clazz.getAnnotation(MythicMechanic.class).aliases();
            if (SkillMechanic.class.isAssignableFrom(clazz)) {
               MECHANICS.put(name.toUpperCase(), (Class<? extends SkillMechanic>)clazz);

               for (String alias : aliases) {
                  MECHANICS.put(alias.toUpperCase(), (Class<? extends SkillMechanic>)clazz);
               }
            }
         } catch (Exception ex) {
            MythicLogger.error("Failed to load mechanic {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.targeters", MythicTargeter.class)) {
         try {
            String name = clazz.getAnnotation(MythicTargeter.class).name();
            String[] aliases = clazz.getAnnotation(MythicTargeter.class).aliases();
            if (SkillTargeter.class.isAssignableFrom(clazz)) {
               TARGETERS.put(name.toUpperCase(), (Class<? extends SkillTargeter>)clazz);

               for (String alias : aliases) {
                  TARGETERS.put(alias.toUpperCase(), (Class<? extends SkillTargeter>)clazz);
               }
            }
         } catch (Exception ex2) {
            MythicLogger.error("Failed to load targeter {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.audience", MythicAudience.class)) {
         try {
            String name = clazz.getAnnotation(MythicAudience.class).name();
            String[] aliases = clazz.getAnnotation(MythicAudience.class).aliases();
            if (SkillAudience.class.isAssignableFrom(clazz)) {
               AUDIENCES.put(name.toUpperCase(), (Class<? extends SkillAudience>)clazz);

               for (String alias : aliases) {
                  AUDIENCES.put(alias.toUpperCase(), (Class<? extends SkillAudience>)clazz);
               }
            }
         } catch (Exception ex3) {
            MythicLogger.error("Failed to load audience {0}", clazz.getCanonicalName());
         }
      }

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.auras.components", MythicAuraComponent.class)) {
         try {
            String name = clazz.getAnnotation(MythicAuraComponent.class).name();
            String[] aliases = clazz.getAnnotation(MythicAuraComponent.class).aliases();
            if (AuraComponentFactory.class.isAssignableFrom(clazz)) {
               AURACOMPONENTS.put(name.toUpperCase(), (Class<? extends AuraComponentFactory>)clazz);

               for (String alias : aliases) {
                  AURACOMPONENTS.put(alias.toUpperCase(), (Class<? extends AuraComponentFactory>)clazz);
               }
            }
         } catch (Exception ex4) {
            MythicLogger.error("Failed to load aura component {0}", clazz.getCanonicalName());
         }
      }
   }
}
