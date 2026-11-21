package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import java.util.concurrent.TimeUnit;

@MythicAIGoal(
   name = "goToLocation",
   aliases = {"goto"},
   version = "4.8",
   description = "Go to a location"
)
public class GoToLocationGoal extends Pathfinder implements PathfindingGoal {
   private double x;
   private double y;
   private double z;
   private float speed = 1.0F;
   private AbstractLocation destination;
   private float distanceSq = 4.0F;
   private Cooldown cooldown = Cooldown.of(250L, TimeUnit.MILLISECONDS);

   public GoToLocationGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;

      try {
         if (this.dataVar1 != null) {
            String[] sp = this.dataVar1.split(",");
            this.x = Integer.valueOf(sp[0]).intValue();
            this.y = Integer.valueOf(sp[1]).intValue();
            this.z = Integer.valueOf(sp[2]).intValue();
         }

         if (this.dataVar2 != null) {
            this.speed = Float.valueOf(this.dataVar2);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.destination = new AbstractLocation(entity.getWorld(), this.x, this.y, this.z);
   }

   @Override
   public boolean shouldStart() {
      return this.entity.getLocation().distanceSquared(this.destination) > this.distanceSq;
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
      if (this.cooldown.test()) {
         ai().navigateToLocation(this.entity, this.destination, this.speed);
      }
   }

   @Override
   public boolean shouldEnd() {
      return this.entity.getLocation().distanceSquared(this.destination) <= this.distanceSq;
   }

   @Override
   public void end() {
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }
}
