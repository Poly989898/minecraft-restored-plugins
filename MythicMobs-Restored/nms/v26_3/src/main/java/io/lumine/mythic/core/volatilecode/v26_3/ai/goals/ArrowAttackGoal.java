package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.bukkit.craftbukkit.entity.CraftEntity;

@MythicAIGoal(
   name = "arrowAttack",
   aliases = {"rangedAttack"},
   description = "A basic bow attack"
)
public class ArrowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected double speedModifier;
   protected int attackIntervalMin;
   protected int attackIntervalMax;
   protected float attackRadius;

   public ArrowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
      this.attackIntervalMin = mlc.getInteger(new String[]{"attackspeedmax", "smax"}, 20);
      this.attackIntervalMax = mlc.getInteger(new String[]{"attackspeedmin", "amin"}, 60);
      this.attackRadius = mlc.getFloat(new String[]{"attackradius", "radius", "r"}, 15.0F);
   }

   @Override
   public boolean isValid() {
      return ((CraftEntity)this.entity.getBukkitEntity()).getHandle() instanceof RangedAttackMob;
   }

   @Override
   public Goal create() {
      return new RangedAttackGoal(
         (RangedAttackMob)PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackIntervalMin, this.attackIntervalMax, this.attackRadius
      );
   }
}
