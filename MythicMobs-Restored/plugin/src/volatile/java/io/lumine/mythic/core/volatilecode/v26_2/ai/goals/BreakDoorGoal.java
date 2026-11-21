package io.lumine.mythic.core.volatilecode.v26_2.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

@MythicAIGoal(
   name = "breakDoor",
   aliases = {"breakDoors"},
   description = "Break down doors"
)
public class BreakDoorGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public BreakDoorGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      PathfinderMob nmsEntity = PathfinderHolder.getNMSEntity(this.entity);
      if (nmsEntity.getNavigation() instanceof GroundPathNavigation groundNav) {
         groundNav.setCanOpenDoors(true);
      }

      return new net.minecraft.world.entity.ai.goal.BreakDoorGoal(nmsEntity, d -> true);
   }
}
