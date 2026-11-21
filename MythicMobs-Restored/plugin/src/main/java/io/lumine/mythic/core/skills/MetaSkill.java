package io.lumine.mythic.core.skills;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.IWaitableSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicSkillEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.skills.audience.CustomAudience;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.cooldowns.CooldownMode;
import io.lumine.mythic.core.skills.mechanics.CancelSkill;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.DelaySkill;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

public class MetaSkill extends AbstractSkill implements Skill {
   @Getter
   protected final File file;
   @Getter
   protected final String internalName;
   @Getter
   protected final MythicConfig config;
   @Getter
   protected final Collection<SkillHolder> parents = Lists.newArrayList();
   protected Map<String, MetaSkill.SkillAction> actions = new HashMap<>();
   @Getter
   protected MetaSkill.SkillAction baseSkill = new MetaSkill.SkillAction("Skill").register(this.actions);
   protected MetaSkill.SkillAction failedConditionsSkill = new MetaSkill.SkillAction("OnFailSkill", "FailedConditionsSkill").register(this.actions);
   protected MetaSkill.SkillAction onCooldownSkill = new MetaSkill.SkillAction("OnCooldownSkill").register(this.actions);
   protected boolean stopIfNoTargets = false;
   @Getter @Setter
   protected boolean inlineSkill = false;
   @Getter
   protected boolean hidden = false;
   @Getter
   protected SkillAudience executionAudience = null;
   @Getter
   protected boolean executionAudienceIsEmpty = false;
   private boolean warnedUnresolvedAudience = false;
   protected List<String> killMessages;

   public MetaSkill(SkillExecutor manager, Pack pack, File file, String name, MythicConfig mc) {
      super(manager, file);
      this.pack = pack;
      this.file = file;
      this.internalName = name;
      this.config = mc;
      this.parseSharedCooldownConfig(mc);
      this.stopIfNoTargets = mc.getBoolean("CancelIfNoTargets", true);
      this.hidden = mc.getBoolean("Hidden", false);
      String executionAudienceStr = mc.getString(new String[]{"ExecutionAudience", "Options.ExecutionAudience"}, null);
      if (executionAudienceStr != null) {
         if (!executionAudienceStr.equalsIgnoreCase("NONE") && !executionAudienceStr.equalsIgnoreCase("EMPTY")) {
            this.executionAudience = this.getPlugin().getSkillManager().getAudience(executionAudienceStr);
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Skill {0} has ExecutionAudience: {1}", this.internalName, executionAudienceStr);
         } else {
            this.executionAudienceIsEmpty = true;
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Skill {0} has ExecutionAudience set to NONE/EMPTY", this.internalName);
         }
      }

      this.actions.forEach((id, action) -> {
         String maybeRedirect = mc.getString(id, null);
         if (maybeRedirect != null) {
            this.getManager().queueSecondPass(() -> action.setRedirect(this.getPlugin().getSkillManager().getSkill(file, this, maybeRedirect)));
         }

         mc.getStringList(id + "s").forEach(mechanic -> {
            mechanic = MythicLineConfigImpl.unparseBlock(mechanic);

            try {
               SkillMechanic mechanicSkill = this.getPlugin().getSkillManager().getMechanic(pack, mc.getFile(), this, mechanic);
               if (mechanicSkill != null) {
                  mechanicSkill.setParent(this);
                  action.addMechanic(mechanicSkill);
               }
            } catch (Error | Exception var6x) {
               MythicLogger.errorGenericConfig("Critical Error while attempting to load mechanic line '" + mechanic + "'");
            }
         });
      });

      for (String s : mc.getStringList("Conditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditions == null) {
               this.conditions = new ArrayList<>();
            }

            this.conditions.add(sc);
         }
      }

      for (String s : mc.getStringList("TargetConditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditionsTarget == null) {
               this.conditionsTarget = new ArrayList<>();
            }

            this.conditionsTarget.add(sc);
         }
      }

      for (String s : mc.getStringList("TriggerConditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditionsTrigger == null) {
               this.conditionsTrigger = new ArrayList<>();
            }

            this.conditionsTrigger.add(sc);
         }
      }
   }

   public MetaSkill(SkillExecutor manager, MobType mob, Pack pack, File file, String name, MythicConfig mc) {
      super(manager, file);
      this.pack = pack;
      this.file = file;
      this.internalName = name;
      this.config = mc;
      this.parseSharedCooldownConfig(mc);
      this.stopIfNoTargets = mc.getBoolean("CancelIfNoTargets", true);
      this.hidden = mc.getBoolean("Hidden", false);
      String executionAudienceStr = mc.getString(new String[]{"ExecutionAudience", "Options.ExecutionAudience"}, null);
      if (executionAudienceStr != null) {
         if (!executionAudienceStr.equalsIgnoreCase("NONE") && !executionAudienceStr.equalsIgnoreCase("EMPTY")) {
            this.executionAudience = this.getPlugin().getSkillManager().getAudience(executionAudienceStr);
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Skill {0} has ExecutionAudience: {1}", this.internalName, executionAudienceStr);
         } else {
            this.executionAudienceIsEmpty = true;
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Skill {0} has ExecutionAudience set to NONE/EMPTY", this.internalName);
         }
      }

      this.actions.forEach((id, action) -> {
         String maybeRedirect = mc.getString(id, null);
         if (maybeRedirect != null) {
            if (maybeRedirect.startsWith("self:")) {
               maybeRedirect = maybeRedirect.replace("self:", "mob:" + mob.getInternalName() + ":");
            }

            String mechanic = maybeRedirect;
            this.getManager().queueSecondPass(() -> action.setRedirect(this.getPlugin().getSkillManager().getSkill(file, this, mechanic)));
         }

         mc.getStringList(id + "s").forEach(mechanicx -> {
            mechanicx = MythicLineConfigImpl.unparseBlock(mechanicx);
            if (mechanicx.startsWith("selfskill:")) {
               mechanicx = mechanicx.replace("selfskill:", "skill:mob:" + mob.getInternalName() + ":");
            }

            try {
               SkillMechanic mechanicSkill = this.getPlugin().getSkillManager().getMechanic(pack, mc.getFile(), this, mechanicx);
               if (mechanicSkill != null) {
                  mechanicSkill.setParent(this);
                  action.addMechanic(mechanicSkill);
               }
            } catch (Error | Exception var7x) {
               MythicLogger.errorGenericConfig("Critical Error while attempting to load mechanic line '" + mechanicx + "'");
            }
         });
      });

      for (String s : mc.getStringList("Conditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         if (s.contains("self:")) {
            s = s.replace("self:", "mob:" + mob.getInternalName() + ":");
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditions == null) {
               this.conditions = new ArrayList<>();
            }

            this.conditions.add(sc);
         }
      }

      for (String s : mc.getStringList("TargetConditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         if (s.contains("self:")) {
            s = s.replace("self:", "mob:" + mob.getInternalName() + ":");
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditionsTarget == null) {
               this.conditionsTarget = new ArrayList<>();
            }

            this.conditionsTarget.add(sc);
         }
      }

      for (String s : mc.getStringList("TriggerConditions")) {
         if (s.contains("\"")) {
            String[] split = s.split("\"");
            int i = 0;
            String ns = "";

            for (String ss : split) {
               if (i % 2 == 1) {
                  ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
               } else {
                  ns = ns.concat(ss);
               }

               i++;
            }

            s = ns;
         }

         if (s.contains("self:")) {
            s = s.replace("self:", "mob:" + mob.getInternalName() + ":");
         }

         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            if (this.conditionsTrigger == null) {
               this.conditionsTrigger = new ArrayList<>();
            }

            this.conditionsTrigger.add(sc);
         }
      }
   }

   private void parseSharedCooldownConfig(MythicConfig config) {
      String cd = config.getString("Cooldown", null);
      if (cd != null) {
         this.cooldown = PlaceholderDuration.of(cd, DurationUnit.SECONDS);
      }

      CooldownMode cooldownMode = config.getEnum(new String[]{"CooldownMode", "ChargeCooldownMode"}, CooldownMode.class, this.cooldownMode);
      if (cd == null && cooldownMode == CooldownMode.NONE) {
         this.cooldown = PlaceholderDuration.of("1", DurationUnit.SECONDS);
      }

      this.configureChargeCooldown(config.getInteger(new String[]{"Charges"}, this.charges), cooldownMode);
   }

   public MetaSkill(SkillExecutor manager, File file, Collection<String> skillList) {
      super(manager, file);
      this.pack = manager.getPlugin().getPackManager().getBasePack();
      this.file = file;
      this.internalName = "#\u03bb." + UUID.randomUUID().toString();
      this.config = null;
      this.cooldown = null;

      for (String s : skillList) {
         s = MythicLineConfigImpl.unparseBlock(s);
         SkillMechanic ms = null;

         try {
            ms = MythicBukkit.inst().getSkillManager().getMechanic(this.pack, file, this, s);
         } catch (Exception e) {
            MythicLogger.errorGenericConfig("Critical Error while attempting to load mechanic line '" + s + "'");
         } catch (Error ex) {
            MythicLogger.errorGenericConfig("Critical Error while attempting to load mechanic line '" + s + "'");
         }

         if (ms != null) {
            this.baseSkill.addMechanic(ms);
         }
      }
   }

   @Override
   public void addParent(SkillHolder parent) {
      this.parents.add(parent);
   }

   @Override
   public boolean isUsable(SkillMetadata data) {
      return this.isUsable(data, null);
   }

   @Override
   public boolean executeIfUsable(SkillMetadata data) {
      return !this.isUsable(data) ? false : this.executeInternal(data);
   }

   @Override
   public boolean executeIfUsable(SkillMetadata data, SkillTrigger trigger) {
      return !this.isUsable(data, trigger) ? false : this.executeInternal(data);
   }

   @Override
   public boolean isUsable(SkillMetadata meta, SkillTrigger trigger) {
      SkillCaster skillCaster = meta.getCaster();
      if (!this.rollChance(meta)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Roll failed.");
         return false;
      } else if (skillCaster != null && skillCaster.isSkillOnCooldown(this)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Cooldown check failed.");
         this.execute(this.onCooldownSkill, meta);
         return false;
      } else {
         if (this.conditionsTarget != null) {
            for (SkillCondition mc : this.conditionsTarget) {
               if (!mc.evaluateTargets(meta) && this.stopIfNoTargets) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: TargetConditions failed.");
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  this.execute(this.failedConditionsSkill, meta);
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         if (this.conditionsTrigger != null) {
            for (SkillCondition mc : this.conditionsTrigger) {
               if (!mc.evaluateTrigger(meta)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: TriggerConditions failed.");
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  this.execute(this.failedConditionsSkill, meta);
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         if (this.conditions != null) {
            for (SkillCondition mc : this.conditions) {
               if (!mc.evaluateCaster(meta)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Conditions failed.");
                  mc.getOnFailSkill().ifPresent(s -> s.execute(meta));
                  this.execute(this.failedConditionsSkill, meta);
                  return false;
               }

               mc.getOnPassSkill().ifPresent(s -> s.execute(meta));
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill usable!");
         return true;
      }
   }

   @Override
   public void execute(
      SkillTrigger basetrigger,
      SkillCaster caster,
      AbstractEntity trigger,
      AbstractLocation origin,
      HashSet<AbstractEntity> eTargets,
      HashSet<AbstractLocation> lTargets,
      float power
   ) {
      SkillMetadata data = new SkillMetadataImpl(basetrigger, caster, trigger, origin, eTargets, lTargets, power);
      this.execute(data);
   }

   @Override
   public void execute(SkillMetadata data) {
      this.executeInternal(data);
   }

   private boolean executeInternal(SkillMetadata data) {
      this.applyExecutionAudience(data);
      if (MythicSkillEvent.getHandlerList().getRegisteredListeners().length != 0) {
         MythicSkillEvent event = Events.callAndReturn(new MythicSkillEvent(data, this));
         if (event.isCancelled()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Event cancelled.");
            return false;
         }
      }

      Object cooldownLock = this.tryAcquireCooldownLock(data);
      if (this.isCooldownLockFailed(cooldownLock)) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Cooldown lock failed.");
         this.execute(this.onCooldownSkill, data);
         return false;
      } else {
         Queue<SkillMechanic> skillqueue = new MetaSkill.ObjectArrayFIFOQueueCloneable<>(this.baseSkill.getMechanics());

         boolean ex;
         try {
            if (this.consumeCharge(data)) {
               this.baseSkill.executeRedirect(data);
               execute(data, skillqueue);
               return true;
            }

            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: Cooldown check failed.");
            this.execute(this.onCooldownSkill, data);
            ex = false;
         } catch (Exception e) {
            MythicLogger.error("Couldn't execute skill '" + this.internalName + "': Enable debugging for a stack trace.");
            if (this.inlineSkill) {
               ArrayList<String> inlineInfo = new ArrayList<>();
               if (this.file != null) {
                  inlineInfo.add("Filepath: " + this.file.getPath());
               }

               if (this.baseSkill != null && !this.baseSkill.mechanics.isEmpty() && this.baseSkill.mechanics.get(0).getConfig() != null) {
                  inlineInfo.add("First Skill Line: " + this.baseSkill.mechanics.get(0).getConfig().getLine());
               }

               if (!inlineInfo.isEmpty()) {
                  MythicLogger.error("Additional Inline Skill Information: ");

                  for (String info : inlineInfo) {
                     MythicLogger.error("  " + info);
                  }
               }
            }

            MythicLogger.handleMinorError(e);
            return false;
         } finally {
            this.releaseCooldownLock(data, cooldownLock);
         }

         return ex;
      }
   }

   private void applyExecutionAudience(SkillMetadata data) {
      Set<UUID> declared = this.resolveDeclaredAudience(data);
      if (declared != null) {
         Optional<Set<UUID>> audience = data.getExecutionAudience();
         if (audience.isPresent()) {
            HashSet<UUID> narrowed = new HashSet<>(audience.get());
            narrowed.retainAll(declared);
            data.isolateExecutionAudience(narrowed);
         } else {
            data.isolateExecutionAudience(declared);
         }
      }
   }

   private Set<UUID> resolveDeclaredAudience(SkillMetadata data) {
      if (this.executionAudienceIsEmpty) {
         return Collections.emptySet();
      } else if (this.executionAudience != null && data.getCaster() != null) {
         if (this.executionAudience instanceof CustomAudience ca && ca.getAudience().isEmpty()) {
            if (!this.warnedUnresolvedAudience) {
               this.warnedUnresolvedAudience = true;
               MythicLogger.error(
                  "Skill {0}: ExecutionAudience {1} did not resolve to a known audience; treating as unrestricted", this.internalName, ca.getAudienceName()
               );
            }

            return null;
         } else {
            Collection<AbstractPlayer> resolved = this.executionAudience.get(data, data.getCaster().getEntity());
            HashSet<UUID> uuids = new HashSet<>();

            for (AbstractPlayer player : resolved) {
               uuids.add(player.getUniqueId());
            }

            return uuids;
         }
      } else {
         return null;
      }
   }

   public void execute(MetaSkill.SkillAction action, SkillMetadata data) {
      Queue<SkillMechanic> skillqueue = new MetaSkill.ObjectArrayFIFOQueueCloneable<>(action.getMechanics());

      try {
         action.executeRedirect(data);
         execute(data, skillqueue);
      } catch (Exception e) {
         MythicLogger.error("Couldn't execute " + action.getId() + " '" + this.internalName + "': Enable debugging for a stack trace.");
         MythicLogger.handleMinorError(e);
      }
   }

   public static void execute(SkillMetadata data, Queue<SkillMechanic> skillqueue) {
      while (!skillqueue.isEmpty()) {
         SkillMechanic mbs = skillqueue.poll();
         if (data.isTerminated()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Terminating SkillMechanic {0}.", mbs.getConfigLine());
            break;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Evaluating SkillMechanic {0}", mbs.getConfigLine());
         if (mbs instanceof CancelSkill && mbs.isUsableFromSkill(data)) {
            break;
         }

         if (mbs instanceof DelaySkill delaySkill) {
            AbstractSkill.DelayedSkill ds = new AbstractSkill.DelayedSkill(data, skillqueue);
            if (data.isAsync()) {
               Schedulers.async().runLater(ds, delaySkill.getTicks(data));
            } else {
               Schedulers.of(data.getCaster().getEntity().getBukkitEntity()).runLater(ds, delaySkill.getTicks(data));
            }
            break;
         }

         if (mbs.getDispatchTarget() instanceof IWaitableSkill waitable) {
            if (mbs.isUsableFromSkill(data)) {
               mbs.execute(data.deepClone());
               waitable.completableExecution(data.deepClone()).thenAccept(success -> {
                  if (success) {
                     execute(data, skillqueue);
                  }
               });
            }
            break;
         }

         if (mbs instanceof CustomMechanic cm && cm.getMechanic().isPresent() && cm.getMechanic().get() instanceof IWaitableSkill waitablex) {
            if (mbs.isUsableFromSkill(data)) {
               mbs.execute(data.deepClone());
               waitablex.completableExecution(data.deepClone()).thenAccept(success -> {
                  if (success) {
                     execute(data, skillqueue);
                  }
               });
            }
            break;
         }

         if (mbs.isUsableFromSkill(data)) {
            mbs.execute(data.deepClone());
         }
      }
   }

   public static class ObjectArrayFIFOQueueCloneable<K> extends ObjectArrayFIFOQueue<K> implements Queue<K>, Cloneable {
      public ObjectArrayFIFOQueueCloneable() {
      }

      public ObjectArrayFIFOQueueCloneable(int size) {
         super(size);
      }

      public ObjectArrayFIFOQueueCloneable(MetaSkill.ObjectArrayFIFOQueueCloneable<K> queue) {
         super(queue.length);
         this.start = queue.start;
         this.end = queue.end;
         System.arraycopy(queue.array, 0, this.array, 0, this.end - this.start);
      }

      @Override
      public boolean contains(Object o) {
         for (int i = this.start; i < this.end; i++) {
            if (this.array[i].equals(o)) {
               return true;
            }
         }

         return false;
      }

      @NotNull
      @Override
      public Iterator<K> iterator() {
         return (Iterator<K>)Arrays.asList(Arrays.copyOfRange(this.array, this.start, this.end)).iterator();
      }

      @NotNull
      @Override
      public K[] toArray() {
         return (K[])this.array.clone();
      }

      @NotNull
      @Override
      public <T> T[] toArray(@NotNull T[] a) {
         return (T[])Arrays.copyOf(this.array, this.end - this.start, (Class<? extends T[]>)a.getClass());
      }

      @Override
      public boolean add(K k) {
         this.enqueue(k);
         return true;
      }

      @Override
      public boolean remove(Object o) {
         for (int i = this.start; i < this.end; i++) {
            if (this.array[i].equals(o)) {
               System.arraycopy(this.array, i + 1, this.array, i, this.end - i - 1);
               this.end--;
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(@NotNull Collection<?> c) {
         for (Object o : c) {
            if (!this.contains(o)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(@NotNull Collection<? extends K> c) {
         for (K k : c) {
            this.enqueue(k);
         }

         return true;
      }

      @Override
      public boolean removeAll(@NotNull Collection<?> c) {
         boolean changed = false;

         for (Object o : c) {
            changed |= this.remove(o);
         }

         return changed;
      }

      @Override
      public boolean retainAll(@NotNull Collection<?> c) {
         boolean changed = false;

         for (int i = this.start; i < this.end; i++) {
            if (!c.contains(this.array[i])) {
               this.remove(this.array[i]);
               changed = true;
            }
         }

         return changed;
      }

      @Override
      public boolean offer(K k) {
         this.enqueue(k);
         return true;
      }

      @Override
      public K remove() {
         return (K)this.dequeue();
      }

      @Override
      public K poll() {
         return (K)(this.start == this.end ? null : this.dequeue());
      }

      @Override
      public K element() {
         return (K)this.first();
      }

      @Override
      public K peek() {
         return (K)(this.start == this.end ? null : this.array[this.start]);
      }

      public K get(int index) {
         return (K)(index >= 0 && index < this.size() ? this.array[index] : null);
      }

      @Override
      public boolean isEmpty() {
         return super.isEmpty();
      }

      public MetaSkill.ObjectArrayFIFOQueueCloneable<K> clone() {
         return new MetaSkill.ObjectArrayFIFOQueueCloneable<>(this);
      }
   }

   @Getter
   @Setter
   @ToString
   public static class SkillAction {
      private final String id;
      private final String[] aliases;
      private Optional<Skill> redirect = Optional.empty();
      private MetaSkill.ObjectArrayFIFOQueueCloneable<SkillMechanic> mechanics = new MetaSkill.ObjectArrayFIFOQueueCloneable<>();

      public SkillAction(String id, String... aliases) {
         this.id = id;
         this.aliases = aliases;
      }

      public void executeRedirect(SkillMetadata meta) {
         this.redirect.ifPresent(skill -> skill.executeIfUsable(meta));
      }

      public void addMechanic(SkillMechanic ms) {
         this.mechanics.add(ms);
      }

      public MetaSkill.SkillAction register(Map<String, MetaSkill.SkillAction> actions) {
         actions.put(this.id, this);

         for (String alias : this.aliases) {
            actions.put(alias, this);
         }

         return this;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof MetaSkill.SkillAction other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null ? other$id == null : this$id.equals(other$id)) {
               if (!Arrays.deepEquals(this.getAliases(), other.getAliases())) {
                  return false;
               } else {
                  Object this$redirect = this.getRedirect();
                  Object other$redirect = other.getRedirect();
                  if (this$redirect == null ? other$redirect == null : this$redirect.equals(other$redirect)) {
                     Object this$mechanics = this.getMechanics();
                     Object other$mechanics = other.getMechanics();
                     return this$mechanics == null ? other$mechanics == null : this$mechanics.equals(other$mechanics);
                  } else {
                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof MetaSkill.SkillAction;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $id = this.getId();
         result = result * 59 + ($id == null ? 43 : $id.hashCode());
         result = result * 59 + Arrays.deepHashCode(this.getAliases());
         Object $redirect = this.getRedirect();
         result = result * 59 + ($redirect == null ? 43 : $redirect.hashCode());
         Object $mechanics = this.getMechanics();
         return result * 59 + ($mechanics == null ? 43 : $mechanics.hashCode());
      }
   }
}
