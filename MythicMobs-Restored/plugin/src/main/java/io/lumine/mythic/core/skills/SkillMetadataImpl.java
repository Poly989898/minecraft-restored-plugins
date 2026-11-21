package io.lumine.mythic.core.skills;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.ExecutionAudienceState;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;

public class SkillMetadataImpl implements SkillMetadata, Cloneable {
   @Getter
   private final SkillTrigger cause;
   private IParentSkill caller = null;
   @Getter @Setter
   private SkillTriggerMetadata triggerMetadata;
   @Getter
   private SkillCaster caster;
   @Getter @Setter
   private AbstractItemStack casterItem;
   @Setter
   private StatSnapshot casterStats = null;
   @Getter
   private AbstractEntity trigger;
   @Setter
   private StatSnapshot triggerStats = null;
   @Getter
   private AbstractLocation origin;
   private Collection<AbstractEntity> eTargets;
   private Collection<AbstractLocation> lTargets;
   @Getter @Setter
   private boolean consoleTarget;
   @Getter @Setter
   private Map<String, Object> metadata;
   @Getter @Setter
   private Map<String, String> parameters;
   @Getter @Setter
   private VariableRegistry variables;
   @Getter @Setter
   private float power = 1.0F;
   private boolean executeAfterDeath;
   @Getter
   private boolean parentCancelled = false;
   @Getter
   private boolean async = true;
   private AtomicBoolean isTerminated;
   private volatile ExecutionAudienceStateImpl executionAudienceState;

   public SkillMetadataImpl(SkillTrigger cause, SkillCaster am, AbstractEntity trigger) {
      this.initDefaults();
      this.cause = cause;
      this.caster = am;
      this.casterItem = getCasterItemSafe(am);
      this.trigger = trigger;
      this.executeAfterDeath = cause == SkillTriggers.DEATH || cause == SkillTriggers.DESPAWNED;
   }

   public SkillMetadataImpl(
      SkillTrigger cause,
      SkillCaster am,
      AbstractEntity trigger,
      AbstractLocation origin,
      Collection<AbstractEntity> feTargets,
      Collection<AbstractLocation> flTargets,
      float power
   ) {
      this.initDefaults();
      this.cause = cause;
      this.caster = am;
      this.casterItem = getCasterItemSafe(am);
      this.trigger = trigger;
      this.origin = origin;
      this.eTargets = feTargets;
      this.lTargets = flTargets;
      this.power = power;
      this.executeAfterDeath = cause == SkillTriggers.DEATH || cause == SkillTriggers.DESPAWNED;
   }

   private static AbstractItemStack getCasterItemSafe(SkillCaster am) {
      try {
         return am.getEntity().getItemMainHand().map(AbstractItemStack::copy).orElse(null);
      } catch (Exception e) {
         return null;
      }
   }

   public SkillMetadataImpl(SkillMetadataImpl source) {
      this.initDefaults();
      this.cause = source.getCause();
      this.caster = source.getCaster();
      this.casterItem = source.getCasterItem();
      this.trigger = source.getTrigger();
      this.origin = source.getOrigin();
      this.eTargets = source.getEntityTargets();
      this.lTargets = source.getLocationTargets();
      this.power = source.getPower();
      this.executeAfterDeath = source.getExecuteAfterDeath();
      this.executionAudienceState = source.executionAudienceState;
   }

   private SkillMetadataImpl(SkillMetadataImpl source, boolean shallowCopy) {
      this.cause = source.cause;
      this.caller = source.caller;
      this.triggerMetadata = source.triggerMetadata;
      this.caster = source.caster;
      this.casterItem = source.casterItem;
      this.casterStats = source.casterStats;
      this.trigger = source.trigger;
      this.triggerStats = source.triggerStats;
      this.origin = source.origin;
      this.eTargets = source.eTargets;
      this.lTargets = source.lTargets;
      this.consoleTarget = source.consoleTarget;
      this.metadata = source.metadata;
      this.parameters = source.parameters;
      this.variables = source.variables;
      this.power = source.power;
      this.executeAfterDeath = source.executeAfterDeath;
      this.parentCancelled = source.parentCancelled;
      this.async = source.async;
      this.isTerminated = source.isTerminated;
      this.executionAudienceState = source.executionAudienceState;
   }

   private void initDefaults() {
      this.metadata = Maps.newHashMap();
      this.parameters = Maps.newHashMap();
      this.variables = new VariableRegistry();
      this.isTerminated = new AtomicBoolean(false);
      this.executionAudienceState = new ExecutionAudienceStateImpl();
   }

   public SkillMetadataImpl setOrigin(AbstractLocation o) {
      this.origin = o;
      return this;
   }

   @Override
   public boolean getIsAsync() {
      return this.async;
   }

   public SkillMetadataImpl setIsAsync(boolean b) {
      this.async = b;
      return this;
   }

   @Override
   public IParentSkill getCallingEvent() {
      return this.caller;
   }

   @Override
   public void setCallingEvent(IParentSkill caller) {
      this.caller = caller;
   }

   @Override
   public void cancelEvent() {
      if (this.caller != null) {
         this.caller.setCancelled();
      }

      this.parentCancelled = true;
   }

   @Override
   public boolean hasEntityTargets() {
      return this.eTargets != null && !this.eTargets.isEmpty();
   }

   @Override
   public Collection<AbstractEntity> getEntityTargets() {
      return this.eTargets;
   }

   public SkillMetadataImpl setEntityTargets(Collection<AbstractEntity> targets) {
      if (this.lTargets != null) {
         this.lTargets = Collections.emptySet();
      }

      this.eTargets = targets;
      this.setConsoleTarget(false);
      return this;
   }

   public SkillMetadataImpl setEntityTarget(AbstractEntity target) {
      if (this.lTargets != null) {
         this.lTargets = Collections.emptySet();
      }

      ArrayList<AbstractEntity> list = new ArrayList<>(1);
      list.add(target);
      this.eTargets = list;
      this.setConsoleTarget(false);
      return this;
   }

   @Override
   public boolean hasLocationTargets() {
      return this.lTargets != null && !this.lTargets.isEmpty();
   }

   @Override
   public Collection<AbstractLocation> getLocationTargets() {
      return this.lTargets;
   }

   public SkillMetadataImpl setLocationTargets(Collection<AbstractLocation> targets) {
      if (this.eTargets != null) {
         this.eTargets = Collections.emptySet();
      }

      this.lTargets = targets;
      this.setConsoleTarget(false);
      return this;
   }

   public SkillMetadataImpl setLocationTarget(AbstractLocation target) {
      if (this.eTargets != null) {
         this.eTargets = Collections.emptySet();
      }

      ArrayList<AbstractLocation> list = new ArrayList<>(1);
      list.add(target);
      this.lTargets = list;
      this.setConsoleTarget(false);
      return this;
   }

   @Override
   public boolean hasTarget() {
      return this.isConsoleTarget() || this.eTargets != null && !this.eTargets.isEmpty() || this.lTargets != null && !this.lTargets.isEmpty();
   }

   public SkillMetadataImpl setMetadata(String key, Object value) {
      Objects.requireNonNull(key, "key");
      if (value == null) {
         this.metadata.remove(key);
         return this;
      } else {
         this.metadata.put(key, value);
         return this;
      }
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      Objects.requireNonNull(key, "key");
      return Optional.ofNullable(this.metadata.getOrDefault(key, null));
   }

   @Override
   public <T> Optional<T> getMetadata(Class<T> clazz, String key) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(key, "key");
      Object value = this.metadata.getOrDefault(key, null);
      if (value == null) {
         return Optional.empty();
      } else {
         return !clazz.isInstance(value) ? Optional.empty() : Optional.of(clazz.cast(value));
      }
   }

   @Override
   public Optional<Object> removeMetadata(String key) {
      Objects.requireNonNull(key, "key");
      return Optional.ofNullable(this.metadata.remove(key));
   }

   @Override
   public <T> Optional<T> removeMetadata(Class<T> clazz, String key) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(key, "key");
      Object value = this.metadata.remove(key);
      if (value == null) {
         return Optional.empty();
      } else {
         return !clazz.isInstance(value) ? Optional.empty() : Optional.of(clazz.cast(value));
      }
   }

   @Override
   public void clearMetadata() {
      this.metadata.clear();
   }

   @Override
   public void snapshotCasterStats() {
      Optional<StatRegistry> maybeStats = MythicBukkit.inst().getStatManager().getStatRegistry(this.caster.getEntity());
      maybeStats.ifPresent(statRegistry -> this.casterStats = statRegistry.getSnapshot());
   }

   @Override
   public Optional<StatSnapshot> getCasterStats() {
      return this.casterStats != null ? Optional.of(this.casterStats) : MythicBukkit.inst().getStatManager().getStatRegistryAsSnapshot(this.caster.getEntity());
   }

   @Override
   public Optional<StatSnapshot> getTriggerStats() {
      if (this.trigger == null) {
         return Optional.empty();
      } else {
         return this.triggerStats != null ? Optional.of(this.triggerStats) : MythicBukkit.inst().getStatManager().getStatRegistryAsSnapshot(this.trigger);
      }
   }

   public SkillMetadataImpl clone() throws CloneNotSupportedException {
      return this.getClass() != SkillMetadataImpl.class ? (SkillMetadataImpl)super.clone() : new SkillMetadataImpl(this, true);
   }

   public SkillMetadataImpl deepClone() {
      return this.deepClone(true);
   }

   public SkillMetadataImpl deepCloneNoTargets() {
      try {
         SkillMetadataImpl clone = this.clone();
         clone.setParameters(Maps.newHashMap(this.parameters));
         clone.setCallingEvent(this.getCallingEvent());
         clone.setTerminatedReference(this.isTerminated);
         return clone;
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public SkillMetadataImpl deeperClone() {
      return this.deeperClone(true);
   }

   public SkillMetadataImpl deepClone(boolean terminatable) {
      try {
         SkillMetadataImpl clone = this.clone();
         clone.setParameters(Maps.newHashMap(this.parameters));
         clone.setCallingEvent(this.getCallingEvent());
         if (this.getEntityTargets() != null && !this.getEntityTargets().isEmpty()) {
            clone.setEntityTargets(new ArrayList<>(this.getEntityTargets()));
         }

         if (this.getLocationTargets() != null && !this.getLocationTargets().isEmpty()) {
            clone.setLocationTargets(new ArrayList<>(this.getLocationTargets()));
         }

         if (terminatable) {
            clone.setTerminatedReference(this.isTerminated);
         } else {
            clone.setTerminatedReference(new AtomicBoolean());
         }

         return clone;
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public SkillMetadataImpl deeperClone(boolean terminatable) {
      try {
         SkillMetadataImpl clone = this.clone();
         clone.setMetadata(Maps.newHashMap(this.metadata));
         clone.setParameters(Maps.newHashMap(this.parameters));
         clone.setVariables(this.variables.clone());
         clone.setCallingEvent(this.getCallingEvent());
         if (this.getEntityTargets() != null && !this.getEntityTargets().isEmpty()) {
            clone.setEntityTargets(new ArrayList<>(this.getEntityTargets()));
         }

         if (this.getLocationTargets() != null && !this.getLocationTargets().isEmpty()) {
            clone.setLocationTargets(new ArrayList<>(this.getLocationTargets()));
         }

         if (terminatable) {
            clone.setTerminatedReference(this.isTerminated);
         } else {
            clone.setTerminatedReference(new AtomicBoolean());
         }

         return clone;
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public void setCaster(SkillCaster caster) {
      this.caster = caster;
      if (this.casterItem == null) {
         this.casterItem = caster.getEntity().getItemMainHand().orElse(null);
      }
   }

   @Override
   public void setTrigger(AbstractEntity entity) {
      this.trigger = entity;
   }

   @Override
   public void setTerminatedReference(AtomicBoolean atomicBoolean) {
      this.isTerminated = atomicBoolean;
   }

   @Override
   public void terminate() {
      this.isTerminated.set(true);
   }

   @Override
   public boolean isTerminated() {
      return this.isTerminated.get();
   }

   @Override
   public boolean getExecuteAfterDeath() {
      return this.executeAfterDeath;
   }

   @Override
   public void setExecuteAfterDeath(boolean bool) {
      this.executeAfterDeath = bool;
   }

   @Override
   public ExecutionAudienceState getExecutionAudienceState() {
      return this.executionAudienceState;
   }

   @Override
   public Optional<Set<UUID>> getExecutionAudience() {
      return this.executionAudienceState.get();
   }

   public SkillMetadataImpl setExecutionAudience(Collection<UUID> audience) {
      this.executionAudienceState.set(audience);
      return this;
   }

   @Override
   public boolean hasExecutionAudience() {
      return this.executionAudienceState.isSet();
   }

   public SkillMetadataImpl isolateExecutionAudience(Collection<UUID> audience) {
      this.executionAudienceState = new ExecutionAudienceStateImpl(audience);
      return this;
   }
}
