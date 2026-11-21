package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R6.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;

@MythicAIGoal(
   name = "moveTowardsRestriction",
   description = "Move back towards a mob's designated restriction area"
)
public class MoveTowardsRestrictionGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public MoveTowardsRestrictionGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalMoveTowardsRestriction(PathfinderHolder.getNMSEntity(this.entity), 0.6);
   }
}
