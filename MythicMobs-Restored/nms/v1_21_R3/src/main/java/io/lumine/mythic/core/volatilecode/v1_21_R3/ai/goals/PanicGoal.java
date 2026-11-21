package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;

@MythicAIGoal(
   name = "panic",
   aliases = {"panicWhenOnFire"},
   description = "Run around panicing when on fire and look for water"
)
public class PanicGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public PanicGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalPanic(PathfinderHolder.getNMSEntity(this.entity), 1.25);
   }
}
