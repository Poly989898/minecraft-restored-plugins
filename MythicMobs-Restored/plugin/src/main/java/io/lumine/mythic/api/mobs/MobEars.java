package io.lumine.mythic.api.mobs;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.skills.SkillEventBus;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;

public interface MobEars {
   void tick();

   default void handleDetection(
      ActiveMob activeMob,
      AbstractWorld world,
      AbstractVector sourcePosition,
      AbstractEntity sourceEntity,
      AbstractEntity triggeringEntity,
      String soundType,
      float volume
   ) {
      SkillEventBus eventBus = MythicProvider.get().getSkillManager().getEventBus();
      AbstractLocation origin;
      if (triggeringEntity != null) {
         origin = sourcePosition.toLocation(world);
      } else {
         origin = sourceEntity.getLocation();
      }

      SkillMetadata data = eventBus.buildSkillMetadata(SkillTriggers.HEAR, null, activeMob, sourceEntity, origin, false);
      data.getVariables().putFloat("volume", volume);
      data.getVariables().putString("sound-type", soundType);
      TriggeredSkill ts = eventBus.processTriggerMechanics(data);
   }
}
