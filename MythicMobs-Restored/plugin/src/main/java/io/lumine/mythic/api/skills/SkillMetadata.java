package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.VariableHolder;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public interface SkillMetadata extends PlaceholderMeta, VariableHolder {
   SkillTrigger getCause();

   @Override
   SkillCaster getCaster();

   AbstractItemStack getCasterItem();

   @Override
   AbstractEntity getTrigger();

   AbstractLocation getOrigin();

   Map<String, String> getParameters();

   @Override
   VariableRegistry getVariables();

   void setVariables(VariableRegistry variableRegistry);

   float getPower();

   void setPower(float value);

   boolean getExecuteAfterDeath();

   void setExecuteAfterDeath(boolean flag);

   boolean isParentCancelled();

   boolean isAsync();

   SkillMetadata setOrigin(AbstractLocation abstractLocation);

   boolean getIsAsync();

   SkillMetadata setIsAsync(boolean flag);

   IParentSkill getCallingEvent();

   void setCallingEvent(IParentSkill iParentSkill);

   void cancelEvent();

   boolean hasEntityTargets();

   Collection<AbstractEntity> getEntityTargets();

   SkillMetadata setEntityTargets(Collection<AbstractEntity> abstractEntities);

   SkillMetadata setEntityTarget(AbstractEntity abstractEntity);

   boolean hasLocationTargets();

   Collection<AbstractLocation> getLocationTargets();

   SkillMetadata setLocationTargets(Collection<AbstractLocation> abstractLocations);

   SkillMetadata setLocationTarget(AbstractLocation abstractLocation);

   void setConsoleTarget(boolean flag);

   boolean isConsoleTarget();

   boolean hasTarget();

   SkillMetadata setMetadata(String str, Object obj);

   @Override
   Optional<Object> getMetadata(String str);

   @Override
   <T> Optional<T> getMetadata(Class<T> clazz, String str);

   @Override
   Optional<Object> removeMetadata(String str);

   @Override
   <T> Optional<T> removeMetadata(Class<T> clazz, String str);

   @Override
   void clearMetadata();

   void snapshotCasterStats();

   Optional<StatSnapshot> getCasterStats();

   Optional<StatSnapshot> getTriggerStats();

   ExecutionAudienceState getExecutionAudienceState();

   Optional<Set<UUID>> getExecutionAudience();

   SkillMetadata setExecutionAudience(Collection<UUID> uuids);

   boolean hasExecutionAudience();

   SkillMetadata isolateExecutionAudience(Collection<UUID> uuids);

   SkillMetadata deepClone();

   default SkillMetadata deepCloneNoTargets() {
      return this.deepClone();
   }

   SkillMetadataImpl deeperClone();

   SkillMetadata deepClone(boolean flag);

   SkillMetadataImpl deeperClone(boolean flag);

   void setCaster(SkillCaster skillCaster);

   void setTrigger(AbstractEntity abstractEntity);

   SkillTriggerMetadata getTriggerMetadata();

   void setTerminatedReference(AtomicBoolean atomicBoolean);

   void terminate();

   boolean isTerminated();
}
