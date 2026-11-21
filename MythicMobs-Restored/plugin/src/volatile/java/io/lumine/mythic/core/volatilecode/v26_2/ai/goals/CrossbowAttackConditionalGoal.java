package io.lumine.mythic.core.volatilecode.v26_2.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;

@MythicAIGoal(
   name = "crossbowAttackConditional",
   aliases = {"crossbowAttackCond"},
   description = "Crossbow attack the current target while it matches the given conditions"
)
public class CrossbowAttackConditionalGoal extends CrossbowAttackGoal {
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public CrossbowAttackConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
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
   public Goal create() {
      return new CrossbowAttackConditionalGoal.ConditionalRangedCrossbowAttackGoal(
         (Monster)((CrossbowAttackMob)((RangedAttackMob)PathfinderHolder.getNMSEntity(this.entity))), this.speedModifier, this.attackRadius
      );
   }

   public class ConditionalRangedCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends RangedCrossbowAttackGoal<T> {
      private final T mob;

      public ConditionalRangedCrossbowAttackGoal(T mob, double speedModifier, float attackRadius) {
         super(mob, speedModifier, attackRadius);
         this.mob = mob;
      }

      public boolean canUse() {
         return super.canUse() && this.evaluateConditions();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.evaluateConditions();
      }

      public void stop() {
         LivingEntity target = this.mob.getTarget();
         super.stop();
         if (target != null && target.isAlive()) {
            this.mob.setTarget(target);
         }
      }

      private boolean evaluateConditions() {
         if (CrossbowAttackConditionalGoal.this.targetConditions == null && CrossbowAttackConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = CrossbowAttackConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (CrossbowAttackConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : CrossbowAttackConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(CrossbowAttackConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (CrossbowAttackConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : CrossbowAttackConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(CrossbowAttackConditionalGoal.this.entity)) {
                        return false;
                     }
                  }
               }
            } catch (Error | Exception var4) {
               var4.printStackTrace();
            }

            return true;
         }
      }
   }
}
