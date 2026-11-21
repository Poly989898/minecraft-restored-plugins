package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import org.bukkit.craftbukkit.entity.CraftEntity;

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
      return this.entity.isMonster() && ((CraftEntity)this.entity.getBukkitEntity()).getHandle() instanceof CrossbowAttackMob;
   }

   @Override
   public Goal create() {
      return new RangedCrossbowAttackGoal((Monster)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackRadius);
   }
}
