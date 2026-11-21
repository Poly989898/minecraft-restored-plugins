package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R6.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFleeSun;

@MythicAIGoal(
   name = "fleeSun",
   description = "Run away from sunlight"
)
public class FleeSunGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected double speed;

   public FleeSunGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalFleeSun(PathfinderHolder.getNMSEntity(this.entity), this.speed);
   }
}
