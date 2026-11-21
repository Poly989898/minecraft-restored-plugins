package io.lumine.mythic.core.volatilecode.v1_21_R7.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R7.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;

@MythicAIGoal(
   name = "bowAttackConditional",
   aliases = {"skeletonBowAttackConditional", "bowshootConditional", "bowmasterConditional", "bowAttackCond"},
   description = "Bow attack the current target while it matches the given conditions"
)
public class BowAttackConditionalGoal extends BowAttackGoal {
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public BowAttackConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
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
      return new BowAttackConditionalGoal.ConditionalRangedBowAttackGoal(
         (EntityMonster)((IRangedEntity)PathfinderHolder.getNMSEntity(this.entity)), this.speedModifier, this.attackIntervalMin, this.attackRadius
      );
   }

   public class ConditionalRangedBowAttackGoal<T extends EntityMonster & IRangedEntity> extends PathfinderGoalBowShoot<T> {
      public ConditionalRangedBowAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
         super(mob, speedModifier, attackIntervalMin, attackRadius);
      }

      public boolean b() {
         return super.b() && this.evaluateConditions();
      }

      public boolean c() {
         return super.c() && this.evaluateConditions();
      }

      private boolean evaluateConditions() {
         if (BowAttackConditionalGoal.this.targetConditions == null && BowAttackConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = BowAttackConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (BowAttackConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : BowAttackConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(BowAttackConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (BowAttackConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : BowAttackConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(BowAttackConditionalGoal.this.entity)) {
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
