package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomFly;

@MythicAIGoal(
   name = "randomFly",
   aliases = {},
   description = "Fly around randomly"
)
public class RandomFlyGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public RandomFlyGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalRandomFly(PathfinderHolder.getNMSEntity(this.entity), 1.0);
   }
}
