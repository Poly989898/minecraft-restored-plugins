package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;

@MythicAIGoal(
   name = "goToParent",
   aliases = {},
   version = "4.8",
   description = "Path to the mob's parent"
)
public class GoToParentGoal extends Pathfinder implements PathfindingGoal {
   private double followRangeSq;
   private double minRangeSq;
   private float speed;
   private boolean dropTarget;
   private boolean teleportToWorld;

   public GoToParentGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;
      double def = 4.0;

      try {
         def = Double.valueOf(this.dataVar1);
      } catch (Error | Exception e) {
      }

      double followRange = mlc.getDouble(new String[]{"followrange", "fr", "r", "maxrange"}, def);
      this.followRangeSq = Math.pow(followRange, 2.0);
      double minRange = mlc.getDouble(new String[]{"minrange", "mr"}, def);
      this.minRangeSq = Math.pow(minRange, 2.0);
      float defSpeed = 1.0F;

      try {
         defSpeed = Float.valueOf(this.dataVar2);
      } catch (Error | Exception ex) {
      }

      this.speed = mlc.getFloat(new String[]{"speed", "s"}, defSpeed);
      this.dropTarget = mlc.getBoolean(new String[]{"droptarget", "dt"}, true);
      this.teleportToWorld = mlc.getBoolean(new String[]{"teleportToWorld", "tow", "ttw"}, true);
   }

   @Override
   public boolean shouldStart() {
      if (!this.activeMob.getParent().isPresent()) {
         return false;
      } else {
         AbstractLocation destination = this.activeMob.getParent().get().getLocation();
         if (!destination.getWorld().getUniqueId().equals(this.entity.getLocation().getWorld().getUniqueId())) {
            if (this.teleportToWorld) {
               this.entity.teleport(destination);
               return true;
            } else {
               return false;
            }
         } else if (this.entity.getLocation().distanceSquared(destination) > this.followRangeSq) {
            if (this.dropTarget) {
               this.entity.setTarget(null);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
      AbstractLocation destination = this.activeMob.getParent().get().getLocation();
      if (!destination.getWorld().getUniqueId().equals(this.entity.getLocation().getWorld().getUniqueId())) {
         if (this.teleportToWorld) {
            this.entity.teleport(destination);
         }
      } else if (this.entity.getLocation().distanceSquared(destination) > 4096.0) {
         this.entity.teleport(destination);
      } else {
         ai().navigateToLocation(this.entity, destination, this.speed);
      }
   }

   @Override
   public boolean shouldEnd() {
      if (!this.activeMob.getParent().isPresent()) {
         return true;
      } else {
         AbstractLocation destination = this.activeMob.getParent().get().getLocation();
         return !destination.getWorld().getUniqueId().equals(this.entity.getLocation().getWorld().getUniqueId())
            ? true
            : this.entity.getLocation().distanceSquared(destination) <= this.minRangeSq;
      }
   }

   @Override
   public void end() {
   }
}
