package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;

@MythicAIGoal(
   name = "meleeAttack",
   aliases = {},
   description = "Path to the current target"
)
public class MeleeAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speedModifier = 1.0;
   private boolean followingTargetEvenIfNotSeen = true;
   private final double attackReachModifier;

   public MeleeAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speedmodifier", "speed", "s"}, 1.0);
      this.followingTargetEvenIfNotSeen = mlc.getBoolean(new String[]{"followUnseen", "fu"}, false);
      this.attackReachModifier = mlc.getDouble(new String[]{"attackReachModifier", "attackReach", "ar"}, 4.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new MeleeAttackGoal.ModifiedMeleeAttackGoal(
         PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.followingTargetEvenIfNotSeen, this.attackReachModifier
      );
   }

   protected static class ModifiedMeleeAttackGoal extends PathfinderGoalMeleeAttack {
      private final double attackReach;

      public ModifiedMeleeAttackGoal(EntityCreature var0, double var1, boolean var3, double attackReach) {
         super(var0, var1, var3);
         this.attackReach = attackReach;
      }

      protected double a(EntityLiving var0) {
         return this.attackReach + var0.dd();
      }
   }
}
