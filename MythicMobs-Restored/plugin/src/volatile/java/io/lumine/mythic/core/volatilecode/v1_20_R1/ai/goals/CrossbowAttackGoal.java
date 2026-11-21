package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCrossbowAttack;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.ICrossbow;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

@MythicAIGoal(
   name = "crossbowAttack",
   aliases = {},
   description = "Attack with a crossbow"
)
public class CrossbowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected double speedModifier;
   protected float attackRadius;

   public CrossbowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
      this.attackRadius = mlc.getFloat(new String[]{"attackradius", "radius", "r"}, 8.0F);
   }

   @Override
   public boolean isValid() {
      return this.entity.isMonster() && ((CraftEntity)this.entity.getBukkitEntity()).getHandle() instanceof ICrossbow;
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalCrossbowAttack((EntityMonster)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackRadius);
   }
}
