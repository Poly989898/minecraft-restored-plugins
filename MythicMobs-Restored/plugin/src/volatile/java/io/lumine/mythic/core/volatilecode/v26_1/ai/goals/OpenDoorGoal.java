package io.lumine.mythic.core.volatilecode.v26_1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

@MythicAIGoal(
   name = "openDoor",
   aliases = {"openDoors"},
   description = "Open doors"
)
public class OpenDoorGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public OpenDoorGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
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

      return new net.minecraft.world.entity.ai.goal.OpenDoorGoal(nmsEntity, true);
   }
}
