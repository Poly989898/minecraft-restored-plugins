package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCrossbowAttack;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.monster.IRangedEntity;

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
   public PathfinderGoal create() {
      return new CrossbowAttackConditionalGoal.ConditionalRangedCrossbowAttackGoal(
         (EntityMonster)((ICrossbow)((IRangedEntity)PathfinderHolder.getNMSEntity(this.entity))), this.speedModifier, this.attackRadius
      );
   }

   public class ConditionalRangedCrossbowAttackGoal<T extends EntityMonster & IRangedEntity & ICrossbow> extends PathfinderGoalCrossbowAttack<T> {
      private final T mob;

      public ConditionalRangedCrossbowAttackGoal(T mob, double speedModifier, float attackRadius) {
         super(mob, speedModifier, attackRadius);
         this.mob = mob;
      }

      public boolean a() {
         return super.a() && this.evaluateConditions();
      }

      public boolean b() {
         return super.b() && this.evaluateConditions();
      }

      public void d() {
         EntityLiving target = this.mob.j();
         super.d();
         if (target != null && target.bs()) {
            this.mob.h(target);
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
