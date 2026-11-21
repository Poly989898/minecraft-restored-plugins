package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;

@MythicAIGoal(
   name = "leapTowardsTarget",
   aliases = {"leapAtTarget"},
   description = "Leap towards the target"
)
public class LeapTowardsTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected float speed;

   public LeapTowardsTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = (float)mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalLeapAtTarget(PathfinderHolder.getNMSEntity(this.entity), this.speed);
   }
}
