package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.bounds.BoundsSpec;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;

@MythicTargeter(
   author = "Archontas",
   name = "bound",
   aliases = {"bounds"},
   description = "Targets living entities inside a cuboid, sphere, or cylinder bounds shape"
)
public class BoundTargeter extends IEntitySelector {
   private final BoundsSpec bounds;

   public BoundTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.bounds = new BoundsSpec(mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      AbstractEntity caster = data.getCaster().getEntity();
      AbstractWorld world = caster.getWorld();
      if (world == null) {
         return new ArrayList<>();
      } else {
         ArrayList<AbstractEntity> targets = new ArrayList<>();
         PlaceholderContext context = this.bounds.isStatic() ? null : PlaceholderContext.builder().meta(data).entity(caster).build();

         for (AbstractEntity entity : world.getLivingEntities()) {
            boolean inside = this.bounds.isStatic() ? this.bounds.contains(entity.getLocation()) : this.bounds.contains(entity.getLocation(), context);
            if (inside) {
               targets.add(entity);
            }
         }

         return targets;
      }
   }
}
