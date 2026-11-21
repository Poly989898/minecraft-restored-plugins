package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalDoorOpen;
import net.minecraft.world.entity.ai.navigation.Navigation;

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
   public PathfinderGoal create() {
      EntityCreature nmsEntity = PathfinderHolder.getNMSEntity(this.entity);
      if (nmsEntity.N() instanceof Navigation groundNav) {
         groundNav.b(true);
      }

      return new PathfinderGoalDoorOpen(nmsEntity, true);
   }
}
