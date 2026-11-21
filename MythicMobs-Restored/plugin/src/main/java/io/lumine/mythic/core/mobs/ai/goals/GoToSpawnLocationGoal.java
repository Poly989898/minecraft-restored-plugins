package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;

@MythicAIGoal(
   name = "goToSpawnLocation",
   aliases = {"goToSpawn"},
   version = "4.8",
   description = "Path to the mob's spawn location"
)
public class GoToSpawnLocationGoal extends Pathfinder implements PathfindingGoal {
   private double maxRangeSq;
   private double minRangeSq;
   private float speed;
   private boolean dropTarget;

   public GoToSpawnLocationGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;
      double def = 16.0;

      try {
         def = Double.valueOf(this.dataVar1);
      } catch (Error | Exception e) {
      }

      double maxRange = mlc.getDouble(new String[]{"maxrange", "max", "r"}, def);
      this.maxRangeSq = Math.pow(maxRange, 2.0);
      double minRange = mlc.getDouble(new String[]{"minrange", "min", "mr"}, 2.0);
      this.minRangeSq = Math.pow(minRange, 2.0);
      float defSpeed = 1.0F;

      try {
         defSpeed = Float.valueOf(this.dataVar2);
      } catch (Error | Exception ex) {
      }

      this.speed = mlc.getFloat(new String[]{"speed", "s"}, defSpeed);
      this.dropTarget = mlc.getBoolean(new String[]{"droptarget", "dt"}, true);
   }

   @Override
   public boolean shouldStart() {
      AbstractLocation destination = this.activeMob.getSpawnLocation();
      if (!destination.getWorld().getUniqueId().equals(this.entity.getLocation().getWorld().getUniqueId())) {
         this.entity.teleport(destination);
         return true;
      } else if (destination != null && this.entity.getLocation().distanceSquared(destination) > this.maxRangeSq) {
         if (this.dropTarget) {
            this.entity.setTarget(null);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
      ai().navigateToLocation(this.entity, this.activeMob.getSpawnLocation(), this.speed);
   }

   @Override
   public boolean shouldEnd() {
      AbstractLocation destination = this.activeMob.getSpawnLocation();
      return destination == null || this.entity.getLocation().distanceSquared(destination) <= this.minRangeSq;
   }

   @Override
   public void end() {
   }
}
