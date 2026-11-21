package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Item;

@MythicTargeter(
   author = "Ashijin",
   name = "itemsNearOrigin",
   aliases = {"INO"},
   description = "Gets all items in a radius around the origin"
)
public class ItemsNearOriginTargeter extends IEntitySelector {
   double radius;

   public ItemsNearOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble("radius", 5.0);
      this.radius = mlc.getDouble("r", this.radius);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();

      for (AbstractEntity p : this.getPlugin()
         .getVolatileCodeHandler()
         .getWorldHandler()
         .getEntitiesNearLocation(data.getOrigin(), this.radius, ae -> ae.getBukkitEntity() instanceof Item)) {
         if (this.distanceSquared(data.getOrigin(), p) < this.radius * this.radius) {
            targets.add(p);
         }
      }

      return targets;
   }
}
