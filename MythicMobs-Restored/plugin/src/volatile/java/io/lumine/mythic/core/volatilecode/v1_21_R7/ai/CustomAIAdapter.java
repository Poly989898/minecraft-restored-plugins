package io.lumine.mythic.core.volatilecode.v1_21_R7.ai;

import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfinderAdapter;
import java.util.EnumSet;
import lombok.Generated;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;

public class CustomAIAdapter extends PathfinderGoal implements PathfinderAdapter {
   private final Pathfinder goal;

   public static PathfinderGoal create(Pathfinder goal) {
      return new CustomAIAdapter(goal);
   }

   public CustomAIAdapter(Pathfinder goal) {
      this.goal = goal;
      switch (goal.getGoalType()) {
         case MOVE:
            this.a(EnumSet.of(Type.a));
            break;
         case MOVE_LOOK:
            this.a(EnumSet.of(Type.a, Type.b));
            break;
         case TARGET:
            this.a(EnumSet.of(Type.d));
      }
   }

   @Override
   public boolean isValid() {
      return this.goal.isValid();
   }

   public boolean b() {
      try {
         return this.goal.shouldStart();
      } catch (Error | Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   public void d() {
      try {
         this.goal.start();
      } catch (Error | Exception var2) {
         var2.printStackTrace();
      }
   }

   public void a() {
      try {
         this.goal.tick();
      } catch (Error | Exception var2) {
         var2.printStackTrace();
      }
   }

   public boolean c() {
      try {
         return !this.goal.shouldEnd();
      } catch (Error | Exception var2) {
         var2.printStackTrace();
         return true;
      }
   }

   public void e() {
      try {
         this.goal.end();
      } catch (Error | Exception var2) {
         var2.printStackTrace();
      }
   }

   public boolean X_() {
      return this.goal.requiresUpdateEveryTick();
   }

   public boolean W_() {
      return this.goal.isInterruptable();
   }

   @Generated
   public Pathfinder getGoal() {
      return this.goal;
   }
}
