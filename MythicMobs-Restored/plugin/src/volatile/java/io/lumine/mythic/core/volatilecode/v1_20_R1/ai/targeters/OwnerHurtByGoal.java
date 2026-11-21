package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtByTarget;
import org.bukkit.entity.Tameable;

@MythicAIGoal(
   name = "ownerHurtBy",
   aliases = {"ownerHurtByTarget", "ownerDamager", "ownerAttacker"},
   description = "Target something that attacks the mob's owner"
)
public class OwnerHurtByGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public OwnerHurtByGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.getBukkitEntity() instanceof Tameable;
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalOwnerHurtByTarget((EntityTameableAnimal)PathfinderHolder.getNMSEntity(this.entity));
   }
}
