package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface SkillEventBus {
   TriggeredSkill processTrigger(SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity);

   TriggeredSkill processTrigger(SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity, boolean flag);

   TriggeredSkill processTrigger(SkillTrigger skillTrigger, SkillTriggerMetadata skillTriggerMetadata, SkillCaster skillCaster, AbstractEntity abstractEntity, boolean flag);

   TriggeredSkill processTrigger(SkillTrigger skillTrigger, SkillTriggerMetadata skillTriggerMetadata, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag);

   TriggeredSkill processTrigger(SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag, Consumer<SkillMetadata> consumer);

   TriggeredSkill processTrigger(
      SkillTrigger skillTrigger, SkillTriggerMetadata skillTriggerMetadata, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag, Consumer<SkillMetadata> consumer
   );

   TriggeredSkill processTrigger(
      SkillTrigger skillTrigger,
      SkillTriggerMetadata skillTriggerMetadata,
      SkillCaster skillCaster,
      AbstractEntity abstractEntity,
      AbstractLocation abstractLocation,
      boolean flag,
      Consumer<SkillMetadata> consumer,
      Collection<UUID> uuids
   );

   SkillMetadata buildSkillMetadata(SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag);

   SkillMetadata buildSkillMetadata(SkillTrigger skillTrigger, SkillTriggerMetadata skillTriggerMetadata, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag);

   SkillMetadata buildSkillMetadata(SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, boolean flag, Collection<UUID> uuids);

   TriggeredSkill processTriggerMechanics(SkillMetadata skillMetadata);

   TriggeredSkill processTriggerMechanics(SkillMetadata skillMetadata, SkillTriggerMetadata skillTriggerMetadata);

   void processStatsPre(SkillMetadata skillMetadata, SkillTriggerMetadata skillTriggerMetadata, StatSnapshot statSnapshot);

   void processStatsPost(SkillMetadata skillMetadata, SkillTriggerMetadata skillTriggerMetadata, StatSnapshot statSnapshot);
}
