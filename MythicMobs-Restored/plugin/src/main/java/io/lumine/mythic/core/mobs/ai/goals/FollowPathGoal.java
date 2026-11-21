package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import java.util.ArrayList;
import java.util.List;

@MythicAIGoal(
   name = "followRoute",
   aliases = {"followPath"},
   version = "4.9",
   description = "Go from point A to point B"
)
public class FollowPathGoal extends Pathfinder implements PathfindingGoal {
   private int currentDestination = -1;
   private List<AbstractLocation> points = new ArrayList<>();
   private float speed;
   private double tolerance;

   public FollowPathGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE;

      try {
         List<Locus> defpoints = new ArrayList<>();
         if (this.dataVar1 != null) {
            String[] split2 = this.dataVar1.split(";");

            for (String s : split2) {
               try {
                  String[] split3 = s.split(",");
                  double x = Double.valueOf(split3[0]);
                  double y = Double.valueOf(split3[1]);
                  double z = Double.valueOf(split3[2]);
                  defpoints.add(Locus.of(x, y, z));
               } catch (Error | Exception e) {
               }
            }
         }

         for (Locus locus : mlc.getLocationList(new String[]{"points", "p"}, defpoints)) {
            this.points.add(new AbstractLocation(entity.getWorld(), locus.getX(), locus.getY(), locus.getZ()));
         }

         float defSpeed = 1.0F;

         try {
            defSpeed = Float.valueOf(this.dataVar2);
         } catch (Error | Exception ex) {
         }

         this.speed = mlc.getFloat(new String[]{"speed", "s"}, defSpeed);
         this.tolerance = mlc.getDouble(new String[]{"tolerance", "t"}, 3.0);
         this.currentDestination = this.getNearestLocation();
      } catch (Exception ex2) {
         ex2.printStackTrace();
      }
   }

   public int getNearestLocation() {
      AbstractLocation entityLocation = this.entity.getLocation();
      int size = this.points.size();
      if (size == 0) {
         return -1;
      } else if (size == 1) {
         return 0;
      } else {
         int bestIndex = 0;
         double bestDistance = this.points.get(0).distanceSquared(entityLocation);

         for (int i = 1; i < size; i++) {
            double distance = this.points.get(i).distanceSquared(entityLocation);
            if (Double.compare(distance, bestDistance) < 0) {
               bestDistance = distance;
               bestIndex = i;
            }
         }

         return bestIndex;
      }
   }

   @Override
   public boolean shouldStart() {
      if (this.points.size() < 2) {
         return false;
      } else {
         AbstractLocation currentTarget = this.points.get(this.currentDestination);
         if (this.entity.getLocation().distanceSquared(currentTarget) <= this.tolerance) {
            this.currentDestination++;
            if (this.currentDestination > this.points.size()) {
               return false;
            }

            currentTarget = this.points.get(this.currentDestination);
         }

         ai().navigateToLocation(this.entity, currentTarget, this.speed);
         return false;
      }
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
   }

   @Override
   public boolean shouldEnd() {
      return true;
   }

   @Override
   public void end() {
   }
}
