package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Item;

@MythicTargeter(
   author = "Ashijin",
   name = "itemsInRadius",
   aliases = {"IIR"},
   description = "Gets all items in a radius around the caster"
)
public class ItemsInRadiusTargeter extends IEntitySelector {
   private double radius;

   public ItemsInRadiusTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> targets = Sets.newHashSet();

      for (AbstractEntity p : this.getPlugin()
         .getVolatileCodeHandler()
         .getWorldHandler()
         .getEntitiesNearLocation(am.getLocation(), this.radius, ae -> ae.getBukkitEntity() instanceof Item)) {
         if (this.distanceSquared(am.getEntity().getLocation(), p) < this.radius * this.radius) {
            targets.add(p);
         }
      }

      return targets;
   }
}
