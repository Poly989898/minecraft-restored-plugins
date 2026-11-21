package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

@MythicAIGoal(
   name = "moveTowardsTargetConditional",
   aliases = {"moveTowardsTargetCond"},
   description = "Path to the current target"
)
public class MoveTowardsConditionalGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speedModifier;
   private float minRange;
   private float maxRange;
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public MoveTowardsConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
      this.minRange = mlc.getFloat(new String[]{"minrange", "minr", "mr"}, 0.0F);
      this.maxRange = mlc.getFloat(new String[]{"maxrange", "range", "r"}, 32.0F);
      String targetConditionString = mlc.getString(new String[]{"targetconditions", "conditions", "cond", "c"}, "null");
      if (targetConditionString != null) {
         this.targetConditions = getPlugin().getSkillManager().getConditions(targetConditionString);
      }

      String mobConditionString = mlc.getString(new String[]{"mobconditions", "mconditions", "mcond", "mc"}, "null");
      if (mobConditionString != null) {
         this.mobConditions = getPlugin().getSkillManager().getConditions(mobConditionString);
      }
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new MoveTowardsConditionalGoal.PathfinderGoalMoveTowardsTargetIf(
         PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.minRange, this.maxRange
      );
   }

   public class PathfinderGoalMoveTowardsTargetIf extends Goal {
      private final PathfinderMob mob;
      private final double speedModifier;
      private final float minRange;
      private final float maxRange;
      private LivingEntity target;
      private int repathDelay;

      public PathfinderGoalMoveTowardsTargetIf(PathfinderMob mob, double speedModifier, float minRange, float maxRange) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.minRange = minRange;
         this.maxRange = maxRange;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         this.target = this.mob.getTarget();
         if (this.target != null && this.target.isAlive()) {
            double distSq = this.target.distanceToSqr(this.mob);
            return !(distSq < this.minRange * this.minRange) && !(distSq > this.maxRange * this.maxRange) ? this.evaluateConditions() : false;
         } else {
            return false;
         }
      }

      public void start() {
         this.repathDelay = 10;
         this.mob.getNavigation().moveTo(this.target, this.speedModifier);
      }

      public void stop() {
         this.target = null;
         this.mob.getNavigation().stop();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (--this.repathDelay <= 0) {
            this.repathDelay = 10;
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
         }
      }

      private boolean evaluateConditions() {
         if (MoveTowardsConditionalGoal.this.targetConditions == null && MoveTowardsConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = MoveTowardsConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (MoveTowardsConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : MoveTowardsConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(MoveTowardsConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (MoveTowardsConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : MoveTowardsConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(MoveTowardsConditionalGoal.this.entity)) {
                        return false;
                     }
                  }
               }
            } catch (Error | Exception e) {
               e.printStackTrace();
            }

            return true;
         }
      }
   }
}
