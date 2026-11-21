package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.animal.EntityAnimal;

@MythicAIGoal(
   author = "Phil",
   name = "breed",
   description = "LE BREED GOAL, only entities that extends Animal can use this goal"
)
public class BreedGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speedModifier;

   public BreedGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speedmodifier", "speed", "s"}, 1.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isAnimal();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalBreed((EntityAnimal)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier);
   }
}
