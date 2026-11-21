package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveThroughVillage;

@MythicAIGoal(
   name = "moveThroughVillage",
   aliases = {},
   description = "Makes the mob move through villages"
)
public class MoveThroughVillageGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private boolean openDoors = false;

   public MoveThroughVillageGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.6);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalMoveThroughVillage(PathfinderHolder.getNMSEntity(this.entity), this.speed, false, 4, () -> this.openDoors);
   }
}
