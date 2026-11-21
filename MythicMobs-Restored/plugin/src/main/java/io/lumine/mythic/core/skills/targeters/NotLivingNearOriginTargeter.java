package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   name = "notLivingNearOrigin",
   aliases = {"NLNO", "nonLivingNearOrigin"},
   description = "Targets non living entities near origin"
)
public class NotLivingNearOriginTargeter extends IEntitySelector {
   private PlaceholderDouble radius;

   public NotLivingNearOriginTargeter(SkillExecutor manager, MythicLineConfig config) {
      super(manager, config);
      this.radius = config.getPlaceholderDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      double parsedRadius = this.radius.get(data);
      double radiusSq = parsedRadius * parsedRadius;

      for (AbstractEntity abstractEntity : this.getPlugin()
         .getVolatileCodeHandler()
         .getWorldHandler()
         .getEntitiesNearLocation(data.getOrigin(), parsedRadius, ae -> !ae.isLiving())) {
         if (!abstractEntity.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())
            && this.distanceSquared(data.getOrigin(), abstractEntity) < radiusSq) {
            targets.add(abstractEntity);
         }
      }

      return targets;
   }
}
