package io.lumine.mythic.core.skills;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.tasks.TaskScheduler;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.cooldowns.CooldownMode;
import io.lumine.mythic.core.skills.mechanics.CastMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.SudoSkillMechanic;
import io.lumine.mythic.core.skills.targeters.IPathSelector;
import io.lumine.mythic.core.skills.targeters.MPEntity;
import io.lumine.mythic.core.skills.targeters.MPLocation;
import io.lumine.mythic.core.skills.triggers.meta.CancellableTriggerMetadata;
import io.lumine.mythic.core.skills.variables.DeferredVariableRegistry;
import io.lumine.mythic.core.skills.variables.IterationVariableRegistry;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class SkillMechanic extends AbstractSkill implements ISkillMechanic {
   @Getter
   protected final MythicLineConfig config;
   @Getter
   protected Optional<SkillHolder> parent = Optional.empty();
   protected int interval = 0;
   protected volatile ISkillMechanic dispatchOverride = null;
   protected String line;
   @Getter @Setter
   protected SkillTrigger trigger = SkillTriggers.COMBAT;
   @Getter
   protected boolean hasTriggerDefined = false;
   private List<SkillMechanic.SkillHealthConditional> healthConditions;
   @MythicField(
      name = "forceSync",
      aliases = {"sync"},
      description = "Sets the execution of the mechanic to the main thread instead of async."
   )
   protected boolean forceSync = false;
   protected boolean executeAfterDeath = false;
   @MythicField(
      name = "allowCancelled",
      aliases = {"skipCancelled", "allowCancelledDamaged", "legacyOnDamaged", "skipCancelledDamaged", "scd"},
      defValue = "false",
      description = "Allows the mechanic to run even if the backing event was cancelled."
   )
   protected boolean allowCancelled = false;
   @MythicField(
      name = "targetIsOrigin",
      description = "Sets the origin to the target's location instead of the caster's"
   )
   protected boolean targetIsOrigin = false;
   @MythicField(
      name = "origin",
      description = "Sets the origin of the skill",
      premium = true
   )
   protected Optional<SkillTargeter> originOverride;
   private static final ConcurrentHashMap<Class<?>, String> typeNameCache = new ConcurrentHashMap<>();

   public SkillMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc, int interval) {
      super(manager, file);
      this.line = line;
      this.config = mlc;
      this.interval = interval
         + (this.getPlugin().getConfiguration().getClockIntervalMain() - interval % this.getPlugin().getConfiguration().getClockIntervalMain());
      this.init(line, mlc);
   }

   public SkillMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file);
      this.line = line;
      this.config = mlc;
      this.init(line, mlc);
   }

   @Deprecated
   public SkillMechanic(SkillExecutor manager, String line, MythicLineConfig mlc) {
      this(manager, null, line, mlc);
   }

   public SkillMechanic(SkillExecutor manager, File file) {
      super(manager, file);
      this.config = null;
      this.line = "[internal]";
      this.originOverride = Optional.empty();
   }

   private void init(String line, MythicLineConfig mlc) {
      this.parseSharedCooldownConfig(mlc);
      this.delay = mlc.getPlaceholderDuration("delay", DurationUnit.TICKS, "0");
      this.targetInterval = mlc.getPlaceholderDuration(new String[]{"targetinterval", "targeti"}, DurationUnit.TICKS, "0");
      this.repeat = mlc.getPlaceholderInteger("repeat", 0);
      this.repeatInterval = mlc.getPlaceholderDuration(new String[]{"repeatinterval", "repeati"}, DurationUnit.TICKS, "0");
      this.power = mlc.getFloat("power", 1.0F);
      this.powerSplitBetweenTargets = mlc.getBoolean(new String[]{"powersplitbetweentargets", "powersplit", "splitpower"}, false);
      this.forceSync = mlc.getBoolean(new String[]{"forcesync", "sync"}, false);
      this.allowCancelled = mlc.getBoolean(
         new String[]{"allowcancelled", "skipcancelled", "allowcancelleddamaged", "legacyondamaged", "skiponcancelleddamaged", "skipcancelleddamaged", "scd"},
         false
      );
      this.targetIsOrigin = mlc.getBoolean("targetisorigin", false);
      this.sourceIsOrigin = mlc.getBoolean(new String[]{"sourceisorigin", "castfromorigin", "fromorigin", "fo"}, false);
      this.faulty = mlc.getBoolean(new String[]{"faulty"}, true);
      String originOverride = mlc.getString("origin", null);
      if (originOverride != null && MythicBukkit.isVolatile()) {
         this.originOverride = Optional.of(this.parseSkillTargeter(originOverride));
      } else {
         this.originOverride = Optional.empty();
      }

      this.target_creative = mlc.getBoolean("targetcreative", this.target_creative);
      this.chance = mlc.getPlaceholderFloat("chance", 1.0F);
      if (line != null) {
         List<String> split = MythicUtil.splitIgnoringParentheses(line);

         for (int i = 1; i < split.size(); i++) {
            if (split.get(i).startsWith("@")) {
               this.targeter = Optional.of(this.parseSkillTargeter(split.get(i)));
               this.targeter.ifPresent(tar -> tar.setParent(this));
            } else if (split.get(i).startsWith("~")) {
               this.trigger = this.parseSkillTrigger(split.get(i));
               this.hasTriggerDefined = true;
               if (this.trigger == SkillTriggers.DAMAGED && this.hasAllowCancelledDamagedTriggerFlag(split.get(i))) {
                  this.allowCancelled = true;
               }
            } else if (split.get(i).startsWith("=") || split.get(i).startsWith(">") || split.get(i).startsWith("<") && !split.get(i).endsWith(">")) {
               this.healthConditions = new ArrayList<>();

               for (String part : split.get(i).split(",")) {
                  this.healthConditions.add(this.parseHealthConditional(part.trim()));
               }
            } else if (split.get(i).startsWith("?~")) {
               if (this.conditionsTrigger == null) {
                  this.conditionsTrigger = new ObjectArrayList();
               }

               SkillCondition cond = this.parseSkillCondition(split.get(i).substring(1));
               cond.setParent(this);
               this.conditionsTrigger.add(cond);
            } else if (split.get(i).startsWith("?@")) {
               if (this.conditionsTarget == null) {
                  this.conditionsTarget = new ObjectArrayList();
               }

               SkillCondition cond = this.parseSkillCondition(split.get(i).substring(1));
               cond.setParent(this);
               this.conditionsTarget.add(cond);
            } else if (split.get(i).startsWith("?")) {
               if (this.conditions == null) {
                  this.conditions = new ObjectArrayList();
               }

               SkillCondition cond = this.parseSkillCondition(split.get(i));
               cond.setParent(this);
               this.conditions.add(cond);
            } else if (split.get(i).matches("[0-9]*[.]?[0-9]+")) {
               this.chance = PlaceholderFloat.of(split.get(i));
            }
         }

         if (this.trigger == null) {
            this.trigger = SkillTriggers.COMBAT;
            this.hasTriggerDefined = false;
         }
      }
   }

   private void parseSharedCooldownConfig(MythicLineConfig mlc) {
      String cd = mlc.getString(new String[]{"cooldown", "cd"}, null);
      if (cd != null) {
         this.cooldown = PlaceholderDuration.of(cd, DurationUnit.SECONDS);
      }

      this.configureChargeCooldown(
         mlc.getInteger(new String[]{"charges"}, this.charges),
         mlc.getEnum(new String[]{"cooldownmode", "chargecooldownmode"}, CooldownMode.class, this.cooldownMode)
      );
   }

   @Override
   public String getInternalName() {
      return this.parent.isPresent() ? this.parent.get().getInternalName() + ":" + this.getTypeName() : "Unknown:" + this.getTypeName();
   }

   public String getTypeName() {
      if (this instanceof CustomMechanic customMechanic) {
         ISkillMechanic mechanic = customMechanic.getMechanic().orElse(null);
         if (mechanic == null) {
            return "CustomMechanic:NULL";
         } else {
            Class<? extends ISkillMechanic> clazz = (Class<? extends ISkillMechanic>)mechanic.getClass();
            return typeNameCache.computeIfAbsent(
               clazz,
               c -> c.isAnnotationPresent(MythicMechanic.class)
                  ? "CustomMechanic:" + c.getAnnotation(MythicMechanic.class).name()
                  : "CustomMechanic:" + c.getSimpleName()
            );
         }
      } else {
         Class<? extends SkillMechanic> clazz = (Class<? extends SkillMechanic>)this.getClass();
         return typeNameCache.computeIfAbsent(
            clazz, c -> c.isAnnotationPresent(MythicMechanic.class) ? c.getAnnotation(MythicMechanic.class).name() : c.getSimpleName()
         );
      }
   }

   public String getConfigLine() {
      return this.line;
   }

   public void setParent(SkillHolder parent) {
      this.parent = Optional.ofNullable(parent);
   }

   public ISkillMechanic getDispatchTarget() {
      ISkillMechanic override = this.dispatchOverride;
      return (override != null ? override : this);
   }

   public void setAsyncSafe(boolean bool) {
      if (bool) {
         this.threadSafetyLevel = ThreadSafetyLevel.EITHER;
      } else {
         this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      }
   }

   public boolean isAsyncSafe() {
      return this.threadSafetyLevel != ThreadSafetyLevel.SYNC_ONLY;
   }

   public void setTimerInterval(int interval) {
      this.interval = interval;
   }

   public int getTimerInterval() {
      return this.interval;
   }

   protected boolean checkHealth(SkillCaster caster) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "? Performing Health Check");
      if (this.healthConditions != null && !this.healthConditions.isEmpty()) {
         if (caster instanceof ActiveMob am) {
            for (SkillMechanic.SkillHealthConditional cond : this.healthConditions) {
               if (cond.matches(am)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   protected boolean checkSkillTrigger(SkillMetadata meta) {
      SkillTrigger trigger = meta.getCause();
      if (trigger == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "+ Provided SkillTrigger is null");
         return true;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "? Checking SkillTrigger {0} == {1}", trigger, this.trigger);
         if (trigger.equals(SkillTriggers.SIGNAL) || trigger.equals(SkillTriggers.API)) {
            return true;
         } else if (SkillTriggers.TIMER.equals(this.trigger)) {
            return true;
         } else if (SkillTriggers.SIGNAL.equals(this.trigger)) {
            return true;
         } else if (SkillTriggers.SPAWN_OR_LOAD.equals(this.trigger)) {
            return trigger.equals(SkillTriggers.SPAWN) || trigger.equals(SkillTriggers.LOAD);
         } else if (SkillTriggers.COMBAT.equals(this.trigger)) {
            return trigger.equals(SkillTriggers.DAMAGED) || trigger.equals(SkillTriggers.ATTACK);
         } else if (SkillTriggers.KILL.equals(this.trigger)) {
            return trigger.equals(SkillTriggers.KILL) || trigger.equals(SkillTriggers.KILLPLAYER);
         } else {
            return !SkillTriggers.DEFAULT.equals(this.trigger)
               ? this.trigger.equals(trigger)
               : trigger.equals(SkillTriggers.DAMAGED)
                  || trigger.equals(SkillTriggers.ATTACK)
                  || trigger.equals(SkillTriggers.SPAWN)
                  || trigger.equals(SkillTriggers.LOAD)
                  || trigger.equals(SkillTriggers.DEATH)
                  || trigger.equals(SkillTriggers.TIMER);
         }
      }
   }

   protected SkillTrigger parseSkillTrigger(String strTrigger) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, ": Parsing SkillTrigger {0}", strTrigger);
      int end = strTrigger.length();
      int colon = strTrigger.indexOf(":");
      int brace = strTrigger.indexOf("{");
      if (colon >= 0) {
         end = Math.min(end, colon);
      }

      if (brace >= 0) {
         end = Math.min(end, brace);
      }

      String search = strTrigger.substring(3, end).toUpperCase();
      return SkillTrigger.get(search);
   }

   public boolean isUsableFromSkill(SkillMetadata meta) {
      SkillCaster skillCaster = meta.getCaster();
      if (this.onCooldown(skillCaster)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Cooldown check failed.");
         return false;
      } else if (skillCaster instanceof ActiveMob && !this.checkHealth(skillCaster)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Health check failed.");
         return false;
      } else if (!this.rollChance(meta)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Roll failed.");
         return false;
      } else {
         if (this.conditionsTrigger != null) {
            for (SkillCondition mc : this.conditionsTrigger) {
               if (!mc.evaluateTrigger(meta)) {
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: TriggerCondition {0} failed", mc.getTypeName());
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         if (this.conditions != null) {
            for (SkillCondition mc : this.conditions) {
               if (!mc.evaluateCaster(meta)) {
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Condition {0} failed", mc.getTypeName());
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.SKILL_CHECK)) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "+ SkillMechanic {0} is usable!", this.getTypeName());
         }

         return true;
      }
   }

   public boolean isUsableFromCaster(SkillMetadata meta) {
      SkillCaster skillCaster = meta.getCaster();
      if (!this.checkSkillTrigger(meta)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: SkillTrigger check failed.");
         return false;
      } else if (!this.allowCancelled && meta.getCause() == SkillTriggers.DAMAGED && this.isDamagedEventCancelled(meta)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Damaged event was already cancelled and default skip is active.");
         return false;
      } else if (this.onCooldown(skillCaster)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Cooldown check failed.");
         return false;
      } else if (skillCaster instanceof ActiveMob && !this.checkHealth(skillCaster)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Health check failed.");
         return false;
      } else if (!this.rollChance(meta)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Roll failed.");
         return false;
      } else {
         if (this.conditionsTrigger != null) {
            for (SkillCondition mc : this.conditionsTrigger) {
               if (!mc.evaluateTrigger(meta)) {
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: TriggerCondition {0} failed", mc.getTypeName());
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         if (this.conditions != null) {
            for (SkillCondition mc : this.conditions) {
               if (!mc.evaluateCaster(meta)) {
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Condition {0} failed", mc.getTypeName());
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "+ SkillMechanic usable!");
         return true;
      }
   }

   private boolean hasAllowCancelledDamagedTriggerFlag(String triggerToken) {
      int index = triggerToken.indexOf(58);
      if (index != -1 && index != triggerToken.length() - 1) {
         String attributes = triggerToken.substring(index + 1);

         for (String attribute : attributes.split(",")) {
            String parsed = attribute.trim().toLowerCase(Locale.ROOT);
            if (!parsed.isEmpty()) {
               if (parsed.equals("allowcancelled")
                  || parsed.equals("allowifcancelled")
                  || parsed.equals("allowcancelleddamaged")
                  || parsed.equals("legacyondamaged")
                  || parsed.equals("skipcancelled")
                  || parsed.equals("skipifcancelled")
                  || parsed.equals("skiponcancelleddamaged")
                  || parsed.equals("scd")) {
                  return true;
               }

               if (parsed.startsWith("allowcancelled=")
                  || parsed.startsWith("allowifcancelled=")
                  || parsed.startsWith("allowcancelleddamaged=")
                  || parsed.startsWith("legacyondamaged=")
                  || parsed.startsWith("skipcancelled=")
                  || parsed.startsWith("skipifcancelled=")
                  || parsed.startsWith("skiponcancelleddamaged=")
                  || parsed.startsWith("scd=")) {
                  int valueIndex = parsed.indexOf(61);
                  if (valueIndex != -1 && valueIndex < parsed.length() - 1) {
                     String value = parsed.substring(valueIndex + 1).trim();
                     if (value.equals("true") || value.equals("yes") || value.equals("1")) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isDamagedEventCancelled(SkillMetadata meta) {
      return CancellableTriggerMetadata.isEventCancelled(meta);
   }

   public boolean execute(SkillMetadata oData) {
      final SkillMetadata data = oData.deepClone();
      if (this.originOverride.isPresent()) {
         data.setOrigin(this.getPlugin().getSkillManager().getLocationTarget(this.originOverride.get(), oData));
      }

      final int delay = this.delay.get(oData).getTicks();
      double targetInterval = this.targetInterval.get(oData).getTicks();
      final int repeat = this.repeat.get(oData);
      final int repeatInterval = this.repeatInterval.get(oData).getTicks();
      if (repeat > 0 && repeatInterval > 0) {
         final SkillMechanic skill = this;
         new Runnable() {
            private int ticks = 0;
            private final int repeats = repeat;
            private final Task task = SkillMechanic.casterScheduler(data).runRepeating(this, (long)delay, (long)repeatInterval);

            @Override
            public void run() {
               this.ticks++;

               try {
                  SkillMetadata meta = data.deepClone();
                  meta.setVariables(IterationVariableRegistry.ofIteration(meta.getVariables(), this.ticks));
                  skill.executeSkills(meta);
                  if (this.ticks > this.repeats) {
                     this.task.terminate();
                  }
               } catch (Error | Exception e) {
                  e.printStackTrace();
                  this.task.terminate();
               }
            }
         };
         return true;
      } else if (repeat > 0 && repeatInterval == 0) {
         for (int i = 1; i <= repeat; i++) {
            try {
               SkillMetadata meta = data.deepClone();
               meta.setVariables(IterationVariableRegistry.ofIteration(meta.getVariables(), i));
               this.executeSkills(meta);
            } catch (Error | Exception ex) {
               ex.printStackTrace();
            }
         }

         return true;
      } else if (!(targetInterval > 0.0)) {
         if (delay == 0) {
            return this.executeSkills(data);
         } else {
            casterScheduler(data).runLater(() -> this.executeSkills(data), delay);
            return true;
         }
      } else {
         List<AbstractEntity> entityTargets = (oData.getEntityTargets().isEmpty()
            ? Collections.<AbstractEntity>emptyList()
            : new ObjectArrayList<AbstractEntity>(oData.getEntityTargets()));
         List<AbstractLocation> locationTargets = (oData.getLocationTargets().isEmpty()
            ? Collections.<AbstractLocation>emptyList()
            : new ObjectArrayList<AbstractLocation>(oData.getLocationTargets()));
         int intervalDelay = targetInterval < 1.0 ? 1 : (int)targetInterval;
         int amountPerTick = targetInterval >= 1.0 ? 1 : (int)(1.0 / targetInterval);
         int j = 1;
         int entityCount = entityTargets.size();

         for (int entityIdx = 0; entityIdx < entityCount; j++) {
            int remaining = entityCount - entityIdx;
            if (remaining < amountPerTick) {
               amountPerTick = remaining;
            }

            ObjectArrayList<AbstractEntity> thisCastTargets = new ObjectArrayList(amountPerTick);

            for (int i = 0; i < amountPerTick; i++) {
               thisCastTargets.add(entityTargets.get(entityIdx + i));
            }

            entityIdx += amountPerTick;
            SkillMetadata thisData = data.deepCloneNoTargets();
            thisData.setEntityTargets(thisCastTargets);
            thisData.getLocationTargets().clear();
            casterScheduler(data).runLater(() -> this.executeSkills(thisData), (long)j * intervalDelay);
         }

         j = 1;
         int locationCount = locationTargets.size();

         for (int locationIdx = 0; locationIdx < locationCount; j++) {
            int remaining = locationCount - locationIdx;
            if (remaining < amountPerTick) {
               amountPerTick = remaining;
            }

            ObjectArrayList<AbstractLocation> thisCastTargets = new ObjectArrayList(amountPerTick);

            for (int i = 0; i < amountPerTick; i++) {
               thisCastTargets.add(locationTargets.get(locationIdx + i));
            }

            locationIdx += amountPerTick;
            SkillMetadata thisData = data.deepCloneNoTargets();
            thisData.setLocationTargets(thisCastTargets);
            thisData.getEntityTargets().clear();
            casterScheduler(data).runLater(() -> this.executeSkills(thisData), (long)j * intervalDelay);
         }

         return true;
      }
   }

   public boolean executeSkills(
      SkillTrigger skilltrigger,
      ActiveMob am,
      AbstractEntity trigger,
      AbstractLocation origin,
      HashSet<AbstractEntity> eTargets,
      HashSet<AbstractLocation> lTargets,
      float power
   ) {
      SkillMetadata data = new SkillMetadataImpl(skilltrigger, am, trigger, origin, eTargets, lTargets, power);
      return this.executeSkills(data);
   }

   public boolean executeSkills(SkillMetadata data) {
      if (!data.getExecuteAfterDeath()) {
         if (this.executeAfterDeath) {
            data.setExecuteAfterDeath(true);
         } else if (data.getCaster() instanceof ActiveMob am && am.isDead()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Caster is dead, cancelling execution (line: {0})", this.line);
            return false;
         }
      }

      if (data.getOrigin() == null) {
         data.setOrigin(data.getCaster().getEntity().getLocation());
      }

      data.setPower(data.getPower() * this.power);
      Object cooldownLock = this.tryAcquireCooldownLock(data);
      if (this.isCooldownLockFailed(cooldownLock)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Cooldown lock failed.");
         return false;
      } else {
         try {
            if (MythicLogger.isDebugEnabled(MythicLogger.DebugLevel.SKILL_CHECK)) {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.SKILL_CHECK, "Executing SkillMechanic {0} with power {1} (line: {2})", this.getTypeName(), data.getPower(), this.line
               );
            }

            this.evaluateTargets(data);
            if (this.conditionsTarget != null) {
               for (SkillCondition mc : this.conditionsTarget) {
                  if (!mc.evaluateTargets(data)) {
                     mc.getOnFailSkill().ifPresent(s -> s.execute(data));
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: TargetCondition {0} failed", mc.getTypeName());
                     return false;
                  }

                  mc.getOnPassSkill().ifPresent(s -> s.execute(data));
               }
            }

            if (!(this instanceof SudoSkillMechanic)) {
               ISkillMechanic override = this.dispatchOverride;
               ISkillMechanic mechanic;
               if (override != null) {
                  mechanic = override;
               } else if (this instanceof CustomMechanic && ((CustomMechanic)this).getMechanic().isPresent()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic is a CUSTOM mechanic");
                  mechanic = ((CustomMechanic)this).getMechanic().get();
               } else {
                  mechanic = this;
               }

               if (mechanic instanceof IMetaSkill metaSkill) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic is a META mechanic. Executing...");
                  if (this.consumeExecutionCharge(data)) {
                     metaSkill.cast(data);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  if (mechanic instanceof ITargetedEntitySkill && mechanic instanceof ITargetedLocationSkill) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic accepts multiple types...");
                     if (this.targeter.isPresent() && this.targeter.get() instanceof IPathSelector) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Targeter is PATH SELECTOR");
                        if (this.targeter.get() instanceof MPEntity) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic set as ENTITY skill. Executing...");
                           if (this.consumeExecutionCharge(data)) {
                              executeTargetedEntitySkill(mechanic, data, this.targetIsOrigin);
                              return true;
                           }

                           return false;
                        }

                        if (this.targeter.get() instanceof MPLocation) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic set as LOCATION skill. Executing...");
                           if (this.consumeExecutionCharge(data)) {
                              executeTargetedLocationSkill(mechanic, data, this.targetIsOrigin);
                              return true;
                           }

                           return false;
                        }
                     }
                  }

                  if (mechanic instanceof ITargetedEntitySkill) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Mechanic is ITargetedEntitySkill");
                     if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
                        MythicLogger.debug(
                           MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic {0} is an ENTITY skill. Executing...", mechanic.getClass().getName()
                        );
                        if (this.consumeExecutionCharge(data)) {
                           this.runMechanic(data, () -> executeTargetedEntitySkill(mechanic, data, this.targetIsOrigin));
                           return true;
                        }

                        return false;
                     }
                  }

                  if (mechanic instanceof ITargetedLocationSkill) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Mechanic is ITargetedLocationSkill");
                     if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": Executing SkillMechanic as LOCATION skill");
                        if (this.consumeExecutionCharge(data)) {
                           this.runMechanic(data, () -> executeTargetedLocationSkill(mechanic, data, this.targetIsOrigin));
                           return true;
                        }

                        return false;
                     }

                     if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": Executing SkillMechanic as ENTITY_LOCATION skill");
                        if (this.consumeExecutionCharge(data)) {
                           this.runMechanic(data, () -> executeTargetedLocationSkill(mechanic, data, this.targetIsOrigin));
                           return true;
                        }

                        return false;
                     }
                  }

                  if (!(mechanic instanceof INoTargetSkill)) {
                     if (mechanic instanceof CastMechanic castMechanic) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "- SkillMechanic is a CAST mechanic. Executing NoTargets fallback.");
                        castMechanic.failNoTargets(data);
                     }

                     MythicLogger.debug(
                        MythicLogger.DebugLevel.SKILL_INFO, "! No applicable targets available for mechanic {0}. Cancelling.", this.getTypeName()
                     );
                     return false;
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": SkillMechanic runnable with no targets. Executing...");
                     if (this.consumeExecutionCharge(data)) {
                        this.runMechanic(data, () -> executeNoTargetSkill(mechanic, data));
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            } else if (this.consumeExecutionCharge(data)) {
               ((SudoSkillMechanic)this).cast(data);
               return true;
            } else {
               return false;
            }
         } finally {
            this.releaseCooldownLock(data, cooldownLock);
         }
      }
   }

   private boolean consumeExecutionCharge(SkillMetadata data) {
      if (this.consumeCharge(data)) {
         return true;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! SkillMechanic not usable: Cooldown check failed.");
         return false;
      }
   }

   private void runMechanic(SkillMetadata data, Runnable run) {
      AbstractEntity abstractEntity = data.getCaster().getEntity();
      Entity casterEntity = abstractEntity == null ? null : abstractEntity.getBukkitEntity();
      boolean offOwningThread = ServerVersion.isFolia() ? casterEntity == null || !FoliaHelper.isOwnedByCurrentRegion(casterEntity) : !Bukkit.isPrimaryThread();
      if (!data.getIsAsync() && !offOwningThread || this.threadSafetyLevel != ThreadSafetyLevel.SYNC_ONLY && !this.forceSync) {
         if ((!data.getIsAsync() || !offOwningThread) && this.threadSafetyLevel == ThreadSafetyLevel.ASYNC_ONLY) {
            Schedulers.async().run(bindVariables(data, run));
         } else {
            run.run();
         }
      } else {
         (casterEntity != null ? Schedulers.of(casterEntity) : Schedulers.sync()).run(bindVariablesForLaterRun(data, run));
      }
   }

   protected static Runnable bindVariablesForLaterRun(SkillMetadata data, Runnable run) {
      return !ServerVersion.isFolia() && Bukkit.isPrimaryThread() ? run : bindVariables(data, run);
   }

   private static Runnable bindVariables(SkillMetadata data, Runnable run) {
      DeferredVariableRegistry bound = DeferredVariableRegistry.of(data.getVariables());
      data.setVariables(bound);
      return () -> {
         try {
            run.run();
         } finally {
            bound.release();
         }
      };
   }

   protected static TaskScheduler casterScheduler(SkillMetadata data) {
      AbstractEntity abstractEntity = data.getCaster().getEntity();
      Entity entity = abstractEntity == null ? null : abstractEntity.getBukkitEntity();
      return entity != null ? Schedulers.of(entity) : Schedulers.sync();
   }

   protected static void executeTargetedEntitySkill(ISkillMechanic mechanic, SkillMetadata data, boolean targetIsOrigin) {
      ArrayList<AbstractEntity> targets = Lists.newArrayList(data.getEntityTargets());
      targets.forEach(target -> {
         if (targetIsOrigin) {
            ((ITargetedEntitySkill)mechanic).castAtEntity(data.deepClone().setOrigin(target.getLocation()), target);
         } else {
            ((ITargetedEntitySkill)mechanic).castAtEntity(data, target);
         }
      });
   }

   protected static void executeTargetedLocationSkill(ISkillMechanic mechanic, SkillMetadata data, boolean targetIsOrigin) {
      if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty()) {
         data.getLocationTargets().forEach(target -> {
            if (targetIsOrigin) {
               ((ITargetedLocationSkill)mechanic).castAtLocation(data.deepClone().setOrigin(target), target);
            } else {
               ((ITargetedLocationSkill)mechanic).castAtLocation(data, target);
            }
         });
      } else if (data.getEntityTargets() != null) {
         data.getEntityTargets().forEach(entity -> {
            AbstractLocation target = entity.getLocation();
            if (targetIsOrigin) {
               ((ITargetedLocationSkill)mechanic).castAtLocation(data.deepClone().setOrigin(target), target);
            } else {
               ((ITargetedLocationSkill)mechanic).castAtLocation(data, target);
            }
         });
      }
   }

   protected static void executeNoTargetSkill(ISkillMechanic mechanic, SkillMetadata data) {
      ((INoTargetSkill)mechanic).cast(data);
   }

   public boolean getRunAsync() {
      return !this.forceSync;
   }

   protected ActiveMob resolveActiveMob(SkillMetadata data) {
      if (data.getCaster() instanceof ActiveMob activeMob) {
         return activeMob;
      } else {
         if (data.getTrigger() != null) {
            if (this.getPlugin().getSkillManager().getCaster(data.getTrigger()) instanceof ActiveMob activeMob) {
               return activeMob;
            }

            MobExecutor mobManager = this.getPlugin().getMobManager();
            Optional<ActiveMob> activeMob = mobManager.getActiveMob(data.getTrigger().getUniqueId());
            if (activeMob.isPresent()) {
               return activeMob.get();
            }
         }

         return null;
      }
   }

   private SkillMechanic.SkillHealthConditional parseHealthConditional(String raw) {
      boolean isPercent = raw.endsWith("%");
      String trimmed = raw.trim();
      String core = trimmed.replaceAll("[%<>=]", "");
      if (trimmed.startsWith("=") && core.contains("-")) {
         String[] parts = core.split("-", 2);
         return new SkillMechanic.RangeConditional(trimmed, parts[0], parts[1], isPercent);
      } else {
         double value = Double.parseDouble(core);
         if (trimmed.startsWith(">")) {
            return new SkillMechanic.GreaterThanConditional(trimmed, value, isPercent);
         } else if (trimmed.startsWith("<")) {
            return new SkillMechanic.LessThanConditional(trimmed, value, isPercent);
         } else {
            return (trimmed.startsWith("=")
               ? new SkillMechanic.EqualConditional(trimmed, value, isPercent)
               : new SkillMechanic.AlwaysTrueConditional(trimmed));
         }
      }
   }

   private class AlwaysTrueConditional extends SkillMechanic.SkillHealthConditional {
      AlwaysTrueConditional(String expr) {
         super(expr);
      }

      @Override
      public boolean matches(ActiveMob mob) {
         return true;
      }
   }

   private class EqualConditional extends SkillMechanic.SkillHealthConditional {
      private final double rawValue;
      private final boolean percent;

      EqualConditional(String expr, double rawValue, boolean percent) {
         super(expr);
         this.rawValue = rawValue;
         this.percent = percent;
      }

      @Override
      public boolean matches(ActiveMob mob) {
         if (mob.hasMechanicUsedHealthCheck(this.id)) {
            return false;
         } else {
            double current = mob.getEntity().getHealth();
            double max = mob.getEntity().getMaxHealth();
            double frac = current / max;
            double threshold = this.percent ? this.rawValue / 100.0 : this.rawValue / max;
            if (frac <= threshold) {
               mob.markMechanicUsedHealthCheck(this.id);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private class GreaterThanConditional extends SkillMechanic.SkillHealthConditional {
      private final double rawValue;
      private final boolean percent;

      GreaterThanConditional(String expr, double rawValue, boolean percent) {
         super(expr);
         this.rawValue = rawValue;
         this.percent = percent;
      }

      @Override
      public boolean matches(ActiveMob mob) {
         double current = mob.getEntity().getHealth();
         double max = mob.getEntity().getMaxHealth();
         double frac = current / max;
         double threshold = this.percent ? this.rawValue / 100.0 : this.rawValue / max;
         return frac > threshold;
      }
   }

   private class LessThanConditional extends SkillMechanic.SkillHealthConditional {
      private final double rawValue;
      private final boolean percent;

      LessThanConditional(String expr, double rawValue, boolean percent) {
         super(expr);
         this.rawValue = rawValue;
         this.percent = percent;
      }

      @Override
      public boolean matches(ActiveMob mob) {
         double current = mob.getEntity().getHealth();
         double max = mob.getEntity().getMaxHealth();
         double frac = current / max;
         double threshold = this.percent ? this.rawValue / 100.0 : this.rawValue / max;
         return frac < threshold;
      }
   }

   private class RangeConditional extends SkillMechanic.SkillHealthConditional {
      private final double raw1;
      private final double raw2;
      private final boolean percent;

      RangeConditional(String expr, String v1, String v2, boolean percent) {
         super(expr);
         this.raw1 = Double.parseDouble(v1);
         this.raw2 = Double.parseDouble(v2);
         this.percent = percent;
      }

      @Override
      public boolean matches(ActiveMob mob) {
         double current = mob.getEntity().getHealth();
         double max = mob.getEntity().getMaxHealth();
         double frac = current / max;
         double t1 = this.percent ? this.raw1 / 100.0 : this.raw1 / max;
         double t2 = this.percent ? this.raw2 / 100.0 : this.raw2 / max;
         double upper = Math.max(t1, t2);
         double lower = Math.min(t1, t2);
         return frac > lower && frac < upper;
      }
   }

   private abstract class SkillHealthConditional {
      protected final String id;
      protected final String expression;

      protected SkillHealthConditional(String expression) {
         this.id = SkillMechanic.this.getUuid().toString() + "#" + expression;
         this.expression = expression;
      }

      public abstract boolean matches(ActiveMob activeMob);
   }
}
