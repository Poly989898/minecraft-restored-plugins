package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;

@MythicAIGoal(
   name = "moveTowardsTarget",
   aliases = {},
   description = "Path to the current target"
)
public class MoveTowardsTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private float maxRange;

   public MoveTowardsTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
      this.maxRange = mlc.getFloat(new String[]{"maxrange", "range", "r"}, 32.0F);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalMoveTowardsTarget(PathfinderHolder.getNMSEntity(this.entity), this.speed, this.maxRange);
   }
}
