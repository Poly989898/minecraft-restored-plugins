package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtTarget;
import org.bukkit.entity.Tameable;

@MythicAIGoal(
   name = "ownerTarget",
   aliases = {"ownerHurt", "ownerAttack"},
   description = "Target the mob's owner's target"
)
public class OwnerHurtGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public OwnerHurtGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.getBukkitEntity() instanceof Tameable;
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalOwnerHurtTarget((EntityTameableAnimal)PathfinderHolder.getNMSEntity(this.entity));
   }
}
