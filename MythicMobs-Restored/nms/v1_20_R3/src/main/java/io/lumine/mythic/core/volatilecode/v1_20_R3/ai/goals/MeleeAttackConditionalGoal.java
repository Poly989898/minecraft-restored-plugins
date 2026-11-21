package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
import java.util.List;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;

@MythicAIGoal(
   name = "meleeAttackConditional",
   aliases = {"meleeAttackCond"},
   description = "Melee attack the current target while it matches the given conditions"
)
public class MeleeAttackConditionalGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speedModifier;
   private boolean followingTargetEvenIfNotSeen;
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public MeleeAttackConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speedmodifier", "speed", "s"}, 1.0);
      this.followingTargetEvenIfNotSeen = mlc.getBoolean(new String[]{"followUnseen", "fu"}, false);
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
   public PathfinderGoal create() {
      return new MeleeAttackConditionalGoal.ConditionalMeleeAttackGoal(
         PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.followingTargetEvenIfNotSeen
      );
   }

   public class ConditionalMeleeAttackGoal extends PathfinderGoalMeleeAttack {
      public ConditionalMeleeAttackGoal(EntityCreature mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
         super(mob, speedModifier, followingTargetEvenIfNotSeen);
      }

      public boolean a() {
         return super.a() && this.evaluateConditions();
      }

      public boolean b() {
         return super.b() && this.evaluateConditions();
      }

      private boolean evaluateConditions() {
         if (MeleeAttackConditionalGoal.this.targetConditions == null && MeleeAttackConditionalGoal.this.mobConditions == null) {
            return true;
         } else {
            try {
               AbstractEntity target = MeleeAttackConditionalGoal.this.entity.getTarget();
               if (target == null) {
                  return false;
               }

               if (MeleeAttackConditionalGoal.this.targetConditions != null) {
                  for (SkillCondition cond : MeleeAttackConditionalGoal.this.targetConditions) {
                     if (!cond.evaluateToEntity(MeleeAttackConditionalGoal.this.entity, target)) {
                        return false;
                     }
                  }
               }

               if (MeleeAttackConditionalGoal.this.mobConditions != null) {
                  for (SkillCondition condx : MeleeAttackConditionalGoal.this.mobConditions) {
                     if (!condx.evaluateEntity(MeleeAttackConditionalGoal.this.entity)) {
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
