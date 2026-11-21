package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R1.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

@MythicAIGoal(
   name = "moveWithinDistanceOfTargetConditional",
   aliases = {"moveWithinConditional", "moveWithinDistanceOfTargetCond", "moveWithinCond"},
   description = "Moves towards the target to be within a certain range while it matches the given conditions"
)
public class MoveWithinConditionalGoal extends MoveWithinGoal {
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public MoveWithinConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
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
      return new MoveWithinConditionalGoal.ConditionalMoveWithinDistanceOfTargetGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   public class ConditionalMoveWithinDistanceOfTargetGoal extends MoveWithinGoal.MoveWithinDistanceOfTargetGoal {
      public ConditionalMoveWithinDistanceOfTargetGoal(EntityCreature mob) {
         super(mob);
      }

      @Override
      public boolean b() {
         return super.b() && this.evaluateConditions();
      }

      @Override
      public boolean c() {
         return super.c() && this.evaluateConditions();
      }

      private boolean evaluateConditions() {
         if (MoveWithinConditionalGoal.this.targetConditions == null && MoveWithinConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = MoveWithinConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (MoveWithinConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : MoveWithinConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(MoveWithinConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (MoveWithinConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : MoveWithinConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(MoveWithinConditionalGoal.this.entity)) {
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
