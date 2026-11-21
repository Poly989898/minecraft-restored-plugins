package io.lumine.mythic.core.volatilecode.v26_1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;

@MythicAIGoal(
   name = "randomLookAround",
   aliases = {"lookAround"},
   description = "Randomly look around"
)
public class RandomLookAroundGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public RandomLookAroundGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(PathfinderHolder.getNMSEntity(this.entity));
   }
}
