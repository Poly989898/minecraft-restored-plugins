package io.lumine.mythic.core.volatilecode.v26_3.ai;

import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfinderAdapter;
import java.util.EnumSet;
import lombok.Generated;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class CustomAIAdapter extends Goal implements PathfinderAdapter {
   private final Pathfinder goal;

   public static Goal create(Pathfinder goal) {
      return new CustomAIAdapter(goal);
   }

   public CustomAIAdapter(Pathfinder goal) {
      this.goal = goal;
      switch (goal.getGoalType()) {
         case MOVE:
            this.setFlags(EnumSet.of(Flag.MOVE));
            break;
         case MOVE_LOOK:
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            break;
         case TARGET:
            this.setFlags(EnumSet.of(Flag.TARGET));
      }
   }

   @Override
   public boolean isValid() {
      return this.goal.isValid();
   }

   public boolean canUse() {
      try {
         return this.goal.shouldStart();
      } catch (Error | Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public void start() {
      try {
         this.goal.start();
      } catch (Error | Exception e) {
         e.printStackTrace();
      }
   }

   public void tick() {
      try {
         this.goal.tick();
      } catch (Error | Exception e) {
         e.printStackTrace();
      }
   }

   public boolean canContinueToUse() {
      try {
         return !this.goal.shouldEnd();
      } catch (Error | Exception e) {
         e.printStackTrace();
         return true;
      }
   }

   public void stop() {
      try {
         this.goal.end();
      } catch (Error | Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return this.goal.requiresUpdateEveryTick();
   }

   @Override
   public boolean isInterruptable() {
      return this.goal.isInterruptable();
   }

   @Generated
   public Pathfinder getGoal() {
      return this.goal;
   }
}
