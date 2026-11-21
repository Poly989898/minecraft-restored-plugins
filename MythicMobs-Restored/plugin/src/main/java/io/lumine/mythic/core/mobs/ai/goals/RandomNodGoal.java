package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import java.util.concurrent.TimeUnit;

@MythicAIGoal(
   name = "randomNod",
   version = "5.6",
   description = "Randomly nod their head"
)
public class RandomNodGoal extends Pathfinder implements PathfindingGoal {
   private int nodTime;
   private float nodAngle;
   private boolean noddingDown = true;
   private Cooldown cooldown = Cooldown.of(500L, TimeUnit.MILLISECONDS);

   public RandomNodGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;
   }

   @Override
   public boolean shouldStart() {
      return Numbers.randomDouble() < 0.05F;
   }

   @Override
   public void start() {
      this.nodTime = 10 + Numbers.randomInt(10);
      this.nodAngle = 0.0F;
   }

   @Override
   public void tick() {
      if (this.cooldown.test()) {
         if (this.noddingDown) {
            this.nodAngle += 10.0F;
            if (this.nodAngle > 40.0F) {
               this.noddingDown = false;
            }
         } else {
            this.nodAngle -= 10.0F;
            if (this.nodAngle < -20.0F) {
               this.noddingDown = true;
            }
         }

         getPlugin().getVolatileCodeHandler().lookAt(this.getEntity(), this.getEntity().getLocation().getYaw(), this.nodAngle);
         this.nodTime--;
      }
   }

   @Override
   public boolean shouldEnd() {
      return this.nodTime <= 0;
   }

   @Override
   public void end() {
      getPlugin().getVolatileCodeHandler().lookAt(this.getEntity(), this.getEntity().getLocation().getYaw(), 0.0F);
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }
}
