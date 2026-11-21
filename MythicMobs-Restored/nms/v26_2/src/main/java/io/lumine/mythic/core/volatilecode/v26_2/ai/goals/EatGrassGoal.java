package io.lumine.mythic.core.volatilecode.v26_2.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.Goal;

@MythicAIGoal(
   name = "eatGrass",
   description = "Break down doors"
)
public class EatGrassGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public EatGrassGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new EatBlockGoal(PathfinderHolder.getNMSEntity(this.entity));
   }
}
