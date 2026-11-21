package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "entitiesNearOrigin",
   aliases = {"NearOrigin", "ENO"},
   description = "Targets entities near the origin"
)
public class EntitiesNearOriginTargeter extends EntitiesNearbyTargeter {
   public EntitiesNearOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      return this.getEntitiesNearPoint(data, data.getOrigin());
   }
}
