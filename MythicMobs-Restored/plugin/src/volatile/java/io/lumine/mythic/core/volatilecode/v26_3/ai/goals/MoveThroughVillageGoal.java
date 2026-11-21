package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;

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
   public Goal create() {
      return new net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal(
         PathfinderHolder.getNMSEntity(this.entity), this.speed, false, 4, () -> this.openDoors
      );
   }
}
