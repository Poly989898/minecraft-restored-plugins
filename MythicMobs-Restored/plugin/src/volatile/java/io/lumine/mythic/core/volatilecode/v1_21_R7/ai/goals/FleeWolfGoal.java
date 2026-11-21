package io.lumine.mythic.core.volatilecode.v1_21_R7.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R7.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.animal.wolf.EntityWolf;

@MythicAIGoal(
   name = "fleeWolf",
   aliases = {"runfromwolves"},
   description = "Run away from nearby wolves"
)
public class FleeWolfGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected float distance;
   protected double speed;
   protected double safeSpeed;

   public FleeWolfGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.distance = mlc.getFloat(new String[]{"distance", "d"}, 6.0F);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
      this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalAvoidTarget(PathfinderHolder.getNMSEntity(this.entity), EntityWolf.class, this.distance, this.safeSpeed, this.speed);
   }
}
