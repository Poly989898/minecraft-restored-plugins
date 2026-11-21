package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;

@MythicAIGoal(
   name = "randomStroll",
   aliases = {},
   description = "Walk around randomly"
)
public class RandomStrollGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public RandomStrollGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalRandomStroll(PathfinderHolder.getNMSEntity(this.entity), 1.0);
   }
}
