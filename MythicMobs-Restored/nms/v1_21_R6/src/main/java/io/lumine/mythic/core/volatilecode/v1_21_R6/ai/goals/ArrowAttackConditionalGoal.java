package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R6.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.monster.IRangedEntity;

@MythicAIGoal(
   name = "arrowAttackConditional",
   aliases = {"rangedAttackConditional", "arrowAttackCond", "rangedAttackCond"},
   description = "Ranged attack the current target while it matches the given conditions"
)
public class ArrowAttackConditionalGoal extends ArrowAttackGoal {
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public ArrowAttackConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
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
   public PathfinderGoal create() {
      return new ArrowAttackConditionalGoal.ConditionalRangedAttackGoal(
         (IRangedEntity)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackIntervalMin, this.attackIntervalMax, this.attackRadius
      );
   }

   public class ConditionalRangedAttackGoal extends PathfinderGoalArrowAttack {
      public ConditionalRangedAttackGoal(IRangedEntity mob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
         super(mob, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
      }

      public boolean b() {
         return super.b() && this.evaluateConditions();
      }

      public boolean c() {
         return super.c() && this.evaluateConditions();
      }

      private boolean evaluateConditions() {
         if (ArrowAttackConditionalGoal.this.targetConditions == null && ArrowAttackConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = ArrowAttackConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (ArrowAttackConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : ArrowAttackConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(ArrowAttackConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (ArrowAttackConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : ArrowAttackConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(ArrowAttackConditionalGoal.this.entity)) {
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
