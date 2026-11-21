package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEntity;

@MythicAIGoal(
   name = "bowAttack",
   aliases = {"skeletonBowAttack", "bowshoot", "bowmaster"},
   description = "An advanced bow attack"
)
public class BowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected double speedModifier;
   protected int attackIntervalMin;
   protected float attackRadius;

   public BowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
      this.attackIntervalMin = mlc.getInteger(new String[]{"attackspeedmax", "smax"}, 20);
      this.attackRadius = mlc.getFloat(new String[]{"attackradius", "radius", "r"}, 15.0F);
   }

   @Override
   public boolean isValid() {
      return ((CraftEntity)this.entity.getBukkitEntity()).getHandle() instanceof IRangedEntity && this.entity.isMonster();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalBowShoot(
         (EntityMonster)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackIntervalMin, this.attackRadius
      );
   }
}
